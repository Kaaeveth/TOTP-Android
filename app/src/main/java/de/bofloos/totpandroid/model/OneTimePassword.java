package de.bofloos.totpandroid.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Klass zur Erstellung von One-Time Passwörtern zur Zwei-Faktor-Authentifizierung.
 *
 * Die erstellten OTP Generatoren verwenden {@code LiveData} und weitere Hintergrundthreads zur stetigen Berechnung von OTPs.
 * Diese werden in einem Cache gelegt und sollten mit {@link OneTimePassword#removeOTPGenerator(String)} entfernt werden,
 * wenn diese nicht mehr benötigt werden.
 */
public class OneTimePassword {

    private final ScheduledExecutorService otpExecutor;
    private final Map<String, LiveData<OTPResult>> otpObserverStore;
    private final Map<String, ScheduledFuture<?>> otpFutureStore;

    private static OneTimePassword instance;

    private OneTimePassword() {
        this.otpExecutor = new ScheduledThreadPoolExecutor(2);
        this.otpObserverStore = new HashMap<>();
        this.otpFutureStore = new HashMap<>();
    }

    public static OneTimePassword getInstance() {
        if(instance == null)
            instance = new OneTimePassword();
        return instance;
    }

    public static class OTPResult {
        /**
         * Das OneTime-Passwort
         */
        public String otp;
        /**
         * Die Gültigkeit des Passwortes
         */
        public short timeValid;

        public OTPResult(String otp, short timeValid) {
            this.otp = otp;
            this.timeValid = timeValid;
        }
    }

    /**
     * Erstellt ein OTP zum gegebenen Account
     * @param acc Das Konto
     * @return Das OTP und die Gültigkeit
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public static OTPResult generateOTP(Account acc) throws NoSuchAlgorithmException, InvalidKeyException {
        long t = Instant.now().getEpochSecond();
        String otp = OneTimePassword.generateTOTP(acc.secret, t, acc.period, acc.hashAlg);
        short valid = (short) (acc.period - t % acc.period);
        return new OTPResult(otp, valid);
    }

    public static long getRemainingTime(Account acc) {
        long t = Instant.now().getEpochSecond();
        return acc.period - t % acc.period;
    }

    /**
     * Erstellt einen Generator, welcher mit der gegebenen Periodendauer des Accounts OTPs berechnet.
     * Diese werden im Hintergrund berechnet und blockieren somit nicht.
     * @param acc Das Konto
     * @return Ein observable LiveData, welches mit der Periodendauer des Kontos neue OTPs erhält.
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public LiveData<OTPResult> getTimedOTPGenerator(Account acc) throws NoSuchAlgorithmException, InvalidKeyException {
        if(otpObserverStore.containsKey(acc.label)) {
            return otpObserverStore.get(acc.label);
        }

        MutableLiveData<OTPResult> otpData = new MutableLiveData<>();
        otpObserverStore.put(acc.label, otpData);

        // Initiale Berechnung
        OTPResult init = generateOTP(acc);
        otpData.postValue(init);

        ScheduledFuture<?> x = otpExecutor.scheduleWithFixedDelay(() -> {
            try {
                otpData.postValue(generateOTP(acc));
                // Wenn es knallen sollte, dann aber schon in der initialen Berechnung
            } catch (NoSuchAlgorithmException | InvalidKeyException ignored) {}
        }, init.timeValid, acc.period, TimeUnit.SECONDS);
        otpFutureStore.put(acc.label, x);

        return otpData;
    }

    /**
     * Entfernt einen OTP Generator.
     * Das LiveData Objekt, erstellt mit {@link OneTimePassword#getTimedOTPGenerator(Account)}, erhält keine OTPs mehr.
     * Wenn kein Generator für das Konto existiert, hat diese Methode kein Effekt.
     * @param accountLabel Das Label des Kontos, mit welchem der Generator erstellt wurde.
     */
    public void removeOTPGenerator(String accountLabel) {
        ScheduledFuture<?> x = otpFutureStore.get(accountLabel);
        if(x == null) return;

        x.cancel(true);
        otpFutureStore.remove(accountLabel);
        otpObserverStore.remove(accountLabel);
    }

    /**
     * Erstellt ein Timed-One-Time-Password anhand des übergebenen Keys und UNIX-Zeit.
     *
     * Die Methode implementiert TOTP aus <a href="https://datatracker.ietf.org/doc/html/rfc6238">RFC6238</a>.
     * @param key Das gemeinsame Secret
     * @param unixTime Die momentane UNIX-Zeit
     * @param period Die Periodendauer/Gültigkeit des Schlüssels
     * @param alg Der zu verwendende Hash-Algorithmus
     * @return Ein String mit einem 6-stelligen OTP
     * @throws NoSuchAlgorithmException Der Cryptoprovider der JDK kennt den Algorithmus nicht
     * @throws InvalidKeyException Der gegebene Schlüssel ist nicht für HMAC geeignet
     */
    public static String generateTOTP(byte[] key, long unixTime, short period, OTPHashAlgorithms alg)
            throws NoSuchAlgorithmException, InvalidKeyException {
        long T = unixTime / period;

        // T nach byte[] konvertieren mit High-Order Byte Ordnung
        byte[] hmacPayload = new byte[8];
        for(int i = 8; i-- > 0; T >>>= 8) {
            hmacPayload[i] = (byte) T;
        }

        String hmacAlg = toHmacAlgString(alg);
        Mac hmacInstance = Mac.getInstance(hmacAlg);
        hmacInstance.init(new SecretKeySpec(key, hmacAlg));

        byte[] hmac = hmacInstance.doFinal(hmacPayload);

        // Auf 6 Stellen truncaten - Algorithmus im RFC beschrieben
        int offset = hmac[hmac.length-1] & 0xF;
        int binCode = ((hmac[offset] & 0x7F) << 24)
                | ((hmac[offset+1] & 0xFF) << 16)
                | ((hmac[offset+2] & 0xFF) << 8)
                | ((hmac[offset+3]) & 0xFF);

        int otp = binCode % 1000000;

        // Linksbünding mit 0 auffüllen
        StringBuilder result = new StringBuilder(String.valueOf(otp));
        result.insert(0, "000000", 0, 6-result.length());

        return result.toString();
    }

    private static String toHmacAlgString(OTPHashAlgorithms alg) {
        switch(alg) {
            case SHA1:
                return "HmacSha1";
            case SHA256:
                return "HmacSha256";
            case SHA512:
                return "HmacSha512";
            default:
                throw new EnumConstantNotPresentException(OTPHashAlgorithms.class, "Hashalgorithmus nicht abgedeckt");
        }
    }
}

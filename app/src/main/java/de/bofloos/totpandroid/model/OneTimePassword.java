package de.bofloos.totpandroid.model;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

public class OneTimePassword {

    /**
     * Erstellt ein Timed-One-Time-Password anhand des übergebenen Keys und der momentanen System UNIX-Zeit.
     *
     * Die Methode implementiert TOTP aus <a href="https://datatracker.ietf.org/doc/html/rfc6238">RFC6238</a>.
     * @param key Das gemeinsame Secret
     * @param period Die Periodendauer/Gültigkeit des Schlüssels
     * @param alg Der zu verwendende Hash-Algorithmus
     * @return Einen String mit einem 6-stelligen OTP
     * @throws NoSuchAlgorithmException Der Cryptoprovider der JDK kennt den Algorithmus nicht
     * @throws InvalidKeyException Der gegebene Schlüssel ist nicht für HMAC geeignet
     */
    public static String generateTOTP(byte[] key, short period, OTPHashAlgorithms alg) throws NoSuchAlgorithmException, InvalidKeyException {
        long T = Instant.now().getEpochSecond() / period;

        byte[] hmacPayload = new byte[8];
        for(int i = 8; i-- > 0; T >>>= 8) { // Was zum fick
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

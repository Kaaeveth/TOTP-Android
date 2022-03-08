package de.bofloos.totpandroid.viewmodel;

import android.text.TextUtils;
import androidx.lifecycle.ViewModel;
import de.bofloos.totpandroid.model.Account;
import de.bofloos.totpandroid.model.AccountRepository;
import de.bofloos.totpandroid.model.OTPHashAlgorithms;
import de.bofloos.totpandroid.model.OneTimePassword;
import org.apache.commons.codec.binary.Base32;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AuthenticatorsViewModel extends ViewModel {

    private final AccountRepository accountRepo;

    public AuthenticatorsViewModel(AccountRepository accountRepo) {
        this.accountRepo = accountRepo;
    }

    public AccountRepository getAccountRepo() {
        return accountRepo;
    }

    private boolean createAccount(String label, String issuer, byte[] secret,
                                  short period, OTPHashAlgorithms alg) {
        if(accountRepo.accountExists(label))
            return false;

        accountRepo.insertAccount(new Account(label, issuer, secret, alg, period));
        return true;
    }

    public void deleteAccount(Account acc) {
        OneTimePassword.getInstance().removeOTPGenerator(acc.label);
        accountRepo.deleteAccount(acc);
    }

    /**
     * Erstellt ein neues Konto mittels einer URI.
     * <a href="https://github.com/google/google-authenticator/wiki/Key-Uri-Format}">Das Format der URI</a>
     * <br>
     * Darf nicht vom Main/UI-Thread aufgerufen werden.
     * <br>
     * {@link #createAccount(String, String, byte[], short, OTPHashAlgorithms)}
     * @param uri URI im angegeben Format
     * @return {@code false} wenn die URI ungültig ist oder das Konto bereits existiert
     */
    public boolean createAccount(URI uri) {
        String type = uri.getAuthority();
        if(!type.equals("totp") || !uri.getScheme().equals("otpauth"))
            return false;

        Map<String, String> q = parseQuery(uri.getQuery());
        if(q.isEmpty())
            return false;

        String secret = q.get("secret");
        if(TextUtils.isEmpty(secret))
            return false;

        String label = uri.getPath().substring(1);
        if(!Pattern.matches(".+(:|%3A)(%20| )*.+", label))
            return false;

        String periodRaw = q.get("period");
        short period;
        try {
            period = TextUtils.isEmpty(periodRaw) ? 30 : Short.parseShort(periodRaw);
        } catch (NumberFormatException e) {
            return false;
        }

        String algRaw = q.get("algorithm");
        OTPHashAlgorithms alg = OTPHashAlgorithms.SHA1;
        if(!TextUtils.isEmpty(algRaw))
            switch (algRaw) {
                case "SHA256":
                    alg = OTPHashAlgorithms.SHA256;
                    break;
                case "SHA512":
                    alg = OTPHashAlgorithms.SHA512;
                    break;
                // default ist SHA1 - mit Initialisierung abgedeckt
            }

        String issuer = q.get("issuer");
        // Wenn kein Issuer vorhanden, diesen aus dem Label ziehen
        // Der RegEx oberhalb garantiert die Existenz eines solchen
        if(TextUtils.isEmpty(issuer))
            issuer = label.substring(0, label.indexOf(':'));

        return createAccount(label, issuer, new Base32().decode(secret), period, alg);
    }

    /**
     * Erstellt ein neues Konto und speichert dies persistent.
     * Als Periode wird 30 Sekunden und SHA1 als Hash-Algorithmus genommen.
     * Darf nicht vom Main/UI-Thread aufgerufen werden.
     * @param label Das Label des Kontos. Muss einzigartig sein und hat i.d.R das Format
     *              {@code accountname / issuer (“:” / “%3A”) *”%20” accountname}
     * @param issuer Der Anbieter des Kontos. Sollte gleich sein mit dem Issuer im Label (wird aber nicht erzwungen).
     * @param secret Das gemeinsame Secret zur Generierung der OTPs
     * @return {@code true} wenn das Konto erfolgreich angelegt wurde.
     *         Dies ist nicht der Fall, wenn ein Konto mit dem gegebenen Label bereits existiert.
     */
    public boolean createAccount(String label, String issuer, byte[] secret) {
        return createAccount(label, issuer, secret, (short) 30, OTPHashAlgorithms.SHA1);
    }

    /**
     * Parsed die gegebene Query in ihre Key-Value-Paare
     * @param q Der Query-Teil einer URI
     * @return Eine Map mit Key-Value Paaren der Query
     */
    private Map<String, String> parseQuery(String q) {
        if(q == null || q.equals(""))
            return new HashMap<>();

        return Arrays.stream(q.split("&"))
                .map(kv -> kv.split("="))
                .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));
    }

}

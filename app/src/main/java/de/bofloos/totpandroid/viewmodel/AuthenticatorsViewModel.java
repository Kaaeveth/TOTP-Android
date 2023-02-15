package de.bofloos.totpandroid.viewmodel;

import android.content.ContentResolver;
import android.net.Uri;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import de.bofloos.totpandroid.model.Account;
import de.bofloos.totpandroid.model.AccountRepository;
import de.bofloos.totpandroid.model.OTPHashAlgorithms;
import de.bofloos.totpandroid.model.OneTimePassword;
import de.bofloos.totpandroid.util.EventQueue;
import org.apache.commons.codec.binary.Base32;

import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.function.Consumer;
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
     * <a href="https://github.com/google/google-authenticator/wiki/Key-Uri-Format">Das Format der URI</a>
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
        String issuer = q.get("issuer");
        if(TextUtils.isEmpty(issuer)){
            if(Pattern.matches(".+(:|%3A)(%20| )*.+", label)){
                issuer = label.substring(0, label.indexOf(':'));
            } else
                return false;
        }

        String periodRaw = q.get("period");
        short period;
        try {
            if(TextUtils.isEmpty(periodRaw))
                period = 30;
            else {
                period = Short.parseShort(periodRaw);
                if(period <= 0)
                    return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        String algRaw = q.get("algorithm");
        OTPHashAlgorithms alg;
        if(!TextUtils.isEmpty(algRaw)){
            algRaw = algRaw.toUpperCase();
            switch (algRaw) {
                case "SHA1":
                    alg = OTPHashAlgorithms.SHA1;
                    break;
                case "SHA256":
                    alg = OTPHashAlgorithms.SHA256;
                    break;
                case "SHA512":
                    alg = OTPHashAlgorithms.SHA512;
                    break;
                default:
                    return false;
            }
        }
        else
            alg = OTPHashAlgorithms.SHA1;

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

    /**
     * Exportiert die, in der Datenbank hinterlegten Konten, als {@code List<Account>} serialisiert zur gegebenen URI.
     * @param fileUri Content-URI zur Datei, diese muss existieren
     * @param resolver Resolver zum Öffnen der Datei
     * @param cb Callback für wenn die Datei geschrieben worden ist oder ein Fehler aufgetreten ist
     */
    public void exportAccounts(Uri fileUri, ContentResolver resolver, Consumer<Boolean> cb) {
        LiveData<List<Account>> accountsLiveData = accountRepo.getAllAccounts();
        Observer<List<Account>> observer = new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accounts) {
                try {
                    OutputStream out = resolver.openOutputStream(fileUri, "w");
                    ObjectOutputStream accountsWriter = new ObjectOutputStream(out);
                    //Account[] accountsArray = accounts.toArray(new Account[0]);
                    accountsWriter.writeObject(accounts);

                    accountsWriter.close();
                    accountsLiveData.removeObserver(this);
                    cb.accept(true);
                } catch (IOException e) {
                    cb.accept(false);
                }
            }
        };

        accountsLiveData.observeForever(observer);
    }

    /**
     * Importiert die serialisierten Konten aus der gegebenen URI.
     * Die Konten müssen als {@code List<Account>} serialisiert sein.
     * @param fileUri URI zur Datei mit den Konten
     * @param resolver Resolver zum Öffnen der Datei
     * @param cb Callback für wenn die Konten importiert worden sind oder ein Fehler aufgetreten ist.
     */
    public void importAccounts(Uri fileUri, ContentResolver resolver, Consumer<Boolean> cb) {
        // Zugriff auf die Platte in Hintergrundthread legen
        EventQueue.getInstance().post(() -> {
            try {
                InputStream in = resolver.openInputStream(fileUri);
                ObjectInputStream accountsReader = new ObjectInputStream(in);
                List<Account> accounts = (List<Account>) accountsReader.readObject();
                accountsReader.close();

                for(Account a : accounts) {
                    accountRepo.insertAccount(a);
                }

                cb.accept(true);
            } catch (IOException | ClassNotFoundException e) {
                cb.accept(false);
            }
        });
    }
}

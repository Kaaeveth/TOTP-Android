package de.bofloos.totpandroid.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Schema für ein TOTP Konto:
 * <br>
 * <a href="https://github.com/google/google-authenticator/wiki/Key-Uri-Format">Link</a>
 * <br>
 * <br>
 *
 * Die Anzahl der Ziffern wird ignoriert.
 */
@Entity
public class Account {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "label")
    public String label;

    @ColumnInfo(name = "issuer")
    public String issuer;

    /**
     * Secret, welches für die Erstellung von OTPs verwendet wird.
     * Wird verschlüsselt gespeichert
     */
    @ColumnInfo(name = "secret")
    public String secret;

    /**
     * Ausgehandelter Hash Algorithmus für den HMAC
     */
    @ColumnInfo(name = "hashAlg")
    public OTPHashAlgorithms hashAlg;

    /**
     * UNIX Zeit der Erstellung des Secrets in ms
     */
    @ColumnInfo(name = "T0")
    public long T0;

    /**
     * Wie lange ein OTP gültig ist in Sekunden.
     */
    @ColumnInfo(name = "period", defaultValue = "30")
    public short period;
}

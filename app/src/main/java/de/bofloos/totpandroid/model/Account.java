package de.bofloos.totpandroid.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    public Account(@NonNull String label, String issuer, String secret, OTPHashAlgorithms hashAlg, short period) {
        this.label = label;
        this.issuer = issuer;
        this.secret = secret;
        this.hashAlg = hashAlg;
        this.period = period;
    }

    public Account() {
        label = "";
    }

    @Override
    public boolean equals(@Nullable @org.jetbrains.annotations.Nullable Object obj) {
        return obj instanceof Account && label.equals(((Account) obj).label);
    }

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
     * Wie lange ein OTP gültig ist in Sekunden.
     */
    @ColumnInfo(name = "period", defaultValue = "30")
    public short period;
}

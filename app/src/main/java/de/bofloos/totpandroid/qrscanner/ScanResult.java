package de.bofloos.totpandroid.qrscanner;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * Ergebnis der QRScannerActivity.
 *
 * Beinhaltet eine {@code otpauth} URI oder einzelne, manuell eingegebene Felder.
 * Wenn eine URI vorhanden ist, dann sind die restlichen Felder {@code null}.
 * Dies sollte mit {@code isUriResult} vorher überprüft werden.
 */
public class ScanResult implements Parcelable {
    private String issuer;
    private String account;
    private String secret;

    private String uri;

    public ScanResult(String issuer, String account, String secret) {
        this.issuer = issuer;
        this.account = account;
        this.secret = secret;
    }

    public ScanResult(String uri) {
        this.uri = uri;
    }

    protected ScanResult(Parcel in) {
        issuer = in.readString();
        account = in.readString();
        secret = in.readString();
        uri = in.readString();
    }

    public static final Creator<ScanResult> CREATOR = new Creator<ScanResult>() {
        @Override
        public ScanResult createFromParcel(Parcel in) {
            return new ScanResult(in);
        }

        @Override
        public ScanResult[] newArray(int size) {
            return new ScanResult[size];
        }
    };

    public String getIssuer() {
        return issuer;
    }

    public String getAccount() {
        return account;
    }

    public String getSecret() {
        return secret;
    }

    public String getUri() {
        return uri;
    }

    /**
     * Beinhaltet das Parceable eine URI.
     * Wenn dies {@code true} ist, dann sind alle anderen Felder {@code null}.
     * @return Ob eine URI vorhanden ist
     */
    public boolean isUriResult() {
        return uri != null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        if(isUriResult())
            return uri;
        return "Issuer: "+issuer+"; Account: "+account+"; Secret: "+secret;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(issuer);
        parcel.writeString(account);
        parcel.writeString(secret);
        parcel.writeString(uri);
    }
}

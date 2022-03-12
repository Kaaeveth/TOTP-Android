package de.bofloos.totpandroid.ui;

import android.os.CountDownTimer;
import androidx.lifecycle.*;
import de.bofloos.totpandroid.model.Account;
import de.bofloos.totpandroid.model.OneTimePassword;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Basis für Klassen, welche Updates für TOTPs erhalten und diese zeitlich rendern wollen.
 * Ableitende Klassen müssen {@link TimedOTPViewable#setupCodeView()} am Ende ihrer Initialisierung aufrufen.
 */
public abstract class TimedOTPViewable {

    private CountDownTimer timer;
    private Account acc;
    private LifecycleOwner lo;

    public TimedOTPViewable(Account acc, LifecycleOwner lo) {
        this.acc = acc;
        this.lo = lo;
    }

    /**
     * Callback für wenn ein neues OTP bereitsteht.
     * @param code Das neue OTP
     */
    protected abstract void onNewCode(String code);

    /**
     * Callback für wenn eine Sekunde von der Gültigkeit abgelaufen ist
     * @param sec Die restliche Gültigkeit in Sekunden
     */
    protected abstract void onTick(long sec);

    /**
     * Erstellt einen TOTP Generator und bindet diesen an die gegebenen View-Elemente.
     * Muss aufgerufen werden, sobald {@link TimedOTPViewable#onNewCode(String)} und {@link TimedOTPViewable#onTick(long)}
     * betriebsbereit sind.
     */
    protected void setupCodeView() throws NoSuchAlgorithmException, InvalidKeyException {
        LiveData<OneTimePassword.OTPObserverPayload> generator = OneTimePassword.getInstance().getTimedOTPGenerator(acc);

        generator.observe(lo, otp -> {
            onNewCode(otp.otp);
            if(timer != null)
                timer.cancel();
            timer = new CountDownTimer(OneTimePassword.getRemainingTime(acc) * 1000, 1000) {
                @Override
                public void onTick(long l) {
                    TimedOTPViewable.this.onTick(l / 1000);
                }
                @Override
                public void onFinish() {}
            }.start();
        });
    }
}

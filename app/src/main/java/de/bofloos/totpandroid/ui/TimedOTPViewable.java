package de.bofloos.totpandroid.ui;

import android.app.Activity;
import android.os.CountDownTimer;
import androidx.lifecycle.*;
import de.bofloos.totpandroid.model.Account;
import de.bofloos.totpandroid.model.OneTimePassword;
import de.bofloos.totpandroid.util.Util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Interface für Klassen, welche Updates für TOTPs erhalten und diese zeitlich rendern wollen.
 */
public interface TimedOTPViewable {
    Account getAccount();
    Activity getActivity();
    void onSetCodeText(String code);
    LifecycleOwner getViewLifecycleOwner();

    void onTick(long sec);
    CountDownTimer[] timer = new CountDownTimer[1];

    /**
     * Erstellt einen TOTP Generator und bindet diesen an die gegebenen View-Elemente
     */
    default void setupCodeView() {
        Account acc = getAccount();
        Activity parent = getActivity();
        LifecycleOwner lo = getViewLifecycleOwner();

        try {
            LiveData<OneTimePassword.OTPObserverPayload> generator = OneTimePassword.getInstance().getTimedOTPGenerator(acc);

            generator.observe(lo, otp -> {
                onSetCodeText(otp.otp);
                if(timer[0] != null)
                    timer[0].cancel();
                timer[0] = new CountDownTimer(OneTimePassword.getRemainingTime(acc) * 1000, 1000) {
                    @Override
                    public void onTick(long l) {
                        TimedOTPViewable.this.onTick(l / 1000);
                    }
                    @Override
                    public void onFinish() {}
                }.start();
            });

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            Util.showMsg("Fehler beim Erstellen des Codes", parent);
        }
    }
}

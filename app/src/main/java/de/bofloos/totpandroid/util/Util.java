package de.bofloos.totpandroid.util;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.lifecycle.LifecycleOwner;
import de.bofloos.totpandroid.model.Account;
import de.bofloos.totpandroid.model.OneTimePassword;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Util {

    public static void setupCodeView(Account acc, ProgressBar validityBar, TextView codeTv, Activity parent, LifecycleOwner lo) {
        ObjectAnimator progressAnim = ObjectAnimator.ofInt(validityBar, "progress", 0, 100);
        progressAnim.setInterpolator(new DecelerateInterpolator());

        try {
            OneTimePassword.getInstance().getTimedOTPGenerator(acc).observe(lo, otp -> {
                codeTv.setText(otp.otp);
                progressAnim.setDuration(otp.timeValid * 1000);
                parent.runOnUiThread(progressAnim::start);
            });
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            Toast.makeText(parent, "Fehler beim Erstellen des Codes", Toast.LENGTH_LONG).show();
        }
    }
}

package de.bofloos.totpandroid.util;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.lifecycle.LifecycleOwner;
import de.bofloos.totpandroid.model.Account;
import de.bofloos.totpandroid.model.OneTimePassword;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Util {

    /**
     * Erstellt einen TOTP Generator und bindet diesen an die gegebenen View-Elemente
     * @param acc Das Konto mit dem Secret
     * @param validityBar Die ProgressBar, welche die Gültigkeit des Codes anzeigt
     * @param codeTv Die TextView, welche den Code anzeigt
     * @param parent Welche die View rendert
     * @param lo Lifecycle der übergebenen Views
     */
    public static void setupCodeView(Account acc, ProgressBar validityBar, TextView codeTv, Activity parent, LifecycleOwner lo) {
        ObjectAnimator progressAnim = ObjectAnimator.ofInt(validityBar, "progress", 0, 100);
        progressAnim.setInterpolator(new LinearInterpolator());

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

    public static void showMsg(String msg, Context ctx) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }
}

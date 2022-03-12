package de.bofloos.totpandroid.ui.authenticators;

import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.lifecycle.LifecycleOwner;
import de.bofloos.totpandroid.model.Account;
import de.bofloos.totpandroid.ui.TimedOTPViewable;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Ein TimedOTPViewable welche den Code in einer TextView und die Gültigkeit in einer ProgressBar angibt.
 */
public class TimedOTPViewableProgressBarSetup extends TimedOTPViewable {

    private final TextView codeTv;
    private final ProgressBar validityBar;

    public TimedOTPViewableProgressBarSetup(LifecycleOwner lo, Account acc, ProgressBar validityBar, TextView codeTv)
            throws NoSuchAlgorithmException, InvalidKeyException {
        super(acc, lo);

        this.codeTv = codeTv;
        this.validityBar = validityBar;
        validityBar.setMin(0);
        validityBar.setMax(acc.period);
        validityBar.setIndeterminate(false);

        setupCodeView();
    }

    @Override
    public void onTick(long sec) {
        validityBar.setProgress((int) sec, true);
    }

    @Override
    public void onNewCode(String code) {
        codeTv.setText(code);
    }
}

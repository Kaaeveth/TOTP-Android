package de.bofloos.totpandroid.ui.authenticators;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.lifecycle.LifecycleOwner;
import de.bofloos.totpandroid.model.Account;
import de.bofloos.totpandroid.ui.TimedOTPViewable;

public class TimedAccountViewableSetup implements AccountCodeSetupListener, TimedOTPViewable {

    private Activity parent;
    private Account acc;
    private LifecycleOwner lo;
    private TextView codeTv;
    private ProgressBar validityBar;

    public TimedAccountViewableSetup(Activity parent, LifecycleOwner lo) {
        this.parent = parent;
        this.lo = lo;
    }

    @Override
    public void onCodeSetup(Account acc, ProgressBar validityBar, TextView codeTv) {
        this.acc = acc;
        this.codeTv = codeTv;
        this.validityBar = validityBar;
        validityBar.setMin(0);
        validityBar.setMax(acc.period);
        validityBar.setIndeterminate(false);

        setupCodeView();
    }

    @Override
    public void onTick(long sec) {
        validityBar.setProgress((int) sec);
    }

    @Override
    public Account getAccount() {
        return acc;
    }

    @Override
    public Activity getActivity() {
        return parent;
    }

    @Override
    public void onSetCodeText(String code) {
        codeTv.setText(code);
    }

    @Override
    public LifecycleOwner getViewLifecycleOwner() {
        return lo;
    }

    public void setAccount(Account acc) {
        this.acc = acc;
    }
}

package de.bofloos.totpandroid.ui.authenticators;

import android.widget.ProgressBar;
import android.widget.TextView;
import de.bofloos.totpandroid.model.Account;

public interface AccountCodeSetupListener {
    void onCodeSetup(Account acc, ProgressBar validityBar, TextView codeTv);
}

package de.bofloos.totpandroid.ui.authenticators.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import de.bofloos.totpandroid.BaseActivity;
import de.bofloos.totpandroid.R;
import de.bofloos.totpandroid.model.Account;
import de.bofloos.totpandroid.viewmodel.AuthenticatorsViewModel;
import de.bofloos.totpandroid.viewmodel.AuthenticatorsViewModelFactory;

/**
 * Aktivität für die Fragmente AuthenticatorsDetailFragment und AuthenticatorsEditFragment
 */
public class AuthenticatorsDetailActivity extends BaseActivity {

    private static final String PARAM_ACCOUNT = "ACC";

    Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticators_detail);

        getSupportActionBar().setTitle("");

        account = (Account) getIntent().getSerializableExtra(PARAM_ACCOUNT);

        // Detail Fragment per default anzeigen
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_detail_container, AuthenticatorsDetailFragment.newInstance(account))
                .commit();
    }

    public static Intent newIntent(Account acc, Context ctx) {
        Intent i = new Intent(ctx, AuthenticatorsDetailActivity.class);
        i.putExtra(PARAM_ACCOUNT, acc);
        return i;
    }
}
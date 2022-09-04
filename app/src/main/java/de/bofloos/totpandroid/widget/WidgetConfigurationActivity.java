package de.bofloos.totpandroid.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.widget.Button;
import android.widget.RemoteViews;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.bofloos.totpandroid.R;
import de.bofloos.totpandroid.model.Account;
import de.bofloos.totpandroid.ui.authenticators.AuthenticatorsListAdapter;
import de.bofloos.totpandroid.viewmodel.AuthenticatorsViewModel;
import de.bofloos.totpandroid.viewmodel.AuthenticatorsViewModelFactory;

public class WidgetConfigurationActivity extends AppCompatActivity {

    private int appWidgetId;
    private AuthenticatorsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(RESULT_CANCELED);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        viewModel = new ViewModelProvider(this, new AuthenticatorsViewModelFactory(this))
                .get(AuthenticatorsViewModel.class);

        setContentView(R.layout.activity_widget_configuration);
        setup();
    }

    private void setup() {
        RecyclerView accountList = findViewById(R.id.availableAccounts);
        accountList.setLayoutManager(new LinearLayoutManager(this));
        accountList.setHasFixedSize(true);

        AuthenticatorsListAdapter listAdapter = new AuthenticatorsListAdapter(
                viewModel.getAccountRepo().getAllAccounts(), this);
        listAdapter.setOnClickListener(this::finishActivityWithAccount, true);
        accountList.setAdapter(listAdapter);

        // Cancel Button
        Button cancelButton = findViewById(R.id.widgetCancelBtn);
        cancelButton.setOnClickListener(x -> finish());
    }

    private void finishActivityWithAccount(Account acc) {
        WidgetConfigurationStore store = WidgetConfigurationStore.getInstance(this);
        store.setAccountForWidgetId(appWidgetId, acc);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        RemoteViews views = OTPAppWidgetProvider.createWidgetView(this, acc, appWidgetId);
        appWidgetManager.updateAppWidget(appWidgetId, views);

        finishActivity();
    }

    private void finishActivity() {
        Intent resultValue = new Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}
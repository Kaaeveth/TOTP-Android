package de.bofloos.totpandroid.widget;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.lifecycle.LiveData;
import de.bofloos.totpandroid.R;
import de.bofloos.totpandroid.model.Account;
import de.bofloos.totpandroid.model.AccountRepository;
import de.bofloos.totpandroid.model.AppDatabase;

/**
 * Store for binding widget ids to accounts
 */
public class WidgetConfigurationStore {

    private static WidgetConfigurationStore instance;

    public static WidgetConfigurationStore getInstance(Context ctx) {
        if(instance == null)
            instance = new WidgetConfigurationStore(ctx);
        return instance;
    }

    private SharedPreferences store;
    private AccountRepository accountRepository;

    private WidgetConfigurationStore(Context ctx) {
        store = ctx.getSharedPreferences(
                ctx.getResources().getString(R.string.widget_id_store_name), Context.MODE_PRIVATE);
        accountRepository = AppDatabase.getInstance(ctx).getAccountRepository();
    }

    public void setAccountForWidgetId(int widgetId, Account acc){
        store.edit().putString(String.valueOf(widgetId), acc.label).apply();
    }

    public void removeAccountForWidgetId(int widgetId) {
        store.edit().putString(String.valueOf(widgetId), null).apply();
    }

    public LiveData<Account> getAccountForWidgetId(int widgetId) {
        String label = store.getString(String.valueOf(widgetId), null);
        if(label != null) {
            return accountRepository.getAccountByLabel(label);
        } else {
            removeAccountForWidgetId(widgetId);
            return null;
        }
    }
}

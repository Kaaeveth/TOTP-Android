package de.bofloos.totpandroid.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import de.bofloos.totpandroid.R;
import de.bofloos.totpandroid.model.Account;
import de.bofloos.totpandroid.model.OneTimePassword;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

public class OTPAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        WidgetConfigurationStore store = WidgetConfigurationStore.getInstance(context);

        for (int id : appWidgetIds) {
            LiveData<Account> liveAccount = store.getAccountForWidgetId(id);
            if(liveAccount == null) continue;

            Observer<Account> observer = new Observer<Account>() {
                @Override
                public void onChanged(Account account) {
                    liveAccount.removeObserver(this);
                    RemoteViews views = createWidgetView(context, account, id);
                    appWidgetManager.updateAppWidget(id, views);
                }
            };
            liveAccount.observeForever(observer);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        WidgetConfigurationStore store = WidgetConfigurationStore.getInstance(context);
        for(int id : appWidgetIds) {
            store.removeAccountForWidgetId(id);
        }
    }

    private static void setOTPInRemoteView(Account account, RemoteViews views){
        try {
            String otp = OneTimePassword.generateTOTP(account.secret, Instant.now().getEpochSecond(),
                    account.period, account.hashAlg);
            views.setTextViewText(R.id.widgetOtpDisplay, otp);
        } catch (InvalidKeyException | NoSuchAlgorithmException ignored){}
        catch (NullPointerException e) {
            views.setTextViewText(R.id.widgetOtpDisplay, "Error");
        }
    }

    public static RemoteViews createWidgetView(Context ctx, Account account, int widgetId) {
        RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.widget_2fa);

        /* Broadcast zum Aktualisieren des Widgets erstellen.
         * Dieses wird ausgelöst, wenn auf das OTP geklickt wird.*/
        Intent intent = new Intent();
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        // Die widgetId des momentanen Widgets - dieses wird aktualisiert
        intent.putExtra("appWidgetIds", new int[]{widgetId});
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                ctx,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        setOTPInRemoteView(account, views);
        views.setOnClickPendingIntent(R.id.widgetOtpDisplay, pendingIntent);
        return views;
    }

}

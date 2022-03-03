package de.bofloos.totpandroid;

import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;
import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK;

/**
 * Basis für alle Aktivitäten.
 *
 * Färbt die ActionBar und StatusBar einheitlich für alle Views bzw. versteckt diese.
 */
public class BaseActivity extends AppCompatActivity {

    boolean hideActionBar;
    static BiometricPrompt.PromptInfo promptInfo;
    static BiometricPrompt authPrompt;

    //Anzahl der gestarteten Aktivitäten der App.
    //Wird verwendet um zu bestimmen, wann von einer externen Aktivität in die App gewechselt wird,
    //um die Authentifizierung durchzuführen.
    static int startedActivities = 0;

    public BaseActivity(){
        this(false);
    }

    public BaseActivity(boolean hideActionBar) {
        this.hideActionBar = hideActionBar;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Farbe der ActionBar für alle Views/Fragmente setzen
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        if(!hideActionBar)
            actionBar.setBackgroundDrawable(
                    new ColorDrawable(getResources().getColor(R.color.topbar)));
        else
            actionBar.hide();

        setupAuth();
    }

    private void setupAuth() {
        if(authPrompt != null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Authentifizierung")
                    .setAllowedAuthenticators(BIOMETRIC_STRONG | DEVICE_CREDENTIAL | BIOMETRIC_WEAK)
                    .build();
        }

        //TODO: handle error
        authPrompt = new BiometricPrompt(this, getMainExecutor(), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull @NotNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull @NotNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(startedActivities == 1)
            authPrompt.authenticate(promptInfo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startedActivities++;
    }

    @Override
    protected void onStop() {
        super.onStop();
        startedActivities--;
    }
}

package de.bofloos.totpandroid;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

/**
 * Basis für alle Aktivitäten.
 *
 * Färbt die ActionBar und StatusBar einheitlich für alle Views bzw. versteckt diese.
 */
public class BaseActivity extends AppCompatActivity {

    boolean hideActionBar;

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
    }
}

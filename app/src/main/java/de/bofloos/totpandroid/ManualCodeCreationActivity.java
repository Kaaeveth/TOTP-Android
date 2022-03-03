package de.bofloos.totpandroid;

import android.os.Bundle;
import androidx.annotation.Nullable;

public class ManualCodeCreationActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_creation_manual);

        getSupportActionBar().setTitle("Authenticator erstellen");

        findViewById(R.id.saveBtn).setOnClickListener(l -> {
            finish();
        });
    }
}

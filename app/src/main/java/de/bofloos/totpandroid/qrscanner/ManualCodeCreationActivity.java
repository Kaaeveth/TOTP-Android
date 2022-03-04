package de.bofloos.totpandroid.qrscanner;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import de.bofloos.totpandroid.BaseActivity;
import de.bofloos.totpandroid.R;

public class ManualCodeCreationActivity extends BaseActivity {

    EditText issuerInput;
    EditText accountInput;
    EditText secretInput;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_creation_manual);

        getSupportActionBar().setTitle("Authenticator erstellen");

        issuerInput = findViewById(R.id.issuerInput);
        accountInput = findViewById(R.id.accountInput);
        secretInput = findViewById(R.id.secretInput);

        findViewById(R.id.saveBtn).setOnClickListener(this::handleSave);
    }

    private void handleSave(View l) {
        if(!inputOk()) {
            Toast.makeText(this, "Fehlerhafter Input", Toast.LENGTH_LONG).show();
            return;
        }

        Intent res = new Intent();
        ScanResult scanResult = new ScanResult(issuerInput.getText().toString(),
                                    accountInput.getText().toString(), secretInput.getText().toString());
        res.putExtra("RESULT", scanResult);
        setResult(RESULT_OK, res);
        finish();
    }

    private boolean inputOk() {
        return !TextUtils.isEmpty(issuerInput.getText()) &&
               !TextUtils.isEmpty(accountInput.getText()) &&
               !TextUtils.isEmpty(secretInput.getText());
    }
}

package de.bofloos.totpandroid.ui.authenticators;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import de.bofloos.totpandroid.R;
import de.bofloos.totpandroid.QRScannerActivity;
import org.jetbrains.annotations.NotNull;

public class AuthenticatorsFragment extends Fragment {

    static final int QR_REQUEST_CODE = 1337;
    static final String TAG = AuthenticatorsFragment.class.getName();

    AuthenticatorsViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(AuthenticatorsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_authenticators, container, false);
        setupViewElements(v);
        return v;
    }

    private void setupViewElements(View v) {
        v.findViewById(R.id.addCodeBtn).setOnClickListener(l -> {
            Intent qrScanner = new Intent(requireContext(), QRScannerActivity.class);
            startActivityForResult(qrScanner, QR_REQUEST_CODE);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.authenticators_menu, menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode != QR_REQUEST_CODE || resultCode != Activity.RESULT_OK || data == null) {
            Toast.makeText(requireContext(), "Fehler: "+resultCode, Toast.LENGTH_LONG).show();
            return;
        }

        String uri = data.getStringExtra("RESULT");
        Log.d(TAG, "OTP URI: "+uri);
    }
}
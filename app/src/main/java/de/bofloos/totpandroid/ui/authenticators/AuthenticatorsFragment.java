package de.bofloos.totpandroid.ui.authenticators;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.bofloos.totpandroid.R;
import de.bofloos.totpandroid.qrscanner.QRScannerActivity;
import de.bofloos.totpandroid.qrscanner.ScanResult;
import de.bofloos.totpandroid.ui.authenticators.detail.AuthenticatorsDetailActivity;
import de.bofloos.totpandroid.util.EventQueue;
import de.bofloos.totpandroid.viewmodel.AuthenticatorsViewModel;
import de.bofloos.totpandroid.viewmodel.AuthenticatorsViewModelFactory;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AuthenticatorsFragment extends Fragment {

    static final int QR_REQUEST_CODE = 1337;
    static final String TAG = AuthenticatorsFragment.class.getName();

    AuthenticatorsViewModel viewModel;

    RecyclerView authenticatorList;
    AuthenticatorsListAdapter listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        viewModel = new ViewModelProvider(requireActivity(), new AuthenticatorsViewModelFactory(requireContext()))
                .get(AuthenticatorsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_authenticators, container, false);
        setupViewElements(v);
        return v;
    }

    private void setupViewElements(View v) {
        // FAB auf QR-Scanner setzten
        v.findViewById(R.id.addCodeBtn).setOnClickListener(l -> {
            Intent qrScanner = new Intent(requireContext(), QRScannerActivity.class);
            startActivityForResult(qrScanner, QR_REQUEST_CODE);
        });

        // Authenticator List vorbereiten
        authenticatorList = v.findViewById(R.id.authList);
        authenticatorList.setHasFixedSize(true);
        authenticatorList.setLayoutManager(new LinearLayoutManager(requireContext()));

        listAdapter = new AuthenticatorsListAdapter(viewModel.getAccountRepo().getAllAccounts(), getViewLifecycleOwner());
        listAdapter.setOnClickListener(acc -> {
            Intent detailIntent = AuthenticatorsDetailActivity.newIntent(acc, requireContext());
            startActivity(detailIntent);
        });
        authenticatorList.setAdapter(listAdapter);
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
            showMsg("Fehler: "+resultCode);
            return;
        }

        ScanResult res = data.getParcelableExtra("RESULT");
        Log.d(TAG, "ScanResult "+res);

        EventQueue.getInstance().post(() -> {
            try {
                boolean ok;
                if(res.isUriResult()) {
                    ok = viewModel.createAccount(new URI(res.getUri()));
                } else {
                    ok = viewModel.createAccount(res.getAccount(), res.getIssuer(), res.getSecret());
                }
                if(!ok)
                    showMsg("Das Konto existiert bereits oder der Code ist ungültig");
                else
                    showMsg("Erfolg");
            } catch (URISyntaxException e) {
                showMsg("Ungültiger QR-Code Inhalt");
            }
        });
    }

    private void showMsg(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
    }
}
package de.bofloos.totpandroid.ui.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import de.bofloos.totpandroid.R;
import de.bofloos.totpandroid.util.Util;
import de.bofloos.totpandroid.viewmodel.AuthenticatorsViewModel;
import de.bofloos.totpandroid.viewmodel.AuthenticatorsViewModelFactory;

public class SettingsFragment extends Fragment {

    private static final int FILE_CREATE_REQUEST = 42;
    private static final int FILE_CHOOSE_REQUEST = 69;

    AuthenticatorsViewModel viewModel;

    public SettingsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity(), new AuthenticatorsViewModelFactory(requireContext()))
                .get(AuthenticatorsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        v.findViewById(R.id.importCodesBtn).setOnClickListener(l -> {
            new AlertDialog.Builder(requireContext())
                    .setMessage("Bereits bestehende Konten werden ersetzt.\nFortfahren?")
                    .setPositiveButton("Ok", (dialogInterface, i) -> {
                        Intent fileChooser = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        fileChooser.addCategory(Intent.CATEGORY_OPENABLE);
                        fileChooser.setType("*/*");
                        startActivityForResult(fileChooser, FILE_CHOOSE_REQUEST);
                    })
                    .setNegativeButton("Abbrechen", (dialogInterface, i) -> dialogInterface.cancel())
                    .show();
        });

        v.findViewById(R.id.exportCodesBtn).setOnClickListener(l -> {
            Intent fileCreator = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            fileCreator.addCategory(Intent.CATEGORY_OPENABLE);
            fileCreator.setType("application/octet-stream");
            fileCreator.putExtra(Intent.EXTRA_TITLE, "2FAAccounts.bin");
            startActivityForResult(fileCreator, FILE_CREATE_REQUEST);
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && data != null) {
            ProgressDialog dialog = ProgressDialog.show(requireContext(), "",
                    "Am Arbeiten. Gar kein Bock.", true);

            switch (requestCode) {
                case FILE_CREATE_REQUEST:
                    viewModel.exportAccounts(data.getData(), requireContext().getContentResolver(), ok -> {
                        dialog.dismiss();
                        if (!ok)
                            Util.showMsg("Fehler beim Exportieren der Konten", requireContext());
                    });
                    break;
                case FILE_CHOOSE_REQUEST:
                    viewModel.importAccounts(data.getData(), requireContext().getContentResolver(), ok -> {
                        dialog.dismiss();
                        if (!ok)
                            Util.showMsg("Fehler beim Importieren der Konten", requireContext());
                    });
            }
        }
    }
}
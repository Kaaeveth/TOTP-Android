package de.bofloos.totpandroid.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;
import de.bofloos.totpandroid.R;
import de.bofloos.totpandroid.viewmodel.AuthenticatorsViewModel;
import de.bofloos.totpandroid.viewmodel.AuthenticatorsViewModelFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class SettingsFragment extends Fragment {

    private static final int FILE_CREATE_REQUEST = 42;

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

        if(requestCode == FILE_CREATE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            viewModel.exportAccounts(data.getData(), requireContext().getContentResolver(), ok -> {
                if(!ok)
                    Toast.makeText(requireContext(), "Fehler beim Exportieren der Konten", Toast.LENGTH_LONG).show();
            });
        }
    }
}
package de.bofloos.totpandroid.ui.authenticators.detail;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.bofloos.totpandroid.R;
import de.bofloos.totpandroid.model.Account;
import de.bofloos.totpandroid.model.AccountRepository;
import de.bofloos.totpandroid.util.EventQueue;

/**
 * Fragment zum Bearbeiten eines Kontos
 * Use the {@link AuthenticatorsEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AuthenticatorsEditFragment extends Fragment {

    private static final String ARG_ACC = "acc";
    private static final String ARG_ACC_REPO = "accR";

    private Account account;
    private AccountRepository accountRepo;

    private EditText issuerEdit;

    public AuthenticatorsEditFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AuthenticatorsEditFragment.
     */
    public static AuthenticatorsEditFragment newInstance(Account acc, AccountRepository accountRepo) {
        AuthenticatorsEditFragment fragment = new AuthenticatorsEditFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ACC, acc);
        args.putSerializable(ARG_ACC_REPO, accountRepo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            account = (Account) getArguments().getSerializable(ARG_ACC);
            accountRepo = (AccountRepository) getArguments().getSerializable(ARG_ACC_REPO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_authenticators_edit, container, false);
        issuerEdit = v.findViewById(R.id.issuerEdit);
        v.findViewById(R.id.saveEditBtn).setOnClickListener(this::onSave);

        issuerEdit.setText(account.issuer);

        return v;
    }

    private void onSave(View v) {
        Toast.makeText(requireContext(), "Speichern...", Toast.LENGTH_LONG).show();

        // Datenbankzugriff auf Event-Thread auslagern
        EventQueue.getInstance().post(() -> {
            account.issuer = issuerEdit.getText().toString();
            accountRepo.updateAccount(account);
            requireActivity().finish();
        });
    }
}
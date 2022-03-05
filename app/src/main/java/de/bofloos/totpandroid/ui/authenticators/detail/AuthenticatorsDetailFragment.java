package de.bofloos.totpandroid.ui.authenticators.detail;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import de.bofloos.totpandroid.R;
import de.bofloos.totpandroid.model.Account;
import de.bofloos.totpandroid.model.AccountDao;
import de.bofloos.totpandroid.util.EventQueue;
import org.jetbrains.annotations.NotNull;

/**
 * Fragment für die Details eines Kontos.
 * Use the {@link AuthenticatorsDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AuthenticatorsDetailFragment extends Fragment {

    private static final String ARG_ACC = "param1";
    private static final String ARG_ACC_REPO = "param2";

    private Account acc;
    private AccountDao accountRepo;

    public AuthenticatorsDetailFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param acc Konto
     * @return A new instance of fragment AuthenticatorsDetailFragment.
     */
    public static AuthenticatorsDetailFragment newInstance(Account acc, AccountDao accountRepo) {
        AuthenticatorsDetailFragment fragment = new AuthenticatorsDetailFragment();
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
            acc = (Account) getArguments().getSerializable(ARG_ACC);
            accountRepo = (AccountDao) getArguments().getSerializable(ARG_ACC_REPO);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_authenticators_detail, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.authenticators_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {

        int itemId = item.getItemId();
        if(itemId == R.id.delete_button) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Konto löschen")
                    .setMessage("Wollen Sie das Konto wirklich löschen?")
                    .setPositiveButton("Bestätigen", (dialogInterface, i) -> {
                        EventQueue.getInstance().post(() -> accountRepo.deleteAccount(acc));
                        requireActivity().finish();
                    })
                    .setNegativeButton("Abbrechen", (dialogInterface, i) -> dialogInterface.cancel())
                    .show();
        } else if(itemId == R.id.edit_button) {
            //todo: edit fragment
        } else {
            Log.d(getClass().getName(), "Unbekannte ItemId erhalten");
        }

        return super.onOptionsItemSelected(item);
    }
}
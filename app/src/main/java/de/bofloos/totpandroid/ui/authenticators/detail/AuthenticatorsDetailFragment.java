package de.bofloos.totpandroid.ui.authenticators.detail;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

    private TextView issuerTv;
    private TextView accountTv;
    private TextView codeTv;
    private ProgressBar validityBar;

    public AuthenticatorsDetailFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
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
        View v = inflater.inflate(R.layout.fragment_authenticators_detail, container, false);
        issuerTv = v.findViewById(R.id.issuerDetailTv);
        accountTv = v.findViewById(R.id.accountDetailTv);
        codeTv = v.findViewById(R.id.codeDetailTv);
        validityBar = v.findViewById(R.id.validityBar);
        setupDetail();

        return v;
    }

    private void setupDetail() {
        issuerTv.setText(acc.issuer);
        accountTv.setText(acc.label);

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
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_detail_container, AuthenticatorsEditFragment.newInstance(acc, accountRepo))
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        } else {
            Log.d(getClass().getName(), "Unbekannte ItemId erhalten");
        }

        return super.onOptionsItemSelected(item);
    }
}
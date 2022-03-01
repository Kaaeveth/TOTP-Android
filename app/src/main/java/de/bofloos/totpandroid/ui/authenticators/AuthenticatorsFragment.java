package de.bofloos.totpandroid.ui.authenticators;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import de.bofloos.totpandroid.R;
import org.jetbrains.annotations.NotNull;

public class AuthenticatorsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_authenticators, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.authenticators_menu, menu);
    }
}
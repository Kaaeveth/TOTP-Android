package de.bofloos.totpandroid.ui.authenticators;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import de.bofloos.totpandroid.R;
import de.bofloos.totpandroid.model.Account;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AuthenticatorsListAdapter extends RecyclerView.Adapter<AuthenticatorsListAdapter.AuthenticatorsViewHolder> {

    // Die momentan geladenen Konten
    List<Account> currentAccounts;

    public AuthenticatorsListAdapter(LiveData<List<Account>> accountDao, LifecycleOwner lifecycleOwner) {
        accountDao.observe(lifecycleOwner, a -> {
            List<Account> old = currentAccounts;
            currentAccounts = a;

            if(old == null)
                notifyDataSetChanged();
            else if(old.size() < a.size()) {
                notifyItemRangeInserted(old.size(), a.size()-old.size());
            } else {
                // todo: remove case
            }
            //notifyDataSetChanged();
        });
    }

    @NonNull
    @NotNull
    @Override
    public AuthenticatorsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View item = inflater.inflate(R.layout.authenticators_list_item, parent, false);
        return new AuthenticatorsViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AuthenticatorsViewHolder holder, int position) {
        Account acc = currentAccounts.get(position);
        holder.issuerTv.setText(acc.issuer);
        holder.accountTv.setText(acc.label);
        // todo: edit activity and code
        /*holder.layout.setOnClickListener(l -> {

        });*/
    }

    @Override
    public int getItemCount() {
        // Size von 0, wenn keine Konten geladen sind.
        // Ist wichtig damit die Liste erst generiert wird, wenn die Konten aus der Datenbank geladen worden sind.
        return currentAccounts == null ? 0 : currentAccounts.size();
    }

    public void showCodes(boolean show) {

    }

    public static class AuthenticatorsViewHolder extends RecyclerView.ViewHolder {

        public TextView issuerTv;
        public TextView accountTv;
        public TextView codeTv;
        public ProgressBar validityBar;

        public ConstraintLayout layout;

        public AuthenticatorsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            issuerTv = itemView.findViewById(R.id.issuerTv);
            accountTv = itemView.findViewById(R.id.accountTv);
            codeTv = itemView.findViewById(R.id.codeTv);
            validityBar = itemView.findViewById(R.id.validityBar);
            layout = itemView.findViewById(R.id.item_layout);
        }
    }
}
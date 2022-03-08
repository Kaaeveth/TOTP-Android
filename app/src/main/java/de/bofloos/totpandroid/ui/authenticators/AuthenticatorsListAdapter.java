package de.bofloos.totpandroid.ui.authenticators;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;
import de.bofloos.totpandroid.R;
import de.bofloos.totpandroid.model.Account;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AuthenticatorsListAdapter extends RecyclerView.Adapter<AuthenticatorsListAdapter.AuthenticatorsViewHolder> {

    // Die momentan geladenen Konten
    private List<Account> currentAccounts;
    private AccountClickListener onItemListener;
    private AccountCodeSetupListener onCodeSetupListener;
    private boolean showCodes = false;

    public AuthenticatorsListAdapter(LiveData<List<Account>> accountDao, LifecycleOwner lifecycleOwner) {
        // Observer für Account Änderungen
        accountDao.observe(lifecycleOwner, a -> {
            List<Account> old = currentAccounts;
            currentAccounts = a;

            /* Wenn das erste Mal die Liste updated wird oder Elemente gelöscht wurden
               können wir nicht effektiv die Vorher/Nachher-Änderung festellen -> Liste neu aufbauen*/
            if(old == null || old.size() > a.size())
                notifyDataSetChanged();
            else if(old.size() == a.size()) { // Bestehende Konten haben sich geändert
                notifyItemRangeChanged(0, getItemCount());
            } else /*if(old.size() < a.size())*/ {
                // Neue Elemente wurde eingefügt. Elemente werden am Ende der Liste angenommen für effektiveres updaten
                notifyItemRangeInserted(old.size(), a.size()-old.size());
            }
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
        holder.codeGroup.setVisibility(showCodes ? View.VISIBLE : View.GONE);

        if(onItemListener != null)
            holder.layout.setOnClickListener(l -> onItemListener.onAccountClick(acc));
        if(onCodeSetupListener != null)
            onCodeSetupListener.onCodeSetup(acc, holder.validityBar, holder.codeTv);
    }

    public void setOnClickListener(AccountClickListener l, boolean updateNow) {
        this.onItemListener = l;
        if(updateNow)
            notifyItemRangeChanged(0, getItemCount()); // Click Listener für jedes Element updaten
    }

    public void setOnCodeSetupListener(AccountCodeSetupListener l, boolean updateNow) {
        onCodeSetupListener = l;
        if(updateNow)
            notifyItemRangeChanged(0, getItemCount()); // Click Listener für jedes Element updaten
    }

    @Override
    public int getItemCount() {
        // Size von 0, wenn keine Konten geladen sind.
        // Ist wichtig damit die Liste erst generiert wird, wenn die Konten aus der Datenbank geladen worden sind.
        return currentAccounts == null ? 0 : currentAccounts.size();
    }

    public void toggleCodes() {
        showCodes = !showCodes;
        notifyItemRangeChanged(0, getItemCount());
    }

    public static class AuthenticatorsViewHolder extends RecyclerView.ViewHolder {

        public TextView issuerTv;
        public TextView accountTv;
        public TextView codeTv;
        public ProgressBar validityBar;
        public Group codeGroup;

        public ConstraintLayout layout;

        public AuthenticatorsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            issuerTv = itemView.findViewById(R.id.issuerTv);
            accountTv = itemView.findViewById(R.id.accountTv);
            codeTv = itemView.findViewById(R.id.codeTv);
            validityBar = itemView.findViewById(R.id.validityBar);
            codeGroup = itemView.findViewById(R.id.codeGroup);
            layout = itemView.findViewById(R.id.item_layout);

            codeGroup.setVisibility(View.GONE);
        }
    }
}

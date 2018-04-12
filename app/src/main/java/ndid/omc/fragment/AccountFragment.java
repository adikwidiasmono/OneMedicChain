package ndid.omc.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Date;

import ndid.omc.firebase.FirebaseDB;
import ndid.omc.main.R;
import ndid.omc.recview.model.Allergy;

/**
 * Created by adikwidiasmono on 15/11/17.
 */

public class AccountFragment extends Fragment {

    private static final String EXTRA_TEXT = "text";

    private FirebaseRecyclerAdapter adapter;

    private FloatingActionButton fabAdd;
    private RecyclerView recyclerView;

    public static AccountFragment createFor(String text) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.startListening();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        fabAdd = v.findViewById(R.id.fab_add_allergy);
        recyclerView = v.findViewById(R.id.rv_allergy);

        return v;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseDB.REF_ALLERGYS)
                .orderByChild("createdDate").startAt(-1 * new Date().getTime());

        FirebaseRecyclerOptions<Allergy> options = new FirebaseRecyclerOptions.Builder<Allergy>()
                .setQuery(query, Allergy.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Allergy, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_allergy, parent, false);

                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(ViewHolder viewHolder, int position, Allergy allergy) {
                viewHolder.tvCode.setText(allergy.getCode());
                viewHolder.tvName.setText(allergy.getName());
                viewHolder.tvDesc.setText(allergy.getDescription());
                if (allergy.getCreatedBy() == null || allergy.getCreatedBy().trim().length() < 1)
                    viewHolder.tvCreator.setVisibility(View.GONE);
                else
                    viewHolder.tvCreator.setText(allergy.getCreatedBy());
            }
        };

        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        // Reverse adding new data
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog d = new Dialog(getActivity());
                d.setTitle("Add New Allergy");
                d.setContentView(R.layout.input_allergy);
                final EditText etCode = d.findViewById(R.id.et_code);
                final EditText etName = d.findViewById(R.id.et_name);

                final EditText etDesc = d.findViewById(R.id.et_dec);
                etDesc.setSingleLine(false);
                etDesc.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                final EditText etCreator = d.findViewById(R.id.et_creator);
                etCreator.setVisibility(View.GONE);
                final Button btSave = d.findViewById(R.id.bt_save_allergy);

                //SAVE
                btSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //GET DATA
                        String code = etCode.getText().toString();
                        String name = etName.getText().toString();
                        String desc = etDesc.getText().toString();
                        String creator = etCreator.getText().toString();

                        //SET DATA
                        Allergy al = new Allergy();
                        if (code != null && code.length() > 0)
                            al.setCode(code);
                        al.setName(name);
                        al.setDescription(desc);
                        if (creator != null && creator.length() > 0)
                            al.setCreatedBy(creator);

                        //VALIDATE
                        if (name != null && name.length() > 0 && desc != null && desc.length() > 0) {
                            al.setCreatedDate(new Date().getTime());
                            FirebaseDB.init().addAllergy(al);
                            d.dismiss();
                        }
                    }
                });

                d.show();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCode, tvName, tvDesc, tvCreator;

        public ViewHolder(View itemView) {
            super(itemView);

            tvCode = itemView.findViewById(R.id.tv_allergy_code);
            tvName = itemView.findViewById(R.id.tv_allergy_name);
            tvDesc = itemView.findViewById(R.id.tv_allergy_description);
            tvCreator = itemView.findViewById(R.id.tv_creator);
        }
    }
}
package ndid.omc.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Date;

import ndid.omc.firebase.FirebaseAdapterHistories;
import ndid.omc.firebase.FirebaseDB;
import ndid.omc.main.R;
import ndid.omc.main.medrec.DetailMedRecActivity;
import ndid.omc.recview.model.History;

/**
 * Created by adikwidiasmono on 15/11/17.
 */

public class MedicalHistoriesFragment extends Fragment
        implements FirebaseAdapterHistories.FirebaseAdapterHistoriesListener {

    private static final String EXTRA_TEXT = "text";

    private FirebaseAdapterHistories adapter;

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;

    public static MedicalHistoriesFragment createFor(String text) {
        MedicalHistoriesFragment fragment = new MedicalHistoriesFragment();
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
        View v = inflater.inflate(R.layout.fragment_medical_histories, container, false);

        fabAdd = v.findViewById(R.id.fab_add_histories);
        recyclerView = v.findViewById(R.id.rv_histories);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseDB.REF_HISTORIES)
                .orderByChild("createdDate").startAt(-1 * new Date().getTime());

        FirebaseRecyclerOptions<History> options = new FirebaseRecyclerOptions.Builder<History>()
                .setQuery(query, History.class)
                .build();

        adapter = new FirebaseAdapterHistories(options, getActivity(), this);

        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(adapter);
        // Set layout manager to position the items
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);

        fabAdd.setVisibility(View.GONE);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog d = new Dialog(getActivity());
                d.setTitle("Add New History");
                d.setContentView(R.layout.input_history);
                final EditText etHospital = d.findViewById(R.id.et_hospital);
                final EditText etHospitalUrl = d.findViewById(R.id.et_hospital_url);
                final EditText etDoctor = d.findViewById(R.id.et_doctor);
                final EditText etContent = d.findViewById(R.id.et_content);
                final Button btSave = d.findViewById(R.id.bt_save_history);

                //SAVE
                btSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //GET DATA
                        String hospital = etHospital.getText().toString();
                        String hospitalUrl = etHospitalUrl.getText().toString();
                        String doctor = etDoctor.getText().toString();
                        String content = etContent.getText().toString();

                        //SET DATA
                        History his = new History();
                        his.setHospital(hospital);
                        his.setHospitalImgUrl(hospitalUrl);
                        his.setDoctor(doctor);
                        his.setSummary(content);
                        his.setCreatedDate(new Date().getTime());

                        //VALIDATE
                        FirebaseDB.init().addHistories(his);
                        d.dismiss();
                    }
                });

                d.show();
            }
        });
    }

    @Override
    public void onIconClicked(int position) {
//        if (actionMode == null) {
//            actionMode = startSupportActionMode(actionModeCallback);
//        }

        toggleSelection(position);
    }

    @Override
    public void onRowLongClicked(int position) {
        toggleSelection(position);
    }

    @Override
    public void onRowClicked(int position) {
        startActivity(new Intent(getActivity(), DetailMedRecActivity.class));
    }

    @Override
    public void notifyTotalSelected(int totalSelected) {
        if (totalSelected > 0)
            fabAdd.setVisibility(View.VISIBLE);
        else
            fabAdd.setVisibility(View.GONE);
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();
//
//        if (count == 0) {
//            actionMode.finish();
//        } else {
//            actionMode.setTitle(String.valueOf(count));
//            actionMode.invalidate();
//        }
    }
}
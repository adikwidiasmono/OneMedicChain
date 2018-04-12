package ndid.omc.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import ndid.omc.firebase.FirebaseDB;
import ndid.omc.main.R;
import ndid.omc.recview.model.TimelineContent;

/**
 * Created by adikwidiasmono on 15/11/17.
 */

public class TimelineFragment extends Fragment {

    private static final String EXTRA_TEXT = "text";

    private FirebaseRecyclerAdapter adapter;

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;

    public static TimelineFragment createFor(String text) {
        TimelineFragment fragment = new TimelineFragment();
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
        View v = inflater.inflate(R.layout.fragment_timeline, container, false);

        fabAdd = v.findViewById(R.id.fab_add_timeline);
        recyclerView = v.findViewById(R.id.rv_med_rec);

        return v;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child(FirebaseDB.REF_TIMELINE_MED_RECS)
                .orderByChild("createdDate").startAt(-1 * new Date().getTime());

        FirebaseRecyclerOptions<TimelineContent> options = new FirebaseRecyclerOptions.Builder<TimelineContent>()
                .setQuery(query, TimelineContent.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<TimelineContent, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_timeline, parent, false);

                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(final ViewHolder viewHolder, int position, TimelineContent timelineContent) {
                viewHolder.tvCreator.setText(timelineContent.getCreator());
                viewHolder.tvTitle.setText(timelineContent.getTitle());
                viewHolder.tvContent.setText(timelineContent.getContent());

                Timestamp stamp = new Timestamp(timelineContent.getCreatedDate());

                viewHolder.tvCreatedTime.setText(new SimpleDateFormat("h:mm a").format(new Date(stamp.getTime())));
                viewHolder.tvCreatedDate.setText(new SimpleDateFormat("dd MMM yyyy").format(new Date(stamp.getTime())));
                viewHolder.tvHospital.setText(timelineContent.getHospital());

                Picasso.with(getActivity())
                        .load(timelineContent.getCreatorImgUrl())
                        .placeholder(R.drawable.ic_users_32dp)
                        .error(R.drawable.ic_users_32dp)
                        .into(viewHolder.ivCreatorImg);

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (viewHolder.tvTitle.getText().toString().equalsIgnoreCase("TREATMENT PAID")) {
                            showDialog();
                        }
                    }
                });
            }
        };

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
                d.setTitle("Add New Timeline");
                d.setContentView(R.layout.input_timeline);
                final EditText etCreator = d.findViewById(R.id.et_creator);
                final EditText etCreatorUrl = d.findViewById(R.id.et_creator_url);
                final EditText etTitle = d.findViewById(R.id.et_title);
                final EditText etContent = d.findViewById(R.id.et_content);
                final EditText etHospital = d.findViewById(R.id.et_hospital);
                final Button btSave = d.findViewById(R.id.bt_save_timeline);

                //SAVE
                btSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //GET DATA
                        String creator = etCreator.getText().toString();
                        String creatorUrl = etCreatorUrl.getText().toString();
                        String title = etTitle.getText().toString();
                        String content = etContent.getText().toString();
                        String hospital = etHospital.getText().toString();

                        //SET DATA
                        TimelineContent tim = new TimelineContent();
                        tim.setCreator(creator);
                        tim.setCreatorImgUrl(creatorUrl);
                        tim.setTitle(title);
                        tim.setContent(content);
                        tim.setHospital(hospital);
                        tim.setCreatedDate(new Date().getTime());

                        //VALIDATE
                        FirebaseDB.init().addTimeline(tim);
                        d.dismiss();
                    }
                });

                d.show();
            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCreator, tvTitle, tvContent, tvCreatedTime, tvCreatedDate, tvHospital;
        private ImageView ivCreatorImg;

        public ViewHolder(View itemView) {
            super(itemView);

            tvCreator = itemView.findViewById(R.id.tv_creator);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvCreatedTime = itemView.findViewById(R.id.tv_created_time);
            tvCreatedDate = itemView.findViewById(R.id.tv_created_date);
            tvHospital = itemView.findViewById(R.id.tv_hospital);

            ivCreatorImg = itemView.findViewById(R.id.iv_creator);
        }
    }

    private void showDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite OK", "Very Good", "Excellent !!!"))
                .setDefaultRating(2)
                .setTitle("Hospital & Doctor Rating")
                .setDescription("Please select stars and give your feedback for hospital and doctor")
                .setTitleTextColor(R.color.c_blue)
                .setDescriptionTextColor(R.color.textColorSecondary)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.textColorSecondary)
                .setCommentTextColor(R.color.c_black)
                .setCommentBackgroundColor(R.color.c_grey)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .create((FragmentActivity) getActivity())
                .show();
    }

}
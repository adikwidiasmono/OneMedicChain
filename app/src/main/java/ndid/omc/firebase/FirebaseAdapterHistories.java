package ndid.omc.firebase;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import ndid.omc.main.R;
import ndid.omc.recview.model.History;

/**
 * Created by adikwidiasmono on 21/11/17.
 */

public class FirebaseAdapterHistories extends FirebaseRecyclerAdapter<History, FirebaseAdapterHistories.ViewHolder> {
    private Activity activity;
    private FirebaseAdapterHistoriesListener listener;
    private SparseBooleanArray selectedItems;

    // array used to perform multiple animation at once
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;

    // index is used to animate only the selected row
    // dirty fix, find a better solution
    private static int currentSelectedIndex = -1;

    public FirebaseAdapterHistories(FirebaseRecyclerOptions options, Activity activity,
                                    FirebaseAdapterHistoriesListener listener) {
        super(options);
        this.activity = activity;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView tvCreator, tvDoctor, tvSummary, tvCreatedDate, tvHospital;
        private ImageView ivCreatorImg, ivSelected;

        public ViewHolder(View itemView) {
            super(itemView);

            tvCreator = itemView.findViewById(R.id.tv_creator);
            tvDoctor = itemView.findViewById(R.id.tv_title);
            tvSummary = itemView.findViewById(R.id.tv_summary);
            tvCreatedDate = itemView.findViewById(R.id.tv_created_date);
            tvHospital = itemView.findViewById(R.id.tv_hospital);

            ivCreatorImg = itemView.findViewById(R.id.iv_creator);
            ivSelected = itemView.findViewById(R.id.iv_selected);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);

        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(ViewHolder viewHolder, int position, History history) {
        viewHolder.tvDoctor.setText(history.getDoctor());
        viewHolder.tvSummary.setText(history.getSummary());

        Timestamp stamp = new Timestamp(history.getCreatedDate());

        viewHolder.tvCreatedDate.setText(new SimpleDateFormat("dd MMM yyyy").format(new Date(stamp.getTime())));
        viewHolder.tvHospital.setText(history.getHospital());

        Picasso.with(activity)
                .load(history.getHospitalImgUrl())
                .placeholder(R.drawable.ic_rs_64dp)
                .error(R.drawable.ic_rs_64dp)
                .into(viewHolder.ivCreatorImg);

        // change the row state to activated
        viewHolder.itemView.setActivated(selectedItems.get(position, false));

        // handle icon animation
        applyIconAnimation(viewHolder, position);

        // apply click events
        applyClickEvents(viewHolder, position);
    }

    private void applyClickEvents(ViewHolder holder, final int position) {
        holder.ivCreatorImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconClicked(position);
                listener.notifyTotalSelected(selectedItems.size());
            }
        });

        holder.ivSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconClicked(position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRowClicked(position);
            }
        });
    }

    private void applyIconAnimation(ViewHolder holder, int position) {
        listener.notifyTotalSelected(selectedItems.size());
        if (selectedItems.get(position, false)) {
            holder.ivCreatorImg.setVisibility(View.GONE);
//            resetIconYAxis(holder.ivSelected);
            holder.ivSelected.setVisibility(View.VISIBLE);
//            holder.ivSelected.setAlpha(1);
//            if (currentSelectedIndex == position) {
//                FlipAnimator.flipView(activity, holder.ivSelected, holder.ivCreatorImg, true);
//                resetCurrentIndex();
//            }
        } else {
            holder.ivSelected.setVisibility(View.GONE);
//            resetIconYAxis(holder.ivCreatorImg);
            holder.ivCreatorImg.setVisibility(View.VISIBLE);
//            holder.ivCreatorImg.setAlpha(1);
//            if ((reverseAllAnimations && animationItemsIndex.get(position, false)) || currentSelectedIndex == position) {
//                FlipAnimator.flipView(activity, holder.ivSelected, holder.ivCreatorImg, false);
//                resetCurrentIndex();
//            }
        }
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    private void resetIconYAxis(View view) {
        if (view.getRotationY() != 0) {
            view.setRotationY(0);
        }
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public void toggleSelection(int pos) {
        currentSelectedIndex = pos;
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
            animationItemsIndex.delete(pos);
        } else {
            selectedItems.put(pos, true);
            animationItemsIndex.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public interface FirebaseAdapterHistoriesListener {
        void onIconClicked(int position);

        void onRowLongClicked(int position);

        void onRowClicked(int position);

        void notifyTotalSelected(int totalSelected);
    }

}

package ndid.omc.recview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ndid.omc.main.R;
import ndid.omc.recview.model.Allergy;

/**
 * Created by adikwidiasmono on 17/11/17.
 */

public class AllergysAdapter extends RecyclerView.Adapter<AllergysAdapter.ViewHolder> {

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

    private List<Allergy> listAllergy;
    // Store the context for easy access
    private Context mContext;

    // Pass in the contact array into the constructor
    public AllergysAdapter(Context context, List<Allergy> listAllergy) {
        this.listAllergy = listAllergy;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public AllergysAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_allergy, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(AllergysAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Allergy allergy = listAllergy.get(position);

        // Set item views based on your views and data model
        viewHolder.tvCode.setText(allergy.getCode());
        viewHolder.tvName.setText(allergy.getName());
        viewHolder.tvDesc.setText(allergy.getDescription());
        viewHolder.tvCreator.setText(allergy.getCreatedBy());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return listAllergy.size();
    }

}

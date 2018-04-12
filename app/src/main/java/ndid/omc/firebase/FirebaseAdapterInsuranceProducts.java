package ndid.omc.firebase;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import ndid.omc.main.R;
import ndid.omc.recview.model.InsuranceProduct;

/**
 * Created by adikwidiasmono on 21/11/17.
 */

public class FirebaseAdapterInsuranceProducts extends FirebaseRecyclerAdapter<InsuranceProduct, FirebaseAdapterInsuranceProducts.ViewHolder> {
    private Activity activity;
    private FirebaseAdapterInsuranceProductsListener listener;
    private boolean isMyInsurance = false;

    public FirebaseAdapterInsuranceProducts(FirebaseRecyclerOptions options, Activity activity,
                                            FirebaseAdapterInsuranceProductsListener listener,
                                            boolean isMyInsurance) {
        super(options);
        this.activity = activity;
        this.listener = listener;
        this.isMyInsurance = isMyInsurance;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvInsurance, tvInsuranceProduct, tvShortDesc, tvCreatedDate;
        public TextView tvLinkToDetail, tvIamInterested;
        private ImageView ivInsurance;

        public ViewHolder(View itemView) {
            super(itemView);

            tvInsurance = itemView.findViewById(R.id.tv_insurance);
            tvInsuranceProduct = itemView.findViewById(R.id.tv_insurance_product);
            tvShortDesc = itemView.findViewById(R.id.tv_short_desc);
            tvLinkToDetail = itemView.findViewById(R.id.tv_link_to_detail);
            tvIamInterested = itemView.findViewById(R.id.tv_i_am_interested);
            tvCreatedDate = itemView.findViewById(R.id.tv_created_date);

            ivInsurance = itemView.findViewById(R.id.iv_insurance);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_insurance_product, parent, false);

        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(ViewHolder viewHolder, int position, InsuranceProduct product) {
        viewHolder.tvInsurance.setText(product.getInsuranceName());
        viewHolder.tvInsuranceProduct.setText(product.getProductName());
        viewHolder.tvShortDesc.setText(product.getShortDesc());

        Picasso.with(activity)
                .load(product.getInsuranceIconUrl())
                .placeholder(R.drawable.ic_rs_64dp)
                .error(R.drawable.ic_rs_64dp)
                .into(viewHolder.ivInsurance);

        Timestamp stamp = new Timestamp(product.getCreatedDate());
        viewHolder.tvCreatedDate.setText("Valid until " + new SimpleDateFormat("dd MMM yy").format(new Date(stamp.getTime())));

        if (product.getLinkToDetailDesc() == null ||
                product.getLinkToDetailDesc().trim().length() < 1 ||
                !URLUtil.isValidUrl(product.getLinkToDetailDesc())) {
            viewHolder.tvLinkToDetail.setVisibility(View.GONE);
        }

        // apply click events
        applyClickEvents(viewHolder, position, product);

        if (isMyInsurance) {
            viewHolder.tvCreatedDate.setVisibility(View.GONE);
            viewHolder.tvIamInterested.setVisibility(View.GONE);
            viewHolder.tvLinkToDetail.setVisibility(View.GONE);
        }
    }

    private void applyClickEvents(ViewHolder holder, final int position, final InsuranceProduct product) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRowClicked(position, product);
            }
        });

        holder.tvIamInterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onInterestedClicked(position, product);
            }
        });

        holder.tvLinkToDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDetailDescClicked(position, product);
            }
        });
    }

    public interface FirebaseAdapterInsuranceProductsListener {
        void onRowClicked(int position, InsuranceProduct product);

        void onInterestedClicked(int position, InsuranceProduct product);

        void onDetailDescClicked(int position, InsuranceProduct product);
    }

}

package ndid.omc.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Date;

import ndid.omc.firebase.FirebaseAdapterInsuranceProducts;
import ndid.omc.firebase.FirebaseDB;
import ndid.omc.main.R;
import ndid.omc.recview.model.InsuranceProduct;

/**
 * Created by adikwidiasmono on 15/11/17.
 */

public class InsuranceProductsFragment extends Fragment
        implements FirebaseAdapterInsuranceProducts.FirebaseAdapterInsuranceProductsListener {

    private static final String EXTRA_TEXT = "text";

    private FirebaseAdapterInsuranceProducts adapter;

    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;

    private static String title = "";

    public static InsuranceProductsFragment createFor(String text) {
        InsuranceProductsFragment fragment = new InsuranceProductsFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        title = text;
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
        View v = inflater.inflate(R.layout.fragment_insurance_products, container, false);

        fabAdd = v.findViewById(R.id.fab_add_insurance_product);
        recyclerView = v.findViewById(R.id.rv_insurance_products);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Query query;
        if (title.equalsIgnoreCase("My Insurrances")) {
            query = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(FirebaseDB.REF_INSURANCE_PRODUCT)
                    .limitToFirst(2);
        } else {
            query = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(FirebaseDB.REF_INSURANCE_PRODUCT)
                    .orderByChild("createdDate").startAt(-1 * new Date().getTime());
        }

        FirebaseRecyclerOptions<InsuranceProduct> options = new FirebaseRecyclerOptions.Builder<InsuranceProduct>()
                .setQuery(query, InsuranceProduct.class)
                .build();

        if (title.equalsIgnoreCase("My Insurrances")) {
            adapter = new FirebaseAdapterInsuranceProducts(options, getActivity(), this, true);
        } else {
            adapter = new FirebaseAdapterInsuranceProducts(options, getActivity(), this, false);
        }

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
                d.setTitle("Add New Insurance Product");
                d.setContentView(R.layout.input_insurance_product);
                final EditText etInsurance = d.findViewById(R.id.et_insurance);
                final EditText etInsuranceUrl = d.findViewById(R.id.et_insurance_url);
                final EditText etProductName = d.findViewById(R.id.et_product_name);
                final EditText etShortDesc = d.findViewById(R.id.et_short_desc);
                final Button btSave = d.findViewById(R.id.bt_save_product);

                //SAVE
                btSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //GET DATA
                        String insurance = etInsurance.getText().toString();
                        String insuranceUrl = etInsuranceUrl.getText().toString();
                        String productName = etProductName.getText().toString();
                        String shortDesc = etShortDesc.getText().toString();

                        //SET DATA
                        InsuranceProduct prod = new InsuranceProduct();
                        prod.setInsuranceName(insurance);
                        prod.setInsuranceIconUrl(insuranceUrl);
                        prod.setProductName(productName);
                        prod.setShortDesc(shortDesc);
                        prod.setLinkToDetailDesc("-");
                        prod.setCreatedDate(new Date().getTime());

                        //VALIDATE
                        FirebaseDB.init().addInsuranceProducts(prod);
                        d.dismiss();
                    }
                });

                d.show();
            }
        });
    }

    @Override
    public void onRowClicked(int position, InsuranceProduct product) {
        if (product.getLinkToDetailDesc() != null &&
                product.getLinkToDetailDesc().trim().length() > 0 &&
                URLUtil.isValidUrl(product.getLinkToDetailDesc())) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(product.getLinkToDetailDesc()));
            startActivity(browserIntent);
        }
    }

    @Override
    public void onInterestedClicked(int position, InsuranceProduct product) {
        Toast.makeText(getActivity(),
                "Thank you for choosing " + product.getProductName() + " . " + product.getInsuranceName() + " agent will contact you soon.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDetailDescClicked(int position, InsuranceProduct product) {
        if (product.getLinkToDetailDesc() != null &&
                product.getLinkToDetailDesc().trim().length() > 0 &&
                URLUtil.isValidUrl(product.getLinkToDetailDesc())) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(product.getLinkToDetailDesc()));
            startActivity(browserIntent);
        }
    }

}
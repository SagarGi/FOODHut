package com.example.pravinewa.foodhut;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.Context.VIBRATOR_SERVICE;


public class CompleteFragment extends Fragment {
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;

    FirebaseRecyclerOptions<IntrestedItem> recyclerOptions;
    FirebaseRecyclerAdapter<IntrestedItem, IntrestedItemViewHolder> recyclerAdapter;
    Vibrator vibrator;

    public CompleteFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_complete, container, false);

        firebaseAuth = FirebaseAuth.getInstance();


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Order").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        recyclerView = (RecyclerView) view.findViewById(R.id.CompleteRecycle);
        recyclerView.setHasFixedSize(true);
        recyclerOptions = new FirebaseRecyclerOptions.Builder<IntrestedItem>()
                .setQuery(databaseReference,IntrestedItem.class).build();

        recyclerAdapter = new FirebaseRecyclerAdapter<IntrestedItem, IntrestedItemViewHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(IntrestedItemViewHolder holder, int position, IntrestedItem model) {


              String food_name_intrested = model.getIntrestedFoodName();
              final String food_user_intrested = model.getIntrestedUserName();
              final String food_contact_intrested = model.getIntrestedUserContact();
              final String date = model.getIntrestedTime();
              final String foodquantity = String.valueOf(model.getQuantity());
              final String food_price_unit = String.valueOf(model.getItem_price_per_unit());
              final String food_total_price = String.valueOf(model.getTotal_price());
              final String food_shipping_address = model.getIntrestedAddress();
              holder.tvDear.setText("Dear sir/madam,\n");
              holder.intrestedDescription.setText("You have an Order from Mr." + food_user_intrested +  " for your Item " + food_name_intrested + ". Please call him to " +food_contact_intrested +
              " confirm order");
              holder.tvTime.setText("(" + date + ")");
              holder.quantity_Ordered.setText(foodquantity);
              holder.food_Price_odered_perunit.setText("Rs." + food_price_unit);
              holder.total_price_odered.setText("Rs." + food_total_price);
              holder.tvShippingAddress.setText("Shipping Address:\t\t\t\t" + food_shipping_address);
              holder.callToIntrestedUser.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {

                      Intent callIntent = new Intent(Intent.ACTION_DIAL);
                      callIntent.setData(Uri.parse("tel:" + food_contact_intrested));
                      startActivity(callIntent);

                  }
              });

            }

            @NonNull
            @Override
            public IntrestedItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.intresteditem, viewGroup, false);
                return new IntrestedItemViewHolder(view);
            }
        };

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(recyclerAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        recyclerAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerAdapter.startListening();
    }

}

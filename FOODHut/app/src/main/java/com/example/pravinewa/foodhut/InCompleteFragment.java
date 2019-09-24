package com.example.pravinewa.foodhut;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static android.content.Context.VIBRATOR_SERVICE;


public class InCompleteFragment extends Fragment {


    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    FirebaseRecyclerOptions<UserPost> recyclerOptions;
    FirebaseRecyclerAdapter<UserPost, UserPostViewHolder> recyclerAdapter;

    Vibrator vibrator;
    public InCompleteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_in_complete, container, false);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("UserPost").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        recyclerView = (RecyclerView) view.findViewById(R.id.inCompleteRecycle);
        recyclerView.setHasFixedSize(true);
        recyclerOptions = new FirebaseRecyclerOptions.Builder<UserPost>()
                .setQuery(databaseReference,UserPost.class).build();

        recyclerAdapter = new FirebaseRecyclerAdapter<UserPost, UserPostViewHolder>(recyclerOptions) {
            @Override
            protected void onBindViewHolder(UserPostViewHolder holder, int position, UserPost model) {

                final String post_key = getRef(position).getKey();

                Picasso.get().load(model.getFoodImage()).into(holder.foodImg, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getActivity().getApplicationContext(), "Error on load image Cart", Toast.LENGTH_SHORT).show();
                    }
                });
                holder.foodName.setText(model.getFoodName());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent sinlePostIntent = new Intent(getActivity(), FoodPostSingleActivity.class);
                        sinlePostIntent.putExtra("post_id", post_key);
                        startActivity(sinlePostIntent);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        vibrator = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);

                        vibrator.vibrate(30);
                        Bundle bundle = new Bundle();
                        bundle.putString("post_id", post_key);
                        DeleteDialog deleteDialog = new DeleteDialog();
                        deleteDialog.setCancelable(false);
                        deleteDialog.setArguments(bundle);
                        deleteDialog.show(getFragmentManager(),"dialog");
                        return false;
                    }
                });

            }

            @NonNull
            @Override
            public UserPostViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.userpost_linear, viewGroup, false);
                return new UserPostViewHolder(view);
            }
        };

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        gridLayoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(gridLayoutManager);
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

package com.example.pravinewa.foodhut;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotFoundCartDialog extends DialogFragment {

    String post_key;
    DatabaseReference mDatabaseRef;
    public final String TAG = "MyTag: ";
    String postUserID;

//    String food_name, food_description, food_expiry_date, food_image, posted_user, posted_user_contact, food_category, food_status, food_location, user_id, food_currrent_date;
//    long food_price, restore_value_post;

    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        post_key = bundle.getString("post_id");

        return new AlertDialog.Builder(getActivity())
                .setTitle("Not Found!!!")
                .setMessage("This item has been removed by Owner. Press Ok to delete from Cart.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        Bundle bundle = getArguments();
//                        post_key = bundle.getString("post_id");


//                        Log.d(TAG,"postuserid=" +postUserID);
//                        Log.d(TAG,"post_key=" +post_key);
                        DatabaseReference postDeleteCart = FirebaseDatabase.getInstance().getReference("Cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(post_key);
                        postDeleteCart.removeValue();

                    }
                })

                .create();
    }
}

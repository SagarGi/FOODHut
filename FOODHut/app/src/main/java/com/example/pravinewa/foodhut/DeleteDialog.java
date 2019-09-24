package com.example.pravinewa.foodhut;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteDialog extends DialogFragment {

    String post_key;

    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("Remove Post")
                .setMessage("Sure you wanna remove this!")

                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(),"Cancled",Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Bundle bundle = getArguments();
                        post_key = bundle.getString("post_id");

                        DatabaseReference postDeleteCart = FirebaseDatabase.getInstance().getReference("Cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(post_key);
                        DatabaseReference postDeletePost = FirebaseDatabase.getInstance().getReference("Post").child(post_key);
                        DatabaseReference postDeleteUserPost = FirebaseDatabase.getInstance().getReference("UserPost").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(post_key);
                        DatabaseReference postDeleteAdminReport = FirebaseDatabase.getInstance().getReference("AdminReport").child(post_key);

                        postDeleteCart.removeValue();
                        postDeletePost.removeValue();
                        postDeleteAdminReport.removeValue();
                        postDeleteUserPost.removeValue();
                        Snackbar.make((CoordinatorLayout) getActivity().findViewById(R.id.profileLayout), "Item Deleted Successfully.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                })
                .create();
    }
}

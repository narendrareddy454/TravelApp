package uk.ac.tees.aad.w9491709.Utills;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import uk.ac.tees.aad.w9491709.R;


public class MyViewHolder extends RecyclerView.ViewHolder {

    //recyclerview Holder class

    TextView markerName;
    ImageView profileImage;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        markerName=itemView.findViewById(R.id.markerName);
        profileImage=itemView.findViewById(R.id.profileImage);

    }
}

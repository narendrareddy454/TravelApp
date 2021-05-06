package uk.ac.tees.aad.w9491709.Utills;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import de.hdodenhof.circleimageview.CircleImageView;
import uk.ac.tees.aad.w9491709.R;

public class FindFriendViewHolder extends RecyclerView.ViewHolder {
    public CircleImageView profileImage;
    public TextView username,profession;



    public FindFriendViewHolder(@NonNull View itemView) {
        super(itemView);
        profileImage=itemView.findViewById(R.id.profile_image3);
        username=itemView.findViewById(R.id.username);
        profession=itemView.findViewById(R.id.profession);

    }
}

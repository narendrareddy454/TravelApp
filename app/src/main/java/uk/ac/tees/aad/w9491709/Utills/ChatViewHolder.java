package uk.ac.tees.aad.w9491709.Utills;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import uk.ac.tees.aad.w9491709.R;


public class ChatViewHolder  extends RecyclerView.ViewHolder {
    public ImageView imageViewSMS1,imageViewSMS2;
    public TextView sms1,sms2;
    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        imageViewSMS1=itemView.findViewById(R.id.imageU1);
        imageViewSMS2=itemView.findViewById(R.id.imageU2);
        sms1=itemView.findViewById(R.id.sms1);
        sms2=itemView.findViewById(R.id.sms2);
    }
}

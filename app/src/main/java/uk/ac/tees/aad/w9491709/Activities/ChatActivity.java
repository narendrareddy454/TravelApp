package uk.ac.tees.aad.w9491709.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.ac.tees.aad.w9491709.R;
import uk.ac.tees.aad.w9491709.Utills.Chat;
import uk.ac.tees.aad.w9491709.Utills.ChatViewHolder;

public class ChatActivity extends AppCompatActivity {

    private static final int IMAGE_REQUESTCODE = 101;
    Toolbar toolbar;
    EditText inputSMS;
    ImageView btnsendSMS;
    DatabaseReference mSMSRef, Userref;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String OtherUserKey;
    String usernameV, profileImageV, statusV;
    String myProfileImageV;

    CircleImageView profileImageView;
    TextView username, status;
    RecyclerView recyclerViewChat;
    FirebaseRecyclerOptions<Chat> options;
    FirebaseRecyclerAdapter<Chat, ChatViewHolder> adapter;
    int p = 0;
    String URL = "https://fcm.googleapis.com/fcm/send";
    ImageView imageSend;


    //Notification
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = findViewById(R.id.chat_appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Shafaqat Ali");
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageSend = findViewById(R.id.imageSend);
        inputSMS = findViewById(R.id.inputSMS);
        btnsendSMS = findViewById(R.id.btnSendSMS);
        profileImageView = findViewById(R.id.profileImageView);
        username = findViewById(R.id.username);

        recyclerViewChat = findViewById(R.id.recyclerViewChat);
        requestQueue = Volley.newRequestQueue(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerViewChat.setLayoutManager(layoutManager);

        OtherUserKey = getIntent().getStringExtra("UserKey");

        //firerbase
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mSMSRef = FirebaseDatabase.getInstance().getReference().child("Messaage");
        Userref = FirebaseDatabase.getInstance().getReference().child("Users");

        LoadUser();
        LoadMyProfile();
        btnsendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendSMs();
            }
        });
        LoadSMS();
        imageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_REQUESTCODE);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUESTCODE && data != null && resultCode == RESULT_OK) {
            // Log.d("callThis", "onActivityResult: "+"kksksk");
//          Dialog dialog=new Dialog(ChatActivity.this);
//            dialog.setContentView(R.layout.alert_dialog);
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//
//            ImageView imageView=dialog.findViewById(R.id.alertImage);
//            Button btnCancel=dialog.findViewById(R.id.btnCancel);
//            Button btnOK=dialog.findViewById(R.id.btnOK);
//
//            Uri imageUri=data.getData();
//            imageView.setImageURI(imageUri);
//            btnOK.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                }
//            });
        }
    }

    private void LoadMyProfile() {
        Userref.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    myProfileImageV = dataSnapshot.child("profileImageUrl").getValue().toString();
                } else {
                    Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void LoadSMS() {
        options = new FirebaseRecyclerOptions.Builder<Chat>().setQuery(mSMSRef.child(mUser.getUid()).child(OtherUserKey), Chat.class).build();
        adapter = new FirebaseRecyclerAdapter<Chat, ChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull Chat model) {
                p = getItemCount();
//                recyclerViewChat.scrollToPosition(p);
                if (model.getUserID().equals(mUser.getUid())) {

                    holder.sms1.setVisibility(View.GONE);
                    holder.imageViewSMS1.setVisibility(View.GONE);
                    holder.sms2.setVisibility(View.VISIBLE);
                    holder.imageViewSMS2.setVisibility(View.VISIBLE);
                    holder.sms2.setText(model.getSms());
                    Picasso.get().load(myProfileImageV).into(holder.imageViewSMS2);

                } else {

                    holder.sms1.setVisibility(View.VISIBLE);
                    holder.imageViewSMS1.setVisibility(View.VISIBLE);
                    holder.sms1.setText(model.getSms());
                    holder.sms2.setVisibility(View.GONE);
                    holder.imageViewSMS2.setVisibility(View.GONE);
                    Picasso.get().load(profileImageV).into(holder.imageViewSMS1);
                    // Picasso.get().load(profileImageV).into(holder.imageViewSMS2);
                }
            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_sms, parent, false);
                return new ChatViewHolder(v);
            }
        };
        // recyclerViewChat.smoothScrollToPosition(mAdapter.getItemCount());
        adapter.startListening();
        recyclerViewChat.setAdapter(adapter);

    }

    private void LoadUser() {
        Userref.child(OtherUserKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    usernameV = dataSnapshot.child("username").getValue().toString();
                    profileImageV = dataSnapshot.child("profileImageUrl").getValue().toString();
//                    statusV = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(profileImageV).into(profileImageView);
                    username.setText(usernameV);
                   // status.setText(statusV);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(ChatActivity.this, "Something going wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SendSMs() {

        final String sms = inputSMS.getText().toString();
        if (sms.isEmpty()) {
            Toast.makeText(this, "Please Write Something here", Toast.LENGTH_SHORT).show();
        } else {
            final HashMap hashMap = new HashMap();
            hashMap.put("sms", sms);
            hashMap.put("status", "unseen");
            hashMap.put("userID", mUser.getUid());

            mSMSRef.child(OtherUserKey).child(mUser.getUid()).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        mSMSRef.child(mUser.getUid()).child(OtherUserKey).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    inputSMS.setText(null);
                                    recyclerViewChat.smoothScrollToPosition(adapter.getItemCount());
                                    sendNotification(sms);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(ChatActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void sendNotification(String data) {

        try {
            JSONObject obj = new JSONObject();
            obj.put("to", "/topics/" + OtherUserKey + "");


            JSONObject obj2 = new JSONObject();
            obj2.put("title", "Message From " + usernameV);
            obj2.put("body", data);
            obj.put("notification", obj2);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, obj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("content-type", "application/json");
                    map.put("authorization", "key=AAAAEn7vfNY:APA91bGGi8MfKMRwplFXwl-B37JOsdpCwZ4eLpHNgWNdzKeVq9OA7gxX9L5AjR7Lkqk-xgojqd0hofgILqRkhrV0HrxWuM72274pUfZEPfBpeUhbiojqj_SndsFLl6OevGqSgWf7UWTa");
                    return map;
                }
            };
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
package uk.ac.tees.aad.w9491709.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import uk.ac.tees.aad.w9491709.R;
import uk.ac.tees.aad.w9491709.Utills.FindFriendViewHolder;
import uk.ac.tees.aad.w9491709.Utills.Friends;

public class UsersActivity extends AppCompatActivity {

    RecyclerView FriendRecyclerView;
    EditText inputFriendSearch;
    FirebaseRecyclerOptions<Friends> options;
    FirebaseRecyclerAdapter<Friends, FindFriendViewHolder> adapter;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef, mUserRef;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        toolbar = findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FriendRecyclerView = findViewById(R.id.friendRecyclerView);
        FriendRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        inputFriendSearch = findViewById(R.id.inputFriendSearch);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        LoadFriend("");
        inputFriendSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.toString() == null) {
                    LoadFriend("");
                } else {
                    LoadFriend(editable.toString());
                }
            }
        });
    }

    private void LoadFriend(String s) {
        Query query = mUserRef.orderByChild("username").startAt(s).endAt(s + "\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();
        adapter = new FirebaseRecyclerAdapter<Friends, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FindFriendViewHolder holder, int position, @NonNull Friends model) {
                final String UserKey = getRef(position).getKey();


                if (!mUser.getUid().equals(getRef(position).getKey())) {
                    holder.itemView.setVisibility(View.VISIBLE);
                    Picasso.get().load(model.getProfileImageUrl()).into(holder.profileImage);
                    holder.username.setText(model.getUsername());
                    holder.profession.setText(model.getAddress());
                   // holder.itemView.setVisibility(View.VISIBLE);


                } else {
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setVisibility(View.INVISIBLE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(UsersActivity.this, ChatActivity.class);
                        intent.putExtra("UserKey", UserKey);
                        startActivity(intent);
                    }
                });
            }
            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_friend, parent, false);

                return new FindFriendViewHolder(v);
            }
        };
        adapter.startListening();
        FriendRecyclerView.setAdapter(adapter);
    }
//    @Override
//    protected void onStop() {
//        super.onStop();
//        HashMap status = new HashMap();
//        status.put("status", "offline");
//        mUserRef.child(mUser.getUid()).updateChildren(status);
//    }
//
//    @Override
//    protected void onRestart() {
//        HashMap status = new HashMap();
//        status.put("status", "online");
//        mUserRef.child(mUser.getUid()).updateChildren(status);
//        super.onRestart();
//    }
}
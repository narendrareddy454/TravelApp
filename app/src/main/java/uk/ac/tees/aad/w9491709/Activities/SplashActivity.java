package uk.ac.tees.aad.w9491709.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import uk.ac.tees.aad.w9491709.R;

public class SplashActivity extends AppCompatActivity {

    //Variable dec
    ImageView imageView;
    TextView textView;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);  //hide status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //init variable
        imageView=findViewById(R.id.logo);
        textView=findViewById(R.id.textLogo);
        Animation animationlogo;
        Animation animationText;

        animationlogo= AnimationUtils.loadAnimation(this,R.anim.my_logo_anim);
        animationText= AnimationUtils.loadAnimation(this,R.anim.text_anim);
        imageView.setAnimation(animationlogo);
        textView.setAnimation(animationText);

        //Firebase Decleration
        mUserRef= FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        //wait for 5 sec and open main activity
        Runnable runnable = () -> {

            if (mUser!=null)
            {
                CheckUserData();
            }else
            {
                //open new activity
                Intent intent = new Intent(SplashActivity.this, AuthenticationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        };
        new Handler().postDelayed(runnable, 5000);
    }
    //check Edither  user saved setup data or not
    private void CheckUserData() {
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("username").exists())
                {
                    //if user saved data of setup then open main page
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }else {

                    //if user did not saved data of setup page then open setup page
                    Intent intent = new Intent(SplashActivity.this, SetupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SplashActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
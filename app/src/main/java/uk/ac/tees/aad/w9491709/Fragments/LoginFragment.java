package uk.ac.tees.aad.w9491709.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import uk.ac.tees.aad.w9491709.Activities.AuthenticationActivity;
import uk.ac.tees.aad.w9491709.Activities.HomeActivity;
import uk.ac.tees.aad.w9491709.Activities.SetupActivity;
import uk.ac.tees.aad.w9491709.MainActivity;
import uk.ac.tees.aad.w9491709.R;


public class LoginFragment extends Fragment {


    //dec varibale
    TextView createNewAccount;
    EditText inputUserEmail, inputPassword;
    Button btnLogin;
    ProgressDialog mLoadingBar;
    ProgressDialog progressBar;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserRef;



    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_login, container, false);


        //variable init
        inputUserEmail = view.findViewById(R.id.inputEmail);
        inputPassword = view.findViewById(R.id.inputPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        progressBar=new ProgressDialog(getContext());
        createNewAccount = view.findViewById(R.id.createNewAccount);
        mLoadingBar = new ProgressDialog(getContext());
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");


        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationActivity.fragmentManager.beginTransaction().replace(R.id.container,new RegisterFragment(),null).commit();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });



        return view;

    }

    //login method
    private void LoginUser() {

        //get data from input fields
        String email = inputUserEmail.getText().toString();
        String password = inputPassword.getText().toString();

        //check if nothing enter in input fields
        if (email.isEmpty() || !email.contains("@")) {
            inputUserEmail.setError("Enter Correct format Email");
            inputUserEmail.requestFocus();
        } else if (password.isEmpty() || password.length() < 6) {
            inputPassword.setError("ENter min 7 latter password");
        } else {

            //start progress bar
            progressBar.setMessage("Login...");
            progressBar.setCanceledOnTouchOutside(false);
            progressBar.show();

            //sign in using email and password by firebase
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // if sign in successfull check user entered data already or not
                        mUser = mAuth.getCurrentUser();
                        CheckUserData();
                        progressBar.dismiss();
                    } else {
                        //   if sign inno  successfull dismiss everthing
                        progressBar.dismiss();
                        Toast.makeText(getContext(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }
    //check Editer user saved Setup profile data or not.If not then send user t setup Page to add this data
    private void CheckUserData() {
        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("username").exists()) {
                    //if user saved data of setup then open main page
                    Intent intent = new Intent(getContext(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } else {
                    //if user did not saved data of setup page then open setuppage
                    Intent intent = new Intent(getContext(), SetupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}
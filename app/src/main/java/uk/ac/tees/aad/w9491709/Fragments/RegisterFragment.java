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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uk.ac.tees.aad.w9491709.Activities.AuthenticationActivity;
import uk.ac.tees.aad.w9491709.Activities.SetupActivity;
import uk.ac.tees.aad.w9491709.R;


public class RegisterFragment extends Fragment {


    //Veribale Decleration
    EditText inputEmail;
    EditText inputPassword;
    EditText inputConfrimPassword;
    Button btnRegister;
    TextView alreadyHaveAccount;
    ProgressDialog progressBar;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mUserRef;


    public RegisterFragment() {
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);


        //init varibale
        progressBar = new ProgressDialog(getContext());
        inputEmail = view.findViewById(R.id.inputEmail);
        inputPassword = view.findViewById(R.id.inputPassword);
        inputConfrimPassword = view.findViewById(R.id.inputPassword2);
        btnRegister = view.findViewById(R.id.btnRegister);
        alreadyHaveAccount = view.findViewById(R.id.alreadyHaveAccount);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");


        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticationActivity.fragmentManager.beginTransaction().replace(R.id.container, new LoginFragment(), null).commit();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performRegistration();
            }
        });
        return view;
    }

    private void performRegistration() {

        //get data from input that user typed
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String confrimPassword = inputConfrimPassword.getText().toString();


        //show errors if user did'nt anything in input field
        if (email.isEmpty() || !email.contains("@")) {
            inputEmail.setError("Enter Correct format Email");
            inputEmail.requestFocus();
        } else if (password.isEmpty() || password.length() < 6) {
            inputPassword.setError("Enter min 7 latter password");
        } else if (!confrimPassword.equals(password)) {
            inputConfrimPassword.setError("Pasword not match both field");
        } else {
            //if user enter valid format of email and password then perform registration
            progressBar.setMessage("Registration...");
            progressBar.setCanceledOnTouchOutside(false);
            progressBar.show();

            //register user using email and password using firebase
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressBar.dismiss();

                        //after successfull registration open Setup activity
                        Intent intent = new Intent(getContext(), SetupActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    } else {
                        progressBar.dismiss();
                        Toast.makeText(getContext(), "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}
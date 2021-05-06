package uk.ac.tees.aad.w9491709.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.FrameLayout;

import uk.ac.tees.aad.w9491709.Fragments.LoginFragment;
import uk.ac.tees.aad.w9491709.R;

public class AuthenticationActivity extends AppCompatActivity {

   public static FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_authentication);
        fragmentManager = getSupportFragmentManager();

        if (findViewById(R.id.container)!=null)
        {
            if (savedInstanceState!=null)
            {
               return;

            }
            fragmentManager.beginTransaction().add(R.id.container, new LoginFragment(), null).commit();
        }

    }
}
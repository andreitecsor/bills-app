package ie.dam.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ie.dam.project.fragments.LoginFragment;

public class BeginActivity extends AppCompatActivity {

    FirebaseAuth fbAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);
        initialiseComponents(savedInstanceState);
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = fbAuth.getCurrentUser();
        if (currentUser != null) {
            createDashboardActivity();
        }

    }

    private void createDashboardActivity() {
        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
        finish();

    }

    private void initialiseComponents(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.act_begin_frame_layout,
                    new LoginFragment()).commit();
        }
    }
}
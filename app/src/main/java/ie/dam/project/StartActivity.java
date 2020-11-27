package ie.dam.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ie.dam.project.fragments.HomeFragment;
import ie.dam.project.fragments.LoginFragment;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.act_start_frame_layout,
                    new LoginFragment()).commit();
        }
    }
}
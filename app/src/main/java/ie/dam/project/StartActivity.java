package ie.dam.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ie.dam.project.fragments.HomeFragment;
import ie.dam.project.fragments.LoginFragment;
import ie.dam.project.fragments.RegisterFragment;

public class StartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initialiseComponents(savedInstanceState);
    }

    private void initialiseComponents(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.act_start_frame_layout,
                    new LoginFragment()).commit();
        }
    }
}
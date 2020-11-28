package ie.dam.project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ie.dam.project.fragments.LoginFragment;

public class BeginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);
        initialiseComponents(savedInstanceState);
    }

    private void initialiseComponents(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.act_begin_frame_layout,
                    new LoginFragment()).commit();
        }
    }
}
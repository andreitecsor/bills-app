package ie.dam.project.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import ie.dam.project.MainActivity;
import ie.dam.project.R;
import ie.dam.project.StartActivity;


public class LoginFragment extends Fragment {
    private Button registerNowButton;
    private Button loginButton;

    private TextInputEditText emailTiet;
    private TextInputEditText passwordTiet;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initialiseComponents(view);
        loginButton.setOnClickListener(login());
        registerNowButton.setOnClickListener(registerNow());
        return view;
    }

    private View.OnClickListener registerNow() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivity startActivity = (StartActivity) getContext(); //poate returna null. WATCH OUT!!!
                if (startActivity != null) {
                    startActivity.getSupportFragmentManager().beginTransaction().replace(R.id.act_start_frame_layout,
                            new RegisterFragment()).commit();
                }
            }
        };
    }

    private View.OnClickListener login() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivity startActivity = (StartActivity) getContext(); //poate returna null. WATCH OUT!!!
                if (startActivity != null) {
                    if (emailTiet.getText().toString().equals("a") && passwordTiet.getText().toString().equals("a")) {
                        Intent intent = new Intent(startActivity, MainActivity.class);
                        startActivity.startActivity(intent);
                        startActivity.finish();
                    } else {
                        Toast.makeText(startActivity, R.string.start_invalid_credentials, Toast.LENGTH_LONG).show();
                    }
                }
            }
        };
    }

    private void initialiseComponents(View view) {
        registerNowButton = view.findViewById(R.id.frg_login_register_button);
        loginButton = view.findViewById(R.id.frg_login_button);
        emailTiet = view.findViewById(R.id.frg_login_email_tiet);
        passwordTiet = view.findViewById(R.id.frg_login_password_tiet);
    }
}
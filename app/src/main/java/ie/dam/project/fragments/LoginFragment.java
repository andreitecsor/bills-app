package ie.dam.project.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ie.dam.project.DashboardActivity;
import ie.dam.project.R;
import ie.dam.project.BeginActivity;


public class LoginFragment extends Fragment {
    private Button registerNowButton;
    private Button loginButton;

    private TextInputEditText emailTiet;
    private TextInputEditText passwordTiet;

    FirebaseAuth fbAuth;
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
                BeginActivity beginActivity = (BeginActivity) getContext(); //poate returna null. WATCH OUT!!!
                if (beginActivity != null) {
                    beginActivity.getSupportFragmentManager().beginTransaction().replace(R.id.act_begin_frame_layout,
                            new RegisterFragment()).commit();
                }
            }
        };
    }

    //ToDo-> modify function to integrate FIREBASE
    private View.OnClickListener login() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailTiet.getText().toString().trim();
                String password=passwordTiet.getText().toString();
                if(TextUtils.isEmpty(email))
                {
                    emailTiet.setError(getString(R.string.email_empty));
                }
                if(!RegisterFragment.isEmailValid(email))
                {
                    emailTiet.setError(getString(R.string.register_invalid_email_error));
                }
                if(TextUtils.isEmpty(password))
                {
                    passwordTiet.setError(getString(R.string.login_empty_password));
                }

                fbAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(getContext(),"You've been successfully signed in!",Toast.LENGTH_SHORT).show();
                            createDashboardActivity();
                        }
                        else{
                            Toast.makeText(getContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fbAuth=FirebaseAuth.getInstance();
    }

    private void createDashboardActivity()
    {
        BeginActivity beginActivity = (BeginActivity) getContext(); //poate returna null. WATCH OUT!!!
        if (beginActivity != null) {
                Intent intent = new Intent(beginActivity, DashboardActivity.class);
                beginActivity.startActivity(intent);
                beginActivity.finish();
        }
    }
    private void initialiseComponents(View view) {
        registerNowButton = view.findViewById(R.id.frg_login_button_register);
        loginButton = view.findViewById(R.id.frg_login_button_login);
        emailTiet = view.findViewById(R.id.frg_login_tiet_email);
        passwordTiet = view.findViewById(R.id.frg_login_tiet_password);
    }
}
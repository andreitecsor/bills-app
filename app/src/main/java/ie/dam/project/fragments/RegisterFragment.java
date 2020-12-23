package ie.dam.project.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import ie.dam.project.BeginActivity;
import ie.dam.project.DashboardActivity;
import ie.dam.project.R;

public class RegisterFragment extends Fragment {
    public static final String NAME_KEY = "name";
    public static final String SHARED_PREF_FILE = "profile_shared_pref";

    private Button loginNowButton;
    private Button registerButton;
    private TextInputEditText nameET;
    private TextInputEditText emailET;
    private TextInputEditText passwordET;
    private TextInputEditText confirmPasswordET;

    private FirebaseAuth fbAuth;
    private SharedPreferences preferences;

    private String name;
    //TODO: Register button impl

    public RegisterFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initialiseComponents(view);

        loginNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BeginActivity beginActivity = (BeginActivity) getContext(); //poate returna null. WATCH OUT!!!
                if (beginActivity != null) {
                    beginActivity.getSupportFragmentManager().beginTransaction().replace(R.id.act_begin_frame_layout,
                            new LoginFragment()).commit();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameET.getText().toString();

                String email = emailET.getText().toString().trim();
                String password = passwordET.getText().toString();
                String confirmPass = confirmPasswordET.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    nameET.setError(getString(R.string.register_name_error));
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    emailET.setError(getString(R.string.email_empty));
                    return;
                }
                if (!isEmailValid(email)) {
                    emailET.setError(getString(R.string.register_invalid_email_error));
                    return;
                }

                if (password.length() < 8) {
                    passwordET.setError(getString(R.string.register_password_lenght_error));
                    return;
                }
                if (TextUtils.isEmpty(confirmPass)) {
                    confirmPasswordET.setError(getString(R.string.register_confirmPassword_empty_error));
                    return;
                }
                if (!password.equals(confirmPass)) {
                    confirmPasswordET.setError(getString(R.string.register_confirmPassword_noMatch_error));
                    return;
                }

                fbAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "User created succesfully! Please log in with your new account!",
                                    Toast.LENGTH_LONG).show();


                            //todo: open dashboard or go to login fragment (redirecting to login currently)


                            BeginActivity beginActivity = (BeginActivity) getContext(); //poate returna null. WATCH OUT!!!
                            if (beginActivity != null) {
//                           beginActivity.getSupportFragmentManager().beginTransaction().replace(R.id.act_begin_frame_layout,
//                                   new LoginFragment()).commit();
                                fbAuth.signInWithEmailAndPassword(email, password);

                                addNameToSharedPreferences();
                                createDashboardActivity();
                            }
                        } else {
                            Toast.makeText(getContext(), "Cannot create account!" + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        return view;
    }

    private void addNameToSharedPreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(NAME_KEY, name).apply();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fbAuth = FirebaseAuth.getInstance();

    }

    private void initialiseComponents(View view) {
        //PREFERENCES
        preferences = getContext().getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);

        loginNowButton = view.findViewById(R.id.frg_register_button_login);
        registerButton = view.findViewById(R.id.frg_register_button_register);
        nameET = view.findViewById(R.id.frg_register_tiet_name);
        emailET = view.findViewById(R.id.frg_register_tiet_email);
        passwordET = view.findViewById(R.id.frg_register_tiet_password);
        confirmPasswordET = view.findViewById(R.id.frg_register_tiet_password_confirm);
    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private void createDashboardActivity() {
        BeginActivity beginActivity = (BeginActivity) getContext(); //poate returna null. WATCH OUT!!!
        if (beginActivity != null) {
            Intent intent = new Intent(beginActivity, DashboardActivity.class);
            beginActivity.startActivity(intent);
            beginActivity.finish();
        }
    }


}
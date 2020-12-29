package ie.dam.project.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import ie.dam.project.DashboardActivity;
import ie.dam.project.R;
import ie.dam.project.BeginActivity;
import ie.dam.project.util.encryption.AESCrypt;


public class LoginFragment extends Fragment {
    public static final String EMAIL_KEY = "email";
    public static final String PASSWORD_KEY = "password";
    public static final String REMEMBER_ME = "remember_me";
    public static final String REMEMBER_ME_PREFERENCE = "remember_me_check";
    private Button registerNowButton;
    private Button loginButton;

    private TextInputEditText emailTiet;
    private TextInputEditText passwordTiet;
    private CheckBox rememberMeCb;

    FirebaseAuth fbAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    SharedPreferences currentUserPreferences;
    SharedPreferences rememberMePreferences;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        initialiseComponents(view);
        loginButton.setOnClickListener(login());
        registerNowButton.setOnClickListener(registerNow());
        populateLoginPage();
        return view;
    }

    private View.OnClickListener registerNow() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BeginActivity beginActivity = (BeginActivity) getContext(); //poate returna null. WATCH OUT!!!
                if (beginActivity != null) {
                    beginActivity.getSupportFragmentManager()
                            .beginTransaction().setCustomAnimations(R.anim.left_to_right_in,R.anim.left_to_right_out)
                            .replace(R.id.act_begin_frame_layout, new RegisterFragment())
                            .commit();
                }
            }
        };
    }

    //ToDo-> modify function to integrate FIREBASE
    private View.OnClickListener login() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailTiet.getText().toString().trim();
                String password = passwordTiet.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    emailTiet.setError(getString(R.string.email_empty));
                    return;
                }
                if (!RegisterFragment.isEmailValid(email)) {
                    emailTiet.setError(getString(R.string.register_invalid_email_error));
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    passwordTiet.setError(getString(R.string.login_empty_password));
                    return;
                }

                fbAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            createDashboardActivity();
                            FirebaseUser currentUser = fbAuth.getCurrentUser();
                            currentUserPreferences = getContext().getSharedPreferences(currentUser.getUid() + RegisterFragment.SHARED_PREF_FILE_EXTENSION, Context.MODE_PRIVATE);
                            if (fbAuth.getCurrentUser().getDisplayName() == null) {
                                updateUser(currentUserPreferences.getString(RegisterFragment.NAME_KEY, getString(R.string.preference_name_default)), null);

                            }
                            rememberMeToPreferenceFile(email, password);
                            Toast.makeText(getActivity(), "You've been successfully signed in!", Toast.LENGTH_SHORT).show();
                            getActivity().finish();

                        } else {
                            Toast.makeText(getActivity(), "Invalid email or password", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fbAuth = FirebaseAuth.getInstance();

    }

    private void rememberMeToPreferenceFile(String email, String password) {
        if (rememberMeCb.isChecked()) {
            try {

                SharedPreferences.Editor editor = rememberMePreferences.edit();
                editor
                        .putString(EMAIL_KEY, email)
                        .putString(PASSWORD_KEY, AESCrypt.encrypt(password))
                        .putBoolean(REMEMBER_ME_PREFERENCE, true)
                        .apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            rememberMePreferences.edit().clear().apply();

        }
    }

    private void createDashboardActivity() {
        BeginActivity beginActivity = (BeginActivity) getContext(); //poate returna null. WATCH OUT!!!
        if (beginActivity != null) {
            Intent intent = new Intent(beginActivity, DashboardActivity.class);
            beginActivity.startActivity(intent);
            beginActivity.finish();
        }
    }

    private void initialiseComponents(View view) {
        // preferences=getActivity().getSharedPreferences();
        rememberMePreferences = getActivity().getSharedPreferences(REMEMBER_ME, Context.MODE_PRIVATE);
        rememberMeCb = view.findViewById(R.id.checkBox);
        registerNowButton = view.findViewById(R.id.frg_login_button_register);
        loginButton = view.findViewById(R.id.frg_login_button_login);
        emailTiet = view.findViewById(R.id.frg_login_tiet_email);
        passwordTiet = view.findViewById(R.id.frg_login_tiet_password);
    }

    public void updateUser(String name, Uri photoUri) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            currentUser.updateProfile(profileUpdate)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("OK", "User profile updated.");
                                //todo: add remember me to shared preferences


                            } else Log.d("ERROR", task.getException().getMessage());
                        }
                    });
        }

    }

    public void populateLoginPage() {
        if (rememberMePreferences != null) {

            emailTiet.setText(rememberMePreferences.getString(EMAIL_KEY, ""));
            try {
                passwordTiet.setText(AESCrypt.decrypt(rememberMePreferences.getString(PASSWORD_KEY, "")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            rememberMeCb.setChecked(rememberMePreferences.getBoolean(REMEMBER_ME_PREFERENCE, false));
        }
    }
}
package ie.dam.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import ie.dam.project.fragments.LoginFragment;
import ie.dam.project.fragments.RegisterFragment;
import ie.dam.project.util.encryption.AESCrypt;

public class ProfileActivity extends AppCompatActivity {


    EditText emailET;
    EditText passwordET;
    EditText oldPasswordET;
    FirebaseUser currentUser;
    Button saveBtn;

    SharedPreferences preferences;
    SharedPreferences rememberMePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeComponents();
        if (currentUser != null) {
            emailET.setText(currentUser.getEmail().toString());

        }
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPasswordET.getText().toString();
                String password = passwordET.getText().toString();
                String email = emailET.getText().toString().trim();

                if (!oldPassword.isEmpty()) {

                    AuthCredential credential = EmailAuthProvider
                            .getCredential(currentUser.getEmail(), oldPassword);


                    currentUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(getString(R.string.log_ok), getString(R.string.log_user_reauthenticated));
                                        int emailValid = validateEmail(email, currentUser.getEmail());
                                        switch (emailValid) {
                                            case 0:
                                                break;
                                            case 1: {
                                                updateEmail(email);
                                                if (rememberMePreferences.getBoolean(LoginFragment.REMEMBER_ME_PREFERENCE, false)) {
                                                    rememberMePreferences.edit().putString(LoginFragment.EMAIL_KEY, email).apply();
                                                }
                                            }
                                        }
                                        int passwordValid = validatePassword(password);
                                        switch (passwordValid) {
                                            case 0:
                                                break;
                                            case 1:
                                                updatePassword(password);
                                                if (rememberMePreferences.getBoolean(LoginFragment.REMEMBER_ME_PREFERENCE, false)) {
                                                    try {
                                                        rememberMePreferences.edit().putString(LoginFragment.PASSWORD_KEY, AESCrypt.encrypt(password)).apply();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                        }
                                        if (emailValid == -1 && passwordValid == -1) {
                                            Toast.makeText(getApplicationContext(), getString(R.string.no_update_done), Toast.LENGTH_SHORT).show();

                                        } else if (emailValid != 0 && passwordValid != 0 && emailET.getError() == null) {
                                            Toast.makeText(getApplicationContext(), getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                                            finish();
                                            overridePendingTransition(R.anim.top_to_bot_in, R.anim.top_to_bot_out);
                                        }


                                    } else {
                                        Log.d(getString(R.string.log_failed), getString(R.string.reauthenticate_error));
                                        Toast.makeText(getApplicationContext(), getString(R.string.invalid_confirm_password), Toast.LENGTH_SHORT).show();


                                    }
                                }
                            });
                } else
                    Toast.makeText(getApplicationContext(), getString(R.string.no_confirm_password), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void initializeComponents() {
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.overcast_white));
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        preferences = getSharedPreferences(currentUser.getUid() + RegisterFragment.SHARED_PREF_FILE_EXTENSION, MODE_PRIVATE);
        rememberMePreferences = getSharedPreferences(LoginFragment.REMEMBER_ME, MODE_PRIVATE);
        emailET = findViewById(R.id.act_profile_et_email);
        passwordET = findViewById(R.id.act_profile_et_new_password);
        oldPasswordET = findViewById(R.id.act_profile_et_confirm_old_pass);
        saveBtn = findViewById(R.id.act_profile_button_save);

    }

    public void updateUser(String name, Uri photoUri) {
        if (currentUser != null) {
            UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build();

            currentUser.updateProfile(profileUpdate)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(getString(R.string.log_ok), getString(R.string.update_username_log));
                            } else Log.d(getString(R.string.log_error), task.getException().getMessage());
                        }
                    });
        }
    }

    private void updatePassword(String password) {

        currentUser.updatePassword(password)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(getString(R.string.log_ok), getString(R.string.password_update_log));
                        }
                    }
                });
    }

    private void updateEmail(String email) {
        currentUser.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(getString(R.string.log_ok), getString(R.string.email_updated_log));
                        } else {
                            emailET.setError(getString(R.string.email_existing));
                        }
                    }
                });
    }


    public int validateEmail(String email, String currentEmail) {

        if (email.isEmpty() || email.trim().isEmpty()) {
            emailET.setError(getString(R.string.email_empty));
            return 0;
        }
        if (!email.equals(currentEmail)) {
            if (RegisterFragment.isEmailValid(email))
                return 1;
            else {
                emailET.setError(getString(R.string.email_invalid_format));
                return 0;
            }
        } else return -1;


    }

    public int validatePassword(String password) {
        if (password.isEmpty()) {
            return -1;
        }
        if (password.length() < 8) {
            passwordET.setError(getString(R.string.password_length_error));
            return 0;
        } else return 1;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
        overridePendingTransition(R.anim.top_to_bot_in, R.anim.top_to_bot_out);
        finish();

    }

}





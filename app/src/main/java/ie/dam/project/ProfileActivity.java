package ie.dam.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import ie.dam.project.fragments.RegisterFragment;

public class ProfileActivity extends AppCompatActivity {


    EditText emailET;
    EditText passwordET;
    EditText oldPasswordET;
    FirebaseUser currentUser;
    Button saveBtn;

    SharedPreferences preferences;

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
                                        Log.d("OK", "User re-authenticated.");
                                        int emailValid = validateEmail(email, currentUser.getEmail());
                                        switch (emailValid) {
                                            case 0:
                                                break;
                                            case 1: {
                                                updateEmail(email);

                                            }
                                        }
                                        int passwordValid = validatePassword(password);
                                        switch (passwordValid) {
                                            case 0:
                                                break;
                                            case 1:
                                                updatePassword(password);
                                        }
                                        if (emailValid == -1 && passwordValid == -1) {
                                            Toast.makeText(getApplicationContext(), "Nothing to update!", Toast.LENGTH_SHORT).show();

                                        } else if (emailValid != 0 && passwordValid != 0 && emailET.getError() == null) {
                                            Toast.makeText(getApplicationContext(), "Account updated succesfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }


                                    } else {
                                        Log.d("FAILED", "ERROR REAUTHENTICATING");
                                        Toast.makeText(getApplicationContext(), "Invalid Confirm Password", Toast.LENGTH_SHORT).show();


                                    }
                                }
                            });
                } else
                    Toast.makeText(getApplicationContext(), "No confirm password", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void initializeComponents() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        preferences = getSharedPreferences(currentUser.getUid() + RegisterFragment.SHARED_PREF_FILE_EXTENSION, MODE_PRIVATE);
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
                                Log.d("OK", "User profile updated.");
                            } else Log.d("ERROR", task.getException().getMessage());
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
                            Log.d("OK", "User password updated.");
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
                            Log.d("Ok", "User email address updated.");
                        } else {
                            emailET.setError("Email already exists in the database! Enter a different email!");
                        }
                    }
                });
    }


    public int validateEmail(String email, String currentEmail) {

        if (email.isEmpty() || email.trim().isEmpty()) {
            emailET.setError("Email cannot be empty");
            return 0;
        }
        if (!email.equals(currentEmail)) {
            if (RegisterFragment.isEmailValid(email))
                return 1;
            else {
                emailET.setError("Email does not have a valid format");
                return 0;
            }
        } else return -1;


    }

    public int validatePassword(String password) {
        if (password.isEmpty()) {
            return -1;
        }
        if (password.length() < 8) {
            passwordET.setError("New password must have at least 8 characters!");
            return 0;
        } else return 1;
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
    }
}



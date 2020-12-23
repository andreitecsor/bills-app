package ie.dam.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    TextInputEditText nameET;
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
        if (currentUser.getDisplayName() != null) {
            nameET.setText(currentUser.getDisplayName().toString());
            emailET.setText(currentUser.getEmail().toString());
        }
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPasswordET.getText().toString();
                String password = passwordET.getText().toString();
                String name = nameET.getText().toString();
                String email = emailET.getText().toString();

                if (!oldPassword.isEmpty()) {

                    AuthCredential credential = EmailAuthProvider
                            .getCredential(currentUser.getEmail(), oldPassword);


                    currentUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("OK", "User re-authenticated.");
                                        callUpdates(name, null, email, password);
                                        finish();
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
        preferences=getSharedPreferences(RegisterFragment.SHARED_PREF_FILE,MODE_PRIVATE);
        nameET = findViewById(R.id.act_profile_tiet_name);
        emailET = findViewById(R.id.act_profile_et_email);
        passwordET = findViewById(R.id.act_profile_et_new_password);
        oldPasswordET = findViewById(R.id.act_profile_et_confirm_old_pass);
        saveBtn = findViewById(R.id.act_profile_button_save);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
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
                        }
                    }
                });
    }

    private void callUpdates(String name, Uri photo, String email, String password) {
        String currentName = currentUser.getDisplayName().toString();
        String currentEmail = currentUser.getEmail().toString();
        Boolean profileUpdate = false;
        Boolean passwordUpdate = false;
        Boolean emailUpdate = false;

        if (!name.equals(currentName)) {
            if (!currentName.isEmpty())
                profileUpdate = true;
            else {
                Toast.makeText(getApplicationContext(), "Name should not be empty", Toast.LENGTH_SHORT).show();
                return;
            }

        }

        if (!email.equals(currentEmail)) {
            if (RegisterFragment.isEmailValid(email))
                emailUpdate = true;

            else {
                Toast.makeText(getApplicationContext(), "Email is not valid", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (!password.isEmpty()) {
            if (password.length() >= 8) {
                passwordUpdate = true;
                passwordET.setError("");
            } else {
                Toast.makeText(getApplicationContext(), "The new password should be at least 8 characters long!",
                        Toast.LENGTH_SHORT).show();
                return;
            }

        }

        if (profileUpdate == true) {
            updateUser(name, photo);
            addNameToSharedPreferences(name);
        }
        if (passwordUpdate == true) {
            updatePassword(password);
        }
        if (emailUpdate == true) {
            updateEmail(email);
        }
    }
    private void addNameToSharedPreferences(String name) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(RegisterFragment.NAME_KEY, name).apply();
    }
}



package ie.dam.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import ie.dam.project.data.domain.Gender;
import ie.dam.project.fragments.RegisterFragment;

public class PreferenceActivity extends AppCompatActivity {

    public static final String GENDER_KEY = "gender_key";
    TextInputEditText nameET;
    ImageButton image;
    EditText oldPasswordET;
    FirebaseUser currentUser;
    Button saveBtn;
    Spinner genderSpn;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        initializeComponents();
        populateSpinner();
        if (currentUser.getDisplayName() != null) {
            nameET.setText(currentUser.getDisplayName());
            //Todo:get photo from database
        }
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPasswordET.getText().toString();
                String name = nameET.getText().toString();
                String gender = genderSpn.getSelectedItem().toString();


                if (!oldPassword.isEmpty()) {

                    AuthCredential credential = EmailAuthProvider
                            .getCredential(currentUser.getEmail(), oldPassword);


                    currentUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("FIREBASE", "User re-authenticated.");

                                        switch (validate(name)) {
                                            case 0:
                                                break;
                                            case 1: {
                                                updateUser(name, null);
                                                addNameAndGenderToSharedPreferences(name, Gender.getEnum(gender));
                                                finish();
                                            }
                                        }


                                    } else {
                                        Log.d("FIREBASE", "ERROR REAUTHENTICATING");
                                        Toast.makeText(getApplicationContext(), "Invalid Confirm Password", Toast.LENGTH_SHORT).show();


                                    }
                                }
                            });
                } else
                    Toast.makeText(getApplicationContext(), "No confirm password", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private int validate(String name) {
        if (name.equals(currentUser.getDisplayName()))
            return -1;
        if (name.isEmpty()) {
            nameET.setError("Name cannot be empty");
            return 0;
        }
        return 1;
    }

    private void initializeComponents() {
        preferences = getSharedPreferences(RegisterFragment.SHARED_PREF_FILE, MODE_PRIVATE);

        genderSpn = findViewById(R.id.act_preference_spinner_sex);
        nameET = findViewById(R.id.act_preference_tiet_name);
        oldPasswordET = findViewById(R.id.act_preference_et_confirm_old_pass);
        saveBtn = findViewById(R.id.act_preference_button_save);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        image = findViewById(R.id.act_preference_ib);
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
                                Log.d("FIREBASE", "User profile updated.");
                                Toast.makeText(getApplicationContext(), getString(R.string.preference_save_succes), Toast.LENGTH_SHORT).show();
                            } else Log.d("FIREBASE", task.getException().getMessage());
                        }
                    });
        }
    }

    private void addNameAndGenderToSharedPreferences(String name, Gender gender) {
        SharedPreferences.Editor editor = preferences.edit();
        editor
                .putString(RegisterFragment.NAME_KEY, name)
                .putString(GENDER_KEY, gender.toString())
                .apply();

    }

    private void populateSpinner() {
        genderSpn.setAdapter(new ArrayAdapter<Gender>(this, R.layout.support_simple_spinner_dropdown_item, Gender.values()));
        if (preferences != null) {
            if (preferences.contains(GENDER_KEY)) {
                String preferenceGender = preferences.getString(GENDER_KEY, Gender.RATHER_NOT_SAY.toString());
                genderSpn.setSelection(Gender.getEnum(preferenceGender).ordinal());
            }
        }
    }
}


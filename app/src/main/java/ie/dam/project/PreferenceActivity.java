package ie.dam.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import ie.dam.project.data.domain.Gender;
import ie.dam.project.fragments.RegisterFragment;

public class PreferenceActivity extends AppCompatActivity {

    public static final String GENDER_KEY = "gender_key";
    public static final String CURRENCY_KEY = "currency_key";
    public static final int IMAGE_CODE = 500;
    private static final int PICK_IMAGE_CHOOSER_REQUEST_CODE = 300 ;


    TextInputEditText nameET;
    EditText currencyEt;
    ImageButton imageIB;
    EditText oldPasswordET;
    FirebaseUser currentUser;
    Button saveBtn;
    Spinner genderSpn;
    SharedPreferences preferences;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        initializeComponents();
        populateSpinner();
        populateFields();

        imageIB = (ImageButton) findViewById(R.id.act_preference_ib);
        imageIB.setOnClickListener(selectPictureFromGallery());
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = oldPasswordET.getText().toString();
                String name = nameET.getText().toString();
                String gender = genderSpn.getSelectedItem().toString();
                String currency = currencyEt.getText().toString().trim();


                if (!oldPassword.isEmpty()) {

                    AuthCredential credential = EmailAuthProvider
                            .getCredential(currentUser.getEmail(), oldPassword);


                    currentUser.reauthenticate(credential)
                            .addOnCompleteListener(getOnCompleteListener(name, gender, currency));
                } else
                    Toast.makeText(getApplicationContext(), "No confirm password", Toast.LENGTH_SHORT).show();

            }
        });

    }




    private void initializeComponents() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        preferences = getSharedPreferences(currentUser.getUid() + RegisterFragment.SHARED_PREF_FILE_EXTENSION, MODE_PRIVATE);

        currencyEt = findViewById(R.id.act_preference_et_currency);
        genderSpn = findViewById(R.id.act_preference_spinner_sex);
        nameET = findViewById(R.id.act_preference_tiet_name);
        oldPasswordET = findViewById(R.id.act_preference_et_confirm_old_pass);
        saveBtn = findViewById(R.id.act_preference_button_save);

        imageIB = findViewById(R.id.act_preference_ib);
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

    private void populateFields() {
        if (currentUser.getDisplayName() != null) {
            nameET.setText(currentUser.getDisplayName());
            //Todo:get photo from database
        }
        if (preferences.contains(CURRENCY_KEY)) {
            currencyEt.setText(preferences.getString(CURRENCY_KEY, getString(R.string.default_currency)));
        }
    }

    private OnCompleteListener<Void> getOnCompleteListener(String name, String gender, String currency) {
        return new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Log.d("FIREBASE", "User re-authenticated.");

                    //region Name Validation + Update
                    int nameValid = validateName(name);
                    switch (nameValid) {
                        case 0:
                            break;
                        case 1: {
                            updateUser(name, null);
                            addNameToSharedPreferences(name);
                        }
                    }
                    //endregion
                    //region Gender Validation + Update
                    int genderValid = validateGender(gender);
                    if (genderValid == 1) {
                        addGenderToSharedPreferences(gender);
                    }
                    //endregion
                    //region Currency Validation + Update
                    int currencyValid = validateCurrency(currency);
                    switch (currencyValid) {
                        case 0: {
                            break;
                        }
                        case 1: {
                            addCurrencyToPreferenceFiles(currency);
                        }
                    }
                    //endregion
                    //region Final Response
                    if (genderValid == -1 && nameValid == -1 && currencyValid == -1) {
                        Toast.makeText(getApplicationContext(), "Nothing to update!", Toast.LENGTH_SHORT).show();

                    } else if (nameValid != 0 && currencyValid != 0) {
                        Toast.makeText(getApplicationContext(), "Account updated succesfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
//endregion
                } else {
                    Log.d("FIREBASE", "ERROR REAUTHENTICATING");
                    Toast.makeText(getApplicationContext(), "Invalid Confirm Password", Toast.LENGTH_SHORT).show();


                }
            }
        };
    }

    private int validateName(String name) {
        if (name.equals(currentUser.getDisplayName()))
            return -1;
        if (name.isEmpty()) {
            nameET.setError("Name cannot be empty");
            return 0;
        }
        return 1;
    }

    private int validateGender(String gender) {
        String currentGender = preferences.getString(PreferenceActivity.GENDER_KEY, Gender.RATHER_NOT_SAY.toString());
        if (gender.toString().equals(currentGender)) {
            return -1;
        } else return 1;
    }

    private int validateCurrency(String currency) {
        String currentCurrency = preferences.getString(PreferenceActivity.CURRENCY_KEY, getString(R.string.default_currency));
        if (currency.equals(currentCurrency)) {
            return -1;
        }
        if (currency.isEmpty() || currency.length() != 3) {
            currencyEt.setError("Currency should have 3 characters! (Ex: EUR, USD, LEI)");
            return 0;
        }
        return 1;
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
                                // Toast.makeText(getApplicationContext(), getString(R.string.preference_save_succes), Toast.LENGTH_SHORT).show();
                            } else Log.d("FIREBASE", task.getException().getMessage());
                        }
                    });
        }
    }

    private void addNameToSharedPreferences(String name) {
        SharedPreferences.Editor editor = preferences.edit();
        editor
                .putString(RegisterFragment.NAME_KEY, name)
                .apply();

    }

    private void addGenderToSharedPreferences(String gender) {
        SharedPreferences.Editor editor = preferences.edit();
        editor
                .putString(GENDER_KEY, gender)
                .apply();
    }

    private void addCurrencyToPreferenceFiles(String currency) {
        SharedPreferences.Editor editor = preferences.edit();
        editor
                .putString(CURRENCY_KEY, currency)
                .apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            try {
                Uri imageUri = data.getData() ;
                Bitmap bitmap =
                        MediaStore.Images.Media.getBitmap ( this .getContentResolver() , imageUri) ;
                imageIB.setImageBitmap( bitmap ) ;
                resizeImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace() ;
            }
        }
    }
    private View.OnClickListener selectPictureFromGallery() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(gallery,  PICK_IMAGE_CHOOSER_REQUEST_CODE);

            }

        };
    }

    public void resizeImage (Bitmap bitmap) {
        Bitmap resized = Bitmap. createScaledBitmap ( bitmap , imageIB.getWidth() , imageIB.getHeight() , true ) ;
        imageIB .setImageBitmap(resized) ;
    }
}


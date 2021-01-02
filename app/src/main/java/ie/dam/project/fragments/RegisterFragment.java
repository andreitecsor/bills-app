package ie.dam.project.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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

import ie.dam.project.BeginActivity;
import ie.dam.project.DashboardActivity;
import ie.dam.project.R;

public class RegisterFragment extends Fragment {
    public static final String NAME_KEY = "name";
    public static final String SHARED_PREF_FILE_EXTENSION = "_profile_shared_pref";

    private Button loginNowButton;
    private Button registerButton;
    private TextInputEditText nameET;
    private TextInputEditText emailET;
    private TextInputEditText passwordET;
    private TextInputEditText confirmPasswordET;

    private FirebaseAuth fbAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private SharedPreferences preferences;

    private String name;



    public RegisterFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fbAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initialiseComponents(view);
        loginNowButton.setOnClickListener(loginOnClickListener());
        registerButton.setOnClickListener(registerOnClickListener());
        return view;
    }

    private void initialiseComponents(View view) {

        authStateListener = getAuthStateListener();

        fbAuth.addAuthStateListener(authStateListener);

        loginNowButton = view.findViewById(R.id.frg_register_button_login);
        registerButton = view.findViewById(R.id.frg_register_button_register);
        nameET = view.findViewById(R.id.frg_register_tiet_name);
        emailET = view.findViewById(R.id.frg_register_tiet_email);
        passwordET = view.findViewById(R.id.frg_register_tiet_password);
        confirmPasswordET = view.findViewById(R.id.frg_register_tiet_password_confirm);
    }

    private FirebaseAuth.AuthStateListener getAuthStateListener() {
        return new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    BeginActivity beginActivity = (BeginActivity) getContext();
                    if (beginActivity != null) {
                        createLoginFragment(beginActivity);
                        Toast.makeText(getContext(), getString(R.string.create_user_succes),
                                Toast.LENGTH_LONG).show();
                        preferences = getContext().getSharedPreferences(FirebaseAuth.getInstance().getCurrentUser().getUid() + SHARED_PREF_FILE_EXTENSION, Context.MODE_PRIVATE);
                        addNameToSharedPreferences();
                    }

                }


            }
        };
    }

    private void addNameToSharedPreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(NAME_KEY, name).apply();
    }

    private View.OnClickListener loginOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BeginActivity beginActivity = (BeginActivity) getContext(); //poate returna null. WATCH OUT!!!
                if (beginActivity != null) {
                    createLoginFragment(beginActivity);
                }
            }
        };
    }

    private void createLoginFragment(BeginActivity beginActivity) {
        beginActivity.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.right_to_left_in,R.anim.right_to_left_out)
                .replace(R.id.act_begin_frame_layout,
                        new LoginFragment()).commit();
    }

    private View.OnClickListener registerOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameET.getText().toString();
                //region Declarations
                String email = emailET.getText().toString().trim();
                String password = passwordET.getText().toString();
                String confirmPass = confirmPasswordET.getText().toString();
                //endregion Declarations
                //region Validations
                if (TextUtils.isEmpty(name)) {
                    nameET.setError(getString(R.string.begin_register_name_error));
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    emailET.setError(getString(R.string.begin_email_empty));
                    return;
                }
                if (!isEmailValid(email)) {
                    emailET.setError(getString(R.string.begin_register_invalid_email_error));
                    return;
                }

                if (password.length() < 8) {
                    passwordET.setError(getString(R.string.begin_register_password_len_error));
                    return;
                }
                if (TextUtils.isEmpty(confirmPass)) {
                    confirmPasswordET.setError(getString(R.string.begin_register_confirmPassword_empty_error));
                    return;
                }
                if (!password.equals(confirmPass)) {
                    confirmPasswordET.setError(getString(R.string.begin_register_confirmPassword_noMatch_error));
                    return;
                }
                //endregion Validations
                createUser(email, password);

            }
        };
    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void createUser(String email, String password) {
        fbAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(getContext(), getString(R.string.create_account_failed) + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}


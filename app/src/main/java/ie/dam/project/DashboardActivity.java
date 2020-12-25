package ie.dam.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;
import java.util.List;

import ie.dam.project.data.domain.Bill;
import ie.dam.project.data.service.BillService;
import ie.dam.project.fragments.RegisterFragment;
import ie.dam.project.util.asynctask.Callback;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseUser currentUser;
    private CardView billCardButton;
    private CardView profileCardButton;
    private CardView preferencesCardButton;
    private CardView logoutCardButton;
    private TextView hiUser;
    private BillService billService;
    private List<Bill> billList = new ArrayList<>();

    private SharedPreferences preferences;
    private String name;

    //TODO:
    // 1. CREATE A QUERY TO FIND THE NUMBER OF ALL UNPAYED BILLS AND THEIR TOTAL SUM
    // 2. CREATE A QUERY TO FIND THE NUMBER OF ALL OVERDUE BILLS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        initialiseComponents();
        billService = new BillService(getApplicationContext());
        billService.getAll(getAllBills());
        System.out.println(billList);
        updateUser(name,null);


    }

    @Override
    protected void onResume() {
        super.onResume();
        hiUser.setText(getString(R.string.dashboard_hi_user, preferences.getString(RegisterFragment.NAME_KEY,getString(R.string.preference_name_default))));


    }

    private void initialiseComponents() {
        //Preferences
        preferences=getSharedPreferences(RegisterFragment.SHARED_PREF_FILE,MODE_PRIVATE);
        billCardButton = findViewById(R.id.act_dashboard_card_bills);
        billCardButton.setOnClickListener(goToBillsActivity());

        profileCardButton = findViewById(R.id.act_dashboard_card_profile);
        profileCardButton.setOnClickListener(goToProfileActivity());

        preferencesCardButton = findViewById(R.id.act_dashboard_card_preferences);
        preferencesCardButton.setOnClickListener(goToPreferencesActivity());

        logoutCardButton = findViewById(R.id.act_dashboard_card_users);
        logoutCardButton.setOnClickListener(logoutClickEvent());

        hiUser = findViewById(R.id.act_dashboard_tv_hi_user);
        name=preferences.getString(RegisterFragment.NAME_KEY,getString(R.string.preference_name_default));
        hiUser.setText(getString(R.string.dashboard_hi_user, name));
    }


    private View.OnClickListener goToBillsActivity() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BillActivity.class);
                startActivity(intent);
            }
        };
    }

    private View.OnClickListener goToProfileActivity() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);


            }
        };
    }

    private View.OnClickListener goToPreferencesActivity() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PreferenceActivity.class);
               startActivity(intent);
            }
        };
    }

    private View.OnClickListener logoutClickEvent() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
//                startActivity(intent);

                AlertDialog dialog = getAlertDialogLogout();
                dialog.show();
            }
        };
    }

    private AlertDialog getAlertDialogLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
        builder.setMessage(R.string.dashboard_logout_message);
        builder.setTitle(R.string.dashboard_logout_title);
        builder.setPositiveButton(R.string.logout_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });
        builder.setNegativeButton(R.string.logout_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), BeginActivity.class));
        finish();
    }




    private Callback<List<Bill>> getAllBills() {
        return new Callback<List<Bill>>() {
            @Override
            public void runResultOnUiThread(List<Bill> result) {
                if (result != null) {
                    billList.clear();
                    billList.addAll(result);
                    //TODO: populate brief
                }
            }
        };
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
                            } else Log.d("ERROR", task.getException().getMessage());
                        }
                    });
        }

    }

}
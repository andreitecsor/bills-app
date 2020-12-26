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
import java.util.Date;
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
    private TextView unpaidTv;
    private TextView overdueTv;
    private TextView amountTv;

    private BillService billService;
    private List<Bill> billList = new ArrayList<>();

    private SharedPreferences preferences;
    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        billService = new BillService(getApplicationContext());
        initialiseComponents();
        updateUser(name, null);
    }

    private void initialiseComponents() {
        //Preferences
        preferences = getSharedPreferences(RegisterFragment.SHARED_PREF_FILE, MODE_PRIVATE);

        billCardButton = findViewById(R.id.act_dashboard_card_bills);
        profileCardButton = findViewById(R.id.act_dashboard_card_profile);
        preferencesCardButton = findViewById(R.id.act_dashboard_card_preferences);
        logoutCardButton = findViewById(R.id.act_dashboard_card_users);
        unpaidTv = findViewById(R.id.act_dashboard_tv_bills_to_pay);
        overdueTv = findViewById(R.id.act_dashboard_tv_overdue);
        amountTv = findViewById(R.id.act_dashboard_tv_amount);

        billCardButton.setOnClickListener(goToBillsActivity());
        profileCardButton.setOnClickListener(goToProfileActivity());
        preferencesCardButton.setOnClickListener(goToPreferencesActivity());
        logoutCardButton.setOnClickListener(logoutClickEvent());

        hiUser = findViewById(R.id.act_dashboard_tv_hi_user);
        name = preferences.getString(RegisterFragment.NAME_KEY, getString(R.string.preference_name_default));
        hiUser.setText(getString(R.string.dashboard_hi_user, name));

        billService.getAll(overdueBillsSort());
        billService.getNoBillsByPayed(getUnpaidBills(), false);
        billService.getAmountToPay(getAmountToPay(), false);
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

    private View.OnClickListener goToBillsActivity() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BillActivity.class);
                startActivity(intent);
                finish();
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

    private Callback<List<Bill>> overdueBillsSort() {
        return new Callback<List<Bill>>() {
            @Override
            public void runResultOnUiThread(List<Bill> result) {
                if (result != null) {
                    billList.clear();
                    billList.addAll(result);
                    int overdueCount = 0;
                    Date today = new Date();
                    for (Bill bill : billList) {
                        if (bill.isPayed() == false && today.after(bill.getDueTo())) {
                            overdueCount++;
                        }
                    }
                    String updatedTv = overdueTv.getText().toString().replace("NUMBER", String.valueOf(overdueCount));
                    overdueTv.setText(updatedTv);
                }
            }
        };
    }

    private Callback<Integer> getUnpaidBills() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result >= 0) {
                    String updatedTv = unpaidTv.getText().toString().replace("NUMBER", result.toString());
                    unpaidTv.setText(updatedTv);
                }
            }
        };
    }

    private Callback<Double> getAmountToPay() {
        return new Callback<Double>() {
            @Override
            public void runResultOnUiThread(Double result) {
                if (result >= 0) {
                    String updatedTv = amountTv.getText().toString().replace("NUMBER", result.toString());
                    //TODO: cu currency-ul selectat din preference files
                    //updatedTv = amountTv.getText().toString().replace("CURRENCY", ???);
                    amountTv.setText(updatedTv);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        hiUser.setText(getString(R.string.dashboard_hi_user, preferences.getString(RegisterFragment.NAME_KEY, getString(R.string.preference_name_default))));
    }

}
package ie.dam.project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import ie.dam.project.data.domain.Bill;
import ie.dam.project.data.service.BillService;
import ie.dam.project.util.asynctask.Callback;

public class DashboardActivity extends AppCompatActivity {
    private CardView billCardButton;
    private CardView profileCardButton;
    private CardView preferencesCardButton;
    private CardView logoutCardButton;

    private BillService billService;
    private List<Bill> billList = new ArrayList<>();

    //TODO:
    // 1. CREATE A QUERY TO FIND THE NUMBER OF ALL UNPAYED BILLS AND THEIR TOTAL SUM
    // 2. CREATE A QUERY TO FIND THE NUMBER OF ALL OVERDUE BILLS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initialiseComponents();
        billService = new BillService(getApplicationContext());
        billService.getAll(getAllBills());
        System.out.println(billList);

    }

    private void initialiseComponents() {
        billCardButton = findViewById(R.id.act_dashboard_card_bills);
        billCardButton.setOnClickListener(goToBillsActivity());

        profileCardButton = findViewById(R.id.act_dashboard_card_profile);
        profileCardButton.setOnClickListener(goToProfileActivity());

        preferencesCardButton = findViewById(R.id.act_dashboard_card_preferences);
        preferencesCardButton.setOnClickListener(goToPreferencesActivity());

        logoutCardButton = findViewById(R.id.act_dashboard_card_users);
        logoutCardButton.setOnClickListener(logoutClickEvent());
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
//                Intent intent = new Intent(getApplicationContext(), PreferencesActivity.class);
//                startActivity(intent);
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
}
package ie.dam.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ie.dam.project.data.domain.Bill;
import ie.dam.project.data.service.BillService;
import ie.dam.project.util.asynctask.Callback;

public class DashboardActivity extends AppCompatActivity {
    private CardView billCardButton;
    private CardView profileCardButton;
    private CardView preferencesCardButton;
    private CardView usersCardButton;

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

        usersCardButton = findViewById(R.id.act_dashboard_card_users);
        usersCardButton.setOnClickListener(goToUsersActivity());
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
//                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
//                startActivity(intent);
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

    private View.OnClickListener goToUsersActivity() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), UsersActivity.class);
//                startActivity(intent);
            }
        };
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
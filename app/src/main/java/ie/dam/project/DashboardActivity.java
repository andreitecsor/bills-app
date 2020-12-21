package ie.dam.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;

public class DashboardActivity extends AppCompatActivity {
    //TODO:
    // 1. CREATE A QUERY TO FIND THE NUMBER OF ALL UNPAYED BILLS AND THEIR TOTAL SUM
    // 2. CREATE A QUERY TO FIND THE NUMBER OF ALL OVERDUE BILLS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

    }
}
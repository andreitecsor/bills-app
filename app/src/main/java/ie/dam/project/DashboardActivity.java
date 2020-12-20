package ie.dam.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;

public class DashboardActivity extends AppCompatActivity {
    ConstraintLayout cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        System.out.println("Dashboard");
        cl = findViewById(R.id.act_dashboard_cl_bills_btn);
        cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("MERGEE");
            }
        });
    }
}
package ie.dam.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ie.dam.project.data.domain.Bill;
import ie.dam.project.data.domain.BillShownInfo;
import ie.dam.project.data.service.BillService;
import ie.dam.project.fragments.BillListFragment;
import ie.dam.project.util.asynctask.Callback;

public class BillActivity extends AppCompatActivity {
    public static final int ADD_BILL = 101;

    private Fragment currentFragment;
    private BillService billService;
    private FloatingActionButton fabAddBill;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        initialiseComponents();
        billService = new BillService(getApplicationContext());
//        billService.getAllWithSupplierName(getAllWithSupplierName());
    }

    private void initialiseComponents() {
        setCurrentDate();
        fabAddBill = findViewById(R.id.act_bills_fab_add);
        fabAddBill.setOnClickListener(openAddEditActivity());
        openBillListFragment();
    }

    private View.OnClickListener openAddEditActivity() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddEditBillActivity.class);
                startActivityForResult(intent, ADD_BILL);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == ADD_BILL) {
                Bill newBill = (Bill) data.getSerializableExtra(AddEditBillActivity.NEW_BILL);
                billService.insert(insertBill(),newBill);
            }
        }
    }

    private void openBillListFragment() {
        currentFragment = BillListFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.act_bills_frame,
                currentFragment).commit();
    }

    private Callback<Bill> insertBill() {
        return new Callback<Bill>() {
            @Override
            public void runResultOnUiThread(Bill result) {
                if (result != null) {
                    openBillListFragment();
                }
            }
        };
    }


    private void setCurrentDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        TextView currentDate = findViewById(R.id.act_bills_tv_date);
        currentDate.setText(formatter.format(date));
    }
}
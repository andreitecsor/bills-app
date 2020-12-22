package ie.dam.project;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ie.dam.project.data.domain.Bill;
import ie.dam.project.data.domain.BillShownInfo;
import ie.dam.project.data.domain.BillType;
import ie.dam.project.data.domain.Supplier;
import ie.dam.project.data.domain.SupplierWithBills;
import ie.dam.project.data.service.BillService;
import ie.dam.project.data.service.SupplierService;
import ie.dam.project.fragments.BillListFragment;
import ie.dam.project.util.asynctask.Callback;
import ie.dam.project.util.converters.DateConverter;

public class BillActivity extends AppCompatActivity {
    private Fragment currentFragment;
    private BillService billService;
    private List<BillShownInfo> billShownInfos = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        initialiseComponents();
        billService = new BillService(getApplicationContext());
        billService.getAllWithSupplierName(getAllWithSupplierName());
    }

    private void initialiseComponents() {
        setCurrentDate();
    }

    private Callback<List<BillShownInfo>> getAllWithSupplierName() {
        return new Callback<List<BillShownInfo>>() {
            @Override
            public void runResultOnUiThread(List<BillShownInfo> result) {
                if (result != null) {
                    billShownInfos.clear();
                    billShownInfos.addAll(result);
                    System.out.println(billShownInfos);
                    openBillListFragment(billShownInfos);

                }
            }
        };
    }

    private void openBillListFragment(List<BillShownInfo> billShownInfos) {
        currentFragment = BillListFragment.newInstance(billShownInfos);
        getSupportFragmentManager().beginTransaction().replace(R.id.act_bills_frame,
                currentFragment).commit();
    }


    private void setCurrentDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        TextView currentDate = findViewById(R.id.act_bills_tv_date);
        currentDate.setText(formatter.format(date));
    }
}
package ie.dam.project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import ie.dam.project.data.domain.BillShownInfo;
import ie.dam.project.data.service.BillService;
import ie.dam.project.util.adapters.BillAdapter;
import ie.dam.project.util.asynctask.Callback;

public class FilterActivity extends AppCompatActivity {
    private List<BillShownInfo> billShownInfos = new ArrayList<>();

    private BillService billService;

    //Activity Components
    private TextView noFoundTv;
    private Button filterButton;
    private RecyclerView recyclerView;
    private BillAdapter billAdapter;
    private TextView totalAmountTv;


    //Dialog Components
    private AlertDialog filterDialog;
    private Button applyButton;
    private TextInputEditText minAmountTiet;
    private TextInputEditText maxAmountTiet;
    private Switch recurrentSwitch;
    private Switch paidSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        billService = new BillService(getApplicationContext());
        initialiseComponents();
    }

    private void initialiseComponents() {
        noFoundTv = findViewById(R.id.act_filter_tv_no_found);
        totalAmountTv = findViewById(R.id.act_filter_tv_amout_paid);
        filterButton = findViewById(R.id.act_filter_button);
        recyclerView = findViewById(R.id.act_filter_rv);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });
        billService.getAllWithSupplierName(getAllBillShownInfos());
        billService.getAmountToPay(getTotalAmountPaid(), true);
    }

    private Callback<Double> getTotalAmountPaid() {
        return new Callback<Double>() {
            @Override
            public void runResultOnUiThread(Double result) {
                if (result >= 0) {
                    //TODO: Currency based on preference files
                    String updatedTv = totalAmountTv.getText().toString().replace("NUMBER", String.valueOf(result));
                    totalAmountTv.setText(updatedTv);
                }
            }
        };
    }

    private Callback<List<BillShownInfo>> getAllBillShownInfos() {
        return new Callback<List<BillShownInfo>>() {
            @Override
            public void runResultOnUiThread(List<BillShownInfo> result) {
                if (result != null) {
                    noFoundTv.setVisibility(View.INVISIBLE);
                    billShownInfos.clear();
                    billShownInfos.addAll(result);
                    billAdapter = new BillAdapter(billShownInfos, null);
                    recyclerView.setAdapter(billAdapter);
                } else {
                    noFoundTv.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    private void showFilterDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(this); //sau this
        View view = layoutInflater.inflate(R.layout.dialog_filter, null);
        applyButton = view.findViewById(R.id.dialog_filter_button_apply);
        minAmountTiet = view.findViewById(R.id.dialog_filter_tiet_min_amount);
        maxAmountTiet = view.findViewById(R.id.dialog_filter_tiet_max_amount);
        recurrentSwitch = view.findViewById(R.id.dialog_filter_switch_recurrent);
        paidSwitch = view.findViewById(R.id.dialog_filter_switch_paid);
        applyButton.setOnClickListener(applyFilterAction());
        filterDialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        filterDialog.show();
    }

    private View.OnClickListener applyFilterAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double min, max;
                if (!minAmountTiet.getText().toString().isEmpty()) {
                    min = Double.parseDouble(minAmountTiet.getText().toString());
                } else {
                    min = 0;
                }
                if (!maxAmountTiet.getText().toString().isEmpty()) {
                    max = Double.parseDouble(maxAmountTiet.getText().toString());
                } else {
                    max = 999999999;
                }
                if (validate(min, max)) {
                    billService.getFilteredBills(getFilteredBills(), min, max, paidSwitch.isChecked(), recurrentSwitch.isChecked());
                    filterDialog.cancel();
                }
            }
        };
    }

    private Callback<List<BillShownInfo>> getFilteredBills() {
        return new Callback<List<BillShownInfo>>() {
            @Override
            public void runResultOnUiThread(List<BillShownInfo> result) {
                if (result != null && result.size() > 0) {
                    noFoundTv.setVisibility(View.INVISIBLE);
                    billShownInfos.clear();
                    billShownInfos.addAll(result);
                    billAdapter = new BillAdapter(billShownInfos, null);
                    recyclerView.setAdapter(billAdapter);
                } else {
                    recyclerView.setAdapter(null);
                    noFoundTv.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    private boolean validate(double min, double max) {
        if (min < 0 || max < min || max < 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.invalid_amounts), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),BillActivity.class));
        finish();
    }
}
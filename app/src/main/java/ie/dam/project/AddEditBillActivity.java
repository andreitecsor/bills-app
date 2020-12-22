package ie.dam.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ie.dam.project.data.domain.Bill;
import ie.dam.project.data.domain.BillType;
import ie.dam.project.data.domain.Supplier;
import ie.dam.project.data.service.SupplierService;
import ie.dam.project.util.asynctask.Callback;
import ie.dam.project.util.converters.DateConverter;

public class AddEditBillActivity extends AppCompatActivity {
    public static final String NEW_BILL = "NEW_BILL";

    private Spinner typeSpinner;
    private Spinner supplierSpinner;
    private TextView selectedDateTv;
    private DatePickerDialog.OnDateSetListener setDateListener;
    private EditText amountTv;
    private Button saveButton;
    private Switch recurrentSwitch;
    private Switch payedSwitch;

    private SupplierService supplierService;
    private List<Supplier> supplierList = new ArrayList<>();
    private List<String> supplierNames = new ArrayList<>();
    private Map<String, Long> supplierMap = new HashMap<>();
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_bill);
        supplierService = new SupplierService(getApplicationContext());
        intent = getIntent();
        initialiseComponents();
        supplierService.getAll(getAllSuppliers());
    }

    private void initialiseComponents() {
        typeSpinner = findViewById(R.id.act_aebill_spinner_type);
        supplierSpinner = findViewById(R.id.act_aebill_spinner_supplier);
        selectedDateTv = findViewById(R.id.act_aebill_tv_display_date);
        amountTv = findViewById(R.id.act_aebill_et_amount);
        saveButton = findViewById(R.id.act_aebil_button_save);
        payedSwitch = findViewById(R.id.act_aebill_switch_payed);
        recurrentSwitch = findViewById(R.id.act_aebill_switch_recurrent);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:Validare campuri
                Bill bill = new Bill(DateConverter.toDate(selectedDateTv.getText().toString()),
                        Double.parseDouble(amountTv.getText().toString()),
                        payedSwitch.isChecked(),
                        recurrentSwitch.isChecked(),
                        typeSpinner.getSelectedItem().toString(),
                        supplierMap.get(supplierSpinner.getSelectedItem().toString()));
                intent.putExtra(NEW_BILL, bill);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        selectedDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEditBillActivity.this, R.style.CustomDatePicker, setDateListener, year, month, day);
                datePickerDialog.show();
            }
        });
        setDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Date date = new GregorianCalendar(year, month, dayOfMonth).getTime();
                selectedDateTv.setText(DateConverter.toString(date));
            }
        };
        typeSpinner.setAdapter(new ArrayAdapter<BillType>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, BillType.values()));
    }

    private Callback<List<Supplier>> getAllSuppliers() {
        return new Callback<List<Supplier>>() {
            @Override
            public void runResultOnUiThread(List<Supplier> result) {
                if (result != null) {
                    supplierList.clear();
                    supplierList.addAll(result);
                    for (Supplier supplier : supplierList) {
                        supplierNames.add(supplier.getName());
                        supplierMap.put(supplier.getName(), supplier.getSupplierId());
                    }
                    supplierSpinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, supplierNames));
                }
            }
        };
    }
}
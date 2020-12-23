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
import android.widget.Toast;

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
    public static final String PROCESSED_BILL = "P_BILL";

    private Spinner typeSpinner;
    private Spinner supplierSpinner;
    private TextView selectedDateTv;
    private DatePickerDialog.OnDateSetListener setDateListener;
    private EditText amountEt;
    private Button saveButton;
    private Switch recurrentSwitch;
    private Switch payedSwitch;

    private SupplierService supplierService;
    private List<Supplier> supplierList = new ArrayList<>();
    private List<String> supplierNames = new ArrayList<>();
    private Map<String, Long> nameIdMap = new HashMap<>();
    private Map<Long, String> idNameMap = new HashMap<>();
    private Bill auxBill;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_bill);
        supplierService = new SupplierService(getApplicationContext());
        initialiseComponents();
        intent = getIntent();
        supplierService.getAll(getSuppliersMapping());
    }

    private void initialiseComponents() {
        typeSpinner = findViewById(R.id.act_aebill_spinner_type);
        supplierSpinner = findViewById(R.id.act_aebill_spinner_supplier);
        selectedDateTv = findViewById(R.id.act_aebill_tv_display_date);
        amountEt = findViewById(R.id.act_aebill_et_amount);
        saveButton = findViewById(R.id.act_aebil_button_save);
        payedSwitch = findViewById(R.id.act_aebill_switch_payed);
        recurrentSwitch = findViewById(R.id.act_aebill_switch_recurrent);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    auxBill.setDueTo(DateConverter.toDate(selectedDateTv.getText().toString()));
                    auxBill.setAmount(Double.parseDouble(amountEt.getText().toString()));
                    auxBill.setPayed(payedSwitch.isChecked());
                    auxBill.setRecurrent(recurrentSwitch.isChecked());
                    auxBill.setType(typeSpinner.getSelectedItem().toString());
                    auxBill.setSupplierId(nameIdMap.get(supplierSpinner.getSelectedItem().toString()));
                    intent.putExtra(PROCESSED_BILL, auxBill);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        selectedDateTv.setOnClickListener(openDateDialogPicker());
        setDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Date date = new GregorianCalendar(year, month, dayOfMonth).getTime();
                selectedDateTv.setText(DateConverter.toString(date));
            }
        };
        typeSpinner.setAdapter(new ArrayAdapter<BillType>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, BillType.values()));
    }

    private View.OnClickListener openDateDialogPicker() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddEditBillActivity.this, R.style.CustomDatePicker, setDateListener, year, month, day);
                datePickerDialog.show();
            }
        };
    }

    private boolean validate() {
        if (selectedDateTv.getText().toString().equals(getString(R.string.bills_date))) {
            Toast.makeText(getApplicationContext(), R.string.validate_pick_date, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (amountEt.getText() == null || amountEt.getText().toString().isEmpty() || Double.parseDouble(amountEt.getText().toString()) < 0) {
            Toast.makeText(getApplicationContext(), R.string.validate_amount, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private Callback<List<Supplier>> getSuppliersMapping() {
        return new Callback<List<Supplier>>() {
            @Override
            public void runResultOnUiThread(List<Supplier> result) {
                if (result != null) {
                    supplierList.clear();
                    supplierList.addAll(result);
                    for (Supplier supplier : supplierList) {
                        supplierNames.add(supplier.getName());
                        nameIdMap.put(supplier.getName(), supplier.getSupplierId());
                        idNameMap.put(supplier.getSupplierId(), supplier.getName());
                    }
                    supplierSpinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, supplierNames));

                    if (intent.hasExtra(BillActivity.BILL_TO_UPDATE)) {
                        auxBill = (Bill) intent.getSerializableExtra(BillActivity.BILL_TO_UPDATE);
                        setComponentsValues(auxBill);
                    } else {
                        auxBill = new Bill();
                    }
                }
            }
        };
    }


    private void setComponentsValues(Bill auxBill) {
        setTypeOnSpinner(auxBill);
        setSupplierOnSpinner(auxBill);
        selectedDateTv.setText(DateConverter.toString(auxBill.getDueTo()));
        payedSwitch.setChecked(auxBill.isPayed());
        recurrentSwitch.setChecked(auxBill.isRecurrent());
        amountEt.setText(String.valueOf(auxBill.getAmount()));
    }

    private void setTypeOnSpinner(Bill bill) {
        ArrayAdapter spinnerAdapter = (ArrayAdapter) typeSpinner.getAdapter();
        for (int i = 0; i < spinnerAdapter.getCount(); i++) {
            String item = spinnerAdapter.getItem(i).toString();
            if (item != null && item.equals(bill.getType())) {
                typeSpinner.setSelection(i);
                break;
            }
        }
    }

    private void setSupplierOnSpinner(Bill bill) {
        ArrayAdapter spinnerAdapter = (ArrayAdapter) supplierSpinner.getAdapter();
        for (int i = 0; i < spinnerAdapter.getCount(); i++) {
            String item = spinnerAdapter.getItem(i).toString();
            if (item != null && item.equals(idNameMap.get(bill.getSupplierId()))) {
                supplierSpinner.setSelection(i);
                break;
            }
        }
    }
}
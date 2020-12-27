package ie.dam.project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
    public static final String SUPPLIER_TO_UPDATE = "T_P_SUPPLIER";
    public static final int INSERT_OPERATION = 201;
    public static final int UPDATE_DELETE_OPERATION = 202;


    private Spinner typeSpinner;
    private Spinner supplierSpinner;
    private TextView selectedDateTv;
    private DatePickerDialog.OnDateSetListener setDateListener;
    private EditText amountEt;
    private Button saveButton;
    private Button supplierButton;
    private Switch recurrentSwitch;
    private Switch payedSwitch;

    private SupplierService supplierService;
    private List<Supplier> supplierList = new ArrayList<>();
    private List<String> supplierNames = new ArrayList<>();
    private Map<String, Long> nameIdMap = new HashMap<>();
    private Map<Long, String> idNameMap = new HashMap<>();
    private Bill auxBill;
    private Intent billActIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_bill);
        supplierService = new SupplierService(getApplicationContext());
        billActIntent = getIntent();
        initialiseComponents();
    }

    private void initialiseComponents() {
        typeSpinner = findViewById(R.id.act_aebill_spinner_type);
        supplierSpinner = findViewById(R.id.act_aebill_spinner_supplier);
        selectedDateTv = findViewById(R.id.act_aebill_tv_display_date);
        amountEt = findViewById(R.id.act_aebill_et_amount);
        saveButton = findViewById(R.id.act_aebil_button_save);
        supplierButton = findViewById(R.id.act_aebill_button_aesupplier);
        payedSwitch = findViewById(R.id.act_aebill_switch_payed);
        recurrentSwitch = findViewById(R.id.act_aebill_switch_recurrent);
        saveButton.setOnClickListener(saveBillAction());
        selectedDateTv.setOnClickListener(openDateDialogPicker());
        setDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Date date = new GregorianCalendar(year, month, dayOfMonth).getTime();
                selectedDateTv.setText(DateConverter.toString(date));
            }
        };
        typeSpinner.setAdapter(new ArrayAdapter<BillType>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, BillType.values()));
        supplierService.getAll(getSuppliersMapping());
    }

    private View.OnClickListener saveBillAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    auxBill.setDueTo(DateConverter.toDate(selectedDateTv.getText().toString()));
                    auxBill.setAmount(Double.parseDouble(amountEt.getText().toString()));
                    auxBill.setPayed(payedSwitch.isChecked());
                    auxBill.setRecurrent(recurrentSwitch.isChecked());
                    auxBill.setType(typeSpinner.getSelectedItem().toString());
                    auxBill.setSupplierId(nameIdMap.get(supplierSpinner.getSelectedItem().toString()));
                    billActIntent.putExtra(PROCESSED_BILL, auxBill);
                    setResult(RESULT_OK, billActIntent);
                    finish();
                }
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

    private Callback<List<Supplier>> getSuppliersMapping() {
        return new Callback<List<Supplier>>() {
            @Override
            public void runResultOnUiThread(List<Supplier> result) {
                if (result != null && result.size() > 0) {
                    supplierList.clear();
                    supplierNames.clear();
                    nameIdMap.clear();
                    idNameMap.clear();
                    supplierList.addAll(result);
                    for (Supplier supplier : supplierList) {
                        supplierNames.add(supplier.getName());
                        nameIdMap.put(supplier.getName(), supplier.getSupplierId());
                        idNameMap.put(supplier.getSupplierId(), supplier.getName());
                    }
                    supplierSpinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, supplierNames));
                    addOrEditCheck();
                } else {
                    AlertDialog alertDialog = getSupplierAlertDialog();
                    alertDialog.show();
                }
            }
        };
    }

    private AlertDialog getSupplierAlertDialog() {
        return new AlertDialog.Builder(AddEditBillActivity.this)
                .setTitle(R.string.no_suppliers)
                .setMessage(R.string.no_supplier_found)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), AddEditSupplierActivity.class);
                        startActivityForResult(intent, INSERT_OPERATION);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create();
    }

    private void addOrEditCheck() {
        if (billActIntent.hasExtra(BillActivity.BILL_TO_UPDATE)) {
            auxBill = (Bill) billActIntent.getSerializableExtra(BillActivity.BILL_TO_UPDATE);
            setComponentsValues(auxBill);
            supplierButton.setText(getString(R.string.aebill_edit_supplier));
            saveButton.setText(getString(R.string.profile_save_changes));
            supplierButton.setOnClickListener(editSupplierAction());
        } else {
            auxBill = new Bill();
            supplierButton.setOnClickListener(insertSupplierAction());
        }
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

    private View.OnClickListener insertSupplierAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addBillIntent = new Intent(getApplicationContext(), AddEditSupplierActivity.class);
                startActivityForResult(addBillIntent, INSERT_OPERATION);
            }
        };
    }

    private View.OnClickListener editSupplierAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editBillIntent = new Intent(getApplicationContext(), AddEditSupplierActivity.class);
                editBillIntent.putExtra(SUPPLIER_TO_UPDATE, auxBill.getSupplierId());
                startActivityForResult(editBillIntent, UPDATE_DELETE_OPERATION);
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Supplier supplier = (Supplier) data.getSerializableExtra(AddEditSupplierActivity.PROCESSED_SUPPLIER);
            if (requestCode == INSERT_OPERATION) {
                supplierService.insert(insertSupplier(), supplier);
            }
            if (requestCode == UPDATE_DELETE_OPERATION) {
                boolean toDelete = data.getBooleanExtra(AddEditSupplierActivity.TO_BE_DELETED, false);
                boolean toInsert = data.getBooleanExtra(AddEditSupplierActivity.TO_BE_INSERTED, false);
                if (toDelete) {
                    supplierService.delete(deleteSupplier(), supplier);
                    return;
                }
                if (toInsert) {
                    supplierService.insert(insertSupplier(), supplier);
                    return;
                }
                supplierService.update(updateSupplier(), supplier);
                return;
            }
        }
    }

    private Callback<Supplier> insertSupplier() {
        return new Callback<Supplier>() {
            @Override
            public void runResultOnUiThread(Supplier result) {
                if (result != null) {
                    supplierService.getAll(getSuppliersMapping());
                }
            }
        };
    }

    private Callback<Supplier> updateSupplier() {
        return new Callback<Supplier>() {
            @Override
            public void runResultOnUiThread(Supplier result) {
                if (result != null) {
                    supplierService.getAll(getSuppliersMapping());
                }
            }
        };
    }

    private Callback<Integer> deleteSupplier() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result != -1) {
                    finish();
                }
            }
        };
    }
}
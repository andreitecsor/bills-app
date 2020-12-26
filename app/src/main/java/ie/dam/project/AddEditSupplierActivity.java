package ie.dam.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import ie.dam.project.data.domain.Supplier;
import ie.dam.project.data.service.SupplierService;
import ie.dam.project.util.asynctask.Callback;

public class AddEditSupplierActivity extends AppCompatActivity {
    public static final String PROCESSED_SUPPLIER = "P_SUP";
    public static final String TO_BE_DELETED = "T_B_DELETE";
    public static final String TO_BE_INSERTED = "T_B_INSERTED";

    private TextInputEditText nameTiet;
    private TextInputEditText emailTiet;
    private TextInputEditText phoneTiet;
    private Button buttonSave;
    private Button buttonDelete;
    private Supplier auxSupplier;

    private SupplierService supplierService;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_supplier);
        supplierService = new SupplierService(getApplicationContext());
        intent = getIntent();
        initialiseComponents();
    }

    private void initialiseComponents() {
        nameTiet = findViewById(R.id.act_aesupplier_tiet_name);
        emailTiet = findViewById(R.id.act_aesupplier_tiet_email);
        phoneTiet = findViewById(R.id.act_aesupplier_tiet_phone);
        buttonSave = findViewById(R.id.act_aesupplier_button_save);
        buttonSave.setOnClickListener(saveSupplierAction());
        buttonDelete = findViewById(R.id.act_aesupplier_button_delete);
        buttonDelete.setOnClickListener(deleteSupplierAction());
        addOrEditCheck();
    }

    private void addOrEditCheck() {
        if (intent.hasExtra(AddEditBillActivity.SUPPLIER_TO_UPDATE)) {
            buttonDelete.setVisibility(View.VISIBLE);
            buttonSave.setText(getString(R.string.profile_save_changes));
            long id = (long) intent.getLongExtra(AddEditBillActivity.SUPPLIER_TO_UPDATE, -1);
            supplierService.getById(getSupplierById(), id);
            return;
        } else {
            auxSupplier = new Supplier();
        }
    }

    private Callback<Supplier> getSupplierById() {
        return new Callback<Supplier>() {
            @Override
            public void runResultOnUiThread(Supplier result) {
                if (result != null) {
                    auxSupplier = result;
                    setComponentsValues(auxSupplier);
                }
            }
        };
    }

    private void setComponentsValues(Supplier supplier) {
        nameTiet.setText(supplier.getName());
        emailTiet.setText(supplier.getEmail());
        phoneTiet.setText(supplier.getPhone());
    }

    private View.OnClickListener deleteSupplierAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra(PROCESSED_SUPPLIER, auxSupplier);
                intent.putExtra(TO_BE_DELETED, true);
                setResult(RESULT_OK, intent);
                finish();
            }
        };
    }

    private View.OnClickListener saveSupplierAction() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    auxSupplier.setName(nameTiet.getText().toString().toUpperCase());
                    auxSupplier.setPhone(phoneTiet.getText().toString());
                    auxSupplier.setEmail(emailTiet.getText().toString());
                    intent.putExtra(PROCESSED_SUPPLIER, auxSupplier);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        };
    }

    private boolean validate() {
        if (nameTiet.getText() == null || nameTiet.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.invalid_name, Toast.LENGTH_SHORT).show();
            return false;
        }
        String email = emailTiet.getText().toString().trim();
        if (email == null || email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(), R.string.invalid_email, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (phoneTiet.getText() == null || emailTiet.getText().toString().isEmpty() || phoneTiet.getText().toString().trim().length() < 3) {
            Toast.makeText(getApplicationContext(), R.string.invalid_phone, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
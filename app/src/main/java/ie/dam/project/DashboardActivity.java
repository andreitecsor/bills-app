package ie.dam.project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import ie.dam.project.data.DatabaseManager;
import ie.dam.project.data.domain.Bill;
import ie.dam.project.data.domain.Supplier;
import ie.dam.project.data.domain.SupplierWithBills;
import ie.dam.project.data.service.BillService;
import ie.dam.project.data.service.SupplierService;
import ie.dam.project.fragments.RegisterFragment;
import ie.dam.project.util.JSON.HttpManager;
import ie.dam.project.util.JSON.SupplierJsonParser;
import ie.dam.project.util.asynctask.AsyncTaskRunner;
import ie.dam.project.util.asynctask.Callback;

public class DashboardActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private CardView billCardButton;
    private CardView profileCardButton;
    private CardView preferencesCardButton;
    private CardView logoutCardButton;
    private TextView hiUser;
    private TextView unpaidTv;
    private TextView overdueTv;
    private TextView amountTv;
    private TextView overallTv;
    private ProgressBar progressBar;

    private SupplierService supplierService;
    private BillService billService;
    private List<Bill> billList = new ArrayList<>();

    private SharedPreferences preferences;
    private String name;

    private static final AsyncTaskRunner asyncTaskRunner = new AsyncTaskRunner();
    private static final String URL_JSON = "https://jsonkeeper.com/b/VWCL";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        billService = new BillService(getApplicationContext());
        supplierService = new SupplierService(getApplicationContext());
        preferences = getSharedPreferences(currentUser.getUid() + RegisterFragment.SHARED_PREF_FILE_EXTENSION, MODE_PRIVATE);
        addNameToSharedPreferences();
        initialiseComponents();

    }

    private void initialiseComponents() {
        getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.overcast_white));
        getSuppliersWithBillsFromNetwork();
        billCardButton = findViewById(R.id.act_dashboard_card_bills);
        profileCardButton = findViewById(R.id.act_dashboard_card_profile);
        preferencesCardButton = findViewById(R.id.act_dashboard_card_preferences);
        logoutCardButton = findViewById(R.id.act_dashboard_card_users);
        unpaidTv = findViewById(R.id.act_dashboard_tv_bills_to_pay);
        overdueTv = findViewById(R.id.act_dashboard_tv_overdue);
        amountTv = findViewById(R.id.act_dashboard_tv_amount);
        overallTv = findViewById(R.id.act_dashboard_tv_overall);
        progressBar = findViewById(R.id.act_dashboard_progress_bar);
        progressBar.setProgress(0);

        billCardButton.setOnClickListener(goToBillsActivity());
        profileCardButton.setOnClickListener(goToProfileActivity());
        preferencesCardButton.setOnClickListener(goToPreferencesActivity());
        logoutCardButton.setOnClickListener(logoutClickEvent());

        hiUser = findViewById(R.id.act_dashboard_tv_hi_user);
        //  name = preferences.getString(RegisterFragment.NAME_KEY, getString(R.string.preference_name_default));
        hiUser.setText(getString(R.string.dashboard_hi_user, name));

        billService.getAll(overdueBillsSort());
        billService.getNoBillsByPaymentType(getUnpaidBills(), false);
        billService.getAmountToPay(getAmountToPay(), false);
    }


    private View.OnClickListener goToBillsActivity() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BillActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.top_to_bot_in, R.anim.top_to_bot_out);
            }
        };
    }

    private View.OnClickListener goToProfileActivity() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.bot_to_top_in, R.anim.bot_to_top_out);
            }
        };
    }

    private View.OnClickListener goToPreferencesActivity() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PreferenceActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.top_to_bot_in, R.anim.top_to_bot_out);
            }
        };
    }

    private View.OnClickListener logoutClickEvent() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = getAlertDialogLogout();
                dialog.show();
            }
        };
    }

    private AlertDialog getAlertDialogLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
        builder.setMessage(R.string.dashboard_logout_message);
        builder.setTitle(R.string.dashboard_logout_title);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout();
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        DatabaseManager.disableDataBaseManager();
        Toast.makeText(getApplicationContext(), "You signed out succesfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(), BeginActivity.class));
        finish();
    }

    private Callback<List<Bill>> overdueBillsSort() {
        return new Callback<List<Bill>>() {
            @Override
            public void runResultOnUiThread(List<Bill> result) {
                if (result != null) {
                    billList.clear();
                    billList.addAll(result);
                    int overdueCount = 0;
                    int paid = 0;
                    int notPaid = 0;
                    Date today = new Date();
                    for (Bill bill : billList) {
                        if (bill.isPaid() == false && today.after(bill.getDueTo())) {
                            overdueCount++;
                        }
                        if (bill.isPaid()) {
                            paid++;
                        } else {
                            notPaid++;
                        }
                        System.out.println("PAID:" + paid);
                        System.out.println("notPAID:" + notPaid);
                    }
                    String updatedTv = overdueTv.getText().toString().replace("NUMBER", String.valueOf(overdueCount));
                    overdueTv.setText(updatedTv);
                    updateProgressBar(paid, notPaid);
                }
            }
        };
    }

    private void updateProgressBar(int paid, int notPaid) {
        progressBar.setProgress(0);
        int progress = (notPaid == 0) ? 100 : (paid * (100 / (paid + notPaid)));
        System.out.println("PROGRESS:" + progress);
        progressBar.setProgress(progress);
        String toBeReplaced = overallTv.getText().toString();
        String updatedTv = toBeReplaced.replace("PAID", String.valueOf(paid));
        updatedTv = updatedTv.replace("TOTAL", String.valueOf(notPaid + paid));
        overallTv.setText(updatedTv);
    }

    private Callback<Integer> getUnpaidBills() {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result >= 0) {
                    String updatedTv = unpaidTv.getText().toString().replace("NUMBER", result.toString());
                    unpaidTv.setText(updatedTv);
                }
            }
        };
    }

    private Callback<Double> getAmountToPay() {
        return new Callback<Double>() {
            @Override
            public void runResultOnUiThread(Double result) {
                if (result >= 0) {
                    String updatedTv = amountTv.getText().toString().replace("NUMBER", result.toString());
                    updatedTv = updatedTv.replace("CURRENCY", preferences.getString(PreferenceActivity.CURRENCY_KEY, getString(R.string.default_currency)));
                    amountTv.setText(updatedTv);
                }
            }
        };
    }

    private void addNameToSharedPreferences() {
        if (currentUser != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor
                    .putString(RegisterFragment.NAME_KEY, currentUser.getDisplayName())
                    .apply();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        hiUser.setText(getString(R.string.dashboard_hi_user, preferences.getString(RegisterFragment.NAME_KEY, getString(R.string.preference_name_default))));
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        billService.getAmountToPay(getAmountToPay(), false);
    }

    private void getSuppliersWithBillsFromNetwork() {
        Callable<String> asyncOperation = new HttpManager(URL_JSON);
        Callback<String> mainThreadOperation = getMainThreadOperationForJSON();
        asyncTaskRunner.executeAsync(asyncOperation, mainThreadOperation);
    }

    private Callback<String> getMainThreadOperationForJSON() {
        return new Callback<String>() {
            @Override
            public void runResultOnUiThread(String result) {
                List<SupplierWithBills> suppliersWithBills = SupplierJsonParser.fromJson(result);
                System.out.println(suppliersWithBills);
                for (SupplierWithBills supplierWithBills : suppliersWithBills) {
                    Supplier supplier = supplierWithBills.getSupplier();
                    supplierService.getByName(checkSupplierInDb(supplier, supplierWithBills.getBills()), supplier.getName());
                }
            }
        };
    }

    private Callback<Supplier> checkSupplierInDb(Supplier initialSupplier, List<Bill> bills) {
        return new Callback<Supplier>() {
            @Override
            public void runResultOnUiThread(Supplier result) {
                System.out.println(result);
                if (result == null) {
                    supplierService.insert(insertSupplierWithBills(bills), initialSupplier);
                }
            }
        };
    }


    private Callback<Supplier> insertSupplierWithBills(List<Bill> bills) {
        return new Callback<Supplier>() {
            @Override
            public void runResultOnUiThread(Supplier result) {
                if (result != null) {
                    Log.i("SUPPLIER FROM JSON ADDED:  ", result.toString());
                    for (Bill bill : bills) {
                        bill.setSupplierId(result.getSupplierId());
                        billService.insert(insertBillFromJson(), bill);
                    }
                }
            }
        };
    }

    private Callback<Bill> insertBillFromJson() {
        return new Callback<Bill>() {
            @Override
            public void runResultOnUiThread(Bill result) {
                if (result != null) {
                    Log.i("BILL FROM JSON ADDED:  ", result.toString());
                    
                }
            }
        };
    }
}
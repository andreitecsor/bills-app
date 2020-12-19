package ie.dam.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ie.dam.project.data.domain.Bill;
import ie.dam.project.data.domain.BillType;
import ie.dam.project.data.domain.Supplier;
import ie.dam.project.data.service.BillService;
import ie.dam.project.data.service.SupplierService;
import ie.dam.project.fragments.CurrencyFragment;
import ie.dam.project.fragments.FilterFragment;
import ie.dam.project.fragments.HomeFragment;
import ie.dam.project.fragments.ProfileFragment;
import ie.dam.project.util.asynctask.Callback;

public class MainActivity extends AppCompatActivity {
    private Fragment currentFragment;
    private BottomNavigationView bottomNavMenu;
    private SupplierService supplierService;
    private BillService billService;
    private List<Supplier> supplierList = new ArrayList<>();
    private List<Bill> billList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialiseComponents(savedInstanceState);
        supplierService = new SupplierService(getApplicationContext());
        billService = new BillService(getApplicationContext());
        bottomNavMenu.setOnNavigationItemSelectedListener(selectMenuItem());
//        supplierService.getAll(getAllSuppliers());
        Bill bill = new Bill(new Date(), 77.39, false, true, BillType.CONNECTIVITY.toString(), 10);
        billService.insert(insertBill(), bill);
        billService.getAll(getAllBills());
    }

    private void initialiseComponents(Bundle savedInstanceState) {
        bottomNavMenu = findViewById(R.id.act_main_menu);

        //Start-up fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.act_main_frame_layout,
                    new HomeFragment()).commit();
            bottomNavMenu.setSelectedItemId(R.id.menu_home);

        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectMenuItem() {
        return new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = new HomeFragment();
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.menu_currency:
                        selectedFragment = new CurrencyFragment();
                        break;
                    case R.id.menu_add:
                        Intent intent = new Intent(getApplicationContext(), AddEditActivity.class);
                        startActivity(intent);
                    case R.id.menu_filter:
                        selectedFragment = new FilterFragment();
                        break;
                    case R.id.menu_profile:
                        selectedFragment = new ProfileFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.act_main_frame_layout,
                        selectedFragment).commit();

                return true;
            }
        };
    }

    //region Supplier DB CRUD
    private Callback<List<Supplier>> getAllSuppliers() {
        return new Callback<List<Supplier>>() {
            @Override
            public void runResultOnUiThread(List<Supplier> result) {
                if (result != null) {
                    supplierList.clear();
                    supplierList.addAll(result);
                    //TODO: Send it to HomeFragment
                }
            }
        };
    }

    private Callback<Supplier> getSupplierById() {
        return new Callback<Supplier>() {
            @Override
            public void runResultOnUiThread(Supplier result) {
                System.out.println(result);
            }
        };
    }

    private Callback<Supplier> insertSupplier() {
        return new Callback<Supplier>() {
            @Override
            public void runResultOnUiThread(Supplier result) {
                if (result != null) {
                    supplierList.add(result);
                    //TODO: Send the updated list to HomeFragment
                }
            }
        };
    }

    private Callback<Supplier> updateSupplier() {
        return new Callback<Supplier>() {
            @Override
            public void runResultOnUiThread(Supplier result) {
                if (result != null) {
                    for (Supplier supplier : supplierList) {
                        if (supplier.getSupplierId() == result.getSupplierId()) {
                            supplier.setName(result.getName());
                            supplier.setEmail(result.getEmail());
                            supplier.setPhone(result.getPhone());
                            break;
                        }
                    }
                    //TODO: Send the updated list to HomeFragment
                }
            }
        };
    }

    private Callback<Integer> deleteSupplier(final int selectedPosition) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result != -1) {
                    supplierList.remove(selectedPosition);
                    //TODO: 1 - To test it
                    //      2 - Send the updated list to HomeFragment
                }
            }
        };
    }
    //endregion SUPPLIER CRUD

    //region Bill DB CRUD
    private Callback<List<Bill>> getAllBills() {
        return new Callback<List<Bill>>() {
            @Override
            public void runResultOnUiThread(List<Bill> result) {
                if (result != null) {
                    billList.clear();
                    billList.addAll(result);
                    System.out.println(billList);
                    //TODO: Send it to HomeFragment
                }
            }
        };
    }

    private Callback<Bill> insertBill() {
        return new Callback<Bill>() {
            @Override
            public void runResultOnUiThread(Bill result) {
                if (result != null) {
                    billList.add(result);
                    //TODO: Send the updated list to HomeFragment
                }
            }
        };
    }

    private Callback<Bill> updateBill() {
        return new Callback<Bill>() {
            @Override
            public void runResultOnUiThread(Bill result) {
                if (result != null) {
                    for (Bill bill : billList) {
                        if (bill.getBillId() == result.getBillId()) {
                            bill.setAmount(result.getAmount());
                            bill.setDueTo(result.getDueTo());
                            bill.setPayed(result.isPayed());
                            bill.setRecurrent(result.isRecurrent());
                            bill.setSupplierId(result.getSupplierId());
                            break;
                        }
                    }
                    //TODO: Send the updated list to HomeFragment
                }
            }
        };
    }

    private Callback<Integer> deleteBill(final int selectedPosition) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result != -1) {
                    billList.remove(selectedPosition);
                    //TODO: 1 - To test it
                    //      2 - Send the updated list to HomeFragment
                }
            }
        };
    }
    //endregion Bill DB CRUD

}
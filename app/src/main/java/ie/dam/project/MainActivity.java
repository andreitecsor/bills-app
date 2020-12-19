package ie.dam.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import ie.dam.project.data.domain.Supplier;
import ie.dam.project.data.service.SupplierService;
import ie.dam.project.fragments.CurrencyFragment;
import ie.dam.project.fragments.FilterFragment;
import ie.dam.project.fragments.HomeFragment;
import ie.dam.project.fragments.ProfileFragment;
import ie.dam.project.util.asynctask.Callback;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavMenu;
    private SupplierService supplierService;
    private List<Supplier> supplierList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialiseComponents(savedInstanceState);
        supplierService = new SupplierService(getApplicationContext());
        bottomNavMenu.setOnNavigationItemSelectedListener(selectMenuItem());
        Supplier supplier = new Supplier("ORANGE","300","contact@orange.ro");
        supplierService.insert(insertSupplier(),supplier);
        supplierService.getAll(getAllSuppliers());
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

//region SUPPLIER CRUD
    private Callback<List<Supplier>> getAllSuppliers() {
        return new Callback<List<Supplier>>() {
            @Override
            public void runResultOnUiThread(List<Supplier> result) {
                if (result != null) {
                    supplierList.clear();
                    supplierList.addAll(result);
                    System.out.println(supplierList);
//                    notifyAdapter();
                }
            }
        };
    }

    private Callback<Supplier> insertSupplier() {
        return new Callback<Supplier>() {
            @Override
            public void runResultOnUiThread(Supplier result) {
                if (result != null) {
                    System.out.println("IETE"+ result);
                    supplierList.add(result);
//                    notifyAdapter();
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
//                    notifyAdapter();
                }
            }
        };
    }

    private Callback<Integer> deleteSupplier(final int id) {
        return new Callback<Integer>() {
            @Override
            public void runResultOnUiThread(Integer result) {
                if (result != -1) {
                    supplierList.remove(id);
//                    notifyAdapter();
                }
            }
        };
    }
//endregion SUPPLIER CRUD
}
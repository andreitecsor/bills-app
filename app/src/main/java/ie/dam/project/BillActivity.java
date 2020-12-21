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
import ie.dam.project.data.domain.Supplier;
import ie.dam.project.data.service.BillService;
import ie.dam.project.data.service.SupplierService;
import ie.dam.project.fragments.BillListFragment;
import ie.dam.project.util.asynctask.Callback;

public class BillActivity extends AppCompatActivity {
    private Fragment currentFragment;

    private SupplierService supplierService;
    private BillService billService;

    private List<Supplier> supplierList = new ArrayList<>();
    private List<Bill> billList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        initialiseComponents();
        supplierService = new SupplierService(getApplicationContext());
        billService = new BillService(getApplicationContext());
//        supplierService.insert(insertSupplier(), new Supplier("ORANGE", "300", "contact@orange.com"));
        billService.insert(insertBill(), new Bill(new Date(), 77, false, false, "CONNECTIVITY", 1));
        billService.getAll(getAllBills());
    }

    private void initialiseComponents() {
        setCurrentDate();
    }

    private void openBillListFragment(List<Bill> bills) {
        currentFragment = BillListFragment.newInstance(bills);
        getSupportFragmentManager().beginTransaction().replace(R.id.act_bills_frame,
                currentFragment).commit();
    }

    //region Supplier DB CRUD

    private Callback<List<Supplier>> getAllSuppliers() {
        return new Callback<List<Supplier>>() {
            @Override
            public void runResultOnUiThread(List<Supplier> result) {
                if (result != null) {
                    supplierList.clear();
                    supplierList.addAll(result);
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
                    System.out.println("BILLS:" + billList);
                    openBillListFragment(billList);
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

    private void setCurrentDate() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        TextView currentDate = findViewById(R.id.act_bills_tv_date);
        currentDate.setText(formatter.format(date));
    }
}
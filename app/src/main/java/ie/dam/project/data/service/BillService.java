package ie.dam.project.data.service;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.concurrent.Callable;

import ie.dam.project.data.DatabaseManager;
import ie.dam.project.data.dao.BillDao;
import ie.dam.project.data.dao.SupplierDao;
import ie.dam.project.data.domain.Bill;
import ie.dam.project.data.domain.Supplier;
import ie.dam.project.util.asynctask.AsyncTaskRunner;
import ie.dam.project.util.asynctask.Callback;

public class BillService {
    private final SupplierDao supplierDao;
    private final BillDao billDao;
    private final AsyncTaskRunner asyncTaskRunner;

    public BillService(Context context) {
        billDao = DatabaseManager.getInstance(context).getBillDao();
        supplierDao = DatabaseManager.getInstance(context).getSupplierDao();
        asyncTaskRunner = new AsyncTaskRunner();
    }

    public void getAll(Callback<List<Bill>> callback) {
        Callable<List<Bill>> callable = new Callable<List<Bill>>() {
            @Override
            public List<Bill> call() throws Exception {
                return billDao.getAll();
            }
        };
        asyncTaskRunner.executeAsync(callable, callback);
    }

    public void insert(Callback<Bill> callback, final Bill bill) {
        Callable<Bill> callable = new Callable<Bill>() {
            @Override
            public Bill call() {
                if (bill == null) {
                    return null;
                }
                if (supplierDao.getById(bill.getSupplierId()) == null) {
                    Log.w("FOREIGN KEY PROBLEM","INVALID SUPPLIER ID");
                    return null;
                }
                long insertedId = billDao.insert(bill);
                if (insertedId == -1) {
                    return null;
                }
                bill.setSupplierId(insertedId);
                return bill;
            }
        };
        asyncTaskRunner.executeAsync(callable, callback);
    }

    public void update(Callback<Bill> callback, final Bill bill) {
        Callable<Bill> callable = new Callable<Bill>() {
            @Override
            public Bill call() {
                if (bill == null) {
                    return null;
                }
                if (supplierDao.getById(bill.getSupplierId()) == null) {
                    Log.w("FOREIGN KEY PROBLEM","INVALID SUPPLIER ID");
                    return null;
                }
                int updatedRows = billDao.update(bill);
                if (updatedRows < 1) {
                    return null;
                }
                return bill;
            }
        };
        asyncTaskRunner.executeAsync(callable, callback);
    }

    public void delete(Callback<Integer> callback, final Bill bill) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() {
                if (bill == null) {
                    return -1;
                }
                return billDao.delete(bill);
            }
        };
        asyncTaskRunner.executeAsync(callable, callback);
    }


}

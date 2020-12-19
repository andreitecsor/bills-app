package ie.dam.project.data.service;

import android.content.Context;

import java.util.List;
import java.util.concurrent.Callable;

import ie.dam.project.data.DatabaseManager;
import ie.dam.project.data.dao.SupplierDao;
import ie.dam.project.data.domain.Supplier;
import ie.dam.project.util.asynctask.AsyncTaskRunner;
import ie.dam.project.util.asynctask.Callback;

public class SupplierService {
    private final SupplierDao dao;
    private final AsyncTaskRunner taskRunner;

    public SupplierService(Context context) {
        dao = DatabaseManager.getInstance(context).getSupplierDao();
        taskRunner = new AsyncTaskRunner();
    }

    public void getAll(Callback<List<Supplier>> callback) {
        Callable<List<Supplier>> callable = new Callable<List<Supplier>>() {
            @Override
            public List<Supplier> call() throws Exception {
                return dao.getAll();
            }
        };
        taskRunner.executeAsync(callable, callback);
    }

    public void getById(Callback<Supplier> callback, long id) {
        Callable<Supplier> callable = new Callable<Supplier>() {
            @Override
            public Supplier call() throws Exception {
                return dao.getById(id);
            }
        };
        taskRunner.executeAsync(callable, callback);
    }

    public void insert(Callback<Supplier> callback, final Supplier supplier) {
        Callable<Supplier> callable = new Callable<Supplier>() {
            @Override
            public Supplier call() {
                if (supplier == null) {
                    return null;
                }
                long insertedId = dao.insert(supplier);
                if (insertedId == -1) {
                    return null;
                }
                supplier.setSupplierId(insertedId);
                return supplier;
            }
        };
        taskRunner.executeAsync(callable, callback);
    }

    public void update(Callback<Supplier> callback, final Supplier supplier) {
        Callable<Supplier> callable = new Callable<Supplier>() {
            @Override
            public Supplier call() {
                if (supplier == null) {
                    return null;
                }
                int updatedRows = dao.update(supplier);
                if (updatedRows < 1) {
                    return null;
                }
                return supplier;
            }
        };
        taskRunner.executeAsync(callable, callback);
    }

    public void delete(Callback<Integer> callback, final Supplier supplier) {
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() {
                if (supplier == null) {
                    return -1;
                }
                return dao.delete(supplier);
            }
        };
        taskRunner.executeAsync(callable, callback);
    }


}

package ie.dam.project.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import ie.dam.project.data.domain.Supplier;
import ie.dam.project.data.domain.SupplierWithBills;

@Dao
public interface SupplierDao {

    @Transaction
    @Query("SELECT * FROM suppliers")
    List<SupplierWithBills> getAllWithBills();

    @Query("SELECT * FROM suppliers")
    List<Supplier> getAll();

    @Query("SELECT name from suppliers")
    List<String> getAllNames();

    @Query("SELECT * from suppliers WHERE supplierId = :id")
    Supplier getById(long id);

    @Query("SELECT supplierId FROM SUPPLIERS WHERE name=:name")
    long getIdByName(String name);

    @Query("SELECT * FROM suppliers WHERE name=:name")
    Supplier getByName(String name);

    @Insert
    long insert(Supplier supplier);

    @Update
    int update(Supplier supplier);

    @Delete
    int delete(Supplier supplier);

}

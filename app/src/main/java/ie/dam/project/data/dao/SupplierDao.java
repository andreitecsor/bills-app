package ie.dam.project.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import ie.dam.project.data.domain.SupplierWithBills;

@Dao
public interface SupplierDao {

    @Transaction
    @Query("SELECT * FROM suppliers")
    public List<SupplierWithBills> getUsersWithPlaylists();

}

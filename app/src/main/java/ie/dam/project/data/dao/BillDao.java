package ie.dam.project.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import ie.dam.project.data.domain.Bill;
import ie.dam.project.data.domain.BillShownInfo;

@Dao
public interface BillDao {
    @Query("SELECT * FROM bills")
    List<Bill> getAll();

    @Query("SELECT bills.*, suppliers.name  FROM bills JOIN suppliers ON bills.supplierId = suppliers.supplierId")
    List<BillShownInfo> getAllWithSupplierName();

    @Query("SELECT bills.*, suppliers.name  " +
            "FROM bills JOIN suppliers ON bills.supplierId = suppliers.supplierId " +
            "WHERE bills.amount BETWEEN :min AND :max " +
            "AND bills.payed = :paid " +
            "AND bills.recurrent = :recurrent")
    List<BillShownInfo> getFilteredBill(double min, double max, boolean paid, boolean recurrent);

    @Query("SELECT bills.* FROM bills WHERE supplierId= :supplierId")
    List<Bill> getAllBySupplierId(long supplierId);

    @Query("SELECT bills.* FROM bills JOIN suppliers ON bills.supplierId = suppliers.supplierId WHERE suppliers.name = :name")
    List<Bill> getAllBySupplierName(String name);

    @Query("SELECT COUNT(*) FROM bills WHERE payed = :paid")
    int getNoBillsByPaymentType(boolean paid);

    @Query("SELECT SUM(amount) FROM bills WHERE payed = :paid")
    double getAmountByPaymentType(boolean paid);

    @Insert
    long insert(Bill bill);

    @Update
    int update(Bill bill);

    @Delete
    int delete(Bill bill);
}

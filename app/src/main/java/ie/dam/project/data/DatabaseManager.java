package ie.dam.project.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ie.dam.project.data.dao.BillDao;
import ie.dam.project.data.dao.SupplierDao;
import ie.dam.project.data.domain.Bill;
import ie.dam.project.data.domain.Supplier;
import ie.dam.project.util.converters.DateConverter;

/**version trebuie incrementat atunci cand schimbam structura tabelei*/
@Database(entities = {Bill.class, Supplier.class}, exportSchema = true, version = 2)
@TypeConverters({DateConverter.class})
public abstract class DatabaseManager extends RoomDatabase {


    private static final String DATABASE_NAME = "bm_db";
    private static DatabaseManager databaseManager;

    public static DatabaseManager getInstance(Context context) {
        if (databaseManager == null) {
            synchronized (DatabaseManager.class) {
                if (databaseManager == null) {
                    databaseManager = Room.databaseBuilder(context, DatabaseManager.class, FirebaseAuth.getInstance().getUid())
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return databaseManager;
    }

    public static void disableDataBaseManager()
    {
        databaseManager=null;
    }
    public abstract BillDao getBillDao();
    public abstract SupplierDao getSupplierDao();
}

package ie.dam.project.data.domain;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class SupplierWithBills {
    @Embedded public Supplier supplier;
    @Relation(
            parentColumn = "supplierId",
            entityColumn = "supplierId"
    )
    public List<Bill> bills;


    @Override
    public String toString() {
        return "SupplierWithBills{" +
                "supplier=" + supplier +
                ", bills=" + bills +
                '}';
    }

    public SupplierWithBills() {
    }

    public SupplierWithBills(Supplier supplier, List<Bill> bills) {
        this.supplier = supplier;
        this.bills = bills;
    }


}

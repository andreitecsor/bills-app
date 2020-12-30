package ie.dam.project.data.domain;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class SupplierWithBills {
    @Embedded private Supplier supplier;
    @Relation(
            parentColumn = "supplierId",
            entityColumn = "supplierId"
    )
    private List<Bill> bills;


    @Override
    public String toString() {
        return "SupplierWithBills{" +
                "supplier=" + supplier +
                ", bills=" + bills +
                '}';
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public List<Bill> getBills() {
        return bills;
    }

    public void setBills(List<Bill> bills) {
        this.bills = bills;
    }

    public SupplierWithBills() {
    }

    public SupplierWithBills(Supplier supplier, List<Bill> bills) {
        this.supplier = supplier;
        this.bills = bills;
    }


}

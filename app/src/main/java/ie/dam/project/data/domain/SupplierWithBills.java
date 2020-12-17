package ie.dam.project.data.domain;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class SupplierWithBills {
    @Embedded
    private Supplier supplier;
    @Relation(
            parentColumn = "supplierId",
            entityColumn = "supplierId"
    )
    private List<Bill> bills;
}

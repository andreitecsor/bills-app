package ie.dam.project.data.domain;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "bills",
        foreignKeys =
        @ForeignKey(entity = Supplier.class,
                parentColumns = "supplierId",
                childColumns = "supplierId",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE))
public class Bill implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "billId")
    private long billId;

    @NonNull
    @ColumnInfo(name = "dueTo")
    private Date dueTo;

    @NonNull
    @ColumnInfo(name = "amount")
    private double amount;

    @NonNull
    @ColumnInfo(name = "payed")
    private boolean payed;

    @NonNull
    @ColumnInfo(name = "recurrent")
    private boolean recurrent;

    @NonNull
    @ColumnInfo(name = "type")
    private String type;

    @NonNull
    @ColumnInfo(name = "supplierId", index = true)
    private long supplierId;

    @Ignore
    public Bill() {
    }

    public Bill(long billId, Date dueTo, double amount, boolean payed, boolean recurrent, String type, long supplierId) {
        this.billId = billId;
        this.dueTo = dueTo;
        this.amount = amount;
        this.payed = payed;
        this.recurrent = recurrent;
        this.type = type;
        this.supplierId = supplierId;
    }

    @Ignore
    public Bill(Date dueTo, double amount, boolean payed, boolean recurrent, String type, long supplierId) {
        this.dueTo = dueTo;
        this.amount = amount;
        this.payed = payed;
        this.recurrent = recurrent;
        this.type = type;
        this.supplierId = supplierId;
    }

    public long getBillId() {
        return billId;
    }

    public void setBillId(long billId) {
        this.billId = billId;
    }

    public Date getDueTo() {
        return dueTo;
    }

    public void setDueTo(Date dueTo) {
        this.dueTo = dueTo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isPayed() {
        return payed;
    }

    public void setPayed(boolean payed) {
        this.payed = payed;
    }

    public boolean isRecurrent() {
        return recurrent;
    }

    public void setRecurrent(boolean recurrent) {
        this.recurrent = recurrent;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(long supplierId) {
        this.supplierId = supplierId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bill bill = (Bill) o;

        if (billId != bill.billId) return false;
        if (Double.compare(bill.amount, amount) != 0) return false;
        if (payed != bill.payed) return false;
        if (recurrent != bill.recurrent) return false;
        if (supplierId != bill.supplierId) return false;
        if (dueTo != null ? !dueTo.equals(bill.dueTo) : bill.dueTo != null) return false;
        return type != null ? type.equals(bill.type) : bill.type == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (billId ^ (billId >>> 32));
        result = 31 * result + (dueTo != null ? dueTo.hashCode() : 0);
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (payed ? 1 : 0);
        result = 31 * result + (recurrent ? 1 : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (int) (supplierId ^ (supplierId >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId=" + billId +
                ", dueTo=" + dueTo +
                ", amount=" + amount +
                ", payed=" + payed +
                ", recurrent=" + recurrent +
                ", type='" + type + '\'' +
                ", supplierId=" + supplierId +
                '}';
    }
}

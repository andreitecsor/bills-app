package ie.dam.project.domain;

import java.io.Serializable;
import java.util.Date;

public class Bill implements Serializable {
    private Long id;

    private Supplier supplier;

    //TODO: Decide dueTo final data type
    private Date dueTo;

    private Float sum;

    private Boolean payed;

    private Boolean recurrent;

    private BillType type;

    public Bill() {
    }

    public Bill(Long id, Supplier supplier, Date dueTo, Float sum, Boolean payed, Boolean recurrent, BillType type) {
        this.id = id;
        this.supplier = supplier;
        this.dueTo = dueTo;
        this.sum = sum;
        this.payed = payed;
        this.recurrent = recurrent;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    //TODO: Delete setId here
    public void setId(Long id) {
        this.id = id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Date getDueTo() {
        return dueTo;
    }

    public void setDueTo(Date dueTo) {
        this.dueTo = dueTo;
    }

    public Float getSum() {
        return sum;
    }

    public void setSum(Float sum) {
        this.sum = sum;
    }

    public Boolean getPayed() {
        return payed;
    }

    public void setPayed(Boolean payed) {
        this.payed = payed;
    }

    public Boolean getRecurrent() {
        return recurrent;
    }

    public void setRecurrent(Boolean recurrent) {
        this.recurrent = recurrent;
    }

    public BillType getType() {
        return type;
    }

    public void setType(BillType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bill bill = (Bill) o;

        if (id != null ? !id.equals(bill.id) : bill.id != null) return false;
        if (supplier != null ? !supplier.equals(bill.supplier) : bill.supplier != null)
            return false;
        if (dueTo != null ? !dueTo.equals(bill.dueTo) : bill.dueTo != null) return false;
        if (sum != null ? !sum.equals(bill.sum) : bill.sum != null) return false;
        if (payed != null ? !payed.equals(bill.payed) : bill.payed != null) return false;
        if (recurrent != null ? !recurrent.equals(bill.recurrent) : bill.recurrent != null)
            return false;
        return type == bill.type;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (supplier != null ? supplier.hashCode() : 0);
        result = 31 * result + (dueTo != null ? dueTo.hashCode() : 0);
        result = 31 * result + (sum != null ? sum.hashCode() : 0);
        result = 31 * result + (payed != null ? payed.hashCode() : 0);
        result = 31 * result + (recurrent != null ? recurrent.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", supplier=" + supplier +
                ", dueTo=" + dueTo +
                ", sum=" + sum +
                ", payed=" + payed +
                ", recurrent=" + recurrent +
                ", type=" + type +
                '}';
    }
}

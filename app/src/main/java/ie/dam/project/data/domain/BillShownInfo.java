package ie.dam.project.data.domain;

import androidx.room.Embedded;

import java.util.Comparator;

public class BillShownInfo {
    @Embedded private Bill bill;

    private String name;

    public Bill getBill() {
        return bill;
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BillAdapterInfo{" +
                "bill=" + bill +
                ", name='" + name + '\'' +
                '}';
    }
}

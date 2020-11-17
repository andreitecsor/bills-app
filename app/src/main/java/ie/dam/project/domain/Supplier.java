package ie.dam.project.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Supplier implements Serializable {
    private Long id;

    private String name;

    private Set<Bill> bills;

    public Supplier() {
    }

    public Supplier(String name) {
        this.name = name;
        this.bills = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    //TODO: Delete setId here
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Bill> getBills() {
        return bills;
    }

    public void setBills(Set<Bill> bills) {
        this.bills = bills;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Supplier supplier = (Supplier) o;

        if (id != null ? !id.equals(supplier.id) : supplier.id != null) return false;
        return name != null ? name.equals(supplier.name) : supplier.name == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", bills=" + bills +
                '}';
    }
}

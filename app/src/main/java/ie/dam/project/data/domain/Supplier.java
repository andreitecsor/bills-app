package ie.dam.project.data.domain;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity(tableName = "suppliers")
public class Supplier implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "supplierId")
    private long supplierId;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "phone")
    private String phone;

    @NonNull
    @ColumnInfo(name = "email")
    private String email;

    public Supplier(long supplierId, String name, String phone, String email) {
        this.supplierId = supplierId;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    @Ignore
    public Supplier(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(long supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Supplier supplier = (Supplier) o;

        if (supplierId != supplier.supplierId) return false;
        if (name != null ? !name.equals(supplier.name) : supplier.name != null) return false;
        if (phone != null ? !phone.equals(supplier.phone) : supplier.phone != null) return false;
        return email != null ? email.equals(supplier.email) : supplier.email == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (supplierId ^ (supplierId >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "supplierId=" + supplierId +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

package ie.dam.project.util.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ie.dam.project.data.domain.Bill;
import ie.dam.project.data.domain.Supplier;
import ie.dam.project.data.domain.SupplierWithBills;

public class SupplierJsonParser {


    public static final String NAME = "name";
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String BILLS = "bills";


    public static List<SupplierWithBills> fromJson(String json) {
        try {
            JSONArray array = new JSONArray(json);
            return readSuppliers(array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static List<SupplierWithBills> readSuppliers(JSONArray array) throws JSONException {
        List<SupplierWithBills> suppliers = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            SupplierWithBills supplier = readSupplier(array.getJSONObject(i));
            suppliers.add(supplier);
        }
        return suppliers;
    }

    private static SupplierWithBills readSupplier(JSONObject object) throws JSONException {
        String name = object.getString(NAME);
        String phone = object.getString(PHONE);
        String email = object.getString(EMAIL);
        String bills=object.getString(BILLS);
        List<Bill> billList=BillJsonParser.fromJson(bills);

        Supplier supplier= new Supplier(name,phone,email);
        return new SupplierWithBills(supplier,billList);
    }
}

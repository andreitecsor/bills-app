package ie.dam.project.util.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ie.dam.project.data.domain.Bill;
import ie.dam.project.data.domain.BillType;
import ie.dam.project.data.domain.Gender;
import ie.dam.project.data.domain.Supplier;
import ie.dam.project.util.converters.DateConverter;

public class BillJsonParser {


    public static final String DUE_TO = "dueTo";
    public static final String AMOUNT = "amount";
    public static final String PAID = "paid";
    public static final String RECURRENT = "recurrent";
    public static final String TYPE = "type";


    public static List<Bill> fromJson(String json) {
        try {
            JSONArray array = new JSONArray(json);
            return readBills(array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private static List<Bill> readBills(JSONArray array) throws JSONException {
        List<Bill> bills = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Bill bill = readBill(array.getJSONObject(i));
            bills.add(bill);
        }
        return bills;
    }

    private static Bill readBill(JSONObject object) throws JSONException {
        String dueTo = object.getString(DUE_TO);
        Double amount = object.getDouble(AMOUNT);
        Boolean paid = object.getBoolean(PAID);
        Boolean recurrent = object.getBoolean(RECURRENT);
        String type = object.getString(TYPE);
        BillType billType = BillType.MISCELLANEOUS;
        if (BillType.contains(type)) {
            billType = BillType.valueOf(type.toUpperCase());
        }

        return new Bill(DateConverter.toDate(dueTo), amount, paid, recurrent, billType.toString().toUpperCase());

    }
}

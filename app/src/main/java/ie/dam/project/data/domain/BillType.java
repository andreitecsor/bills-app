package ie.dam.project.data.domain;

import androidx.annotation.NonNull;

public enum BillType {
    GAS ("Gas"),
    ELECTRICITY ("Electricity"),
    WATER("Water"),
    ENTERTAINMENT("Entertainment"),
    CONNECTIVITY("Connectivity"),
    MISCELLANEOUS("Miscellaneous");

    private String value;

    BillType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @NonNull
    @Override
    public String toString() {
        return this.value;
    }
    public static boolean contains(String input) {

        for (BillType type : BillType.values()) {
            if (type.name().equals(input)) {
                return true;
            }
        }

        return false;
    }
}

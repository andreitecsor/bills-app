package ie.dam.project.data.domain;

import androidx.annotation.NonNull;

public enum Gender {
    RATHER_NOT_SAY("Rather not say"),
    MALE("Male"),
    FEMALE("Female"),
    OTHERS("Others");

    private String value;

    Gender(String value) {
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

    public static Gender getEnum(String value) {
        for (Gender v : values())
            if (v.getValue().equalsIgnoreCase(value)) return v;
        throw new IllegalArgumentException();
    }
}

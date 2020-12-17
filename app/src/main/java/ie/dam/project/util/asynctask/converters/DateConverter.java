package ie.dam.project.util.asynctask.converters;

import androidx.room.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {
    private static final String FORMAT_DATE = "dd MMM yyyy";
    private static final SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_DATE);

    @TypeConverter
    public static Date toDate(String value) {
        try {
            return formatter.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    @TypeConverter
    public static String toString(Date value) {
        if (value == null) {
            return null;
        }
        return formatter.format(value);
    }
}

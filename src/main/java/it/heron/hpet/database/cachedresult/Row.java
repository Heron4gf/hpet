package it.heron.hpet.database.cachedresult;
import java.util.HashMap;
import java.util.Map;

public class Row {

    private Map<String,Object> row = new HashMap<>();

    public Row() {}

    public void addValue(String key, Object value) {
        row.put(key,value);
    }

    public String getString(String key) {
        return (String)row.get(key);
    }

    public int getInt(String key) {
        return ((Integer) row.get(key)).intValue();
    }

    public boolean getBoolean(String key) {
        return ((Boolean) row.get(key)).booleanValue();
    }

    public double getDouble(String key) {
        return ((Double) row.get(key)).doubleValue();
    }

}

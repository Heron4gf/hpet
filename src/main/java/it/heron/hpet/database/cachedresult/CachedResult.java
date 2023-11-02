package it.heron.hpet.database.cachedresult;

import java.util.ArrayList;
import java.util.List;

public class CachedResult {

    private List<Row> rows = new ArrayList<>();

    private int nextcount = -1;
    public CachedResult(Row... rows) {
        for (Row row : rows) {
            this.rows.add(row);
        }
    }

    public CachedResult() {}

    public void addRow(Row row) {
        rows.add(row);
    }

    /*
     * Check if there is a next row
     * @return true if there is a next row, false otherwise
     */
    public boolean next() {
        nextcount++;
        return nextcount < rows.size();
    }

    /*
     * Get the next row
     * @return the next row
     */
    public Row getRow() {
        if(nextcount == -1) nextcount=0;
        return rows.get(nextcount);
    }

    public int getInt(String key) {
        return getRow().getInt(key);
    }

    public String getString(String key) {
        return getRow().getString(key);
    }
    public double getDouble(String key) {
        return getRow().getDouble(key);
    }

    public Boolean getBoolean(String key) {
        return getRow().getBoolean(key);
    }

    /*
     * Get a row by index
     * @param i index of the row
     */
    public Row getRow(int i) {
        return rows.get(i);
    }

}

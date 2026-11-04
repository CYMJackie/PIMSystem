package model;

import java.util.ArrayList;
import java.util.List;

public class PIRManager {
    private List<Record> records = new ArrayList<>();
    private int currentID = 1;

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = new ArrayList<>(records);
    }

    public void add(Record record) {
        records.add(record);
    }

    public int getCurrentID() {
        return currentID;
    }

    public void generateID() {
        this.currentID++;
    }

    public void setCurrentID(int id) {
        this.currentID = id;
    }
}

package model;

public abstract class Record {
    private final int id;

    protected Record(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

}

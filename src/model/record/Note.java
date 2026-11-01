package model.record;

import model.Record;

public class Note extends Record {
    private String text;

    protected Note(int id, String text) {
        String type = "Note";
        super(id, type);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

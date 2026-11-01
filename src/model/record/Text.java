package model.record;

import model.Record;

public class Text extends Record {
    private String text;

    protected Text(int id, String text) {
        String type = "Text";
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

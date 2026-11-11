package model.record;

import model.Record;

public class Text extends Record {
    private String text;

    public Text(int id, String text) {
        super(id);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

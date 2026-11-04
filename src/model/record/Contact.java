package model.record;

import model.Record;

public class Contact extends Record {
    private String name, phone, address;

    public Contact(int id, String name, String phone, String address) {
        super(id, "Contact");
        this.name = name;
        this.phone = phone;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    //phone number format set according to international standard format(eg. +852, +86...)
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

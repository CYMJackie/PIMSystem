package model.command;

import model.PIRManager;
import model.record.Contact;

public class CreateContact {
    public void createContact(PIRManager manager, String name, String phone, String address) {
        Contact contact = new Contact(manager.getCurrentID(), name, phone, address);
        manager.generateID();
        manager.add(contact);
    }
}

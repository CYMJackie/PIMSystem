package model.command;

import model.PIRManager;

public class Delete {
    public boolean delete(PIRManager manager, int id) {
        return manager.getRecords().removeIf(record -> record.getId() == id);
    }
}

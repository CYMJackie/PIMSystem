import controller.PIMController;
import model.PIRManager;
import view.PIMView;

public class Main {
    public static void main(String[] args) {
        PIRManager manager = new PIRManager();
        PIMView view = new PIMView();
        PIMController controller = new PIMController(manager, view);

        controller.initialize();
    }
}

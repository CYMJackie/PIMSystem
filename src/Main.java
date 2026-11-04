import controller.PIMController;
import model.PIRManager;
import view.PIMView;

void main() {
    PIRManager manager = new PIRManager();
    PIMView view = new PIMView();
    PIMController controller = new PIMController(manager, view);

    controller.initialize();
}

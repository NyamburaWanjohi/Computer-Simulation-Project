import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            InputConfigPanel panel = new InputConfigPanel();
            panel.setVisible(true);
        });

    }
}

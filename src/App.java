import gui.MainFrame;
import database.DatabaseHelper;

public class App {
    public static void main(String[] args) {
        DatabaseHelper.initializeDatabase();
        System.out.println("Database OK");
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
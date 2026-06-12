import controller.ProductController;
import controller.StockController;
import controller.TransactionController;
import view.MainMenuView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set look and feel to system default if possible
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize controllers
        ProductController productController = new ProductController();
        StockController stockController = new StockController();
        TransactionController transactionController = new TransactionController();

        // Launch Main Menu
        SwingUtilities.invokeLater(() -> {
            MainMenuView mainMenu = new MainMenuView(productController, stockController, transactionController);
            mainMenu.setVisible(true);
        });
    }
}

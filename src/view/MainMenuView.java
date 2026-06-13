package view;

import controller.ProductController;
import controller.StockController;
import controller.TransactionController;
import model.Transaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class MainMenuView extends JFrame {
    private ProductController productController;
    private StockController stockController;
    private TransactionController transactionController;

    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    private ProductView productView;
    private CashierView cashierView;
    private HistoryView historyView;

    public MainMenuView(ProductController pc, StockController sc, TransactionController tc) {
        this.productController = pc;
        this.stockController = sc;
        this.transactionController = tc;

        setTitle("Aplikasi Point of Sale (POS)");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(220, 0));
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setBackground(new Color(30, 39, 46));

        // Title Area di atas Sidebar
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(30, 39, 46));
        titlePanel.setBorder(new EmptyBorder(30, 10, 30, 10));
        JLabel lblTitle = new JLabel("POS SISTEM");
        lblTitle.setForeground(new Color(11, 232, 129));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(lblTitle, BorderLayout.CENTER);
        sidebarPanel.add(titlePanel, BorderLayout.NORTH);

        // Menu Buttons Area
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(30, 39, 46));
        menuPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnProduct = createNavButton("Produk & Stok");
        JButton btnCashier = createNavButton("Kasir / Pembayaran");
        JButton btnHistory = createNavButton("Riwayat System");

        menuPanel.add(btnProduct);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(btnCashier);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        menuPanel.add(btnHistory);

        sidebarPanel.add(menuPanel, BorderLayout.CENTER);
        add(sidebarPanel, BorderLayout.WEST);

        // Card Layout
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(new Color(241, 242, 246));

        // Inisialisasi View sebagai Panel
        productView = new ProductView(productController, stockController, this);
        cashierView = new CashierView(transactionController, productController);
        historyView = new HistoryView(transactionController, stockController);

        // Panel Selamat Datang
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBackground(new Color(241, 242, 246));
        JLabel lblWelcome = new JLabel("Selamat Datang di POS SISTEM");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblWelcome.setForeground(new Color(87, 95, 103));
        welcomePanel.add(lblWelcome);

        // Card Panel
        mainContentPanel.add(welcomePanel, "WELCOME");
        mainContentPanel.add(productView, "PRODUCT");
        mainContentPanel.add(cashierView, "CASHIER");
        mainContentPanel.add(historyView, "HISTORY");

        add(mainContentPanel, BorderLayout.CENTER);

        // Action Button
        btnProduct.addActionListener(e -> {
            productView.refreshTable();
            cardLayout.show(mainContentPanel, "PRODUCT");
        });

        btnCashier.addActionListener(e -> {
            cashierView.refreshData();
            cardLayout.show(mainContentPanel, "CASHIER");
        });

        btnHistory.addActionListener(e -> {
            historyView.refreshData();
            cardLayout.show(mainContentPanel, "HISTORY");
        });
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 45));
        btn.setPreferredSize(new Dimension(200, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setBackground(new Color(72, 84, 96));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(11, 232, 129));
                btn.setForeground(new Color(30, 39, 46));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(72, 84, 96));
                btn.setForeground(Color.WHITE);
            }
        });
        return btn;
    }

}

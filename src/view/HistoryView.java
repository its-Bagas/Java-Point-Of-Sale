package view;

import controller.StockController;
import controller.TransactionController;
import model.Transaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class HistoryView extends JPanel {
    private TransactionController transactionController;
    private StockController stockController;

    private JTextArea salesTextArea;
    private JTextArea opnameTextArea;

    public HistoryView(TransactionController tc, StockController sc) {
        this.transactionController = tc;
        this.stockController = sc;

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel historyTitle = new JLabel("Riwayat Sistem");
        historyTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        historyTitle.setForeground(new Color(47, 53, 66));
        historyTitle.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(historyTitle, BorderLayout.NORTH);

        JTabbedPane historyTabbedPane = new JTabbedPane();
        historyTabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        salesTextArea = new JTextArea();
        salesTextArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        salesTextArea.setEditable(false);
        salesTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel salesPanel = new JPanel(new BorderLayout());
        salesPanel.add(new JScrollPane(salesTextArea), BorderLayout.CENTER);
        historyTabbedPane.addTab("Riwayat Penjualan", salesPanel);

        opnameTextArea = new JTextArea();
        opnameTextArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        opnameTextArea.setEditable(false);
        opnameTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel opnamePanel = new JPanel(new BorderLayout());
        opnamePanel.add(new JScrollPane(opnameTextArea), BorderLayout.CENTER);
        historyTabbedPane.addTab("Riwayat Opname", opnamePanel);

        JPanel tabContainer = new JPanel(new BorderLayout());
        tabContainer.setBackground(Color.WHITE);
        tabContainer.setBorder(new EmptyBorder(0, 20, 20, 20));
        tabContainer.add(historyTabbedPane, BorderLayout.CENTER);
        add(tabContainer, BorderLayout.CENTER);
    }

    public void refreshData() {
        // TAB PENJUALAN
        List<Transaction> salesHistory = transactionController.getTransactionHistory();
        StringBuilder salesSb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        if (salesHistory.isEmpty()) {
            salesSb.append("Belum ada riwayat transaksi penjualan.");
        } else {
            for (Transaction t : salesHistory) {
                salesSb.append("ID: ").append(t.getTransactionId()).append(" | ")
                        .append("Waktu: ").append(sdf.format(t.getDate())).append(" | ")
                        .append("Total: Rp ").append(t.getTotal()).append("\n");
            }
        }
        salesTextArea.setText(salesSb.toString());

        // TAB OPNAME
        List<model.StockAdjustment> opnameHistory = stockController.getStockAdjustmentHistory();
        StringBuilder opnameSb = new StringBuilder();
        if (opnameHistory.isEmpty()) {
            opnameSb.append("Belum ada riwayat penyesuaian stok (opname).");
        } else {
            for (model.StockAdjustment adj : opnameHistory) {
                opnameSb.append("ID: ").append(adj.getId()).append("\n")
                        .append("Waktu: ").append(sdf.format(adj.getDate())).append("\n")
                        .append("Produk: ").append(adj.getProduct().getName()).append("\n")
                        .append("Perubahan: ").append(adj.getOldStock()).append(" -> ").append(adj.getNewStock())
                        .append(" (").append(adj.getDifference() > 0 ? "+" : "").append(adj.getDifference())
                        .append(")\n")
                        .append("Alasan: ").append(adj.getReason()).append("\n")
                        .append("--------------------------------------------------\n");
            }
        }
        opnameTextArea.setText(opnameSb.toString());
    }
}

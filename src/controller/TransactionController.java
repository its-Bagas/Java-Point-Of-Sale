package controller;

import model.DataStore;
import model.Product;
import model.Transaction;
import model.TransactionItem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TransactionController {
    private DataStore db;
    private int transactionCounter = 1;

    public TransactionController() {
        this.db = DataStore.getInstance();
    }

    private String generateTransactionId() {
        return String.format("TRX-%04d", transactionCounter++);
    }

    public Transaction processTransaction(List<TransactionItem> items, double paidAmount) throws Exception {
        if (items.isEmpty()) {
            throw new Exception("Keranjang belanja kosong.");
        }

        double total = 0;
        // Validasi stok terlebih dahulu sebelum memotong
        for (TransactionItem item : items) {
            Product p = item.getProduct();
            if (p.getStock() < item.getQuantity()) {
                throw new Exception("Stok tidak mencukupi untuk produk: " + p.getName() + " (Sisa: " + p.getStock() + ")");
            }
            total += item.getSubtotal();
        }

        if (paidAmount < total) {
            throw new Exception("Uang bayar tidak mencukupi. Total: " + total);
        }

        // Potong stok
        for (TransactionItem item : items) {
            Product p = item.getProduct();
            p.setStock(p.getStock() - item.getQuantity());
        }

        double change = paidAmount - total;
        Transaction transaction = new Transaction(generateTransactionId(), new Date(), items, total, paidAmount, change);
        db.getTransactions().add(transaction);

        return transaction;
    }

    public String generateReceipt(Transaction t) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        sb.append("====================================\n");
        sb.append("         Toko Anda (POS)            \n");
        sb.append("====================================\n");
        sb.append("No Transaksi : ").append(t.getTransactionId()).append("\n");
        sb.append("Tanggal      : ").append(sdf.format(t.getDate())).append("\n");
        sb.append("------------------------------------\n");
        
        for (TransactionItem item : t.getItems()) {
            // Baris 1: Nama Produk (Bisa panjang)
            sb.append(item.getProduct().getName()).append("\n");
            
            // Baris 2: Qty x Harga (Kiri) & Subtotal (Kanan)
            String qtyPrice = String.format("%dx @%.0f", item.getQuantity(), item.getProduct().getPrice());
            String subtotal = String.format("%.0f", item.getSubtotal());
            
            int spaces = 36 - qtyPrice.length() - subtotal.length();
            if (spaces < 1) spaces = 1;
            
            sb.append(qtyPrice);
            for (int i = 0; i < spaces; i++) sb.append(" ");
            sb.append(subtotal).append("\n");
        }
        
        sb.append("------------------------------------\n");
        sb.append(String.format("Total        : %20.0f\n", t.getTotal()));
        sb.append(String.format("Bayar        : %20.0f\n", t.getPaidAmount()));
        sb.append(String.format("Kembalian    : %20.0f\n", t.getChange()));
        sb.append("====================================\n");
        sb.append("            Terima Kasih            \n");
        return sb.toString();
    }

    public void saveReceiptToFile(String receiptContent, String transactionId) throws IOException {
        File dir = new File("receipts");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File("receipts/Struk_" + transactionId + ".txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(receiptContent);
        }
    }

    public List<Transaction> getTransactionHistory() {
        return db.getTransactions();
    }
}

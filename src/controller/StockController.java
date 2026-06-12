package controller;

import model.DataStore;
import model.Product;

import java.util.ArrayList;
import java.util.List;

import java.util.Date;
import java.util.UUID;

public class StockController {
    private DataStore db;

    public StockController() {
        this.db = DataStore.getInstance();
    }

    public void addStock(String productId, int amount) throws Exception {
        if (amount <= 0) {
            throw new Exception("Jumlah stok harus lebih besar dari 0.");
        }
        Product p = getProductById(productId);
        if (p != null) {
            int oldStock = p.getStock();
            p.setStock(oldStock + amount);
            
            // Catat log sebagai penambahan biasa (tanpa alasan khusus, dicatat sebagai "Penambahan Stok Rutin")
            model.StockAdjustment log = new model.StockAdjustment(
                "ADJ-" + UUID.randomUUID().toString().substring(0, 8),
                p, oldStock, p.getStock(), amount, "Penambahan Stok Rutin", new Date()
            );
            db.getStockAdjustments().add(log);
        } else {
            throw new Exception("Produk tidak ditemukan.");
        }
    }

    // Fitur Opname (Penyesuaian Fisik)
    public void adjustStock(String productId, int actualStock, String reason) throws Exception {
        if (actualStock < 0) {
            throw new Exception("Stok aktual tidak boleh negatif.");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new Exception("Alasan penyesuaian stok harus diisi (contoh: salah input, barang rusak).");
        }
        
        Product p = getProductById(productId);
        if (p != null) {
            int oldStock = p.getStock();
            int difference = actualStock - oldStock;
            
            p.setStock(actualStock); // Override nilai stok
            
            // Catat ke log audit
            model.StockAdjustment log = new model.StockAdjustment(
                "ADJ-" + UUID.randomUUID().toString().substring(0, 8),
                p, oldStock, actualStock, difference, reason, new Date()
            );
            db.getStockAdjustments().add(log);
        } else {
            throw new Exception("Produk tidak ditemukan.");
        }
    }

    public List<model.StockAdjustment> getStockAdjustmentHistory() {
        return db.getStockAdjustments();
    }

    public Product getProductById(String id) {
        for (Product p : db.getProducts()) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }
    
    public List<Product> getProductsWithLowStock(int threshold) {
        List<Product> lowStock = new ArrayList<>();
        for (Product p : db.getProducts()) {
            if (p.getStock() <= threshold) {
                lowStock.add(p);
            }
        }
        return lowStock;
    }
}

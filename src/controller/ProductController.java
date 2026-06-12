package controller;

import model.DataStore;
import model.Product;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ProductController {
    private DataStore db;
    private int idCounter = 1;

    public ProductController() {
        this.db = DataStore.getInstance();
    }

    public List<Product> getAllProducts() {
        return db.getProducts();
    }

    public String generateId() {
        return String.format("PRD-%03d", idCounter++);
    }

    public void addProduct(String name, double price, int stock, File sourceImage) throws Exception {
        if (price < 0) throw new Exception("Harga tidak boleh negatif.");
        if (stock < 0) throw new Exception("Stok tidak boleh negatif.");
        
        String id = generateId();
        String imagePath = null;

        if (sourceImage != null) {
            imagePath = copyImageFile(sourceImage, id);
        }

        Product product = new Product(id, name, price, stock, imagePath);
        db.getProducts().add(product);
    }

    public void editProduct(String id, String name, double price, File sourceImage) throws Exception {
        if (price < 0) throw new Exception("Harga tidak boleh negatif.");
        
        Product p = getProductById(id);
        if (p == null) throw new Exception("Produk tidak ditemukan.");

        p.setName(name);
        p.setPrice(price);

        if (sourceImage != null) {
            String imagePath = copyImageFile(sourceImage, id);
            p.setImagePath(imagePath);
        }
    }

    public void deleteProduct(String id) throws Exception {
        Product p = getProductById(id);
        if (p != null) {
            // Hapus file gambar dari penyimpanan lokal jika ada
            if (p.getImagePath() != null && !p.getImagePath().isEmpty()) {
                File imgFile = new File(p.getImagePath());
                if (imgFile.exists()) {
                    imgFile.delete();
                }
            }
            db.getProducts().remove(p);
        } else {
            throw new Exception("Produk tidak ditemukan.");
        }
    }

    public Product getProductById(String id) {
        for (Product p : db.getProducts()) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    public List<Product> searchProducts(String keyword) {
        List<Product> result = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        String lowerKeyword = keyword.toLowerCase();
        for (Product p : db.getProducts()) {
            if (p.getName().toLowerCase().contains(lowerKeyword) || p.getId().toLowerCase().contains(lowerKeyword)) {
                result.add(p);
            }
        }
        return result;
    }

    private String copyImageFile(File source, String productId) throws IOException {
        File dir = new File("images");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String ext = "";
        String fileName = source.getName();
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            ext = fileName.substring(i);
        }
        
        Path destPath = Paths.get("images", productId + ext);
        Files.copy(source.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
        return destPath.toString();
    }
}

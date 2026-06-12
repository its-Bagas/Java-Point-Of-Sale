package model;

import java.util.Date;

public class StockAdjustment {
    private String id;
    private Product product;
    private int oldStock;
    private int newStock;
    private int difference;
    private String reason;
    private Date date;

    public StockAdjustment(String id, Product product, int oldStock, int newStock, int difference, String reason, Date date) {
        this.id = id;
        this.product = product;
        this.oldStock = oldStock;
        this.newStock = newStock;
        this.difference = difference;
        this.reason = reason;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public int getOldStock() {
        return oldStock;
    }

    public int getNewStock() {
        return newStock;
    }

    public int getDifference() {
        return difference;
    }

    public String getReason() {
        return reason;
    }

    public Date getDate() {
        return date;
    }
}

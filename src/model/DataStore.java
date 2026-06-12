package model;

import java.util.ArrayList;
import java.util.List;

public class DataStore {
    private static DataStore instance;
    private List<Product> products;
    private List<Transaction> transactions;
    private List<StockAdjustment> stockAdjustments;

    private DataStore() {
        products = new ArrayList<>();
        transactions = new ArrayList<>();
        stockAdjustments = new ArrayList<>();
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public List<StockAdjustment> getStockAdjustments() {
        return stockAdjustments;
    }
}

package model;

import java.util.Date;
import java.util.List;

public class Transaction {
    private String transactionId;
    private Date date;
    private List<TransactionItem> items;
    private double total;
    private double paidAmount;
    private double change;

    public Transaction(String transactionId, Date date, List<TransactionItem> items, double total, double paidAmount, double change) {
        this.transactionId = transactionId;
        this.date = date;
        this.items = items;
        this.total = total;
        this.paidAmount = paidAmount;
        this.change = change;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Date getDate() {
        return date;
    }

    public List<TransactionItem> getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public double getChange() {
        return change;
    }
}

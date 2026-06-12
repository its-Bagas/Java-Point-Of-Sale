package model;

public class TransactionItem {
    private Product product;
    private int quantity;
    private double subtotal;

    public TransactionItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.subtotal = product.getPrice() * quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.subtotal = this.product.getPrice() * quantity;
    }
}

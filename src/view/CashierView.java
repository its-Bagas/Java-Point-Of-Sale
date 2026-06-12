package view;

import controller.ProductController;
import controller.TransactionController;
import model.Product;
import model.Transaction;
import model.TransactionItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class CashierView extends JPanel {
    private TransactionController transactionController;
    private ProductController productController;

    private JLabel lblTotal;
    private JLabel lblChange;
    private JTextField txtPaid;
    private JTextField txtSearch;
    private DefaultTableModel cartModel;
    private JTable cartTable;
    private JPanel productGridPanel;

    private List<TransactionItem> cart;
    private double currentTotal = 0;

    public CashierView(TransactionController tc, ProductController pc) {
        this.transactionController = tc;
        this.productController = pc;
        this.cart = new ArrayList<>();

        initComponents();
        loadProductGrid("");
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        // Search Bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        searchPanel.add(new JLabel("Cari Produk: "));
        txtSearch = new JTextField(15);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JButton btnSearch = createStyledButton("Cari", new Color(52, 152, 219));
        JButton btnReset = createStyledButton("Reset", new Color(149, 165, 166));

        txtSearch.addActionListener(e -> loadProductGrid(txtSearch.getText()));
        btnSearch.addActionListener(e -> loadProductGrid(txtSearch.getText()));
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            loadProductGrid("");
        });

        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnReset);

        // Product Panel
        productGridPanel = new JPanel();
        productGridPanel.setLayout(new GridLayout(0, 4, 10, 10));
        productGridPanel.setBackground(Color.WHITE);
        productGridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.WHITE);
        wrapperPanel.add(productGridPanel, BorderLayout.NORTH);

        JScrollPane gridScroll = new JScrollPane(wrapperPanel);
        gridScroll.setBorder(BorderFactory.createTitledBorder("Pilih Produk"));
        gridScroll.setBackground(Color.WHITE);
        gridScroll.getViewport().setBackground(Color.WHITE);
        gridScroll.getVerticalScrollBar().setUnitIncrement(16);
        gridScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Search & Product Panel
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBackground(Color.WHITE);
        centerContainer.add(searchPanel, BorderLayout.NORTH);
        centerContainer.add(gridScroll, BorderLayout.CENTER);

        add(centerContainer, BorderLayout.CENTER);

        // Keranjang & Chekout Panel
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setPreferredSize(new Dimension(380, 0));
        rightPanel.setBackground(Color.WHITE);

        cartModel = new DefaultTableModel(new String[] { "Produk", "Harga", "Qty", "Subtotal" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartModel);
        cartTable.setRowHeight(30);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        cartTable.getTableHeader().setBackground(Color.WHITE);
        cartTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));
        cartTable.setBackground(Color.WHITE);
        cartTable.setFillsViewportHeight(true);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.setShowVerticalLines(false);
        cartTable.setGridColor(new Color(200, 200, 200));
        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.setBorder(BorderFactory.createTitledBorder("Keranjang Belanja"));
        cartScroll.setBackground(Color.WHITE);
        cartScroll.getViewport().setBackground(Color.WHITE);

        JPanel cartContainer = new JPanel(new BorderLayout());
        cartContainer.setBackground(Color.WHITE);
        cartContainer.add(cartScroll, BorderLayout.CENTER);

        // Cart Action
        JPanel cartBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        cartBtnPanel.setBackground(Color.WHITE);

        JButton btnEditQty = createStyledButton("Ubah Qty", new Color(243, 156, 18));
        btnEditQty.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnEditQty.addActionListener(e -> editCartItem());

        JButton btnRemoveItem = createStyledButton("Hapus Item", new Color(231, 76, 60));
        btnRemoveItem.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnRemoveItem.addActionListener(e -> removeCartItem());

        cartBtnPanel.add(btnEditQty);
        cartBtnPanel.add(btnRemoveItem);
        cartContainer.add(cartBtnPanel, BorderLayout.SOUTH);

        rightPanel.add(cartContainer, BorderLayout.CENTER);

        JPanel checkoutPanel = new JPanel(new GridLayout(4, 2, 5, 10));
        checkoutPanel.setBackground(Color.WHITE);
        checkoutPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Pembayaran"),
                new EmptyBorder(10, 5, 5, 5)));

        checkoutPanel.add(new JLabel("Total Tagihan:"));
        lblTotal = new JLabel("Rp 0.0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotal.setForeground(new Color(231, 76, 60));
        checkoutPanel.add(lblTotal);

        checkoutPanel.add(new JLabel("Uang Bayar:"));
        txtPaid = new JTextField();
        txtPaid.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Listener Real-Time Change Calculation
        txtPaid.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateChange();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateChange();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateChange();
            }
        });
        checkoutPanel.add(txtPaid);

        checkoutPanel.add(new JLabel("Kembalian:"));
        lblChange = new JLabel("Rp 0.0");
        lblChange.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblChange.setForeground(new Color(46, 204, 113));
        checkoutPanel.add(lblChange);

        JButton btnPay = createStyledButton("Bayar", new Color(46, 204, 113));
        btnPay.addActionListener(e -> processPayment());
        checkoutPanel.add(btnPay);

        JButton btnClearCart = createStyledButton("Kosongkan", new Color(149, 165, 166));
        btnClearCart.addActionListener(e -> clearCart());
        checkoutPanel.add(btnClearCart);

        rightPanel.add(checkoutPanel, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bg.brighter());
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void refreshData() {
        if (txtSearch != null) {
            txtSearch.setText("");
        }
        loadProductGrid("");
    }

    private void loadProductGrid(String keyword) {
        productGridPanel.removeAll();

        List<Product> products;
        if (keyword == null || keyword.trim().isEmpty()) {
            products = productController.getAllProducts();
        } else {
            products = productController.searchProducts(keyword);
        }

        for (Product p : products) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);

            card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 2));
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Image
            JLabel lblImg = new JLabel();
            lblImg.setHorizontalAlignment(SwingConstants.CENTER);
            if (p.getImagePath() != null && !p.getImagePath().isEmpty()) {

                ImageIcon icon = new ImageIcon(
                        new ImageIcon(p.getImagePath()).getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH));
                lblImg.setIcon(icon);
            } else {
                lblImg.setText("NO IMAGE");
            }
            lblImg.setPreferredSize(new Dimension(90, 90));
            card.add(lblImg, BorderLayout.CENTER);

            JPanel infoPanel = new JPanel(new GridLayout(2, 1));
            infoPanel.setBackground(Color.WHITE);
            infoPanel.setBorder(new EmptyBorder(5, 2, 5, 2));

            JLabel lblName = new JLabel(p.getName(), SwingConstants.CENTER);
            lblName.setFont(new Font("Segoe UI", Font.BOLD, 11));

            JLabel lblPrice = new JLabel("Rp" + p.getPrice() + " | Stk:" + p.getStock(), SwingConstants.CENTER);
            lblPrice.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            lblPrice.setForeground(new Color(100, 100, 100));

            infoPanel.add(lblName);
            infoPanel.add(lblPrice);
            card.add(infoPanel, BorderLayout.SOUTH);

            // Click Event
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (p.getStock() <= 0) {
                        JOptionPane.showMessageDialog(CashierView.this, "Stok habis untuk produk ini!");
                        return;
                    }
                    String qtyStr = JOptionPane.showInputDialog(CashierView.this,
                            "Masukkan jumlah untuk " + p.getName() + ":", "1");
                    if (qtyStr != null && !qtyStr.trim().isEmpty()) {
                        try {
                            int qty = Integer.parseInt(qtyStr);
                            addToCart(p, qty);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(CashierView.this, "Jumlah harus berupa angka.");
                        }
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    card.setBorder(BorderFactory.createLineBorder(new Color(46, 204, 113), 2)); // Ketebalan tetap 2px
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 2)); // Ketebalan tetap 2px
                }
            });

            productGridPanel.add(card);
        }

        productGridPanel.revalidate();
        productGridPanel.repaint();
    }

    private void addToCart(Product selectedProduct, int quantity) {
        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, "Jumlah harus lebih dari 0.");
            return;
        }

        // Cari apakah produk sudah ada di keranjang
        TransactionItem existingItem = null;
        int rowIndex = -1;
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).getProduct().getId().equals(selectedProduct.getId())) {
                existingItem = cart.get(i);
                rowIndex = i;
                break;
            }
        }

        int currentQtyInCart = (existingItem != null) ? existingItem.getQuantity() : 0;

        if (selectedProduct.getStock() < (quantity + currentQtyInCart)) {
            JOptionPane.showMessageDialog(this, "Stok tidak mencukupi! Sisa stok yang tersedia: "
                    + (selectedProduct.getStock() - currentQtyInCart));
            return;
        }

        if (existingItem != null) {
            // Update item yang sudah ada
            currentTotal -= existingItem.getSubtotal();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            currentTotal += existingItem.getSubtotal();

            // Update baris tabel
            cartModel.setValueAt(existingItem.getQuantity(), rowIndex, 2);
            cartModel.setValueAt(existingItem.getSubtotal(), rowIndex, 3);
        } else {
            // Tambahkan item baru
            TransactionItem item = new TransactionItem(selectedProduct, quantity);
            cart.add(item);
            currentTotal += item.getSubtotal();

            cartModel.addRow(new Object[] {
                    selectedProduct.getName(),
                    selectedProduct.getPrice(),
                    quantity,
                    item.getSubtotal()
            });
        }

        lblTotal.setText("Rp " + currentTotal);
        updateChange();
    }

    private void editCartItem() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item di keranjang yang ingin diubah jumlahnya.");
            return;
        }

        TransactionItem item = cart.get(selectedRow);
        String qtyStr = JOptionPane.showInputDialog(this,
                "Ubah jumlah untuk " + item.getProduct().getName() + ":",
                item.getQuantity());

        if (qtyStr != null && !qtyStr.trim().isEmpty()) {
            try {
                int newQty = Integer.parseInt(qtyStr);
                if (newQty <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Jumlah harus lebih dari 0. Gunakan 'Hapus Item' jika ingin membatalkan.");
                    return;
                }
                if (item.getProduct().getStock() < newQty) {
                    JOptionPane.showMessageDialog(this,
                            "Stok tidak mencukupi! Stok maksimal: " + item.getProduct().getStock());
                    return;
                }

                currentTotal -= item.getSubtotal();
                item.setQuantity(newQty);
                currentTotal += item.getSubtotal();

                cartModel.setValueAt(newQty, selectedRow, 2);
                cartModel.setValueAt(item.getSubtotal(), selectedRow, 3);
                lblTotal.setText("Rp " + currentTotal);
                updateChange();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka.");
            }
        }
    }

    private void removeCartItem() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih item di keranjang yang ingin dihapus.");
            return;
        }

        TransactionItem item = cart.get(selectedRow);
        currentTotal -= item.getSubtotal();

        cart.remove(selectedRow);
        cartModel.removeRow(selectedRow);

        lblTotal.setText("Rp " + currentTotal);
        updateChange();
    }

    private void updateChange() {
        try {
            double paid = Double.parseDouble(txtPaid.getText());
            double change = paid - currentTotal;
            if (change < 0) {
                lblChange.setText("Kurang: Rp " + Math.abs(change));
                lblChange.setForeground(new Color(231, 76, 60));
            } else {
                lblChange.setText("Rp " + change);
                lblChange.setForeground(new Color(46, 204, 113));
            }
        } catch (NumberFormatException e) {
            lblChange.setText("Rp 0.0");
            lblChange.setForeground(Color.BLACK);
        }
    }

    private void processPayment() {
        try {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Keranjang belanja masih kosong.");
                return;
            }
            if (txtPaid.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Harap masukkan uang pembayaran.");
                return;
            }

            double paid = Double.parseDouble(txtPaid.getText());
            if (paid < currentTotal) {
                JOptionPane.showMessageDialog(this, "Uang pembayaran kurang! Total tagihan adalah Rp " + currentTotal);
                return;
            }

            // Transaction Process and Receipt
            Transaction t = transactionController.processTransaction(cart, paid);
            String receipt = transactionController.generateReceipt(t);

            transactionController.saveReceiptToFile(receipt, t.getTransactionId());

            clearCart();
            loadProductGrid("");

            showReceiptPopup(receipt, t.getTransactionId(), t.getChange());

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Input bayar harus berupa angka valid.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showReceiptPopup(String receiptText, String transactionId, double change) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Pembayaran Sukses - Struk", true);
        dialog.setSize(350, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(Color.WHITE);

        // Header kembalian
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(new Color(46, 204, 113));
        headerPanel.setBorder(new EmptyBorder(15, 10, 15, 10));

        JLabel lblSuccess = new JLabel("TRANSAKSI BERHASIL", SwingConstants.CENTER);
        lblSuccess.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblSuccess.setForeground(Color.WHITE);

        JLabel lblChange = new JLabel("Kembalian: Rp " + change, SwingConstants.CENTER);
        lblChange.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblChange.setForeground(Color.WHITE);

        headerPanel.add(lblSuccess);
        headerPanel.add(lblChange);
        dialog.add(headerPanel, BorderLayout.NORTH);

        // Receipt Text Area
        JTextArea txtReceipt = new JTextArea(receiptText);
        txtReceipt.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtReceipt.setEditable(false);
        txtReceipt.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(txtReceipt);
        scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        dialog.add(scroll, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton btnSave = createStyledButton("Simpan Struk", new Color(52, 152, 219));
        JButton btnClose = createStyledButton("Tutup", new Color(149, 165, 166));

        btnSave.addActionListener(e -> {
            try {
                transactionController.saveReceiptToFile(receiptText, transactionId);
                JOptionPane.showMessageDialog(dialog, "Struk berhasil disimpan di folder 'receipts/'.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Gagal menyimpan struk: " + ex.getMessage());
            }
        });

        btnClose.addActionListener(e -> dialog.dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnClose);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void clearCart() {
        cart.clear();
        cartModel.setRowCount(0);
        currentTotal = 0;
        lblTotal.setText("Rp 0.0");
        lblChange.setText("Rp 0.0");
        txtPaid.setText("");
    }
}

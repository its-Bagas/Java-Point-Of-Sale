package view;

import controller.ProductController;
import controller.StockController;
import model.Product;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class ProductView extends JPanel {
    private ProductController controller;
    private StockController stockController;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField txtSearch;
    private JFrame parentFrame;

    private JPanel alertPanel;
    private JLabel lblAlertText;

    public ProductView(ProductController controller, StockController stockController, JFrame parentFrame) {
        this.controller = controller;
        this.stockController = stockController;
        this.parentFrame = parentFrame;
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        alertPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        alertPanel.setBackground(new Color(253, 236, 234));
        alertPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(231, 76, 60)));

        lblAlertText = new JLabel("Peringatan: Stok produk menipis!");
        lblAlertText.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblAlertText.setForeground(new Color(192, 57, 43));
        alertPanel.add(lblAlertText);
        alertPanel.setVisible(false);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setBackground(Color.WHITE);

        JButton btnAdd = createStyledButton(" Tambah Produk", new Color(46, 204, 113));
        btnAdd.addActionListener(e -> showProductDialog(null));
        actionPanel.add(btnAdd);

        JPanel rightTopPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightTopPanel.setBackground(Color.WHITE);

        rightTopPanel.add(new JLabel("Cari Produk:"));
        txtSearch = new JTextField(15);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JButton btnSearch = createStyledButton("Cari", new Color(52, 73, 94));
        btnSearch.addActionListener(e -> searchProduct());

        rightTopPanel.add(txtSearch);
        rightTopPanel.add(btnSearch);

        topPanel.add(actionPanel, BorderLayout.WEST);
        topPanel.add(rightTopPanel, BorderLayout.EAST);

        JPanel northContainer = new JPanel();
        northContainer.setLayout(new BoxLayout(northContainer, BoxLayout.Y_AXIS));
        northContainer.setBackground(Color.WHITE);
        northContainer.add(alertPanel);
        northContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        northContainer.add(topPanel);

        add(northContainer, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[] { "ID", "Gambar", "Nama Produk", "Harga", "Stok", "Aksi" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1)
                    return ImageIcon.class;
                return Object.class;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(60);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));
        table.setBackground(Color.WHITE);
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.setShowVerticalLines(false);
        table.setGridColor(new Color(200, 200, 200));

        table.getColumnModel().getColumn(5).setPreferredWidth(270);

        DefaultTableCellRenderer centerHeaderRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 14));
                l.setBackground(Color.WHITE);
                l.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));
                return l;
            }
        };
        table.getColumnModel().getColumn(5).setHeaderRenderer(centerHeaderRenderer);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 4) {
                    int stock = Integer.parseInt(value.toString());
                    if (stock <= 5) {
                        c.setForeground(new Color(231, 76, 60));
                        c.setFont(new Font("Segoe UI", Font.BOLD, 15));
                    } else {
                        c.setForeground(isSelected ? Color.WHITE : Color.BLACK);
                        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    }
                } else {
                    c.setForeground(isSelected ? Color.WHITE : Color.BLACK);
                    c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                }
                return c;
            }
        });

        table.getColumnModel().getColumn(5).setCellRenderer(new ActionPanelRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ActionPanelEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void searchProduct() {
        String keyword = txtSearch.getText();
        List<Product> result = controller.searchProducts(keyword);
        refreshTableData(result);
    }

    public void refreshTable() {
        refreshTableData(controller.getAllProducts());
    }

    private void refreshTableData(List<Product> products) {
        tableModel.setRowCount(0);
        for (Product p : products) {
            ImageIcon icon = null;
            if (p.getImagePath() != null && !p.getImagePath().isEmpty()) {
                icon = new ImageIcon(
                        new ImageIcon(p.getImagePath()).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
            } else {
                icon = new ImageIcon(
                        new java.awt.image.BufferedImage(40, 40, java.awt.image.BufferedImage.TYPE_INT_ARGB));
            }

            tableModel.addRow(new Object[] {
                    p.getId(),
                    icon,
                    p.getName(),
                    "Rp " + p.getPrice(),
                    p.getStock(),
                    ""
            });
        }

        List<Product> lowStock = stockController.getProductsWithLowStock(5);
        if (lowStock.isEmpty()) {
            alertPanel.setVisible(false);
        } else {
            lblAlertText.setText("PERHATIAN: Terdapat " + lowStock.size()
                    + " produk yang stoknya menipis. Harap segera tambah stok!");
            alertPanel.setVisible(true);
        }
        alertPanel.revalidate();
    }

    // Pop Up Form Add/Edit Product
    private void showProductDialog(Product productToEdit) {
        JDialog dialog = new JDialog(parentFrame, productToEdit == null ? "Tambah Produk" : "Edit Produk", true);
        dialog.setSize(450, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JTextField txtName = new JTextField(20);
        JTextField txtPrice = new JTextField(20);
        JTextField txtStock = new JTextField(20);

        JLabel lblImagePreview = new JLabel("Tidak ada gambar", SwingConstants.CENTER);
        lblImagePreview.setPreferredSize(new Dimension(100, 100));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        final File[] selectedImage = new File[] { null };

        formPanel.add(new JLabel("Nama Produk:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtName, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Harga:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPrice, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Stok:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtStock, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Gambar:"), gbc);
        gbc.gridx = 1;
        JPanel imgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JButton btnBrowse = new JButton("Pilih...");
        btnBrowse.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                selectedImage[0] = fc.getSelectedFile();
                ImageIcon icon = new ImageIcon(new ImageIcon(selectedImage[0].getAbsolutePath()).getImage()
                        .getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                lblImagePreview.setIcon(icon);
                lblImagePreview.setText("");
            }
        });
        imgPanel.add(btnBrowse);
        formPanel.add(imgPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        formPanel.add(lblImagePreview, gbc);

        if (productToEdit != null) {
            txtName.setText(productToEdit.getName());
            txtPrice.setText(String.valueOf(productToEdit.getPrice()));
            txtStock.setText(String.valueOf(productToEdit.getStock()));
            txtStock.setEditable(false);
            txtStock.setToolTipText("Gunakan tombol 'Opname' di tabel untuk mengubah fisik stok");

            if (productToEdit.getImagePath() != null && !productToEdit.getImagePath().isEmpty()) {
                ImageIcon icon = new ImageIcon(new ImageIcon(productToEdit.getImagePath()).getImage()
                        .getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                lblImagePreview.setIcon(icon);
                lblImagePreview.setText("");
            }
        }

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = createStyledButton("Simpan", new Color(46, 204, 113));
        JButton btnCancel = createStyledButton("Batal", new Color(149, 165, 166));

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                String name = txtName.getText();
                double price = Double.parseDouble(txtPrice.getText().replace("Rp ", ""));

                if (productToEdit == null) {
                    int stock = Integer.parseInt(txtStock.getText());
                    controller.addProduct(name, price, stock, selectedImage[0]);
                    JOptionPane.showMessageDialog(dialog, "Produk berhasil ditambahkan!");
                } else {
                    controller.editProduct(productToEdit.getId(), name, price, selectedImage[0]);
                    JOptionPane.showMessageDialog(dialog, "Produk berhasil diperbarui!");
                }
                refreshTable();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Harga/Stok harus angka valid.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // Pop Up Opname (Stock Adjustment)
    private void showOpnameDialog(String productId, String productName) {
        Product p = controller.getProductById(productId);
        if (p == null)
            return;

        JDialog dialog = new JDialog(parentFrame, "Penyesuaian Stok (Opname)", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel lblInfo = new JLabel(
                "<html><b>" + productName + "</b><br>Stok Sistem Saat Ini: <b>" + p.getStock() + "</b></html>");
        JTextField txtActualStock = new JTextField(10);
        JTextField txtReason = new JTextField(20);

        gbc.gridwidth = 2;
        formPanel.add(lblInfo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        formPanel.add(new JLabel("Stok Aktual:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtActualStock, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Alasan Perubahan:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtReason, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = createStyledButton("Simpan Opname", new Color(230, 126, 34)); // Orange
        JButton btnCancel = createStyledButton("Batal", new Color(149, 165, 166));

        btnCancel.addActionListener(e -> dialog.dispose());
        btnSave.addActionListener(e -> {
            try {
                int actualStock = Integer.parseInt(txtActualStock.getText());
                String reason = txtReason.getText();
                stockController.adjustStock(productId, actualStock, reason);
                JOptionPane.showMessageDialog(dialog, "Opname berhasil disimpan! (Riwayat dicatat)");
                refreshTable();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Stok aktual harus angka bulat.", "Error Input",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // Action Renderers
    private JButton createTableButton(String text, Color bg) {
        JButton btn = createStyledButton(text, bg);
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        return btn;
    }

    class ActionPanelRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JButton btnAddStock = createTableButton("Stok", new Color(155, 89, 182));
        private JButton btnOpname = createTableButton("Opname", new Color(230, 126, 34));
        private JButton btnEdit = createTableButton("Edit", new Color(52, 152, 219));
        private JButton btnDelete = createTableButton("Hapus", new Color(231, 76, 60));

        public ActionPanelRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 12));
            setOpaque(true);
            setBackground(Color.WHITE);

            add(btnAddStock);
            add(btnOpname);
            add(btnEdit);
            add(btnDelete);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }

    class ActionPanelEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton btnAddStock;
        private JButton btnOpname;
        private JButton btnEdit;
        private JButton btnDelete;

        private String currentProductId;
        private String currentProductName;
        private boolean isPushed;
        private String actionType = "";

        public ActionPanelEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 12));
            panel.setBackground(Color.WHITE);

            btnAddStock = createTableButton("Stok", new Color(142, 68, 173));
            btnOpname = createTableButton("Opname", new Color(211, 84, 0));
            btnEdit = createTableButton("Edit", new Color(41, 128, 185));
            btnDelete = createTableButton("Hapus", new Color(192, 57, 43));

            panel.add(btnAddStock);
            panel.add(btnOpname);
            panel.add(btnEdit);
            panel.add(btnDelete);

            btnAddStock.addActionListener(e -> {
                actionType = "STOK";
                fireEditingStopped();
            });
            btnOpname.addActionListener(e -> {
                actionType = "OPNAME";
                fireEditingStopped();
            });
            btnEdit.addActionListener(e -> {
                actionType = "EDIT";
                fireEditingStopped();
            });
            btnDelete.addActionListener(e -> {
                actionType = "DELETE";
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentProductId = table.getValueAt(row, 0).toString();
            currentProductName = table.getValueAt(row, 2).toString();
            actionType = "";
            isPushed = true;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                if (actionType.equals("STOK")) {
                    String input = JOptionPane.showInputDialog(parentFrame,
                            "Masukan Jumlah stok yang ingin ditambahkan untuk : " + currentProductName,
                            "Tambah Stok", JOptionPane.QUESTION_MESSAGE);

                    if (input != null && !input.trim().isEmpty()) {
                        try {
                            int amount = Integer.parseInt(input);
                            stockController.addStock(currentProductId, amount);
                            JOptionPane.showMessageDialog(parentFrame,
                                    "Stok " + currentProductName + " berhasil ditambah " + amount + "!");
                            SwingUtilities.invokeLater(() -> refreshTable());
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(parentFrame, "Jumlah harus berupa angka valid.",
                                    "Error Input",
                                    JOptionPane.ERROR_MESSAGE);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else if (actionType.equals("OPNAME")) {
                    SwingUtilities.invokeLater(() -> showOpnameDialog(currentProductId, currentProductName));
                } else if (actionType.equals("EDIT")) {
                    Product p = controller.getProductById(currentProductId);
                    if (p != null) {
                        SwingUtilities.invokeLater(() -> showProductDialog(p));
                    }
                } else if (actionType.equals("DELETE")) {
                    int confirm = JOptionPane.showConfirmDialog(parentFrame, "Yakin menghapus produk ini?",
                            "Konfirmasi",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            controller.deleteProduct(currentProductId);
                            JOptionPane.showMessageDialog(parentFrame, "Produk berhasil dihapus!");
                            SwingUtilities.invokeLater(() -> refreshTable());
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
            isPushed = false;
            return "";
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}

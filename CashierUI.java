package ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import db.DBConnection;
import model.Product;
import model.SaleRecord;

public class CashierUI extends JFrame {

    // --- Cashier tab components ---
    private JComboBox<Product> productCombo;
    private JTextField qtyField;
    private JButton addButton;
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private JTextField paymentField;
    private JLabel changeLabel;
    private JButton payButton;
    private JButton clearButton;

    // --- Manage Products tab components ---
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JTextField editNameField;
    private JTextField editPriceField;
    private JTextField newNameField;
    private JTextField newPriceField;

    // --- Sales History tab components ---
    private JTable salesTable;
    private DefaultTableModel salesTableModel;

    private List<Product> products;
    private DecimalFormat df = new DecimalFormat("#,##0.00");

    public CashierUI() {
        setTitle("Cashier System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        initComponents();

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Cashier", buildCashierPanel());
        tabs.addTab("Manage Products", buildManageProductsPanel());
        tabs.addTab("Sales History", buildSalesHistoryPanel());

        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabs.getSelectedIndex() == 2) {
                    refreshSalesTable();
                }
            }
        });

        add(tabs);
        loadProductsIntoCombo();
    }

    private void initComponents() {
        // --- Cashier components ---
        productCombo = new JComboBox<>();
        qtyField = new JTextField(10);
        qtyField.setText("1");

        addButton = new JButton("Add to Cart");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addToCart();
            }
        });

        String[] columns = {"Product", "Price", "Qty", "Subtotal"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(tableModel);
        totalLabel = new JLabel("Total: $0.00");

        paymentField = new JTextField(10);
        paymentField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                autoCalculateChange();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                autoCalculateChange();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                autoCalculateChange();
            }
        });

        changeLabel = new JLabel("Change: $0.00");

        payButton = new JButton("Pay");
        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processPayment();
            }
        });

        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAll();
            }
        });

        // --- Manage Products components ---
        String[] prodCols = {"ID", "Name", "Price"};
        productTableModel = new DefaultTableModel(prodCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(productTableModel);

        editNameField = new JTextField(15);
        editPriceField = new JTextField(8);
        newNameField = new JTextField(15);
        newPriceField = new JTextField(8);

        // --- Sales History components ---
        String[] saleCols = {"ID", "Total", "Payment", "Change", "Date"};
        salesTableModel = new DefaultTableModel(saleCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        salesTable = new JTable(salesTableModel);
    }

    private JPanel buildCashierPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Product:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panel.add(productCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Quantity:"), gbc);

        gbc.gridx = 1;
        panel.add(qtyField, gbc);

        gbc.gridx = 2;
        panel.add(addButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        panel.add(new JScrollPane(cartTable), gbc);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        panel.add(totalLabel, gbc);

        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Payment:"), gbc);

        gbc.gridx = 1;
        panel.add(paymentField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(changeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(payButton, gbc);

        gbc.gridx = 2;
        panel.add(clearButton, gbc);

        return panel;
    }

    private JPanel buildManageProductsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Product table
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.gridheight = 3;
        panel.add(new JScrollPane(productTable), gbc);

        // --- Add Product section ---
        gbc.gridy = 3;
        gbc.gridheight = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridwidth = 4;
        panel.add(new JLabel("Add New Product:"), gbc);

        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(newNameField, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 3;
        panel.add(newPriceField, gbc);

        JButton addProductBtn = new JButton("Add Product");
        addProductBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewProduct();
            }
        });
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        panel.add(addProductBtn, gbc);

        // --- Edit/Delete section ---
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 4;
        panel.add(new JLabel("Selected Product:"), gbc);

        gbc.gridy = 7;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(editNameField, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 3;
        panel.add(editPriceField, gbc);

        gbc.gridy = 8;
        gbc.gridx = 2;
        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSelectedProduct();
            }
        });
        panel.add(saveBtn, gbc);

        gbc.gridx = 3;
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedProduct();
            }
        });
        panel.add(deleteBtn, gbc);

        // Selection listener for product table
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = productTable.getSelectedRow();
                if (row >= 0) {
                    editNameField.setText(productTableModel.getValueAt(row, 1).toString());
                    editPriceField.setText(productTableModel.getValueAt(row, 2).toString());
                }
            }
        });

        refreshProductTable();
        return panel;
    }

    private JPanel buildSalesHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(salesTable), BorderLayout.CENTER);
        return panel;
    }

    private void loadProductsIntoCombo() {
        try {
            products = DBConnection.loadProducts();
            DefaultComboBoxModel<Product> model = new DefaultComboBoxModel<>();
            for (Product p : products) {
                model.addElement(p);
            }
            productCombo.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Failed to load products from database:\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshProductTable() {
        productTableModel.setRowCount(0);
        try {
            List<Product> list = DBConnection.loadProducts();
            for (Product p : list) {
                productTableModel.addRow(new Object[]{
                    p.getId(), p.getName(), String.format("%.2f", p.getPrice())
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Failed to load products:\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshSalesTable() {
        salesTableModel.setRowCount(0);
        try {
            List<SaleRecord> sales = DBConnection.loadSales();
            for (SaleRecord s : sales) {
                salesTableModel.addRow(new Object[]{
                    s.getId(),
                    String.format("%.2f", s.getTotal()),
                    String.format("%.2f", s.getPayment()),
                    String.format("%.2f", s.getChangeAmount()),
                    s.getDate()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Failed to load sales:\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addNewProduct() {
        String name = newNameField.getText().trim();
        String priceText = newPriceField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Product name is required.",
                "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Product price is required.",
                "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price must be a valid number.",
                "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (price < 0) {
            JOptionPane.showMessageDialog(this, "Price cannot be negative.",
                "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            DBConnection.addProduct(name, price);
            newNameField.setText("");
            newPriceField.setText("");
            refreshProductTable();
            loadProductsIntoCombo();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Failed to add product:\n" + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSelectedProduct() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a product to edit.",
                "Edit Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) productTableModel.getValueAt(row, 0);
        String name = editNameField.getText().trim();
        String priceText = editPriceField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Product name is required.",
                "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Product price is required.",
                "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price must be a valid number.",
                "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (price < 0) {
            JOptionPane.showMessageDialog(this, "Price cannot be negative.",
                "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            DBConnection.updateProduct(id, name, price);
            refreshProductTable();
            loadProductsIntoCombo();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Failed to update product:\n" + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedProduct() {
        int row = productTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a product to delete.",
                "Delete Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) productTableModel.getValueAt(row, 0);
        String name = (String) productTableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete \"" + name + "\"?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DBConnection.deleteProduct(id);
                refreshProductTable();
                loadProductsIntoCombo();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                    "Failed to delete product:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ---- Cashier operations (unchanged) ----

    private void addToCart() {
        Product selected = (Product) productCombo.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                "No product selected.",
                "Cart Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String qtyText = qtyField.getText().trim();
        if (qtyText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Quantity cannot be empty.",
                "Input Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Quantity must be a valid number.",
                "Input Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (qty <= 0) {
            JOptionPane.showMessageDialog(this,
                "Quantity must be greater than zero.",
                "Input Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        double subtotal = selected.getPrice() * qty;
        tableModel.addRow(new Object[]{
            selected.getName(),
            String.format("$%.2f", selected.getPrice()),
            qty,
            String.format("$%.2f", subtotal)
        });

        updateTotal();
        qtyField.setText("1");
    }

    private void updateTotal() {
        double total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String subStr = (String) tableModel.getValueAt(i, 3);
            total += Double.parseDouble(subStr.replace("$", ""));
        }
        totalLabel.setText("Total: $" + df.format(total));
        autoCalculateChange();
    }

    private void autoCalculateChange() {
        double total = getTotalValue();
        String paymentText = paymentField.getText().trim();

        if (paymentText.isEmpty()) {
            changeLabel.setText("Change: $0.00");
            return;
        }

        try {
            double payment = Double.parseDouble(paymentText);
            if (payment < 0) {
                changeLabel.setText("Change: $0.00");
                return;
            }
            double change = payment - total;
            changeLabel.setText("Change: $" + df.format(change >= 0 ? change : 0));
        } catch (NumberFormatException e) {
            changeLabel.setText("Change: $0.00");
        }
    }

    private double getTotalValue() {
        double total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String subStr = (String) tableModel.getValueAt(i, 3);
            total += Double.parseDouble(subStr.replace("$", ""));
        }
        return total;
    }

    private void processPayment() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "Cart is empty. Add items before paying.",
                "Payment Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = getTotalValue();
        String paymentText = paymentField.getText().trim();

        if (paymentText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a payment amount.",
                "Payment Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        double payment;
        try {
            payment = Double.parseDouble(paymentText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Payment must be a valid number.",
                "Payment Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (payment < 0) {
            JOptionPane.showMessageDialog(this,
                "Payment cannot be negative.",
                "Payment Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (payment < total) {
            JOptionPane.showMessageDialog(this,
                "Insufficient payment. Total is $" + df.format(total) +
                " but payment is $" + df.format(payment) + ".",
                "Payment Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        double change = payment - total;

        try {
            DBConnection.insertSale(total, payment, change);
            JOptionPane.showMessageDialog(this,
                "Sale completed successfully!\n" +
                "Total: $" + df.format(total) + "\n" +
                "Payment: $" + df.format(payment) + "\n" +
                "Change: $" + df.format(change),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            clearAll();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Failed to record sale:\n" + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearAll() {
        tableModel.setRowCount(0);
        totalLabel.setText("Total: $0.00");
        paymentField.setText("");
        changeLabel.setText("Change: $0.00");
        qtyField.setText("1");
        if (products != null && !products.isEmpty()) {
            productCombo.setSelectedIndex(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CashierUI().setVisible(true);
            }
        });
    }
}
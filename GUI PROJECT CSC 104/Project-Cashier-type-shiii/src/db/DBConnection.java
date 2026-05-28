package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Displayable;
import model.Product;
import model.SaleRecord;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3307/cashierdb";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection;

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    public static List<Product> loadProducts() throws SQLException {
        String sql = "SELECT id, name, price FROM products ORDER BY name";
        List<Product> products = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                products.add(new Product(id, name, price));
            }
        }

        return products;
    }

    public static void insertSale(double total, double payment, double change) throws SQLException {
        String sql = "INSERT INTO sales (total, payment, change_amount) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, total);
            stmt.setDouble(2, payment);
            stmt.setDouble(3, change);
            stmt.executeUpdate();
        }
    }

    public static void addProduct(String name, double price) throws SQLException {
        String sql = "INSERT INTO products (name, price) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.executeUpdate();
        }
    }

    public static void updateProduct(int id, String name, double price) throws SQLException {
        String sql = "UPDATE products SET name = ?, price = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        }
    }

    public static void deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public static List<SaleRecord> loadSales() throws SQLException {
        String sql = "SELECT id, total, payment, change_amount, date FROM sales ORDER BY id DESC";
        List<SaleRecord> sales = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                double total = rs.getDouble("total");
                double payment = rs.getDouble("payment");
                double change = rs.getDouble("change_amount");
                String date = rs.getString("date");
                sales.add(new SaleRecord(id, total, payment, change, date));
            }
        }

        return sales;
    }

    public static List<Displayable> getAllDisplayables() throws SQLException {
        List<Displayable> list = new ArrayList<>();
        list.addAll(loadProducts());
        list.addAll(loadSales());
        return list;
    }
}
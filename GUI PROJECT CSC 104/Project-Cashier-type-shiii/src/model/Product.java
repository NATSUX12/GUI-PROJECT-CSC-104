package model;

import java.text.DecimalFormat;

public class Product extends SystemEntity implements Displayable, Comparable<Product> {

    private String name;
    private double price;

    private static final DecimalFormat df = new DecimalFormat("#,##0.00");

    public Product(int id, String name, double price) {
        super(id);
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String getDisplayName() {
        return name;
    }

    @Override
    public String getSummary() {
        return "Product: " + name + " | Price: P" + df.format(price);
    }

    @Override
    public String toString() {
        return name + " - $" + String.format("%.2f", price);
    }

    @Override
    public int compareTo(Product other) {
        return this.name.compareTo(other.name);
    }
}
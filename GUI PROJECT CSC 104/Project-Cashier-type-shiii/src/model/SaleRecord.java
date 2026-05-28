package model;

import java.text.DecimalFormat;

public class SaleRecord extends SystemEntity implements Displayable {

    private double total;
    private double payment;
    private double changeAmount;
    private String date;

    private static final DecimalFormat df = new DecimalFormat("#,##0.00");

    public SaleRecord(int id, double total, double payment, double changeAmount, String date) {
        super(id);
        this.total = total;
        this.payment = payment;
        this.changeAmount = changeAmount;
        this.date = date;
    }

    @Override
    public String getDisplayName() {
        return "Sale #" + getId() + " - P" + df.format(total);
    }

    @Override
    public String getSummary() {
        return "Sale: P" + df.format(total) + " | Change: P" + df.format(changeAmount);
    }

    public double getTotal() {
        return total;
    }

    public double getPayment() {
        return payment;
    }

    public double getChangeAmount() {
        return changeAmount;
    }

    public String getDate() {
        return date;
    }
}
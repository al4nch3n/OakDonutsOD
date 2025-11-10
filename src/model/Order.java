package model;

import java.sql.Timestamp;

public class Order {
    private int id;
    private Timestamp date;
    private String items;
    private double total;

    public Order(int id, Timestamp date, String items, double total) {
        this.id = id;
        this.date = date;
        this.items = items;
        this.total = total;
    }

    public Order(Timestamp date, String items, double total) {
        this.date = date;
        this.items = items;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getItems() {
        return items;
    }

    public double getTotal() {
        return total;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Order #" + id + " | " + date + " | $" + total;
    }
}
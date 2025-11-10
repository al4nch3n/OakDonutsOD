package gui;

import database.DatabaseHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.sql.Timestamp;

public class MainFrame extends JFrame {
    private JTable donutTable;
    private DefaultTableModel donutModel;
    private JTextArea orderArea;
    private double total = 0;

    public MainFrame() {
        setTitle("Oak Donuts OD");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        DatabaseHelper.initializeDatabase();
        loadDonuts();

        donutModel = new DefaultTableModel(new String[]{"ID", "Name", "Price"}, 0);
        donutTable = new JTable(donutModel);
        JScrollPane tableScroll = new JScrollPane(donutTable);

        JButton addOrderBtn = new JButton("Add to Order");
        JButton saveOrderBtn = new JButton("Save Order");
        JButton addDonutBtn = new JButton("Add Donut");
        JButton deleteDonutBtn = new JButton("Delete Donut");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addOrderBtn);
        buttonPanel.add(saveOrderBtn);
        buttonPanel.add(addDonutBtn);
        buttonPanel.add(deleteDonutBtn);

        orderArea = new JTextArea();
        JScrollPane orderScroll = new JScrollPane(orderArea);

        add(tableScroll, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.NORTH);
        add(orderScroll, BorderLayout.SOUTH);

        addOrderBtn.addActionListener(this::addToOrder);
        saveOrderBtn.addActionListener(this::saveOrder);
        addDonutBtn.addActionListener(this::addDonut);
        deleteDonutBtn.addActionListener(this::deleteDonut);

        refreshDonutTable();
    }

    private void loadDonuts() {
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM donuts")) {
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.executeUpdate("INSERT INTO donuts (name, price) VALUES " +
                        "('Glazed', 1.25), ('Chocolate', 1.50), ('Strawberry', 1.75)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshDonutTable() {
        donutModel.setRowCount(0);
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM donuts")) {
            while (rs.next()) {
                donutModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addToOrder(ActionEvent e) {
        int row = donutTable.getSelectedRow();
        if (row == -1) return;
        String name = donutModel.getValueAt(row, 1).toString();
        double price = Double.parseDouble(donutModel.getValueAt(row, 2).toString());
        orderArea.append(name + " - $" + price + "\n");
        total += price;
    }

    private void saveOrder(ActionEvent e) {
        if (orderArea.getText().isEmpty()) return;
        Timestamp now = new Timestamp(System.currentTimeMillis());
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO orders (date, items, total) VALUES (?, ?, ?)")) {
            ps.setTimestamp(1, now);
            ps.setString(2, orderArea.getText());
            ps.setDouble(3, total);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Order saved!");
            orderArea.setText("");
            total = 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void addDonut(ActionEvent e) {
        String name = JOptionPane.showInputDialog(this, "Donut name:");
        String priceStr = JOptionPane.showInputDialog(this, "Price:");
        if (name == null || priceStr == null) return;
        try {
            double price = Double.parseDouble(priceStr);
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                         "INSERT INTO donuts (name, price) VALUES (?, ?)")) {
                ps.setString(1, name);
                ps.setDouble(2, price);
                ps.executeUpdate();
                refreshDonutTable();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void deleteDonut(ActionEvent e) {
        int row = donutTable.getSelectedRow();
        if (row == -1) return;
        int id = (int) donutModel.getValueAt(row, 0);
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM donuts WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            refreshDonutTable();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
package com.booksales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Sale {
    private int saleId;
    private int customerId;
    private int bookId;
    private int quantity;
    private double totalPrice;
    private String saleDate;

    // Constructors
    public Sale() {}

    public Sale(int saleId, int customerId, int bookId, int quantity, double totalPrice, String saleDate) {
        this.saleId = saleId;
        this.customerId = customerId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.saleDate = saleDate;
    }

    // Getters and Setters
    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    // CRUD Operations

    // Create: Add a new sale
    public void addSale() {
        String saleQuery = "INSERT INTO sales (customer_id, book_id, quantity, total_price, sale_date) VALUES (?, ?, ?, ?, ?)";
        String updateStockQuery = "UPDATE books SET stock = stock - ? WHERE book_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            // Start a transaction
            conn.setAutoCommit(false);

            // Step 1: Add the sale
            try (PreparedStatement saleStmt = conn.prepareStatement(saleQuery)) {
                saleStmt.setInt(1, this.customerId);
                saleStmt.setInt(2, this.bookId);
                saleStmt.setInt(3, this.quantity);
                saleStmt.setDouble(4, this.totalPrice);
                saleStmt.setString(5, this.saleDate);
                saleStmt.executeUpdate();
            }

            // Step 2: Update the book's stock
            try (PreparedStatement updateStockStmt = conn.prepareStatement(updateStockQuery)) {
                updateStockStmt.setInt(1, this.quantity);
                updateStockStmt.setInt(2, this.bookId);
                updateStockStmt.executeUpdate();
            }

            // Commit the transaction
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            // Rollback the transaction in case of an error
            try (Connection conn = DBConnection.getConnection()) {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        }
    // Read: Get all sales
    public static List<Sale> getAllSales() {
        List<Sale> sales = new ArrayList<>();
        String query = "SELECT * FROM sales";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Sale sale = new Sale(
                    rs.getInt("sale_id"),
                    rs.getInt("customer_id"),
                    rs.getInt("book_id"),
                    rs.getInt("quantity"),
                    rs.getDouble("total_price"),
                    rs.getString("sale_date")
                );
                sales.add(sale);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sales;
    }

    // Update: Update an existing sale
    public void updateSale() {
        String query = "UPDATE sales SET customer_id = ?, book_id = ?, quantity = ?, total_price = ?, sale_date = ? WHERE sale_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, this.customerId);
            stmt.setInt(2, this.bookId);
            stmt.setInt(3, this.quantity);
            stmt.setDouble(4, this.totalPrice);
            stmt.setString(5, this.saleDate);
            stmt.setInt(6, this.saleId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete: Delete a sale by ID
    public static void deleteSale(int saleId) {
        String query = "DELETE FROM sales WHERE sale_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, saleId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static String processSale(int customerId, int bookId, int quantity) {
    // Step 1: Check if there's enough stock
    int currentStock = Book.getStock(bookId);
    if (currentStock < quantity) {
        return "Insufficient stock! Only " + currentStock + " items left.";
    }

    // Step 2: Calculate the total price
    Book book = Book.getBookById(bookId);
    if (book == null) {
        return "Book not found!";
    }
    double totalPrice = quantity * book.getPrice();

    // Step 3: Add the sale and update the stock
    Sale sale = new Sale();
    sale.setCustomerId(customerId);
    sale.setBookId(bookId);
    sale.setQuantity(quantity);
    sale.setTotalPrice(totalPrice);
    sale.setSaleDate(new java.sql.Date(System.currentTimeMillis()).toString()); // Use current date
    sale.addSale(); // Add the sale and update the stock

    return "Sale completed successfully!";
}
}

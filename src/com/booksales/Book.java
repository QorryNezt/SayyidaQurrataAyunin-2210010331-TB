package com.booksales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Book {
    private int bookId;
    private String title;
    private String author;
    private double price;
    private int stock;

    // Constructors
    public Book() {}

    public Book(int bookId, String title, String author, double price, int stock) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
    }

    public static void decreaseStock(int bookId, int quantity) {
    String query = "UPDATE books SET stock = stock - ? WHERE book_id = ? AND stock >= ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, quantity);
        stmt.setInt(2, bookId);
        stmt.setInt(3, quantity); // Ensure stock does not go negative
        int rowsAffected = stmt.executeUpdate();
        
        if (rowsAffected == 0) {
            System.out.println("Not enough stock available for book ID: " + bookId);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    
    // Getters and Setters
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    // CRUD Operations

    // Create: Add a new book
    public void addBook() {
        String query = "INSERT INTO books (title, author, price, stock) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, this.title);
            stmt.setString(2, this.author);
            stmt.setDouble(3, this.price);
            stmt.setInt(4, this.stock);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Read: Get all books
    public static List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Book book = new Book(
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getDouble("price"),
                    rs.getInt("stock")
                );
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    // Read: Get a single book by ID
    // In Book.java
public static int getStock(int bookId) {
    int stock = 0;
    String query = "SELECT stock FROM books WHERE book_id = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, bookId);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                stock = rs.getInt("stock");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return stock;
}

public static Book getBookById(int bookId) {
    Book book = null;
    String query = "SELECT * FROM books WHERE book_id = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, bookId);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                book = new Book(
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getDouble("price"),
                    rs.getInt("stock")
                );
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return book;
}

    // Update: Update an existing book
    public void updateBook() {
        String query = "UPDATE books SET title = ?, author = ?, price = ?, stock = ? WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, this.title);
            stmt.setString(2, this.author);
            stmt.setDouble(3, this.price);
            stmt.setInt(4, this.stock);
            stmt.setInt(5, this.bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete: Delete a book by ID
    public static void deleteBook(int bookId) {
        String query = "DELETE FROM books WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
package com.booksales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Asus
 */
public class Book {
    private int bookId;
    private String title;
    private String author;
    private double price;
    private int stock;

    // Getters and Setters

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
}

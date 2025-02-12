/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.booksales;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Asus
 */
public class salesFrame extends javax.swing.JFrame {

    /**
     * Creates new form salesFrame
     */
    public salesFrame() {
        initComponents();
        loadCustomersIntoComboBox();
        loadBooksIntoComboBox();
        loadSalesIntoTable();
    }

private void clearInputFields() {
    cbbCustomer.setSelectedIndex(0);
    cbbBook.setSelectedIndex(0);
    txtQty.setText("");
    txtPrice.setText("");
}
    
    private void calculateTotalPrice() {
    try {
        // Get the selected book's price
        String bookInfo = cbbBook.getSelectedItem().toString();
        int bookId = Integer.parseInt(bookInfo.split(" - ")[0]); // Extract book ID
        Book book = Book.getBookById(bookId);

        // Get the quantity
        int quantity = Integer.parseInt(txtQty.getText().trim());

        // Calculate the total price
        double totalPrice = book.getPrice() * quantity;

        // Set the total price in the text field
        txtPrice.setText(String.valueOf(totalPrice));
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Please enter a valid quantity!", "Error", JOptionPane.ERROR_MESSAGE);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    
    private String getCustomerNameById(int customerId) {
    try (Connection con = DBConnection.getConnection()) {
        String sql = "SELECT name FROM customers WHERE customer_id = ?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, customerId);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getString("name");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error fetching customer name: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return "";
}

private String getBookTitleById(int bookId) {
    try (Connection con = DBConnection.getConnection()) {
        String sql = "SELECT title FROM books WHERE book_id = ?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, bookId);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getString("title");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error fetching book title: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    return "";
}
    
    private void loadSalesIntoTable() {
    try {
        // Step 1: Define the column names for the table
        String[] columnNames = {"Sale ID", "Customer ID", "Book ID", "Quantity", "Total Price", "Sale Date"};

        // Step 2: Create a DefaultTableModel with the column names
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        // Step 3: Retrieve data from the database
        List<Sale> sales = Sale.getAllSales();

        // Step 4: Add each sale to the table model
        for (Sale sale : sales) {
            Object[] row = {
                sale.getSaleId(),
                sale.getCustomerId(),
                sale.getBookId(),
                sale.getQuantity(),
                sale.getTotalPrice(),
                sale.getSaleDate()
            };
            model.addRow(row);
        }

        // Step 5: Set the model to the jTable
        tbSales.setModel(model);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error connecting to the database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}

    
    private void loadCustomersIntoComboBox() {
    try (Connection con = DBConnection.getConnection()) {
        String sql = "SELECT customer_id, name FROM customers";
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        cbbCustomer.removeAllItems(); // Clear existing items

        while (rs.next()) {
            int id = rs.getInt("customer_id");
            String name = rs.getString("name");
            cbbCustomer.addItem(id + " - " + name);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading customers: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void loadBooksIntoComboBox() {
    try (Connection con = DBConnection.getConnection()) {
        String sql = "SELECT book_id, title FROM books";
        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        cbbBook.removeAllItems(); // Clear existing items

        while (rs.next()) {
            int id = rs.getInt("book_id");
            String title = rs.getString("title");
            cbbBook.addItem(id + " - " + title);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private double getBookPrice(String bookID) {
    double price = 0;
    try (Connection con = DBConnection.getConnection()) {
        String sql = "SELECT price FROM books WHERE book_id = ?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, Integer.parseInt(bookID));
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            price = rs.getDouble("price");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error getting book price: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
    return price;
}
    
    private int getBookStock(String bookID) {
    int stock = 0;
    try (Connection con = DBConnection.getConnection()) {
        String sql = "SELECT stock FROM books WHERE book_id = ?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, Integer.parseInt(bookID));
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            stock = rs.getInt("stock");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error getting book stock: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
    return stock;
}
    
    private void updateBookStock(String bookID, int quantitySold) {
    try (Connection con = DBConnection.getConnection()) {
        String sql = "UPDATE books SET stock = stock - ? WHERE book_id = ?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, quantitySold);
        pst.setInt(2, Integer.parseInt(bookID));
        pst.executeUpdate();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error updating book stock: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnExit = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbSales = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        cbbCustomer = new javax.swing.JComboBox<>();
        cbbBook = new javax.swing.JComboBox<>();
        txtQty = new javax.swing.JTextField();
        txtPrice = new javax.swing.JTextField();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Bahnschrift", 0, 24)); // NOI18N
        jLabel1.setText("Data Penjualan");

        btnExit.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        btnExit.setText("Exit");
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(333, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(118, 118, 118)
                .addComponent(btnExit)
                .addGap(14, 14, 14))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnExit)
                    .addComponent(jLabel1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        tbSales.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbSales.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbSalesMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbSales);

        jLabel2.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jLabel2.setText("ID Pelanggan");

        jLabel3.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jLabel3.setText("ID Buku");

        jLabel4.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jLabel4.setText("Jumlah");

        jLabel5.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jLabel5.setText("Total Harga");

        cbbCustomer.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        cbbCustomer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbbBook.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        cbbBook.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        txtQty.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        txtQty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtQtyFocusLost(evt);
            }
        });
        txtQty.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQtyActionPerformed(evt);
            }
        });
        txtQty.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtQtyKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtQtyKeyTyped(evt);
            }
        });

        txtPrice.setEditable(false);
        txtPrice.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N

        btnAdd.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        btnAdd.setText("Tambah");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnEdit.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        btnEdit.setText("Edit");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnDelete.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        btnDelete.setText("Hapus");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnCancel.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        btnCancel.setText("Batal");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(cbbCustomer, javax.swing.GroupLayout.Alignment.LEADING, 0, 157, Short.MAX_VALUE)
                        .addComponent(cbbBook, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtQty, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtPrice, javax.swing.GroupLayout.Alignment.LEADING)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(btnAdd)
                        .addGap(18, 18, 18)
                        .addComponent(btnEdit)
                        .addGap(18, 18, 18)
                        .addComponent(btnDelete)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancel)
                        .addGap(55, 55, 55)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbbCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbbBook, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtQty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdd)
                    .addComponent(btnEdit)
                    .addComponent(btnDelete)
                    .addComponent(btnCancel))
                .addContainerGap(51, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_END);
        getContentPane().add(jSeparator1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExitActionPerformed
        MainFrame mainForm = new MainFrame(); // Create MainFrame instance
    mainForm.setVisible(true); // Show MainFrame
    this.dispose(); // Close bookFrame
    }//GEN-LAST:event_btnExitActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
 try {
        // Retrieve values from form
        String customerInfo = cbbCustomer.getSelectedItem().toString();
        String bookInfo = cbbBook.getSelectedItem().toString();
        int quantity = Integer.parseInt(txtQty.getText().trim());
        double totalPrice = Double.parseDouble(txtPrice.getText().trim());

        // Extract customer ID and book ID from combo boxes
        int customerId = Integer.parseInt(customerInfo.split(" - ")[0]);
        int bookId = Integer.parseInt(bookInfo.split(" - ")[0]);

        // Check if the requested quantity is available in stock
        int currentStock = getBookStock(String.valueOf(bookId));
        if (quantity > currentStock) {
            JOptionPane.showMessageDialog(this, "Not enough stock available!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Insert the sale record into the database
        Connection con = DBConnection.getConnection();
        String query = "INSERT INTO sales (customer_id, book_id, quantity, total_price, sale_date) VALUES (?, ?, ?, ?, NOW())";
        PreparedStatement pst = con.prepareStatement(query);

        pst.setInt(1, customerId);
        pst.setInt(2, bookId);
        pst.setInt(3, quantity);
        pst.setDouble(4, totalPrice);
        pst.executeUpdate();

        // Reduce the stock of the sold book
        updateBookStock(String.valueOf(bookId), quantity);

        JOptionPane.showMessageDialog(this, "Sales Data Added Successfully!");

        // Refresh the table and clear fields
        loadSalesIntoTable();
        clearInputFields();

        pst.close();
        con.close();
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnAddActionPerformed

    private void txtQtyKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQtyKeyTyped
                // Code Placeholder (Not Used)
    }//GEN-LAST:event_txtQtyKeyTyped

    private void tbSalesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbSalesMouseClicked
       int selectedRow = tbSales.getSelectedRow();
    if (selectedRow != -1) { // Ensure a row is selected
        // Populate the combo boxes and text fields with the selected sale's data
        String customerId = tbSales.getValueAt(selectedRow, 1).toString(); // Customer ID
        String customerName = getCustomerNameById(Integer.parseInt(customerId)); // Customer Name
        cbbCustomer.setSelectedItem(customerId + " - " + customerName); // Set customer combo box

        String bookId = tbSales.getValueAt(selectedRow, 2).toString(); // Book ID
        String bookTitle = getBookTitleById(Integer.parseInt(bookId)); // Book Title
        cbbBook.setSelectedItem(bookId + " - " + bookTitle); // Set book combo box

        txtQty.setText(tbSales.getValueAt(selectedRow, 3).toString()); // Quantity
        txtPrice.setText(tbSales.getValueAt(selectedRow, 4).toString()); // Total Price
    }
    }//GEN-LAST:event_tbSalesMouseClicked

    private void txtQtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtQtyFocusLost
          calculateTotalPrice();
    }//GEN-LAST:event_txtQtyFocusLost

    private void txtQtyKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQtyKeyPressed
        
    }//GEN-LAST:event_txtQtyKeyPressed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        int selectedRow = tbSales.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih data yang ingin diedit!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int saleId = Integer.parseInt(tbSales.getValueAt(selectedRow, 0).toString());
    int customerId = ((Customer) cbbCustomer.getSelectedItem()).getCustomerId();
    int bookId = ((Book) cbbBook.getSelectedItem()).getBookId();
    int quantity = Integer.parseInt(txtQty.getText());
    double totalPrice = Double.parseDouble(txtPrice.getText());

    String query = "UPDATE sales SET customer_id=?, book_id=?, quantity=?, total_price=? WHERE sale_id=?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, customerId);
        stmt.setInt(2, bookId);
        stmt.setInt(3, quantity);
        stmt.setDouble(4, totalPrice);
        stmt.setInt(5, saleId);
        stmt.executeUpdate();

        JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!");
        loadSalesIntoTable(); // Refresh table
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal mengedit data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
         int selectedRow = tbSales.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int saleId = Integer.parseInt(tbSales.getValueAt(selectedRow, 0).toString());
    int bookId = Integer.parseInt(tbSales.getValueAt(selectedRow, 2).toString());
    int quantity = Integer.parseInt(tbSales.getValueAt(selectedRow, 3).toString());

    int confirm = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    try (Connection conn = DBConnection.getConnection()) {
        conn.setAutoCommit(false);

        // Delete sale record
        String deleteQuery = "DELETE FROM sales WHERE sale_id=?";
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
            deleteStmt.setInt(1, saleId);
            deleteStmt.executeUpdate();
        }

        // Restore stock
        String updateStockQuery = "UPDATE books SET stock = stock + ? WHERE book_id=?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateStockQuery)) {
            updateStmt.setInt(1, quantity);
            updateStmt.setInt(2, bookId);
            updateStmt.executeUpdate();
        }

        conn.commit();
        JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
        loadSalesIntoTable(); // Refresh table
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        clearInputFields();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtQtyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQtyActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtQtyActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(salesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(salesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(salesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(salesFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new salesFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnExit;
    private javax.swing.JComboBox<String> cbbBook;
    private javax.swing.JComboBox<String> cbbCustomer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable tbSales;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtQty;
    // End of variables declaration//GEN-END:variables
}

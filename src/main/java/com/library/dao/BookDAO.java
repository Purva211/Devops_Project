package com.library.dao;

import com.library.model.Book;
import com.library.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    /**
     * Adds a new book to the database.
     * When adding a new book, available_quantity is initially set equal to quantity.
     *
     * @param book Book object containing title, author, category, and total quantity
     * @return true if insertion succeeded, false otherwise
     */
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (title, author, category, quantity, available_quantity) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getCategory());
            ps.setInt(4, book.getQuantity());
            ps.setInt(5, book.getQuantity()); // available_quantity initially equals total quantity
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Database error during addBook: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all books from the database.
     * Maps every database column to the Book object.
     *
     * @return List of all Book objects
     */
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, title, author, category, quantity, available_quantity FROM books";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Database error during getAllBooks: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Retrieves a book by its unique ID.
     *
     * @param id Book ID
     * @return Book object if found, null otherwise
     */
    public Book getBookById(int id) {
        String sql = "SELECT id, title, author, category, quantity, available_quantity FROM books WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractBookFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Database error during getBookById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates book details (title, author, category, and quantity).
     * Preserves existing available_quantity to avoid overwriting issued book state.
     *
     * @param book Book object containing updated information and ID
     * @return true if updated successfully, false otherwise
     */
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, category = ?, quantity = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getCategory());
            ps.setInt(4, book.getQuantity());
            ps.setInt(5, book.getId());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Database error during updateBook: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a book by its unique ID.
     *
     * @param id Book ID
     * @return true if a row was deleted, false otherwise
     */
    public boolean deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Database error during deleteBook: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Searches for books by title or author using a keyword.
     * Uses LIKE with PreparedStatement for case-insensitive matching.
     *
     * @param keyword Search keyword
     * @return List of matching Book objects
     */
    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT id, title, author, category, quantity, available_quantity FROM books WHERE title LIKE ? OR author LIKE ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + (keyword != null ? keyword.trim() : "") + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(extractBookFromResultSet(rs));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Database error during searchBooks: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Helper method to map a ResultSet row to a Book object.
     *
     * @param rs ResultSet positioned at current row
     * @return Book object
     * @throws SQLException if a database access error occurs
     */
    private Book extractBookFromResultSet(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("category"),
            rs.getInt("quantity"),
            rs.getInt("available_quantity")
        );
    }
}

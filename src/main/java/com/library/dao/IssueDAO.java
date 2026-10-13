package com.library.dao;

import com.library.model.Issue;
import com.library.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IssueDAO {

    /**
     * Issues a book to a user.
     * Transaction logic:
     * 1. Check availability of the book (available_quantity > 0)
     * 2. Ensure user has not already actively issued this book
     * 3. Insert record into issues table (issue_date = CURDATE(), status = 'ISSUED')
     * 4. Decrement available_quantity in books table by 1
     *
     * @param userId ID of the student
     * @param bookId ID of the book to issue
     * @return true if successfully issued, false otherwise
     */
    public boolean issueBook(int userId, int bookId) {
        String checkAvailabilitySql = "SELECT available_quantity FROM books WHERE id = ? FOR UPDATE";
        String checkAlreadyIssuedSql = "SELECT COUNT(*) FROM issues WHERE user_id = ? AND book_id = ? AND status = 'ISSUED'";
        String insertIssueSql = "INSERT INTO issues (user_id, book_id, issue_date, status) VALUES (?, ?, CURDATE(), 'ISSUED')";
        String decrementBookSql = "UPDATE books SET available_quantity = available_quantity - 1 WHERE id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // 1. Check book availability
            try (PreparedStatement psCheck = conn.prepareStatement(checkAvailabilitySql)) {
                psCheck.setInt(1, bookId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false; // Book does not exist
                    }
                    int availableQuantity = rs.getInt("available_quantity");
                    if (availableQuantity <= 0) {
                        conn.rollback();
                        return false; // Out of stock
                    }
                }
            }

            // 2. Check if student already has this book issued and not yet returned
            try (PreparedStatement psAlready = conn.prepareStatement(checkAlreadyIssuedSql)) {
                psAlready.setInt(1, userId);
                psAlready.setInt(2, bookId);
                try (ResultSet rs = psAlready.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        conn.rollback();
                        return false; // Already issued to this student
                    }
                }
            }

            // 3. Insert issue record
            try (PreparedStatement psInsert = conn.prepareStatement(insertIssueSql)) {
                psInsert.setInt(1, userId);
                psInsert.setInt(2, bookId);
                int rowsInserted = psInsert.executeUpdate();
                if (rowsInserted <= 0) {
                    conn.rollback();
                    return false;
                }
            }

            // 4. Decrement available_quantity
            try (PreparedStatement psUpdate = conn.prepareStatement(decrementBookSql)) {
                psUpdate.setInt(1, bookId);
                int rowsUpdated = psUpdate.executeUpdate();
                if (rowsUpdated <= 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            System.err.println("Database error during issueBook: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Returns an issued book.
     * Transaction logic:
     * 1. Check issue status is 'ISSUED'
     * 2. Update issue record: status = 'RETURNED', return_date = CURDATE()
     * 3. Increment available_quantity in books table by 1
     *
     * @param issueId ID of the issue record
     * @return true if successfully returned, false otherwise
     */
    public boolean returnBook(int issueId) {
        String checkIssueSql = "SELECT book_id, status FROM issues WHERE id = ? FOR UPDATE";
        String updateIssueSql = "UPDATE issues SET status = 'RETURNED', return_date = CURDATE() WHERE id = ?";
        String incrementBookSql = "UPDATE books SET available_quantity = available_quantity + 1 WHERE id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            int bookId = -1;

            // 1. Verify issue record exists and is currently ISSUED
            try (PreparedStatement psCheck = conn.prepareStatement(checkIssueSql)) {
                psCheck.setInt(1, issueId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false; // Issue record not found
                    }
                    String currentStatus = rs.getString("status");
                    if (!"ISSUED".equalsIgnoreCase(currentStatus)) {
                        conn.rollback();
                        return false; // Already returned
                    }
                    bookId = rs.getInt("book_id");
                }
            }

            // 2. Update issue record
            try (PreparedStatement psUpdateIssue = conn.prepareStatement(updateIssueSql)) {
                psUpdateIssue.setInt(1, issueId);
                int rowsUpdated = psUpdateIssue.executeUpdate();
                if (rowsUpdated <= 0) {
                    conn.rollback();
                    return false;
                }
            }

            // 3. Increment available quantity of book
            try (PreparedStatement psUpdateBook = conn.prepareStatement(incrementBookSql)) {
                psUpdateBook.setInt(1, bookId);
                int rowsUpdated = psUpdateBook.executeUpdate();
                if (rowsUpdated <= 0) {
                    conn.rollback();
                    return false;
                }
            }

            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException e) {
            System.err.println("Database error during returnBook: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Retrieves all issue records for a specific student.
     *
     * @param userId Student user ID
     * @return List of Issue objects for this student
     */
    public List<Issue> getIssuesByUserId(int userId) {
        List<Issue> issues = new ArrayList<>();
        String sql = "SELECT i.id, i.user_id, i.book_id, i.issue_date, i.return_date, i.status, " +
                     "       b.title AS book_title, b.author AS book_author " +
                     "FROM issues i " +
                     "JOIN books b ON i.book_id = b.id " +
                     "WHERE i.user_id = ? " +
                     "ORDER BY i.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Issue issue = new Issue(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("book_id"),
                        rs.getDate("issue_date"),
                        rs.getDate("return_date"),
                        rs.getString("status"),
                        rs.getString("book_title"),
                        rs.getString("book_author"),
                        null,
                        null
                    );
                    issues.add(issue);
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error during getIssuesByUserId: " + e.getMessage());
            e.printStackTrace();
        }
        return issues;
    }

    /**
     * Retrieves all issued and returned book records for Admin overview.
     *
     * @return List of all Issue objects with user and book details
     */
    public List<Issue> getAllIssues() {
        List<Issue> issues = new ArrayList<>();
        String sql = "SELECT i.id, i.user_id, i.book_id, i.issue_date, i.return_date, i.status, " +
                     "       b.title AS book_title, b.author AS book_author, " +
                     "       u.name AS user_name, u.email AS user_email " +
                     "FROM issues i " +
                     "JOIN books b ON i.book_id = b.id " +
                     "JOIN users u ON i.user_id = u.id " +
                     "ORDER BY i.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Issue issue = new Issue(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("book_id"),
                    rs.getDate("issue_date"),
                    rs.getDate("return_date"),
                    rs.getString("status"),
                    rs.getString("book_title"),
                    rs.getString("book_author"),
                    rs.getString("user_name"),
                    rs.getString("user_email")
                );
                issues.add(issue);
            }

        } catch (SQLException e) {
            System.err.println("Database error during getAllIssues: " + e.getMessage());
            e.printStackTrace();
        }
        return issues;
    }

    /**
     * Returns the set of book IDs currently actively issued (status = 'ISSUED') to a given user.
     *
     * @param userId Student user ID
     * @return Set of active book IDs
     */
    public Set<Integer> getActiveIssuedBookIds(int userId) {
        Set<Integer> activeBookIds = new HashSet<>();
        String sql = "SELECT book_id FROM issues WHERE user_id = ? AND status = 'ISSUED'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    activeBookIds.add(rs.getInt("book_id"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error during getActiveIssuedBookIds: " + e.getMessage());
            e.printStackTrace();
        }
        return activeBookIds;
    }

    /**
     * Retrieves an issue by its ID.
     *
     * @param issueId Issue ID
     * @return Issue object if found, null otherwise
     */
    public Issue getIssueById(int issueId) {
        String sql = "SELECT i.id, i.user_id, i.book_id, i.issue_date, i.return_date, i.status, " +
                     "       b.title AS book_title, b.author AS book_author, " +
                     "       u.name AS user_name, u.email AS user_email " +
                     "FROM issues i " +
                     "JOIN books b ON i.book_id = b.id " +
                     "JOIN users u ON i.user_id = u.id " +
                     "WHERE i.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, issueId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Issue(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("book_id"),
                        rs.getDate("issue_date"),
                        rs.getDate("return_date"),
                        rs.getString("status"),
                        rs.getString("book_title"),
                        rs.getString("book_author"),
                        rs.getString("user_name"),
                        rs.getString("user_email")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Database error during getIssueById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}

package com.library.dao;

import com.library.model.User;
import com.library.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class UserDAO {

    /**
     * Registers a new user in the database.
     * New registrations default to the role "STUDENT".
     * Handles duplicate email constraint gracefully.
     * 
     * @param user User object containing name, email, password (and optional role)
     * @return true if registration succeeded, false if duplicate email or other SQL error
     */
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        
        // Ensure role is set, defaulting to STUDENT for new registrations
        String role = (user.getRole() != null && !user.getRole().trim().isEmpty()) ? user.getRole() : "STUDENT";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, role);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Registration failed: Duplicate email address - " + user.getEmail());
            return false;
        } catch (SQLException e) {
            // MySQL error code 1062 is duplicate entry
            if (e.getErrorCode() == 1062) {
                System.err.println("Registration failed: Duplicate email address - " + user.getEmail());
            } else {
                System.err.println("Database error during user registration: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Authenticates a user by email and password.
     * 
     * @param email User email
     * @param password User password
     * @return User object if credentials are correct, null otherwise
     */
    public User loginUser(String email, String password) {
        String sql = "SELECT id, name, email, password, role FROM users WHERE email = ? AND password = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during user login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves a user by their unique ID.
     * 
     * @param id User ID
     * @return User object if found, null otherwise
     */
    public User getUserById(int id) {
        String sql = "SELECT id, name, email, password, role FROM users WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during getUserById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if an email is already registered in the system.
     * 
     * @param email User email to check
     * @return true if email exists, false otherwise
     */
    public boolean isEmailRegistered(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Database error checking email existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Standalone main method to verify UserDAO functionality.
     */
    public static void main(String[] args) {
        UserDAO dao = new UserDAO();
        System.out.println("=== Testing UserDAO ===");
        
        String testEmail = "testuser_" + System.currentTimeMillis() + "@library.com";
        User newUser = new User("Test Student", testEmail, "pass123", "STUDENT");
        
        // 1. Test registerUser
        boolean registered = dao.registerUser(newUser);
        System.out.println("1. registerUser: " + (registered ? "SUCCESS" : "FAILED"));
        
        // 2. Test duplicate email registration
        boolean duplicate = dao.registerUser(newUser);
        System.out.println("2. duplicate email handled properly: " + (!duplicate ? "SUCCESS (Rejected duplicate)" : "FAILED"));
        
        // 3. Test login with correct credentials
        User loggedIn = dao.loginUser(testEmail, "pass123");
        System.out.println("3. loginUser (valid credentials): " + (loggedIn != null && loggedIn.getRole().equals("STUDENT") ? "SUCCESS (User: " + loggedIn.getName() + ", Role: " + loggedIn.getRole() + ")" : "FAILED"));
        
        // 4. Test login with wrong password
        User wrongPass = dao.loginUser(testEmail, "wrongpassword");
        System.out.println("4. loginUser (invalid credentials): " + (wrongPass == null ? "SUCCESS (Returned null)" : "FAILED"));
        
        // 5. Test getUserById
        if (loggedIn != null) {
            User fetched = dao.getUserById(loggedIn.getId());
            System.out.println("5. getUserById: " + (fetched != null && fetched.getEmail().equals(testEmail) ? "SUCCESS (ID: " + fetched.getId() + ")" : "FAILED"));
        }
        
        System.out.println("=== UserDAO Test Finished ===");
    }
}

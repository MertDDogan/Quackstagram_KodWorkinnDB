// ready + SQL check

import java.nio.file.*;
import java.util.stream.Stream;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.io.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CommentManager {
    private Path commentsPath = Paths.get("data", "comments.txt");
    // No need for currentUser here since we are not using it in this class.

    public CommentManager() {
        
         try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? ";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, "quackstagram");
                pstmt.setString(2, "comment");

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("Table 'comment' exists.");
                    } else {
                        System.out.println("Table 'comment' does not exist.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MySQL JDBC driver not found.");
        }
        
        /* 
        try {
            // Ensure the comments file exists
            if (!Files.exists(commentsPath)) {
                Files.createFile(commentsPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    public void saveComment(String imageID, String username, String commentText) {
        // We applied Single Responsibility Principle (SRP) by making this method only responsible for saving a single comment.

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO quackstagram.comment (picture_id, user_name, comment_text) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, imageID);
                pstmt.setString(2, username);
                pstmt.setString(3, commentText);

                int rowsAffected = pstmt.executeUpdate();
                System.out.println("Rows inserted: " + rowsAffected);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*

        String commentEntry = imageID + ":" + username + ":" + commentText;
        try (BufferedWriter writer = Files.newBufferedWriter(commentsPath, StandardOpenOption.APPEND)) {
            writer.write(commentEntry);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    public void displayCommentsForImage(String imageID, JPanel panel) {
        // We applied Single Responsibility Principle (SRP) by making this method only responsible for displaying comments for a specific image.
        String user_name, comment_text;
        panel.removeAll(); // Clear previous comments

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT user_name, comment_text FROM quackstagram.comment WHERE picture_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, imageID);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        user_name = rs.getString("user_name");
                        comment_text = rs.getString("comment_text");
                        JLabel commentLabel = new JLabel(user_name + ": " + comment_text);
                         panel.add(commentLabel);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MySQL JDBC driver not found.");
        }
        
        /* 
        try (Stream<String> lines = Files.lines(commentsPath)) {
            lines.filter(line -> line.startsWith(imageID + ":"))
                 .forEach(line -> {
                     String[] parts = line.split(":", 3); // Split into imageID, username, and comment
                     if (parts.length == 3) {
                         JLabel commentLabel = new JLabel(parts[1] + ": " + parts[2]);
                         panel.add(commentLabel);
                     }
                 });
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        panel.revalidate();
        panel.repaint();
    }

    public static String returnCommentsForImage(String imageID) {
        // We applied Single Responsibility Principle (SRP) by making this method only responsible for returning comments as a string.
        StringBuilder commentsStringBuilder = new StringBuilder();
        String user_name, comment_text;
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT user_name, comment_text FROM quackstagram.comment WHERE picture_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, imageID);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        user_name = rs.getString("user_name");
                        comment_text = rs.getString("comment_text");
                        commentsStringBuilder.append(user_name).append(": ").append(comment_text).append("\n");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MySQL JDBC driver not found.");
        }
        /*
        Path commentsPath = Paths.get("data", "comments.txt");
        try (Stream<String> lines = Files.lines(commentsPath)) {
            lines.filter(line -> line.startsWith(imageID + ":"))
                 .forEach(line -> {
                     String[] parts = line.split(":", 3); // Split into imageID, username, and comment
                     if (parts.length == 3) {
                        commentsStringBuilder.append(parts[1]).append(": ").append(parts[2]).append("\n");
                    }
                    });
                  
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        return commentsStringBuilder.toString();
    }
    
}

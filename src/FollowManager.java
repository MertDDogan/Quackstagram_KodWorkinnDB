// ready + SQL check
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import javax.swing.JLabel;
//import javax.swing.JPanel;

import java.io.*;

public class FollowManager {
    private static Path followingPath = Paths.get("data", "following.txt");
    //contructor class checks the file "following.txt" and if not exists it creates it

    public FollowManager() {
        try {
            // Ensure the comments file exists
            if (!Files.exists(followingPath)) {
                Files.createFile(followingPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // addFollower gets user name and new_User_to_follow , checks if that user not followed and then adds this to the line of the user following and updates the following.txt
    public static void addFollower(String user, String newUserToFollow) {
        List<String> lines = new ArrayList<>();
        boolean userFound = false;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(followed_user) FROM quackstagram.follow WHERE follower_user = ? AND followed_user = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, user);
                pstmt.setString(2, newUserToFollow);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Integer count_followed_user = rs.getInt("COUNT(followed_user)");
                        if (count_followed_user != 0) {userFound = true;} 
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MySQL JDBC driver not found.");
        }
        if (!userFound) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String insert_query = "INSERT INTO quackstagram.follow (follower_user, followed_user) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insert_query)) {
                    pstmt.setString(1, user);
                    pstmt.setString(2, newUserToFollow);
                    int rowsAffected = pstmt.executeUpdate();
                    System.out.println("Rows inserted: " + rowsAffected);
                }
            } catch (SQLException e) {
              e.printStackTrace();
            }
        }

        /* 
        try {
            // Read all lines of the file
            lines = Files.readAllLines(followingPath);
            
            // Loop through each line to find the user
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith(user + ":")) {
                    // User found, append the new user to follow
                    if (!lines.get(i).contains(newUserToFollow)){
                        lines.set(i, lines.get(i) + "; " + newUserToFollow);
                        userFound = true;
                        break;
                    } else { System.out.printf("User %s is already followed by %s", newUserToFollow, user);}
                }
            }

            // If the user was not found, add a new line for the user
            if (!userFound) {
                lines.add(user + ": " + newUserToFollow);
            }

            // Write the updated lines back to the file
            Files.write(followingPath, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    public static boolean checkUserIsAlreadyFollowed(String user, String newUserToFollow) {
        List<String> lines = new ArrayList<>();
        boolean userFound = false;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(followed_user) FROM quackstagram.follow WHERE follower_user = ? AND followed_user = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, user);
                pstmt.setString(2, newUserToFollow);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Integer count_followed_user = rs.getInt("COUNT(followed_user)");
                        if (count_followed_user != 0) {userFound = true;} 
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MySQL JDBC driver not found.");
        }
        /*
        try {
            // Read all lines of the file
            lines = Files.readAllLines(followingPath);
            
            // Loop through each line to find the user
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith(user + ":")) {
                    // User found, append the new user to follow
                    if (!lines.get(i).contains(newUserToFollow)){
                        lines.set(i, lines.get(i) + "; " + newUserToFollow);
                        userFound = false;
                        break;
                    } else { System.out.printf("User %s is already followed by %s", newUserToFollow, user);
                        userFound = true; }
                }
            }

            // If the user was not found, add a new line for the user     
         } 
            catch (IOException e) {
            e.printStackTrace();
        }
        */
        return userFound;
        } 
    }
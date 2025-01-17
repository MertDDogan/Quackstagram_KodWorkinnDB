// ready + SQL check
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRelationshipManager {
    // Singleton instance variable
    private static UserRelationshipManager instance;
    private final String followersFilePath = "data/followers.txt";

    // Private constructor to prevent external instantiation
    private UserRelationshipManager() {}

    // Public method to get the singleton instance
    public static synchronized UserRelationshipManager getInstance() {
        if (instance == null) {
            instance = new UserRelationshipManager();
        }
        return instance;
    }

    // Method to follow a user, ensuring they're not following themselves or duplicating relationships
    public void followUser(String follower, String followed) throws IOException {
        if (follower == null || followed == null || follower.equals(followed)) {
            throw new IllegalArgumentException("Follower and followed cannot be null and must be different.");
        }

        // Only proceed if the follower is not already following the target user
        if (!isAlreadyFollowing(follower, followed)) {
            // Append to the followers file

            try (Connection conn = DatabaseConnection.getConnection()) {
                    String insert_query = "INSERT INTO quackstagram.follow (follower_user, followed_user) VALUES (?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insert_query)) {
                        pstmt.setString(1, follower);
                        pstmt.setString(2, followed);
                        int rowsAffected = pstmt.executeUpdate();
                        System.out.println("Rows inserted: " + rowsAffected);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            /*
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(followersFilePath, true))) {
                writer.write(follower + ":" + followed);
                writer.newLine();
            }
            */
        }
    }

    // Check if the follower is already following the target
    private boolean isAlreadyFollowing(String follower, String followed) throws IOException {
        List<String> following = getFollowing(follower);
        return following.contains(followed);
    }

    // Retrieve a list of followers for a given user
    public List<String> getFollowers(String username) throws IOException {
        return getRelationships(username, false);
    }

    // Retrieve a list of users that the given user is following
    public List<String> getFollowing(String username) throws IOException {
        return getRelationships(username, true);
    }

    // Generic method to extract relationships from the file, for reuse in followers and following methods
    private List<String> getRelationships(String username, boolean isFollowing) throws IOException {
        List<String> relationships = new ArrayList<>();

        // we need 2 different select queries for isFollowing=true and isFollowing=false

        if (isFollowing) { 
            try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT followed_user FROM quackstagram.follow WHERE follower_user = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String followed_user = rs.getString("followed_user");
                        relationships.add(followed_user);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MySQL JDBC driver not found.");
        }

        } else {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "SELECT follower_user FROM quackstagram.follow WHERE followed_user = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, username);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            String follower_user = rs.getString("follower_user");
                            relationships.add(follower_user);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("MySQL JDBC driver not found.");
            }

        }

        /*

        try (BufferedReader reader = new BufferedReader(new FileReader(followersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    if (isFollowing && parts[0].equals(username)) {
                        relationships.add(parts[1]);
                    } else if (!isFollowing && parts[1].equals(username)) {
                        relationships.add(parts[0]);
                    }
                }
            }
        }
        */
        return relationships;
    }
}

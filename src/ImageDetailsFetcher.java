// No need
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ImageDetailsFetcher {

    // We implemented the Singleton design pattern to ensure that this class has only one instance as well as having a global access point
    private static ImageDetailsFetcher instance;

    private static final String DETAILS_FILE = "path/to/your/image_details.txt";
    
    // This is the private constructor, which ensures that the class cannot be instantiated from outside this class
    private ImageDetailsFetcher() {}
    
    public static synchronized ImageDetailsFetcher getInstance() {
        if (instance == null) {
            instance = new ImageDetailsFetcher();
        }
        return instance;
    }

    // Removed the "static" keyword from the remaining methods to ensure that they only work on the singleton's instance

    public String fetchUsernameForImage(String imageId) {
        return fetchDetailForImage(imageId, 1); // Username is at index 1
    }

    public String fetchBioForImage(String imageId) {
        return fetchDetailForImage(imageId, 2); // Bio is at index 2
    }

    public String fetchTimeSincePostingForImage(String imageId) {
        String timestampString = fetchDetailForImage(imageId, 3); // Timestamp is at index 3
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            LocalDateTime timestamp = LocalDateTime.parse(timestampString, formatter);
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(timestamp, now);
            long days = duration.toDays();
            return days + " day" + (days != 1 ? "s" : "") + " ago";
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown time"; // Default if not found or error
        }
    }

    public int fetchLikesForImage(String imageId) {
        String likesString = fetchDetailForImage(imageId, 4); // Likes are at index 4
        try {
            return Integer.parseInt(likesString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0; // Default to 0 if parsing fails
        }
    }

    private String fetchDetailForImage(String imageId, int detailIndex) {
        
        try (BufferedReader reader = new BufferedReader(new FileReader(DETAILS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("ImageID:" + imageId)) {
                    String[] details = line.split(", ");
                    if (details.length > detailIndex) {
                        return details[detailIndex].split(":")[1].trim();
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ""; // Return empty string if detail not found or error occurs
    }
}

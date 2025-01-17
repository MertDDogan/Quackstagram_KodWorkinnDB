// no need
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ImageLikesManager {

    private static ImageLikesManager instance; 
    private final String likesFilePath = "data/likes.txt";

    private ImageLikesManager() {}

    public static synchronized ImageLikesManager getInstance() {
        if (instance == null) {
            instance = new ImageLikesManager();
        }
        return instance;
    }

    public void likeImage(String username, String imageID) throws IOException {
        Map<String, Set<String>> likesMap = readLikes();
        likesMap.computeIfAbsent(imageID, k -> new HashSet<>()); // More concise way to ensure the set exists.
        if (likesMap.get(imageID).add(username)) { // Directly add and check to avoid extra map look-up.
            saveLikes(likesMap);
        }
    }

    private Map<String, Set<String>> readLikes() throws IOException {

        Map<String, Set<String>> likesMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(likesFilePath))) {
            reader.lines().forEach(line -> {
                String[] parts = line.split(":", 2); // Safeguard for splitting into exactly two parts.
                if (parts.length == 2) { // Check to prevent IndexOutOfBoundsException.
                    likesMap.put(parts[0], Arrays.stream(parts[1].split(",")).collect(Collectors.toSet()));
                }
            });
        }
        return likesMap;
    }
    
    private void saveLikes(Map<String, Set<String>> likesMap) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(likesFilePath, false))) {
            likesMap.forEach((imageID, users) -> {
                try {
                    writer.write(imageID + ":" + String.join(",", users));
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace(); // Handle writing error within lambda expression.
                }
            });
        }
    }
}



/*import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ImageLikesManager {

    private final String likesFilePath = "data/likes.txt";

    // Method to like an image
    public void likeImage(String username, String imageID) throws IOException {
        Map<String, Set<String>> likesMap = readLikes();
        likesMap.computeIfAbsent(imageID, k -> new HashSet<>()); // More concise way to ensure the set exists.
        if (likesMap.get(imageID).add(username)) { // Directly add and check to avoid extra map look-up.
            saveLikes(likesMap);
        }
    }

    // Method to read likes from file
    private Map<String, Set<String>> readLikes() throws IOException {
        Map<String, Set<String>> likesMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(likesFilePath))) {
            reader.lines().forEach(line -> {
                String[] parts = line.split(":", 2); // Safeguard for splitting into exactly two parts.
                if (parts.length == 2) { // Check to prevent IndexOutOfBoundsException.
                    likesMap.put(parts[0], Arrays.stream(parts[1].split(",")).collect(Collectors.toSet()));
                }
            });
        }
        return likesMap;
    }
    

    // Method to save likes to file
    private void saveLikes(Map<String, Set<String>> likesMap) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(likesFilePath, false))) {
            likesMap.forEach((imageID, users) -> {
                try {
                    writer.write(imageID + ":" + String.join(",", users));
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace(); // Handle writing error within lambda expression.
                }
            });
        }
    }
    

}
*/
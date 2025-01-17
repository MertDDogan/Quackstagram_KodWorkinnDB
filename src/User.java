// ready
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

// Represents a user on Quackstagram
class User {
    private String username;
    private String bio;
    private String password; // Note: Storing passwords in plain text is unsafe in real-world applications.
    private int postsCount;
    private int followersCount;
    private int followingCount;
    private List<Picture> pictures; // Encapsulation Pattern applied here.

    // Constructor Overloading (part of Creational Patterns)
    public User(String username, String bio, String password) {
        this.username = username;
        this.bio = bio;
        this.password = password;
        this.pictures = new ArrayList<>();
        this.postsCount = 0; // Explicitly initializing integer fields for clarity.
        this.followersCount = 0;
        this.followingCount = 0;
    }

    // Simplified constructor applying Constructor Overloading (Creational Pattern)
    public User(String username){
        this(username, "", ""); // Using this() promotes Constructor Chaining (Creational Pattern).
    }

    // Refactored addPicture Method: Command Pattern can be applied here as we are encapsulating all information needed for the action.
    public void addPicture(Picture picture) {
        if (picture != null) {
            pictures.add(picture);
            postsCount++;
        }
    }

    // Getter methods for user details
    public String getUsername() { return username; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public int getPostsCount() { return postsCount; }
    public int getFollowersCount() { return followersCount; }
    public int getFollowingCount() { return followingCount; }
   
    // Returning an unmodifiable list is a best practice to avoid external changes to internal lists.
    public List<Picture> getPictures() {
        return Collections.unmodifiableList(pictures); // Applying Unmodifiable Collection Pattern.
    }

    // Setter methods for followers and following counts. No specific design pattern, but follows Encapsulation.
    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }
    public void setFollowingCount(int followingCount) { this.followingCount = followingCount; }
    public void setPostCount(int postsCount) { this.postsCount = postsCount; }

    // Refactored toString Method for easier user information readability and maintenance.
    @Override
    public String toString() {
        return String.format("User{username='%s', bio='%s', postsCount=%d, followersCount=%d, followingCount=%d}", 
                             username, bio, postsCount, followersCount, followingCount);
    }
}
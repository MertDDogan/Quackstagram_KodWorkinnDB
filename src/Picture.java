// ready
import java.util.List;
import java.util.ArrayList;

// Represents a picture on Quackstagram
class Picture {
    private final String imagePath;
    private final String caption;
    private int likesCount;
    private final List<String> comments;
    private final List<PictureObserver> observers;

    private Picture(Builder builder) {
        this.imagePath = builder.imagePath;
        this.caption = builder.caption;
        this.likesCount = builder.likesCount;
        this.comments = builder.comments;
        this.observers = new ArrayList<>();
    }

    // Add a comment to the picture
    public void addComment(String comment) {
        comments.add(comment);
        notifyObservers(); // Notify observers about the new comment
    }

    // Increment likes count
    public void like() {
        likesCount++;
        notifyObservers(); // Notify observers about the increased likes count
    }

    // Getter methods for picture details
    public String getImagePath() { return imagePath; }
    public String getCaption() { return caption; }
    public int getLikesCount() { return likesCount; }
    public List<String> getComments() { return comments; }

    // Observer pattern: Attach an observer
    public void attachObserver(PictureObserver observer) {
        observers.add(observer);
    }

    // Observer pattern: Detach an observer
    public void detachObserver(PictureObserver observer) {
        observers.remove(observer);
    }

    // Observer pattern: Notify observers
    private void notifyObservers() {
        for (PictureObserver observer : observers) {
            observer.update(this);
        }
    }

    // Builder pattern for constructing Picture objects
    static class Builder {
        private final String imagePath;
        private final String caption;
        private int likesCount = 0;
        private final List<String> comments = new ArrayList<>();

        public Builder(String imagePath, String caption) {
            this.imagePath = imagePath;
            this.caption = caption;
        }

        public Builder likesCount(int likesCount) {
            this.likesCount = likesCount;
            return this;
        }

        public Builder addComment(String comment) {
            comments.add(comment);
            return this;
        }

        public Picture build() {
            return new Picture(this);
        }
    }
}

// Observer interface for Picture observers
interface PictureObserver {
    void update(Picture picture);
}
   // ready + SQL check
    import javax.imageio.ImageIO;
    import javax.swing.*;

    import java.awt.event.MouseAdapter;
    import java.awt.event.MouseEvent;

    import java.awt.*;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;

    import java.awt.image.BufferedImage;
    import java.io.BufferedReader;
    import java.io.BufferedWriter;
    import java.io.File;
    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.ArrayList;
    import java.util.List;

    public class QuakstagramHomeUI extends JFrame {
        private static final int WIDTH = 300;
        private static final int HEIGHT = 500;
        private static final int NAV_ICON_SIZE = 20; // Corrected static size for bottom icons
        private static final int IMAGE_WIDTH = WIDTH - 100; // Width for the image posts
        private static final int IMAGE_HEIGHT = 150; // Height for the image posts
        private static final Color LIKE_BUTTON_COLOR = new Color(255, 90, 95); // Color for the like button
        private CardLayout cardLayout;
        private JPanel cardPanel;
        private JPanel homePanel;
        private JPanel imageViewPanel;

        public QuakstagramHomeUI() {
            setTitle("Quakstagram Home");
            setSize(WIDTH, HEIGHT);
            setMinimumSize(new Dimension(WIDTH, HEIGHT));
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout());
            cardLayout = new CardLayout();
            cardPanel = new JPanel(cardLayout);

            homePanel = new JPanel(new BorderLayout());
            imageViewPanel = new JPanel(new BorderLayout());

            initializeUI();

            cardPanel.add(homePanel, "Home");
            cardPanel.add(imageViewPanel, "ImageView");

            add(cardPanel, BorderLayout.CENTER);
            cardLayout.show(cardPanel, "Home"); // Start with the home view

            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            headerPanel.setBackground(new Color(51, 51, 51));
            JLabel lblRegister = new JLabel("🐥 Quackstagram 🐥");
            lblRegister.setFont(new Font("Arial", Font.BOLD, 16));
            lblRegister.setForeground(Color.WHITE);
            headerPanel.add(lblRegister);
            headerPanel.setPreferredSize(new Dimension(WIDTH, 40));

            add(headerPanel, BorderLayout.NORTH);

            JPanel navigationPanel = new JPanel();
            navigationPanel.setBackground(new Color(249, 249, 249));
            navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.X_AXIS));
            navigationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            navigationPanel.add(createIconButton("img/icons/home.png", "home"));
            navigationPanel.add(Box.createHorizontalGlue());
            navigationPanel.add(createIconButton("img/icons/search.png", "explore"));
            navigationPanel.add(Box.createHorizontalGlue());
            navigationPanel.add(createIconButton("img/icons/add.png", "add"));
            navigationPanel.add(Box.createHorizontalGlue());
            navigationPanel.add(createIconButton("img/icons/heart.png", "notification"));
            navigationPanel.add(Box.createHorizontalGlue());
            navigationPanel.add(createIconButton("img/icons/profile.png", "profile"));

            add(navigationPanel, BorderLayout.SOUTH);
        }

        private void initializeUI() {
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            JScrollPane scrollPane = new JScrollPane(contentPanel);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            String[][] sampleData = createSampleData();
            populateContentPanel(contentPanel, sampleData);
            homePanel.add(scrollPane, BorderLayout.CENTER);
        }

        private void populateContentPanel(JPanel panel, String[][] sampleData) {
            panel.removeAll(); // Clear existing content before repopulating
            if (sampleData.length == 0) {
                panel.add(new JLabel("No posts available")); // Add this line for debugging
            }
            for (String[] postData : sampleData) {
                JPanel itemPanel = new JPanel();
                itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
                itemPanel.setBackground(Color.WHITE);
                itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                itemPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

                JLabel nameLabel = new JLabel(postData[0]);
                nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

                JLabel imageLabel = new JLabel();
                imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                try {
                    BufferedImage originalImage = ImageIO.read(new File(postData[3])); // Assuming .png format
                    ImageIcon imageIcon = new ImageIcon(originalImage.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH));
                    imageLabel.setIcon(imageIcon);
                } catch (IOException ex) {
                    imageLabel.setText("Image not available");
                }

                JLabel descriptionLabel = new JLabel("<html>" + postData[1] + "</html>");
                descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                
                // JLabel commentsLabel = new JLabel("<html>" + postData[1] + "</html>");
                JLabel commentsLabel = new JLabel("<html>" + " "+ "</html>");
                commentsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                
                JLabel likesLabel = new JLabel(postData[2]);
                likesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                JButton likeButton = new JButton("❤");
                likeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                likeButton.setBackground(LIKE_BUTTON_COLOR);
                likeButton.setOpaque(true);
                likeButton.setBorderPainted(false);
                String imageId = new File(postData[3]).getName().split("\\.")[0];

                likeButton.addActionListener(e -> handleLikeAction(imageId, likesLabel));

                // System.out.println(CommentManager.returnCommentsForImage(imageId));
                // Call CommentManager and returnCommentsForImage method to fetch all comments for the image and compy to commentsLabel
                commentsLabel.setText("<html>" + CommentManager.returnCommentsForImage(imageId)+ "</html>");
                

                itemPanel.add(nameLabel);
                itemPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                itemPanel.add(imageLabel);
                itemPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                itemPanel.add(descriptionLabel);
                itemPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                itemPanel.add(likesLabel);
                itemPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                itemPanel.add(commentsLabel);
                itemPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                itemPanel.add(likeButton);

                panel.add(itemPanel);
                panel.add(Box.createRigidArea(new Dimension(0, 10))); // Add space between posts
            }
            panel.revalidate();
            panel.repaint();
        }

        

        private void handleLikeAction(String imageId, JLabel likesLabel) {
            Path imagePath = Paths.get("img", "image_details.txt");
            try {
                List<String> lines = Files.readAllLines(imagePath);
                boolean found = false;
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).contains(imageId)) {
                        String[] parts = lines.get(i).split(", ");
                        int likes = Integer.parseInt(parts[4].split(": ")[1]);
                        likes++; // Increment the like count
                        parts[4] = "Likes: " + likes;
                        lines.set(i, String.join(", ", parts));
                        likesLabel.setText("Likes: " + likes);
                        found = true;
                        break;
                    }
                }
                if (found) {
                    Files.write(imagePath, lines);
                }
            } catch (IOException e) {
                e.printStackTrace(); // Proper error handling should be implemented
            }
        }
        // Observer Design Pattern is used here to notify changes in like count.
        // The JLabel likesLabel acts as the observer, and the like count change serves as the notification.


        private String[][] createSampleData() {
            // IMP: Returned 2D array is: current_user, 
            String currentUser = "";

            try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT user_name FROM quackstagram.current_user ";
            try (Statement stmt = conn.prepareStatement(query)) {
                try (ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        currentUser = rs.getString("user_name");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MySQL JDBC driver not found.");
        }


        System.out.println("The user Logged in QuakstagramHomeUI.java : "+currentUser);
            /*
            try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "users.txt"))) {
                String line = reader.readLine();
                if (line != null) {
                    currentUser = line.split(":")[0].trim();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            // add below to SQL
            // bring all followed users and put into the string followedUsers with ; and blank seperation
            // sample: mert is following Lorin; cdog1; Xylo
            // select followed_user from follow where following_user = current_user;

            String followedUsers = null;
            //String followedUsers = "";
            String followed_user = "";
            try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT followed_user FROM quackstagram.follow WHERE follow.follower_user = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, currentUser);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        followed_user = rs.getString("followed_user");
                        followedUsers = followedUsers + " " + followed_user;
                        /*
                        if (followedUsers == null) {followedUsers=followed_user;
                        } else { followedUsers = followedUsers + "; " + followed_user;}
                        */
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MySQL JDBC driver not found.");
        }
        if (followedUsers == null) {followedUsers = "";}
        System.out.println(currentUser+" is following "+ " " + followedUsers + " --> from MYSQL READ");

            /*
            try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "following.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(currentUser + ":")) {
                        followedUsers = line.split(":")[1].trim();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(currentUser+" is following "+ " " + followedUsers + " --> from file READ");
            */
            // things to be done : user, description, path ve likes info to be checked in images.txt and the to replaced info from DB SQL query 
            List<String[]> data = new ArrayList<>();
            
             
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "SELECT * FROM quackstagram.picture ";
                try (Statement stmt = conn.prepareStatement(query)) {
                    try (ResultSet rs = stmt.executeQuery(query)) {
                        while (rs.next()) {
                            String picture_name = rs.getString("picture_name");
                            String user_name = rs.getString("user_name");
                            String path_root = rs.getString("image_path");
                            String caption = rs.getString("caption");
                            String timestamp = rs.getString("timestamp");
                            Integer likes_count = rs.getInt("likes_count");
                            String image_path = path_root + picture_name + ".png";
                            String likes = "Likes: " + likes_count;
                            
                            if (followedUsers.contains(user_name)) {
                                data.add(new String[]{user_name, caption, likes, image_path});
                                // System.out.println(user_name + " DB " + caption + " " + likes);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("MySQL JDBC driver not found.");
            }
            
            
            
            
            
             /* 
            try {
                List<String> lines = Files.readAllLines(Paths.get("img", "image_details.txt"));
                for (String line : lines) {
                    String[] details = line.split(", ");
                    if (details.length >= 5) { // Ensure there are enough parts in the line
                        String user = details[1].split(": ")[1];
                        String description = details[2].split(": ")[1];
                        String path = "img/uploaded/" + details[0].split(": ")[1] + ".png";
                        String likes = "Likes: " + details[4].split(": ")[1];
                        if (followedUsers.contains(user)) {
                            data.add(new String[]{user, description, likes, path});
                            System.out.println(user + " file " + description + " " + likes);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace(); // Proper error handling should be implemented
            }
            */
            return data.toArray(new String[0][]);
        }

        private JButton createIconButton(String iconPath, String buttonType) {
            ImageIcon iconOriginal = new ImageIcon(iconPath);
            Image iconScaled = iconOriginal.getImage().getScaledInstance(NAV_ICON_SIZE, NAV_ICON_SIZE, Image.SCALE_SMOOTH);
            JButton button = new JButton(new ImageIcon(iconScaled));
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setContentAreaFilled(false);
            
            // Assign actions based on the type of the button
            switch (buttonType) {
                case "home":
                    button.addActionListener(e -> openHomeUI());
                    break;
                case "profile":
                    button.addActionListener(e -> openProfileUI());
                    break;
                case "notification":
                    button.addActionListener(e -> notificationsUI());
                    break;
                case "explore":
                    button.addActionListener(e -> exploreUI());
                    break;
                case "add":
                    button.addActionListener(e -> ImageUploadUI());
                    break;
            }
            return button;
        }

       




    private void openProfileUI() {
            // Open InstagramProfileUI frame
            this.dispose();
            String loggedInUsername = "";

            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "SELECT user_name FROM quackstagram.current_user ";
                try (Statement stmt = conn.prepareStatement(query)) {
                    try (ResultSet rs = stmt.executeQuery(query)) {
                        while (rs.next()) {
                            loggedInUsername = rs.getString("user_name");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("MySQL JDBC driver not found.");
            }
    
    
        /*
            // Read the logged-in user's username from users.txt
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "users.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                loggedInUsername = line.split(":")[0].trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        User user = new User(loggedInUsername);
            InstagramProfileUI profileUI = new InstagramProfileUI(user);
            profileUI.setVisible(true);
        }
    
        private void notificationsUI() {
            // Open InstagramProfileUI frame
            this.dispose();
            NotificationsUI notificationsUI = new NotificationsUI();
            notificationsUI.setVisible(true);
        }

        private void ImageUploadUI() {
            // Open InstagramProfileUI frame
            this.dispose();
            ImageUploadUI upload = new ImageUploadUI();
            upload.setVisible(true);
        }
    
        private void openHomeUI() {
            // Open InstagramProfileUI frame
            this.dispose();
            QuakstagramHomeUI homeUI = new QuakstagramHomeUI();
            homeUI.setVisible(true);
        }
    
        private void exploreUI() {
            // Open InstagramProfileUI frame
            this.dispose();
            ExploreUI explore = new ExploreUI();
            explore.setVisible(true);
        }
        // Singleton Design Pattern is used here.
        private static class SingletonHelper{
            private static final QuakstagramHomeUI INSTANCE = new QuakstagramHomeUI();
        }
        public static QuakstagramHomeUI getInstance(){
            return SingletonHelper.INSTANCE;
        }
    }


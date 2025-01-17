// ready + SQl check
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.stream.Stream;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class ExploreUI extends JFrame {
    private CommentManager commentManager = new CommentManager();
    private User currentUser;
    private String viewedUser;
    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;
    private static final int NAV_ICON_SIZE = 20; // Size for navigation icons
    private static final int IMAGE_SIZE = WIDTH / 3; // Size for each image in the grid

    public ExploreUI() {
        setTitle("Explore");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        initializeUI();
    }

    private void initializeUI() {
        getContentPane().removeAll();
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
        add(createNavigationPanel(), BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private JPanel createMainContentPanel() {
        // Create the main content panel with search and image grid
        // Search bar at the top
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField(" Search Users");
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, searchField.getPreferredSize().height)); // Limit the height

        // Image Grid
        JPanel imageGridPanel = new JPanel(new GridLayout(0, 3, 2, 2)); // 3 columns, auto rows

        // Load images from the uploaded folder
        File imageDir = new File("img/uploaded");
        if (imageDir.exists() && imageDir.isDirectory()) {
            File[] imageFiles = imageDir.listFiles((dir, name) -> name.matches(".*\\.(png|jpg|jpeg)"));
            if (imageFiles != null) {
                for (File imageFile : imageFiles) {
                    ImageIcon imageIcon = new ImageIcon(new ImageIcon(imageFile.getPath()).getImage().getScaledInstance(IMAGE_SIZE, IMAGE_SIZE, Image.SCALE_SMOOTH));
                    JLabel imageLabel = new JLabel(imageIcon);
                    imageLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            displayImage(imageFile.getPath()); // Call method to display the clicked image
                        }
                    });
                    imageGridPanel.add(imageLabel);
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(imageGridPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Main content panel that holds both the search bar and the image grid
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.add(searchPanel);
        mainContentPanel.add(scrollPane); // This will stretch to take up remaining space
        return mainContentPanel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(51, 51, 51)); // Dark background for contrast
        JLabel lblRegister = new JLabel("Explore 🐥", SwingConstants.CENTER); // Use Explore label with duck emoji
        lblRegister.setFont(new Font("Arial", Font.BOLD, 16)); // Keep the font bold and readable
        lblRegister.setForeground(Color.WHITE); // White text for readability
        headerPanel.add(lblRegister); // Add the label to the panel
        headerPanel.setPreferredSize(new Dimension(WIDTH, 40)); // Consistent size for the header
        return headerPanel; // Return the fully constructed panel
    }

    private JPanel createNavigationPanel() {
        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5)); // Improved layout for equal spacing
        navigationPanel.setBackground(new Color(249, 249, 249)); // Light grey background
        String[] icons = {"home", "search", "add", "heart", "profile"}; // Icon types
        for (String icon : icons) {
            navigationPanel.add(createIconButton("img/icons/" + icon + ".png", icon)); // Simplified icon creation
        }
        return navigationPanel; // Return the constructed navigation panel
    }

    private void displayImage(String imagePath) {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createNavigationPanel(), BorderLayout.SOUTH);

        String imageId = new File(imagePath).getName().split("\\.")[0];
        String username = fetchUsernameForImage(imageId);
        String bio = fetchBioForImage(imageId);
        String timeSincePosting = fetchTimeSincePostingForImage(imageId);
        int likes = fetchLikesForImage(imageId);

        JPanel imageViewerPanel = new JPanel(new BorderLayout());
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            ImageIcon imageIcon = new ImageIcon(originalImage);
            imageLabel.setIcon(imageIcon);
        } catch (IOException ex) {
            imageLabel.setText("Image not found");
        }
        imageViewerPanel.add(imageLabel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        username = fetchUsernameForImage(imageId);  // Ensure this method fetches from your data source
        JButton usernameLabel = new JButton(username);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));

        this.viewedUser=username;

        usernameLabel.addActionListener(e -> viewProfileUI());


        JLabel timeLabel = new JLabel(timeSincePosting);
        timeLabel.setHorizontalAlignment(JLabel.RIGHT);
        topPanel.add(usernameLabel, BorderLayout.WEST);
        topPanel.add(timeLabel, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JTextArea bioTextArea = new JTextArea(bio);
        bioTextArea.setEditable(false);
        JLabel likesLabel = new JLabel("Likes: " + likes);
        bottomPanel.add(bioTextArea, BorderLayout.CENTER);
        bottomPanel.add(likesLabel, BorderLayout.SOUTH);

        JPanel commentsSection = createCommentsSection(imageId);

        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.add(topPanel, BorderLayout.NORTH);
        containerPanel.add(imageViewerPanel, BorderLayout.CENTER);
        containerPanel.add(bottomPanel, BorderLayout.SOUTH);
        containerPanel.add(commentsSection, BorderLayout.SOUTH);

        add(containerPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private String fetchUsernameForImage(String imageId) {
        String user_name = "";
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT user_name FROM quackstagram.picture WHERE picture.picture_name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, imageId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        user_name = rs.getString("user_name");
                
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MySQL JDBC driver not found.");
        }
        return user_name;
    
        /*
        Path detailsPath = Paths.get("img", "image_details.txt");
        try (Stream<String> lines = Files.lines(detailsPath)) {
            String details = lines.filter(line -> line.contains("ImageID: " + imageId)).findFirst().orElse("");
            if (!details.isEmpty()) {
                String[] parts = details.split(", ");
                return parts[1].split(": ")[1]; // Assuming Username is the second attribute
            }
            } catch (IOException ex) {
            ex.printStackTrace();
            }
            return "Unknown"; // Default username if not found
            
        */
    }
    private String fetchBioForImage(String imageId) {
        
        String caption = "";
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT caption FROM quackstagram.picture WHERE picture.picture_name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, imageId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        caption = rs.getString("caption");
                
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MySQL JDBC driver not found.");
        }
        return caption;

        /*
        Path detailsPath = Paths.get("img", "image_details.txt");
                try (Stream<String> lines = Files.lines(detailsPath)) {
                    String details = lines.filter(line -> line.contains("ImageID: " + imageId)).findFirst().orElse("");
                    if (!details.isEmpty()) {
                        String[] parts = details.split(", ");
                        return parts[2].split(":")[1]; // Assuming Bio is the third attribute
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return "No bio available"; // Default bio if not found
        */
    }
            
    private String fetchTimeSincePostingForImage(String imageId) {

        String timeSincePost = "";
        String timeDiff = "";
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT timestamp FROM quackstagram.picture WHERE picture.picture_name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, imageId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        timeSincePost = rs.getString("timestamp");
                
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MySQL JDBC driver not found.");
        }
        if (timeSincePost != "") {
            // LocalDateTime timestamp = LocalDateTime.parse(parts[3].split(": ")[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            // timeSincePost = "2023-12-17 19:07:43"; // timeSincePost is a String
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            //LocalDateTime timestamp = LocalDateTime.parse(timeSincePost, formatter);
            // LocalDateTime timestamp = LocalDateTime.parse(timeSincePost, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            //LocalDateTime now = LocalDateTime.now();

            try {
                // Parsing the string to LocalDateTime using the specified formatter
                LocalDateTime timestamp = LocalDateTime.parse(timeSincePost, formatter);
                LocalDateTime now = LocalDateTime.now();
                
                // Print parsed dates for debugging
                //System.out.println("Parsed timestamp: " + timestamp);
                //System.out.println("Current datetime: " + now);
                
                // Calculating the difference in days
                long days = ChronoUnit.DAYS.between(timestamp, now);
                
                // Returning the result in the desired format
                timeDiff = days + " day" + (days != 1 ? "s" : "") + " ago";
                //System.out.println(timeDiff);
                
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error parsing the date-time string.");
            }            
            // return days + " day" + (days != 1 ? "s" : "") + " ago";
            return timeDiff;
        } else {return "Unknown time"; }
        
            /*
                Path detailsPath = Paths.get("img", "image_details.txt");
                try (Stream<String> lines = Files.lines(detailsPath)) {
                    String details = lines.filter(line -> line.contains("ImageID: " + imageId)).findFirst().orElse("");
                    if (!details.isEmpty()) {
                        String[] parts = details.split(", ");
                        LocalDateTime timestamp = LocalDateTime.parse(parts[3].split(": ")[1], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        LocalDateTime now = LocalDateTime.now();
                        long days = ChronoUnit.DAYS.between(timestamp, now);
                        return days + " day" + (days != 1 ? "s" : "") + " ago";
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return "Unknown time"; // Default if not found
            */
    }
            
    private int fetchLikesForImage(String imageId) {

        Integer likesOfPost = 0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT likes_count FROM quackstagram.picture WHERE picture.picture_name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, imageId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        likesOfPost = rs.getInt("likes_count");
                
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MySQL JDBC driver not found.");
        }
        return likesOfPost;
            /*
                Path detailsPath = Paths.get("img", "image_details.txt");
                try (Stream<String> lines = Files.lines(detailsPath)) {
                    String details = lines.filter(line -> line.contains("ImageID:" + imageId)).findFirst().orElse("");
                    if (!details.isEmpty()) {
                        String[] parts = details.split(", ");
                        return Integer.parseInt(parts[4].split(": ")[1]); // Assuming Likes is the fifth attribute
                    }
                } catch (IOException | NumberFormatException ex) {
                    ex.printStackTrace();
                }
                return 0; // Default likes if not found
            */
    }
            
    private String fetchCurrentUsername() {
                /* Path userPath = Paths.get("data", "current_user.txt");
                try {
                    if (Files.exists(userPath)) {
                        return Files.readString(userPath).trim(); // Reads the username directly from the file
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // return "Unknown"; // Default case if username is not found
                return "Unknown"; */
                
                /*  DATABASE VERSION
                String loggedInUsername;
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
                    return loggedInUsername;
                    */

                this.currentUser = SessionManager.getCurrentUser();
                return this.currentUser.getUsername();
    }
            
            private JPanel createCommentsSection(String imageId) {
                JPanel commentsSection = new JPanel();
                commentsSection.setLayout(new BoxLayout(commentsSection, BoxLayout.Y_AXIS));
                commentManager.displayCommentsForImage(imageId, commentsSection);
            
                JTextField commentField = new JTextField(20);
                JButton submitCommentButton = new JButton("Submit Comment");
                submitCommentButton.addActionListener(e -> {
                    String username = fetchCurrentUsername(); // Implement fetching current logged-in username
                    String commentText = commentField.getText();
                    if (!commentText.isEmpty()) {
                        commentManager.saveComment(imageId, username, commentText);
                        commentManager.displayCommentsForImage(imageId, commentsSection);
                        commentField.setText("");
                    }
                });
            
                JPanel commentFormPanel = new JPanel();
                commentFormPanel.setLayout(new FlowLayout());
                commentFormPanel.add(commentField);
                commentFormPanel.add(submitCommentButton);
            
                commentsSection.add(commentFormPanel);
                return commentsSection;
            }
            
            private JButton createIconButton(String iconPath, String buttonType) {
                ImageIcon iconOriginal = new ImageIcon(iconPath);
                Image iconScaled = iconOriginal.getImage().getScaledInstance(NAV_ICON_SIZE, NAV_ICON_SIZE, Image.SCALE_SMOOTH);
                JButton button = new JButton(new ImageIcon(iconScaled));
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
            
                // Define actions based on button type
                if ("home".equals(buttonType)) {
                    button.addActionListener(e -> openHomeUI());
                } else if ("profile".equals(buttonType)) {
                    button.addActionListener(e -> openProfileUI());
                } else if ("notification".equals(buttonType)) {
                    button.addActionListener(e -> notificationsUI());
                } else if ("explore".equals(buttonType)) {
                    button.addActionListener(e -> exploreUI());
                } else if ("add".equals(buttonType)) {
                    button.addActionListener(e -> ImageUploadUI());
                }
                return button;
            }
            
            private void ImageUploadUI() {
                // Open InstagramProfileUI frame
                this.dispose();
                ImageUploadUI upload = new ImageUploadUI();
                upload.setVisible(true);
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
            
            private void viewProfileUI() {
                // Open InstagramProfileUI frame
                this.dispose();
                viewProfileUI viewProfileUI = new viewProfileUI(this.viewedUser);
                viewProfileUI.setVisible(true);
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
            
        }
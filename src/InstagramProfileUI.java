// ready + SQL check
import javax.swing.*;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.awt.*;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;



public class InstagramProfileUI extends JFrame {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;
    private static final int PROFILE_IMAGE_SIZE = 80; // Adjusted size for the profile image to match UI
    private static final int GRID_IMAGE_SIZE = WIDTH / 3; // Static size for grid images
    private static final int NAV_ICON_SIZE = 20; // Corrected static size for bottom icons
    private JPanel contentPanel; // Panel to display the image grid or the clicked image
    private JPanel headerPanel;   // Panel for the header
    private JPanel navigationPanel; // Panel for the navigation
    private User currentUser; // User object to store the current user's information

    public InstagramProfileUI(User user) {
        this.currentUser = SessionManager.getCurrentUser();
         // Initialize counts
        int imageCount = 0;
        int followersCount = 0;
        int followingCount = 0;
        CommentManager commentManager = new CommentManager(); // Place this in the class scope or constructor

       
        // Step 1: Read image_details.txt to count the number of images posted by the user

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(user_name) FROM quackstagram.picture WHERE user_name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, currentUser.getUsername());
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int user_count = rs.getInt("COUNT(user_name)");
                        System.out.println("Counted Users " + user_count);
                        imageCount = user_count;
                    }
                    
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    /*    
    Path imageDetailsFilePath = Paths.get("img", "image_details.txt");
    try (BufferedReader imageDetailsReader = Files.newBufferedReader(imageDetailsFilePath)) {
        String line;
        while ((line = imageDetailsReader.readLine()) != null) {
            if (line.contains("Username: " + currentUser.getUsername())) {
                imageCount++;
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    */
    // Step 2: Read following.txt to calculate followers and following
    // Count number of users that the Current user is following !!!
    try (Connection conn = DatabaseConnection.getConnection()) {
        String query = "SELECT COUNT(followed_user) FROM quackstagram.follow WHERE follower_user = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, currentUser.getUsername());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Integer followed_user = rs.getInt("COUNT(followed_user)");
                    followingCount = followed_user;
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("MySQL JDBC driver not found.");
    }

    // Count number of followers that is following the Current user  !!!
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(follower_user) FROM quackstagram.follow WHERE followed_user = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, currentUser.getUsername());
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Integer follower_user = rs.getInt("COUNT(follower_user)");
                        followersCount = follower_user;
                        
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MySQL JDBC driver not found.");
        }

    /*    
    Path followingFilePath = Paths.get("data", "following.txt");
    try (BufferedReader followingReader = Files.newBufferedReader(followingFilePath)) {
        String line;
        while ((line = followingReader.readLine()) != null) {
            String[] parts = line.split(":");
            if (parts.length == 2) {
                String username = parts[0].trim();
                String[] followingUsers = parts[1].split(";");
                if (username.equals(currentUser.getUsername())) {
                    followingCount = followingUsers.length;
                } else {
                    for (String followingUser : followingUsers) {
                        if (followingUser.trim().equals(currentUser.getUsername())) {
                            followersCount++;
                        }
                    }
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    */
    String bio = "";

    try (Connection conn = DatabaseConnection.getConnection()) {
        String query = "SELECT bio FROM quackstagram.user WHERE user_name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, currentUser.getUsername());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bio = rs.getString("bio");
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("MySQL JDBC driver not found.");
    }
    
    /*
    Path bioDetailsFilePath = Paths.get("data", "credentials.txt");
    try (BufferedReader bioDetailsReader = Files.newBufferedReader(bioDetailsFilePath)) {
        String line;
        while ((line = bioDetailsReader.readLine()) != null) {
            String[] parts = line.split(":");
            if (parts[0].equals(currentUser.getUsername()) && parts.length >= 3) {
                bio = parts[2];
                break; // Exit the loop once the matching bio is found
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    */
    System.out.println("Bio for " + currentUser.getUsername() + ": " + bio);
    currentUser.setBio(bio);
    

    currentUser.setFollowersCount(followersCount);
    currentUser.setFollowingCount(followingCount);
    currentUser.setPostCount(imageCount);

     //setTitle("DACS Profile");
        setTitle(currentUser.getUsername()+"'s Profile");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        contentPanel = new JPanel();
        headerPanel = createHeaderPanel();       // Initialize header panel
        navigationPanel = createNavigationPanel(); // Initialize navigation panel

        initializeUI(); // Apply Factory Method pattern
    }


      public InstagramProfileUI() {

        setTitle("DACS Profile");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        contentPanel = new JPanel();
        headerPanel = createHeaderPanel();       // Initialize header panel
        navigationPanel = createNavigationPanel(); // Initialize navigation panel
        initializeUI();
    }
    private void initializeUI() {
        clearContentPane(); // Apply Command pattern
        setupLayout(); // Apply Template Method pattern
        revalidateAndRepaintUI(); // Apply Template Method pattern
    }
    
    private void clearContentPane() {
        getContentPane().removeAll(); // Clear existing components
    }
    
    private void setupLayout() {
        add(headerPanel, BorderLayout.NORTH); // Apply Composite pattern
        add(navigationPanel, BorderLayout.SOUTH); // Apply Composite pattern
        initializeImageGrid(); // Apply Template Method pattern
    }
    
    private void revalidateAndRepaintUI() {
        revalidate(); // Apply Template Method pattern
        repaint(); // Apply Template Method pattern
    }
    

    private JPanel createHeaderPanel() {
        boolean isCurrentUser = checkIfCurrentUser();
        
        // Initialize header panel with layout and background
        headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.GRAY);
        
        // Add subcomponents to the headerPanel
        headerPanel.add(createTopHeaderPanel(isCurrentUser));
        headerPanel.add(createProfileNameAndBioPanel());
    
        return headerPanel;
    }
    
    private boolean checkIfCurrentUser() {
        String loggedInUsername = getLoggedInUsername();
        return currentUser.getUsername().equals(loggedInUsername);
    }
    
    private String getLoggedInUsername() {
        String currentUsername = null;
         try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT user_name FROM quackstagram.current_user ";
            try (Statement stmt = conn.prepareStatement(query)) {
                try (ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        currentUsername = rs.getString("user_name");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MySQL JDBC driver not found.");
            return "";
        }
        if (currentUsername != null) {return currentUsername; } else { return "";}

        /*
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "users.txt"))) {
            String line = reader.readLine(); // Read only the first line
            return line != null ? line.split(":")[0].trim() : "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        */
    }
    
   
    
    private JPanel createTopHeaderPanel(boolean isCurrentUser) {
        JPanel topHeaderPanel = new JPanel();
        topHeaderPanel.setLayout(new BoxLayout(topHeaderPanel, BoxLayout.X_AXIS));
        topHeaderPanel.setBackground(new Color(249, 249, 249));
        topHeaderPanel.add(Box.createHorizontalStrut(10)); // Padding from left edge
        topHeaderPanel.add(createProfileImage()); // Add profile image
        
        // Central panel holding stats and potentially the follow button
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(createStatsPanel()); // Add stats panel
        if (isCurrentUser) {
            centerPanel.add(createFollowButton(isCurrentUser)); // Add follow/edit profile button
        }
        
        // Align the center panel in the middle
        topHeaderPanel.add(centerPanel);
        topHeaderPanel.add(Box.createHorizontalGlue()); // Push everything to the left
    
        return topHeaderPanel;
    }
    
    
    private JLabel createProfileImage() {
        ImageIcon profileIcon = new ImageIcon(new ImageIcon("img/storage/profile/" + currentUser.getUsername() + ".png")
                                               .getImage().getScaledInstance(PROFILE_IMAGE_SIZE, PROFILE_IMAGE_SIZE, Image.SCALE_SMOOTH));
        JLabel profileImage = new JLabel(profileIcon);
        profileImage.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return profileImage;
    }
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(1, 3, 10, 0)); // 1 row, 3 cols, horizontal gap 10
        statsPanel.setBackground(new Color(249, 249, 249));
        
        // Assuming createStatLabel returns a formatted JLabel
        statsPanel.add(createStatLabel(String.valueOf(currentUser.getPostsCount()), "Posts"));
        statsPanel.add(createStatLabel(String.valueOf(currentUser.getFollowersCount()), "Followers"));
        statsPanel.add(createStatLabel(String.valueOf(currentUser.getFollowingCount()), "Following"));
    
        return statsPanel;
    }
    private JButton createFollowButton(boolean isCurrentUser) {
        JButton followButton = new JButton(isCurrentUser ? "Edit Profile" : "Follow");
        followButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        followButton.setFont(new Font("Arial", Font.BOLD, 12));
        followButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, followButton.getMinimumSize().height));
        followButton.setBackground(new Color(225, 228, 232));
        followButton.setForeground(Color.BLACK);
        followButton.setOpaque(true);
        followButton.setBorderPainted(false);
        followButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        if (!isCurrentUser) {
            followButton.addActionListener(e -> {
                handleFollowAction(currentUser.getUsername());
                followButton.setText("Following");
            });
        }
        return followButton;
    }
    private JPanel createProfileNameAndBioPanel() {
        JPanel profileNameAndBioPanel = new JPanel(new BorderLayout());
        profileNameAndBioPanel.setBackground(new Color(249, 249, 249));
        
        User currentUser = SessionManager.getCurrentUser(); 
        JLabel profileNameLabel = new JLabel(currentUser.getUsername());
        profileNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        profileNameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        
        JTextArea profileBio = new JTextArea(currentUser.getBio());
        profileBio.setEditable(false);
        profileBio.setFont(new Font("Arial", Font.PLAIN, 12));
        profileBio.setBackground(new Color(249, 249, 249));
        profileBio.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        profileNameAndBioPanel.add(profileNameLabel, BorderLayout.NORTH);
        profileNameAndBioPanel.add(profileBio, BorderLayout.CENTER);
    
        return profileNameAndBioPanel;
    }

    public void saveComment(String imageID, String username, String commentText) {
        
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
        Path commentsPath = Paths.get("data", "comments.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(commentsPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(commentEntry);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace(); // In real-world applications, better error handling is needed
        }
        */
    }
    public void displayCommentsForImage(String imageID) {

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT comment_text FROM quackstagram.comment WHERE picture_ID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, imageID);
                try (ResultSet rs = pstmt.executeQuery(query)) {
                    while (rs.next()) {
                        String imageComment = rs.getString("comment_text");
                        System.out.println("Comments for the Image:" + " " + imageID + ": " + imageComment);
                    }
                }
                
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /*
        Path commentsPath = Paths.get("data", "comments.txt");
        try (Stream<String> lines = Files.lines(commentsPath)) {
            lines.forEach(line -> {
                String[] parts = line.split(":");
                if (parts.length == 3 && parts[0].equals(imageID)) {
                    System.out.println(parts[1] + ": " + parts[2]); // Replace with your method to display comments in the UI
                }
            });
        } catch (IOException e) {
            e.printStackTrace(); // Better error handling recommended
        }
        */
    }
    
    
    
    


   private void handleFollowAction(String usernameToFollow) {
    //Path followingFilePath = Paths.get("data", "following.txt");
    //Path usersFilePath = Paths.get("data", "users.txt");
    String currentUserUsername = "";

         try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT user_name FROM quackstagram.current_user ";
            try (Statement stmt = conn.prepareStatement(query)) {
                try (ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        currentUserUsername = rs.getString("user_name");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MySQL JDBC driver not found.");
        }
        System.out.println("Real user is "+currentUserUsername);
        // // If currentUserUsername is not empty, process following.txt
        if (!currentUserUsername.isEmpty()) {
            boolean found = false;
            // check if the current user is already following the usernameToFollow ?
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "SELECT COUNT(followed_user) FROM quackstagram.follow WHERE follower_user = ? AND followed_user = ? ";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, currentUserUsername);
                    pstmt.setString(2, usernameToFollow);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            Integer followed_user = rs.getInt("COUNT(followed_user)");
                            if (followed_user != 0) {found = true;};
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("MySQL JDBC driver not found.");
            }
            if (!found) { 
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String query = "INSERT INTO quackstagram.follow (follower_user, followed_user) VALUES (?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                        pstmt.setString(1, currentUserUsername);
                        pstmt.setString(2, usernameToFollow);
        
                        int rowsAffected = pstmt.executeUpdate();
                        System.out.println("Rows inserted: " + rowsAffected);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }




    /* 
    try {
        // Read the current user's username from users.txt
        try (BufferedReader reader = Files.newBufferedReader(usersFilePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
               currentUserUsername = parts[0];
            }
        }

        System.out.println("Real user is "+currentUserUsername);
        // If currentUserUsername is not empty, process following.txt
        if (!currentUserUsername.isEmpty()) {
            boolean found = false;
            StringBuilder newContent = new StringBuilder();

            // Read and process following.txt
            if (Files.exists(followingFilePath)) {
                try (BufferedReader reader = Files.newBufferedReader(followingFilePath)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(":");
                        if (parts[0].trim().equals(currentUserUsername)) {
                            found = true;
                            if (!line.contains(usernameToFollow)) {
                                line = line.concat(line.endsWith(":") ? "" : "; ").concat(usernameToFollow);
                            }
                        }
                        newContent.append(line).append("\n");
                    }
                }
            }

            // If the current user was not found in following.txt, add them
            if (!found) {
                newContent.append(currentUserUsername).append(": ").append(usernameToFollow).append("\n");
            }

            // Write the updated content back to following.txt
            try (BufferedWriter writer = Files.newBufferedWriter(followingFilePath)) {
                writer.write(newContent.toString());
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    */
}

    

    
    
    private JPanel createNavigationPanel() {
        // Navigation Bar
        JPanel navigationPanel = new JPanel();
        navigationPanel.setBackground(new Color(249, 249, 249));
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.X_AXIS));
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        navigationPanel.add(createIconButton("img/icons/home.png", "home"));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("img/icons/search.png","explore"));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("img/icons/add.png","add"));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("img/icons/heart.png","notification"));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("img/icons/profile.png", "profile"));

        return navigationPanel;

    }

private void initializeImageGrid() {
    contentPanel.removeAll(); // Clear existing content
    contentPanel.setLayout(new GridLayout(0, 3, 5, 5)); // Grid layout for image grid

    Path imageDir = Paths.get("img", "uploaded");
    try (Stream<Path> paths = Files.list(imageDir)) {
        paths.filter(path -> path.getFileName().toString().startsWith(currentUser.getUsername() + "_"))
             .forEach(path -> {
                 ImageIcon imageIcon = new ImageIcon(new ImageIcon(path.toString()).getImage().getScaledInstance(GRID_IMAGE_SIZE, GRID_IMAGE_SIZE, Image.SCALE_SMOOTH));
                 JLabel imageLabel = new JLabel(imageIcon);
                 imageLabel.addMouseListener(new MouseAdapter() {
                     @Override
                     public void mouseClicked(MouseEvent e) {
                         displayImage(imageIcon); // Call method to display the clicked image
                     }
                 });
                 contentPanel.add(imageLabel);
             });
    } catch (IOException ex) {
        ex.printStackTrace();
        // Handle exception (e.g., show a message or log)
    }

    JScrollPane scrollPane = new JScrollPane(contentPanel);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    add(scrollPane, BorderLayout.CENTER); // Add the scroll pane to the center

    revalidate();
    repaint();
}



    private void displayImage(ImageIcon imageIcon) {
        contentPanel.removeAll(); // Remove existing content
        contentPanel.setLayout(new BorderLayout()); // Change layout for image display

        JLabel fullSizeImageLabel = new JLabel(imageIcon);
        fullSizeImageLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(fullSizeImageLabel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            getContentPane().removeAll(); // Remove all components from the frame
            initializeUI(); // Re-initialize the UI
        });
        contentPanel.add(backButton, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }



    private JLabel createStatLabel(String number, String text) {
        JLabel label = new JLabel("<html><div style='text-align: center;'>" + number + "<br/>" + text + "</div></html>", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(Color.BLACK);
        return label;
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
            //
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
        ImageUploadUI upload = new ImageUploadUI(); //done
        upload.setVisible(true);
    }

    private void openProfileUI() {
        // Open InstagramProfileUI frame
        this.dispose();
        InstagramProfileUI profileUI = new InstagramProfileUI(currentUser); //done
        profileUI.setVisible(true);
    }
 
     private void notificationsUI() {
        // Open InstagramProfileUI frame
        this.dispose();
        NotificationsUI notificationsUI = new NotificationsUI(); // done
        notificationsUI.setVisible(true);
    }
 
    private void openHomeUI() {
        // Open InstagramProfileUI frame
        this.dispose();
        QuakstagramHomeUI homeUI = new QuakstagramHomeUI(); // done
        homeUI.setVisible(true);
    }
 
    private void exploreUI() {
        // Open InstagramProfileUI frame
        this.dispose();
        ExploreUI explore = new ExploreUI();
        explore.setVisible(true);
    }   

    
}
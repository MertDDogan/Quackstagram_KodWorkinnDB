// ready + SQL check

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ImageUploadUI extends JFrame {
    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;
    private static final int NAV_ICON_SIZE = 20;
    private JLabel imagePreviewLabel;
    private JTextArea bioTextArea;
    private JButton uploadButton;
    private JButton saveButton;
    private String bioText;

    public ImageUploadUI() {
        // Constructor enhanced with Factory Method and Facade patterns
        initializeFrame();
        initializeUI();
    }

    private void initializeFrame() {
        // Facade Pattern simplifies JFrame configuration
        setTitle("Upload Image");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    private void initializeUI() {
        // Facade Pattern for initializing user interface components
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
        add(createNavigationPanel(), BorderLayout.SOUTH);
    }

    private JPanel createContentPanel() {
        // Factory Method for creating the content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Image preview
        imagePreviewLabel = new JLabel(new ImageIcon());
        imagePreviewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imagePreviewLabel.setPreferredSize(new Dimension(WIDTH, HEIGHT / 3));
        contentPanel.add(imagePreviewLabel);

        bioTextArea = new JTextArea("Enter a caption");
        bioTextArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        bioTextArea.setLineWrap(true);
        bioTextArea.setWrapStyleWord(true);
        JScrollPane bioScrollPane = new JScrollPane(bioTextArea);
        bioScrollPane.setPreferredSize(new Dimension(WIDTH - 50, HEIGHT / 6));
        contentPanel.add(bioScrollPane);

        uploadButton = new JButton("Upload Image");
        uploadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadButton.addActionListener(this::uploadAction);
        contentPanel.add(uploadButton);

        saveButton = new JButton("Save Caption");
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.addActionListener(this::saveBioAction);
        contentPanel.add(saveButton);

        return contentPanel;
    }

    
    
    private void uploadAction(ActionEvent event) {
        JFileChooser fileChooser = createFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            processSelectedImage(selectedFile);
        }
    }
    
    private JFileChooser createFileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an image file");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(filter);
        return fileChooser;
    }
    
    private void processSelectedImage(File selectedFile) {
        try {
            // Read the username or define a default one, handling possible null values or implement your method.
            String username = readUsername();
            if (username == null || username.isEmpty()) {
                username = "defaultUser"; // Replace with your logic or prompt user.
            }
    
            // Get the next image ID for uniqueness.
            int imageId = getNextImageId(username);
    
            // Determine the file extension and prepare the new file name.
            String extension = getFileExtension(selectedFile);
            String newFileName = username + "_" + imageId + "." + extension;
    
            // Define the destination path within your project directory or another specified location.
            Path destinationPath = Paths.get("img", "uploaded", newFileName);
            Files.createDirectories(destinationPath.getParent()); // Ensure the directory exists.
    
            // Copy the file to the new location.
            Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
    
            // Update the UI to display the selected image.
            ImageIcon imageIcon = new ImageIcon(destinationPath.toString());
            imagePreviewLabel.setIcon(new ImageIcon(imageIcon.getImage().getScaledInstance(imagePreviewLabel.getWidth(), imagePreviewLabel.getHeight(), Image.SCALE_DEFAULT)));
    
            // Optionally, save image info such as user, caption (if already entered), and timestamp.
            // saveImageInfo(String.valueOf(imageId), username, bioTextArea.getText());
            saveImageInfo(String.valueOf(imageId), username, bioText);
    
            // Indicate that an image has been successfully uploaded.
           // boolean imageUploaded = true; // This could trigger other UI updates or enablements.
    
            // Notify the user of the successful upload.
            JOptionPane.showMessageDialog(this, "Image uploaded successfully!");
    
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error processing the selected image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error in image naming convention: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    
    private int getNextImageId(String username) throws IOException {
        Integer currentPictureIndex = 0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(picture_name) FROM quackstagram.picture WHERE picture.user_name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        currentPictureIndex = rs.getInt("COUNT(picture_name)");
                        
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MySQL JDBC driver not found.");
        }
        return currentPictureIndex + 1; // Return the next available ID
        /*
        Path storageDir = Paths.get("img", "uploaded"); // Ensure this is the directory where images are saved
        if (!Files.exists(storageDir)) {
            Files.createDirectories(storageDir);
        }
    
        int maxId = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(storageDir, username + "_*")) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                int idEndIndex = fileName.lastIndexOf('.');
                if (idEndIndex != -1) {
                    String idStr = fileName.substring(username.length() + 1, idEndIndex);
                    try {
                        int id = Integer.parseInt(idStr);
                        if (id > maxId) {
                            maxId = id;
                        }
                    } catch (NumberFormatException ex) {
                        // Ignore filenames that do not have a valid numeric ID
                    }
                }
            }
        }
        return maxId + 1; // Return the next available ID
        */
    }
    
    private void saveImageInfo(String imageId, String username, String bio) throws IOException {
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO quackstagram.picture (picture_name, user_name, image_path, caption, timestamp, likes_count) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username + "_" + imageId);
                pstmt.setString(2, username);
                pstmt.setString(3, "img/uploaded/");
                pstmt.setString(4, bio);
                pstmt.setString(5, timestamp);
                pstmt.setInt(6, 0);


                int rowsAffected = pstmt.executeUpdate();
                System.out.println("Rows inserted: " + rowsAffected);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        /*
        Path infoFilePath = Paths.get("img", "image_details.txt");
        if (!Files.exists(infoFilePath)) {
            Files.createFile(infoFilePath);
        }
        
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    
        try (BufferedWriter writer = Files.newBufferedWriter(infoFilePath, StandardOpenOption.APPEND)) {
            // writer.write(String.format("ImageID: %s, Username: %s, Bio: %s, Timestamp: %s, Likes: 0", imageId, username, bio, timestamp));
            writer.write(String.format("ImageID: %s_%s, Username: %s, Bio: %s, Timestamp: %s, Likes: 0", username, imageId, username, bio, timestamp)); // MERT: fixed write up image_detail.txt file
            writer.newLine();
        }
        */
    
}


    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf + 1);
    }

    private void saveBioAction(ActionEvent event) {
        // Here you would handle saving the bio text
        bioText = bioTextArea.getText();
        // System.out.println(" Caption entered: "+bioText);
        // For example, save the bio text to a file or database
        JOptionPane.showMessageDialog(this, "Caption saved: " + bioText);
    }
   
    private JPanel createHeaderPanel() {
       
        // Header Panel (reuse from InstagramProfileUI or customize for home page)
         // Header with the Register label
         JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
         headerPanel.setBackground(new Color(51, 51, 51)); // Set a darker background for the header
         JLabel lblRegister = new JLabel(" Upload Image 🐥");
         lblRegister.setFont(new Font("Arial", Font.BOLD, 16));
         lblRegister.setForeground(Color.WHITE); // Set the text color to white
         headerPanel.add(lblRegister);
         headerPanel.setPreferredSize(new Dimension(WIDTH, 40)); // Give the header a fixed height
         return headerPanel;
   }

   private String readUsername() throws IOException {
    String currentUsername = "";

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
        }
    return currentUsername;
    
    
    /*
    Path usersFilePath = Paths.get("data", "users.txt");
    try (BufferedReader reader = Files.newBufferedReader(usersFilePath)) {
        String line = reader.readLine();
        if (line != null) {
            return line.split(":")[0]; // Extract the username from the first line
        }
    }
    return null; // Return null if no username is found
    */
}

   private JPanel createNavigationPanel() {
       // Create and return the navigation panel
        // Navigation Bar
        JPanel navigationPanel = new JPanel();
        navigationPanel.setBackground(new Color(249, 249, 249));
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.X_AXIS));
        navigationPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        navigationPanel.add(createIconButton("img/icons/home.png", "home"));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("img/icons/search.png","explore"));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("img/icons/add.png"," "));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("img/icons/heart.png","notification"));
        navigationPanel.add(Box.createHorizontalGlue());
        navigationPanel.add(createIconButton("img/icons/profile.png", "profile"));

        return navigationPanel;
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
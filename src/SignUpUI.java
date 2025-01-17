// ready + SQL Check
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SignUpUI extends JFrame {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;
    private JTextField txtUsername, txtPassword, txtBio;
    private JLabel lblPhoto;
    private String credentialsFilePath = "data/credentials.txt";
    private String profilePhotoStoragePath = "img/storage/profile/";
    //String url = "jdbc:mysql://localhost:3306/quackstagram";
    //String mysql_username = "root";  // Replace with your username
    //String mysql_password = "System99";  // Replace with your password

    public SignUpUI() {

        initializeFrame();
        buildUIComponents();
        this.setVisible(true);
    }

    private void initializeFrame() {
        setTitle("Quackstagram - Register");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
    }

    private void buildUIComponents() {
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createFieldsPanel(), BorderLayout.CENTER);
        add(createRegisterPanel(), BorderLayout.SOUTH);
    }

    // Factory Method Design Pattern is used here to create header panel.
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(51, 51, 51));
        JLabel lblRegister = new JLabel("Quackstagram 🐥", JLabel.CENTER);
        lblRegister.setFont(new Font("Arial", Font.BOLD, 16));
        lblRegister.setForeground(Color.WHITE);
        headerPanel.add(lblRegister);
        return headerPanel;
    }

    private JPanel createFieldsPanel() {
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        txtUsername = new JTextField("Username");
        txtPassword = new JTextField("Password");
        txtBio = new JTextField("Bio");

        lblPhoto = new JLabel(new ImageIcon(new ImageIcon("img/logos/DACS.png").getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        lblPhoto.setPreferredSize(new Dimension(80, 80));

        fieldsPanel.add(lblPhoto);
        fieldsPanel.add(txtUsername);
        fieldsPanel.add(txtPassword);
        fieldsPanel.add(txtBio);
        
        JButton btnUploadPhoto = new JButton("Upload Photo");
        btnUploadPhoto.addActionListener(e -> handleProfilePictureUpload());
        fieldsPanel.add(btnUploadPhoto);

        return fieldsPanel;
    }

    private JPanel createRegisterPanel() {
        JPanel registerPanel = new JPanel(new BorderLayout());
        JButton btnRegister = new JButton("Register");
        btnRegister.addActionListener(e -> onRegisterClicked(e));
        registerPanel.add(btnRegister, BorderLayout.CENTER);

        JButton btnSignIn = new JButton("Already have an account? Sign In");
        btnSignIn.addActionListener(e -> openSignInUI());
        registerPanel.add(btnSignIn, BorderLayout.SOUTH);

        return registerPanel;
    }

    // Singleton Design Pattern is used here to ensure only one instance of the method is created.
    private boolean doesUsernameExist(String username) {
        
        String user_name=null;
        String passwd=null;
        String bio=null;
        Integer posts_count, followers_count, following_count;


        // SQL Query Time
         
        //try (Connection conn = DriverManager.getConnection(url, mysql_username, mysql_password)) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM quackstagram.user WHERE user.user_name = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        int user_id = rs.getInt("user_id");
                        user_name = rs.getString("user_name");
                        passwd = rs.getString("password");
                        bio = rs.getString("bio");
                        posts_count = rs.getInt("posts_count");
                        followers_count = rs.getInt("followers_count");
                        following_count = rs.getInt("following_count");
                        System.out.println("ID: " + user_id + ", username: " + user_name + ", passwd: " + passwd + ", bio: " + bio+", posts_count: " + posts_count+", followers_count: " + followers_count+", following_count: " + following_count);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MySQL JDBC driver not found.");
        }

        if (user_name != null) {return true;} else return false;

        /*
        Path path = Paths.get(credentialsFilePath);
        try (Stream<String> lines = Files.lines(path)) {
            return lines.anyMatch(line -> line.startsWith(username + ":"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error checking username existence.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
        */
    }

    // Method to handle profile picture upload
    // Strategy Design Pattern is used here to encapsulate the algorithm for handling profile picture upload.
    private void handleProfilePictureUpload() {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            saveProfilePicture(fileChooser.getSelectedFile(), txtUsername.getText().trim());
        }
    }

    private void saveProfilePicture(File file, String username) {
        // and update picture table in the Quackstagram DB ??? not to be in table picture
        try {
            BufferedImage image = ImageIO.read(file);
            File outputFile = new File(profilePhotoStoragePath + username + ".png");
            ImageIO.write(image, "png", outputFile);

        } catch (IOException e) {
            e.printStackTrace();
        }


        // WRITE ALSO THE PROFILE PICTURE TO PICTURES Table
        /*
        try (Connection conn = DatabaseConnection.getConnection()) {    
            String query = "INSERT INTO pictures (picture_name, user_name, image_path) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username+".png");
                pstmt.setString(2, username);
                pstmt.setString(3, profilePhotoStoragePath);

                int rowsAffected = pstmt.executeUpdate();
                System.out.println("Rows inserted: " + rowsAffected);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        */
        
    }
    
    private void saveCredentials(String username, String password, String bio) {
        // Consolidate credential string construction for clarity
        //String credential = String.format("%s:%s:%s%n", username, password, bio);


        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO quackstagram.user (user_name, password, bio) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setString(3, bio);

                int rowsAffected = pstmt.executeUpdate();
                System.out.println("Rows inserted: " + rowsAffected);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        
        /*
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(credentialsFilePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(credential);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save user credentials.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        */
    }
    private void onRegisterClicked(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String bio = txtBio.getText();
    
        if (doesUsernameExist(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different username.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            saveCredentials(username, password, bio);
            handleProfilePictureUpload();
            dispose(); // Close the SignUpUI frame
            
            // Open the SignInUI frame
            SwingUtilities.invokeLater(() -> {
                SignInUI signInFrame = new SignInUI();
                signInFrame.setVisible(true);
            });
        }
    }
    

    private void openSignInUI() {
        dispose(); // Close the current frame
        SwingUtilities.invokeLater(() -> new SignInUI().setVisible(true)); // Open the SignInUI frame
    }
}

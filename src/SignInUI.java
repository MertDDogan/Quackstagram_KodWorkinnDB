// ready + SQL Check
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;
import java.util.Arrays;
import java.awt.event.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class SignInUI extends JFrame {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnSignIn, btnRegisterNow;
    private JLabel lblPhoto;
    private User newUser;

    public SignInUI() {
        
        setTitle("Quackstagram - Sign In");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        initializeUI();
    }

    private void initializeUI() {
        JPanel headerPanel = createHeaderPanel();
        JPanel fieldsPanel = createFieldsPanel();
        JPanel registerPanel = createRegisterPanel();

        add(headerPanel, BorderLayout.NORTH);
        add(fieldsPanel, BorderLayout.CENTER);
        add(registerPanel, BorderLayout.SOUTH);
    }

    // Factory Method Design Pattern is used here to create header panel.
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(51, 51, 51));
        JLabel lblRegister = new JLabel("Quackstagram 🐥");
        lblRegister.setFont(new Font("Arial", Font.BOLD, 16));
        lblRegister.setForeground(Color.WHITE);
        headerPanel.add(lblRegister);
        headerPanel.setPreferredSize(new Dimension(WIDTH, 40));
        return headerPanel;
    }

    private JPanel createFieldsPanel() {
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        txtUsername = new JTextField("Username");
        txtUsername.setForeground(Color.GRAY);
        txtPassword = new JPasswordField();
        txtPassword.setForeground(Color.BLACK);

        // Adapter Design Pattern is used here for focus listeners.
        txtUsername.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtUsername.getText().equals("Username")) {
                    txtUsername.setText("");
                    txtUsername.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtUsername.getText().isEmpty()) {
                    txtUsername.setForeground(Color.GRAY);
                    txtUsername.setText("Username");
                }
            }
        });

        txtPassword.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(txtPassword.getPassword()).equals("Password")) {
                    txtPassword.setText("");
                    txtPassword.setEchoChar('*');
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (new String(txtPassword.getPassword()).isEmpty()) {
                    txtPassword.setEchoChar((char) 0);
                    txtPassword.setText("Password");
                }
            }
        });

        // Composite Design Pattern is used to create the fields panel.
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(createPhotoPanel());
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(txtUsername);
        fieldsPanel.add(Box.createVerticalStrut(10));
        fieldsPanel.add(txtPassword);
        fieldsPanel.add(Box.createVerticalStrut(10));

        return fieldsPanel;
    }

    private JPanel createPhotoPanel() {
        JPanel photoPanel = new JPanel();
        photoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        lblPhoto = new JLabel();
        lblPhoto.setPreferredSize(new Dimension(80, 80));
        lblPhoto.setHorizontalAlignment(JLabel.CENTER);
        lblPhoto.setVerticalAlignment(JLabel.CENTER);
        lblPhoto.setIcon(new ImageIcon(new ImageIcon("img/logos/DACS.png").getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)));
        photoPanel.add(lblPhoto);
        return photoPanel;
    }

    private JPanel createRegisterPanel() {
        JPanel registerPanel = new JPanel(new BorderLayout());
        btnSignIn = new JButton("Sign-In");
        btnSignIn.addActionListener(this::onSignInClicked);
        btnSignIn.setBackground(new Color(255, 90, 95));
        btnSignIn.setForeground(Color.BLACK);
        btnSignIn.setFocusPainted(false);
        btnSignIn.setBorderPainted(false);
        btnSignIn.setFont(new Font("Arial", Font.BOLD, 14));
        registerPanel.add(btnSignIn, BorderLayout.CENTER);

        btnRegisterNow = new JButton("No Account? Register Now");
        btnRegisterNow.addActionListener(this::onRegisterNowClicked);
        btnRegisterNow.setBackground(Color.WHITE);
        btnRegisterNow.setForeground(Color.BLACK);
        btnRegisterNow.setFocusPainted(false);
        btnRegisterNow.setBorderPainted(false);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(Color.white);
        buttonPanel.add(btnSignIn);
        buttonPanel.add(btnRegisterNow);

        registerPanel.add(buttonPanel, BorderLayout.SOUTH);
        return registerPanel;
    }

    // Observer Design Pattern is used here for event listeners.
    private void onSignInClicked(ActionEvent event) {
        String enteredUsername = txtUsername.getText();
        char[] enteredPassword = txtPassword.getPassword();
        String entered_passwd = new String(enteredPassword);
        
        try {
            if (verifyCredentials(enteredUsername, entered_passwd)) {
                SwingUtilities.invokeLater(() -> {
                    InstagramProfileUI profileUI = new InstagramProfileUI(newUser);
                    profileUI.setVisible(true);
                });
                dispose(); // Close the SignInUI frame
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            Arrays.fill(enteredPassword, '0'); // Clear the password from memory safely.
        }
    }

    private void onRegisterNowClicked(ActionEvent event) {
        dispose(); // Close the SignInUI frame
        SwingUtilities.invokeLater(() -> {
            SignUpUI signUpFrame = new SignUpUI();
            signUpFrame.setVisible(true);
        });
    }

    private boolean verifyCredentials(String username, String password) {
        String separator = File.separator; // Get system-dependent path separator
        String filePath = "data" + separator + "credentials.txt"; // Construct the path using the separator
        
          // TEST DB Connectivity
        // Database URL and credentials
        String user_name=null;
        String passwd=null;
        String bio=null;
        Integer posts_count, followers_count, following_count;


        // SQL Query Time
         
        // try (Connection conn = DriverManager.getConnection(url, mysql_username, mysql_password)) {
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

        if (user_name != null && password.equals(passwd)) {
        
            newUser = new User(user_name, bio, bio);
            SessionManager.setCurrentUser(newUser); // Set the user in the session
            saveUserInformation(newUser);
            return true;
        }
        /*
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] credentials = line.split(":");
                if (credentials[0].equals(username) && Arrays.equals(credentials[1].toCharArray(), password)) {
                    String bio = credentials.length > 2 ? credentials[2] : "";
                    newUser = new User(username, bio, bio);
                    SessionManager.setCurrentUser(newUser); // Set the user in the session
                    saveUserInformation(newUser);
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        return false;
    }

    private void saveUserInformation(User user) {
        String current_user = null;
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT user_name FROM quackstagram.current_user ";
            try (Statement stmt = conn.prepareStatement(query)) {
                try (ResultSet rs = stmt.executeQuery(query)) {
                    while (rs.next()) {
                        current_user = rs.getString("user_name");
                        System.out.println("saveUserInfor dayim :"+ current_user);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("MySQL JDBC driver not found.");
        }
        
        if (current_user == null) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String insert_query = "INSERT INTO quackstagram.current_user (user_name) VALUES (?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insert_query)) {
                    pstmt.setString(1, user.getUsername());
                    int rowsAffected = pstmt.executeUpdate();
                    System.out.println("Rows inserted: " + rowsAffected);
                    current_user = user.getUsername();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {

            try (Connection conn = DatabaseConnection.getConnection()) {
                String update_query = "UPDATE quackstagram.current_user SET user_name = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(update_query)) {
                    pstmt.setString(1, user.getUsername());
                    int rowsAffected = pstmt.executeUpdate();
                    System.out.println("Rows inserted: " + rowsAffected);
                    current_user = user.getUsername();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }


        }
        System.out.println(" The Current user is: "+current_user);

        // BELOW FILE PART to be REMOVED
        String separator = File.separator; // Get system-dependent path separator
        String filePath = "data" + separator + "users.txt"; // Construct the path using the separator
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            writer.write(user.getUsername());
            writer.newLine();
            } catch (IOException e) {
            e.printStackTrace();
            }
            }
            public static void main(String[] args) {
                SwingUtilities.invokeLater(() -> {
                    SignInUI frame = new SignInUI();
                    frame.setVisible(true);
                });
            }
        }

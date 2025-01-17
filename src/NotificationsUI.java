// ready + SQL check
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

// Observer Design Pattern
public class NotificationsUI extends JFrame {

    private static final int WIDTH = 300;
    private static final int HEIGHT = 500;
    private static final int NAV_ICON_SIZE = 20; // Size for navigation icons

    // Template Method Design Pattern
    public NotificationsUI() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Notifications");
        setSize(WIDTH, HEIGHT);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel headerPanel = createHeaderPanel();
        JPanel navigationPanel = createNavigationPanel();
        JPanel contentPanel = createContentPanel(); // Factory Method Design Pattern

        String currentUsername = getCurrentUsername();
        loadNotifications(contentPanel, currentUsername);

        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(contentPanel), BorderLayout.CENTER);
        add(navigationPanel, BorderLayout.SOUTH);
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        return contentPanel;
    }

    private void loadNotifications(JPanel contentPanel, String currentUsername) {

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT liking_user, liked_picture, date_time FROM quackstagram.likes WHERE liked_user = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, currentUsername);
                try (ResultSet rs = pstmt.executeQuery(query)) {
                    while (rs.next()) {
                        String liking_user = rs.getString("liking_user");
                        String liked_picture = rs.getString("liked_picture");
                        String date_time = rs.getString("date_time");
                        createAndAddNotificationPanel(contentPanel, liking_user, date_time);
                    }
                }
                
                
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*
        Path notificationPath = Paths.get("data", "notifications.txt");
        try (Stream<String> lines = Files.lines(notificationPath)) {
            lines.map(line -> line.split(";"))
                 .filter(parts -> parts[0].trim().equals(currentUsername))
                 .forEach(parts -> createAndAddNotificationPanel(contentPanel, parts));
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }


    private void createAndAddNotificationPanel(JPanel contentPanel, String userWhoLiked, String timestamp) {
        String message = userWhoLiked + " liked your picture - " + getElapsedTime(timestamp) + " ago";
        JPanel notificationPanel = new JPanel(new BorderLayout());
        notificationPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        notificationPanel.add(new JLabel(message), BorderLayout.CENTER);
        contentPanel.add(notificationPanel);
    }
    /* Version before SQL version
    private void createAndAddNotificationPanel(JPanel contentPanel, String[] parts) {
        String userWhoLiked = parts[1].trim();
        String timestamp = parts[3].trim();
        String message = userWhoLiked + " liked your picture - " + getElapsedTime(timestamp) + " ago";
        JPanel notificationPanel = new JPanel(new BorderLayout());
        notificationPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        notificationPanel.add(new JLabel(message), BorderLayout.CENTER);
        contentPanel.add(notificationPanel);
    }
    */
    private String getCurrentUsername() {
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

        /*
        try (BufferedReader reader = Files.newBufferedReader(Paths.get("data", "users.txt"))) {
            String line = reader.readLine();
            if (line != null) {
                currentUsername = line.split(":")[0].trim();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        return currentUsername;
    }

    private String getElapsedTime(String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime timeOfNotification = LocalDateTime.parse(timestamp, formatter);
        LocalDateTime currentTime = LocalDateTime.now();

        long daysBetween = ChronoUnit.DAYS.between(timeOfNotification, currentTime);
        long minutesBetween = ChronoUnit.MINUTES.between(timeOfNotification, currentTime) % 60;

        StringBuilder timeElapsed = new StringBuilder();
        if (daysBetween > 0) {
            timeElapsed.append(daysBetween).append(" day").append(daysBetween > 1 ? "s" : "");
        }
        if (minutesBetween > 0) {
            if (daysBetween > 0) {
                timeElapsed.append(" and ");
            }
            timeElapsed.append(minutesBetween).append(" minute").append(minutesBetween > 1 ? "s" : "");
        }
        return timeElapsed.toString();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(51, 51, 51));
        JLabel lblRegister = new JLabel(" Notifications 🐥");
        lblRegister.setFont(new Font("Arial", Font.BOLD, 16));
        lblRegister.setForeground(Color.WHITE);
        headerPanel.add(lblRegister);
        headerPanel.setPreferredSize(new Dimension(WIDTH, 40));
        return headerPanel;
    }

    private JPanel createNavigationPanel() {
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
        } else if ("add".equals(buttonType)) {
            button.addActionListener(e -> ImageUploadUI());
        }
        return button;
    }

    private void ImageUploadUI() {
        this.dispose();
        ImageUploadUI upload = new ImageUploadUI();
        upload.setVisible(true);
    }

    private void openProfileUI() {
        this.dispose();
        String loggedInUsername = getCurrentUsername();
        User user = new User(loggedInUsername);
        InstagramProfileUI profileUI = new InstagramProfileUI(user);
        profileUI.setVisible(true);
    }

    private void notificationsUI() {
        this.dispose();
        NotificationsUI notificationsUI = new NotificationsUI();
        notificationsUI.setVisible(true);
    }

    private void openHomeUI() {
        this.dispose();
        QuakstagramHomeUI homeUI = new QuakstagramHomeUI();
        homeUI.setVisible(true);
    }

    private void exploreUI() {
        this.dispose();
        ExploreUI explore = new ExploreUI();
        explore.setVisible(true);
    }
}
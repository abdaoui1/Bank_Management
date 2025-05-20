package pack1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UnsupportedLookAndFeelException;

public class AuthWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    public AuthWindow(String userType)
    {
        setTitle("Connexion - " + capitalize(userType));
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // === Create main gradient panel to serve as the background for entire window ===
        JPanel gradientBackgroundPanel = new JPanel(new BorderLayout()) {
            /**
             * 
             */
            private static final long serialVersionUID = -7039049924667603217L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create gradient paint for entire background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(41, 128, 185),
                    getWidth(), getHeight(), new Color(142, 68, 173)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        setContentPane(gradientBackgroundPanel);

        // === Title Panel (now transparent since background already has gradient) ===
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false); // Make transparent to show gradient
        titlePanel.setBorder(BorderFactory.createEmptyBorder(25, 10, 25, 10));
        titlePanel.setPreferredSize(new Dimension(500, 100));

        JLabel titleLabel = new JLabel("Authentification");
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // Add subtitle with user type
        JLabel subtitleLabel = new JLabel("Espace " + capitalize(userType));
        subtitleLabel.setFont(new Font("Verdana", Font.ITALIC, 16));
        subtitleLabel.setForeground(new Color(240, 240, 240));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Add the title panel to the main gradient panel
        gradientBackgroundPanel.add(titlePanel, BorderLayout.NORTH);

        // === Create a custom-styled form container ===
        JPanel formContainer = new JPanel() {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Semi-transparent white background
                g2d.setColor(new Color(255, 255, 255, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Add subtle border
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            }
        };
        
        formContainer.setLayout(new GridLayout(4, 1, 15, 15));
        formContainer.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        formContainer.setOpaque(false);

        // Create styled login field
        JPanel loginPanel = new JPanel(new BorderLayout(5, 0));
        loginPanel.setOpaque(false);
        JLabel loginLabel = new JLabel("Nom d'utilisateur");
        loginLabel.setForeground(Color.gray);
        loginLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JTextField loginField = new JTextField();
        loginField.setFont(new Font("Arial", Font.PLAIN, 14));
        loginField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 200), 1, true),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        loginField.setOpaque(true);
        loginField.setBackground(new Color(255, 255, 255, 220));
        
        loginPanel.add(loginLabel, BorderLayout.NORTH);
        loginPanel.add(loginField, BorderLayout.CENTER);
        
        //----------------------
     // Create password section panel
        JPanel passwordPanel = new JPanel(new BorderLayout(5, 0));
        passwordPanel.setOpaque(false);

        // Label for password
        JLabel passwordLabel = new JLabel("Mot de passe");
        passwordLabel.setForeground(Color.GRAY);
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Wrapper panel for password field and eye icon
        JPanel passwordFieldWrapper = new JPanel(new BorderLayout());
        passwordFieldWrapper.setOpaque(true);
        passwordFieldWrapper.setBackground(new Color(255, 255, 255, 220));
        passwordFieldWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 200), 1, true),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Password input field
        JPasswordField passwordField = new JPasswordField();
        passwordField.setEchoChar('●');
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        passwordField.setOpaque(false);

        // Toggle visibility button (clean icon-only)
        JButton toggleEyeButton = new JButton("👁");
        toggleEyeButton.setBorder(null);                         // No border at all
        toggleEyeButton.setContentAreaFilled(false);             // No background
        toggleEyeButton.setFocusPainted(false);                  // No focus ring
        toggleEyeButton.setOpaque(false);                        // Transparent
        toggleEyeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleEyeButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16)); // Emoji-friendly
        toggleEyeButton.setForeground(new Color(80, 80, 80));     // Icon color
        toggleEyeButton.setMargin(new Insets(0, 0, 0, 0));        // No margin
        toggleEyeButton.setPreferredSize(new Dimension(30, 30)); // Smaller size

        // Toggle logic for show/hide password
        toggleEyeButton.addActionListener(new ActionListener() {
            private boolean isVisible = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                isVisible = !isVisible;
                passwordField.setEchoChar(isVisible ? (char) 0 : '●');
                toggleEyeButton.setText(isVisible ? "🙈" : "👁");
            }
        });

        // Add field and button to wrapper
        passwordFieldWrapper.add(passwordField, BorderLayout.CENTER);
        passwordFieldWrapper.add(toggleEyeButton, BorderLayout.EAST);

        // Add label and field to final panel
        passwordPanel.add(passwordLabel, BorderLayout.NORTH);
        passwordPanel.add(passwordFieldWrapper, BorderLayout.CENTER);

        // Optional: spacer panel
        JPanel spacerPanel = new JPanel();
        spacerPanel.setOpaque(false);


        
        
        // Create styled login button
        JButton authBtn = new JButton("Se connecter") {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(25, 60, 200));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(37, 99, 235));
                } else {
                    g2d.setColor(new Color(30, 144, 255));
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                
                // Center text
                String text = "Se connecter";
                int stringWidth = g2d.getFontMetrics().stringWidth(text);
                int stringHeight = g2d.getFontMetrics().getHeight();
                int x = (getWidth() - stringWidth) / 2;
                int y = (getHeight() - stringHeight) / 2 + g2d.getFontMetrics().getAscent();
                
                g2d.drawString(text, x, y);
            }
        };
        
        authBtn.setOpaque(false);
        authBtn.setContentAreaFilled(false);
        authBtn.setBorderPainted(false);
        authBtn.setFocusPainted(false);
        authBtn.setPreferredSize(new Dimension(200, 45));
        authBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add panels to form container
        formContainer.add(loginPanel);
        formContainer.add(passwordPanel);
        formContainer.add(spacerPanel);
        
        // Create button panel with centered button
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(authBtn, BorderLayout.CENTER);
        formContainer.add(buttonPanel);
        
        // Add form container to a wrapper panel to center it
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setOpaque(false);
        formWrapper.setBorder(BorderFactory.createEmptyBorder(20, 70, 70, 70));
        formWrapper.add(formContainer, BorderLayout.CENTER);
        
        // Add the form wrapper to the main panel
        gradientBackgroundPanel.add(formWrapper, BorderLayout.CENTER);

        // === Action handlers ===
        authBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                authBtn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                authBtn.repaint();
            }
        });

        authBtn.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                // Not used
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // Not used
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processLogin(loginField.getText(), new String(passwordField.getPassword()), userType);
                }
            }
        });

        authBtn.addActionListener(e -> {
            processLogin(loginField.getText(), new String(passwordField.getPassword()), userType);
        });
        
        // Add key listener to password field for Enter key
        passwordField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processLogin(loginField.getText(), new String(passwordField.getPassword()), userType);
                }
            }
        });
    }
    
    // Method to process login
    private void processLogin(String login, String password, String userType) {
        String hashedPassword = PasswordUtils.hashPassword(password);

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quarta", "root", "");

            String sql = "SELECT * FROM utilisateurs WHERE login = ? AND password = ? AND type = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, login);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, userType);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Bienvenue " + login + " !");
                dispose();
                switch (userType) {
                    case "client":
                        new ClientInterface(rs.getInt("id")).setVisible(true);
                        break;
                    case "employe":
                        new EmployeInterface(rs.getInt("id")).setVisible(true);
                        break;
                    case "directeur":
                        new DirecteurInterface(rs.getInt("id")).setVisible(true);
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Identifiants incorrects", "Erreur", JOptionPane.ERROR_MESSAGE);
            }

            conn.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erreur de connexion : " + ex.getMessage(), "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        } catch (UnsupportedLookAndFeelException e1) {
            e1.printStackTrace();
        }
    }

    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
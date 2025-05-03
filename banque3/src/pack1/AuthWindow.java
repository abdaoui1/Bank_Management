package pack1;
import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AuthWindow extends JFrame {
    public AuthWindow(String userType) {
        setTitle("Connexion - " + capitalize(userType));
        setSize(500, 600); //450
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout());

        // === Logo ===
        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/imgs/logo.png"));
            Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            logoLabel.setText("Logo manquant");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        }

        mainPanel.add(logoLabel, BorderLayout.NORTH);

        // === Centre ===
        JPanel formPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        formPanel.setBackground(Color.WHITE);

        JTextField loginField = new JTextField();
        loginField.setBorder(BorderFactory.createTitledBorder("Nom d'utilisateur"));

        // === Champ mot de passe avec œil intégré ===
        JPasswordField passField = new JPasswordField();
        passField.setEchoChar('●');
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        passField.setBorder(null); // pas de bordure individuelle

        JButton eyeBtn = new JButton("👁");
        eyeBtn.setPreferredSize(new Dimension(50, 800));
        eyeBtn.setBorderPainted(false);
        eyeBtn.setContentAreaFilled(false);
        eyeBtn.setFocusPainted(false);
        eyeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        eyeBtn.addActionListener(new ActionListener() {
            private boolean visible = false;
            @Override
            public void actionPerformed(ActionEvent e) {
                visible = !visible;
                passField.setEchoChar(visible ? (char) 0 : '.');
                eyeBtn.setText(visible ? "🙈" : "👁");
            }
        });

        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.setBackground(Color.WHITE);
        passPanel.setBorder(BorderFactory.createTitledBorder("Mot de passe"));
        passPanel.add(passField, BorderLayout.CENTER);
        passPanel.add(eyeBtn, BorderLayout.EAST);

        // === Bouton Connexion ===
        JButton authBtn = new JButton("Se connecter");
        authBtn.setBackground(new Color(30, 144, 255));
        authBtn.setForeground(Color.WHITE);
        authBtn.setFocusPainted(false);
        authBtn.setFont(new Font("Arial", Font.BOLD, 14));

        formPanel.add(loginField);
        formPanel.add(passPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(authBtn);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        // === Action du bouton connexion ===
        authBtn.addActionListener(e -> {
            String login = loginField.getText();
            String password = new String(passField.getPassword());

            try {
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/quarta", "root", "");

                String sql = "SELECT * FROM utilisateurs WHERE login = ? AND password = ? AND type = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, login);
                stmt.setString(2, password);
                stmt.setString(3, userType);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Bienvenue " + login + " !");
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
                    JOptionPane.showMessageDialog(this, "Identifiants incorrects",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }

                conn.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur de connexion : " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel( new NimbusLookAndFeel());
        
        SwingUtilities.invokeLater(() -> new AuthWindow("client").setVisible(true));
    }
}
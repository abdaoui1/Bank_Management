package pack1;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;
class AuthWindow extends JFrame {
    private String userType;
    
    public AuthWindow(String userType) {
        this.userType = userType;
        setTitle("Authentification " + userType);
        setSize(350, 200);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel loginLabel = new JLabel("Login:");
        JTextField loginField = new JTextField();
        JLabel passLabel = new JLabel("Mot de passe:");
        JPasswordField passField = new JPasswordField();
        JButton authBtn = new JButton("Se connecter");
        
        panel.add(loginLabel);
        panel.add(loginField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(new JLabel(""));
        panel.add(authBtn);
        
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
                    JOptionPane.showMessageDialog(this, "Authentification réussie!");
                    dispose();
                    
                    switch(userType) {
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
                    JOptionPane.showMessageDialog(this, "Login ou mot de passe incorrect", 
                                                "Erreur", JOptionPane.ERROR_MESSAGE);
                }
                conn.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur de connexion: " + ex.getMessage(), 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        add(panel);
    }
}


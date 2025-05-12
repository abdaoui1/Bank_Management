package pack1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UnsupportedLookAndFeelException;

public class AuthWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    public AuthWindow(String userType)
    {

        setTitle("Connexion - " + capitalize(userType));
        setSize(500, 600); // 450
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout());



        // === Centre ===
        JPanel formPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(150, 50, 30, 50));
        formPanel.setBackground(Color.white);

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
            public void actionPerformed(ActionEvent e)
            {
                visible = !visible;
                passField.setEchoChar(visible ? (char) 0 : '●');
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
        //formPanel.add(Box.createVerticalStrut(1));
        formPanel.add(authBtn);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        // === Action du bouton connexion ===
        
//     // Button press effect
//        authBtn.getModel().addChangeListener(e -> {
//            ButtonModel model = (ButtonModel) e.getSource();
//            if (model.isPressed()) {
//                authBtn.setBackground(Color.pink);
//            } 
////            else if (model.isRollover()) {
////                authBtn.setBackground(hoverColor);
////            } else {
////                authBtn.setBackground(defaultColor);
////            }
//        });
        
        
        authBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                authBtn.setBackground(Color.blue); // Highlight on hover
                authBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                authBtn.setBackground(new Color(30, 144, 255)); // Reset
                authBtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        authBtn.addFocusListener(new FocusListener() {
            
            @Override
            public void focusLost(FocusEvent e)
            {
                //authBtn.setBackground( Color.black ); // Reset
                authBtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                
            }
            
            @Override
            public void focusGained(FocusEvent e)
            {
                authBtn.setBackground( Color.blue); // Reset
                authBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
            }
        });
        
        authBtn.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                

            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                String login = loginField.getText();
                String password = new String(passField.getPassword());
                String hashedPassword = PasswordUtils.hashPassword(password);

                try
                {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quarta", "root", "");

                    String sql = "SELECT * FROM utilisateurs WHERE login = ? AND password = ?  AND type = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, login);
                    stmt.setString(2, hashedPassword);
                    stmt.setString(3, userType);

                    ResultSet rs = stmt.executeQuery();
                    if (rs.next())
                    {
                        JOptionPane.showMessageDialog(null, "Bienvenue " + login + " !");
                        dispose();
                        switch (userType)
                        {
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
                    } else
                    {
                        JOptionPane.showMessageDialog(null, "Identifiants incorrects", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }

                    conn.close();
                } catch (SQLException ex)
                {
                    JOptionPane.showMessageDialog(null, "Erreur de connexion : " + ex.getMessage(), "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                } catch (UnsupportedLookAndFeelException e1)
                {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            
        });

        authBtn.addActionListener(e -> {

            String login = loginField.getText();
            String password = new String(passField.getPassword());
            String hashedPassword = PasswordUtils.hashPassword(password);

            try
            {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quarta", "root", "");

                String sql = "SELECT * FROM utilisateurs WHERE login = ? AND password = ? AND type = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, login);
                stmt.setString(2, hashedPassword);
                stmt.setString(3, userType);

                ResultSet rs = stmt.executeQuery();
                if (rs.next())
                {
                    JOptionPane.showMessageDialog(null, "Bienvenue " + login + " !");
                    dispose();
                    switch (userType)
                    {
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
                } else
                {
                    JOptionPane.showMessageDialog(null, "Identifiants incorrects", "Erreur", JOptionPane.ERROR_MESSAGE);
                }

                conn.close();
            } catch (SQLException ex)
            {
                JOptionPane.showMessageDialog(null, "Erreur de connexion : " + ex.getMessage(), "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            } catch (UnsupportedLookAndFeelException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

    }

    private String capitalize(String s)
    {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }


}
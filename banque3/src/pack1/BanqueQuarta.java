package pack1;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;

class BanqueQuarta extends JFrame {

    private static final long serialVersionUID = -1887623329237351381L;

    public BanqueQuarta() {
        setTitle("Banque Quarta - Accueil");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel title = new JLabel("Banque Quarta", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        
        JButton clientBtn = new JButton("Espace Client");
        JButton employeBtn = new JButton("Espace Employé");
        JButton directeurBtn = new JButton("Espace Directeur");
        
        // Style des boutons
        styleButton(clientBtn, Color.BLUE);
        styleButton(employeBtn, Color.GREEN);
        styleButton(directeurBtn, Color.RED);
        
        panel.add(title);
        panel.add(clientBtn);
        panel.add(employeBtn);
        panel.add(directeurBtn);
        
        add(panel);
        
        // Actions des boutons
        clientBtn.addActionListener(e -> new AuthWindow("client").setVisible(true));
        employeBtn.addActionListener(e -> new AuthWindow("employe").setVisible(true));
        directeurBtn.addActionListener(e -> new AuthWindow("directeur").setVisible(true));
    }
    
    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
    }
}


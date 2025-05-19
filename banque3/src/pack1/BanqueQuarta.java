package pack1;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

class BanqueQuarta extends JFrame {
    
    private static final long serialVersionUID = 1L;

    public BanqueQuarta() {
        setTitle("Banque Quarta");
        setSize(800, 400); // Taille modifiée pour une interface plus large
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // === Utilisation d'un BorderLayout pour diviser la fenêtre ===
        setLayout(new BorderLayout());
        //leftPanel.setBackground(Color.WHITE);
        // === Section gauche : boutons d'accès ===
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(255,255,255)); // Couleur grise claire
        
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS)); // Disposition verticale pour les boutons

        // Titre des boutons
        JLabel title = new JLabel("Accédez à votre espace", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(50, 50, 50)); // Gris foncé
        
        // Espaces entre les boutons
        leftPanel.add(Box.createVerticalStrut(80));
        //leftPanel.add(title);
        

        // Création des boutons
        JButton clientBtn = new JButton("Espace Client");
        JButton employeBtn = new JButton("Espace Employé");
        JButton directeurBtn = new JButton("Espace Directeur");

        // Style des boutons
        styleButton(clientBtn, new Color(30, 144, 255)); // Bleu
        styleButton(employeBtn, new Color(34, 139, 34)); // Vert
        styleButton(directeurBtn, new Color(255, 69, 0)); // Rouge

        // Ajouter les boutons au panel gauche
        leftPanel.add(clientBtn);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(employeBtn);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(directeurBtn);

        // === Section droite : logo et message de bienvenue ===
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new BorderLayout());

        // Message de bienvenue
        JLabel bienvenueLabel = new JLabel("Bienvenue à Banque Quarta", JLabel.CENTER);
        bienvenueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        bienvenueLabel.setForeground(new Color(30, 144, 255)); // Bleu

        // Logo (vous pouvez changer le chemin de l'image selon votre fichier)
        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/imgs/v2.png")); // Remplacer "/logo1.png" par votre image
            Image img = icon.getImage().getScaledInstance(450, 300, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            logoLabel.setText("Logo manquant");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        }

        // Ajouter le message de bienvenue et le logo au panel droit
        rightPanel.add(bienvenueLabel, BorderLayout.NORTH);
        rightPanel.add(logoLabel, BorderLayout.CENTER);

        // Ajouter les panels gauche et droit à la fenêtre principale
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // Actions des boutons
        clientBtn.addActionListener(e -> new AuthWindow("client").setVisible(true));
        employeBtn.addActionListener(e -> new AuthWindow("employe").setVisible(true));
        directeurBtn.addActionListener(e -> new AuthWindow("directeur").setVisible(true));
    }

    // Méthode pour styliser les boutons
    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
//        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFont(new Font("Segoe UI", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(250, 50)); // Taille des boutons (largeur et hauteur identiques)
        button.setMaximumSize(new Dimension(250, 50)); // S'assurer que la taille maximale est identique
    }


}
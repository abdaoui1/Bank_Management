package pack1;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

class ClientInterface extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int clientId;
    private Connection conn;
    private Color primaryColor = new Color(0, 102, 204); // Bleu foncé
    private Color secondaryColor = new Color(240, 240, 240); // Gris clair
    private Color buttonTextColor = new Color(0, 70, 140); // Bleu plus foncé pour le texte
    
    public ClientInterface(int clientId) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new NimbusLookAndFeel() );
        this.clientId = clientId;
        setTitle("Espace Client");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        try {
            this.conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/quarta", "root", "");
            

            
            
            initUI();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de connexion: " + e.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }
    
    private void initUI() throws SQLException {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(secondaryColor);
        tabbedPane.setForeground(primaryColor);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Onglet Compte Courant
        JPanel courantPanel = createComptePanel("courant");
        tabbedPane.addTab("Compte Courant", null, courantPanel, "Gérer votre compte courant");
        
        // Onglet Compte Épargne
        JPanel epargnePanel = createComptePanel("epargne");
        tabbedPane.addTab("Compte Épargne", null, epargnePanel, "Gérer votre compte épargne");
        
        add(tabbedPane);
    }
    
    private JPanel createComptePanel(String typeCompte) throws SQLException {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(secondaryColor);
        
        // Vérifier si le compte existe
        String sql = "SELECT * FROM comptes WHERE id_client = ? AND type_compte = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, clientId);
        stmt.setString(2, typeCompte);
        
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            JLabel noAccountLabel = new JLabel("Vous n'avez pas de compte " + typeCompte, JLabel.CENTER);
            noAccountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            noAccountLabel.setForeground(Color.GRAY);
            panel.add(noAccountLabel, BorderLayout.CENTER);
            return panel;
        }
        
        int compteId = rs.getInt("id");
        String numeroCompte = rs.getString("numero_compte");
        double solde = rs.getDouble("solde");
        
        // Affichage des infos du compte
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(primaryColor, 1), 
            "Informations du compte"));
        infoPanel.setBackground(Color.WHITE);
        
        addStyledLabel(infoPanel, "Numéro de compte:", numeroCompte);
        addStyledLabel(infoPanel, "Type de compte:", 
                      typeCompte.equals("courant") ? "Courant" : "Épargne");
        addStyledLabel(infoPanel, "Solde:", 
                      new DecimalFormat("#,##0.00 DH").format(solde));
        
        // Opérations
        JPanel operationsPanel = new JPanel(new GridLayout(1, 5, 10, 10));
        operationsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        operationsPanel.setBackground(secondaryColor);
        
        JButton consulterBtn = createStyledButton("Consulter", Color.WHITE);
        JButton verserBtn = createStyledButton("Verser", new Color(220, 255, 220)); // Vert clair
        JButton retirerBtn = createStyledButton("Retirer", new Color(255, 220, 220)); // Rouge clair
        JButton transfertBtn = createStyledButton("Transfert", new Color(255, 235, 200)); // Orange clair
        JButton imprimerBtn = createStyledButton("Imprimer relevé", new Color(230, 220, 255)); // Violet clair
        
        operationsPanel.add(consulterBtn);
        operationsPanel.add(verserBtn);
        operationsPanel.add(retirerBtn);
        operationsPanel.add(imprimerBtn);
        if (typeCompte.equals("courant")) {
            operationsPanel.add(transfertBtn);
        } else {
            operationsPanel.add(new JLabel(""));
        }
        
        // Historique des transactions
        sql = "SELECT * FROM transactions WHERE id_compte_source = ? ORDER BY date_transaction DESC LIMIT 5";
        stmt = conn.prepareStatement(sql);
        stmt.setInt(1, compteId);
        rs = stmt.executeQuery();
        
        
//        JTextArea historiqueArea = new JTextArea(10, 40);
//        historiqueArea.setEditable(false);
//        historiqueArea.setFont(new Font("Monospaced", Font.PLAIN, 15)); // Police uniforme, taille augmentée
//        historiqueArea.setBackground(new Color(245, 245, 255)); // Fond doux légèrement bleuté
//        historiqueArea.setBorder(BorderFactory.createCompoundBorder(
//            BorderFactory.createLineBorder(new Color(180, 180, 255), 1), // Bordure fine bleue
//            BorderFactory.createEmptyBorder(10, 10, 10, 10)
//        ));
//        historiqueArea.setForeground(Color.DARK_GRAY); // Couleur de base du texte
//
//        historiqueArea.setText("🕘 Dernières transactions:\n\n");
//
//        while (rs.next()) {
//            String type = rs.getString("type");
//            double montant = rs.getDouble("montant");
//            String date = rs.getString("date_transaction");
//
//            String typeTexte;
//            Color textColor;
//
//            if (type.equals("depot")) {
//                typeTexte = "➕ Dépôt";
//                textColor = new Color(0, 128, 0); // Vert foncé
//            } else if (type.equals("retrait")) {
//                typeTexte = "➖ Retrait";
//                textColor = new Color(178, 34, 34); // Rouge foncé
//            } else {
//                typeTexte = "🔄 Transfert";
//                textColor = new Color(102, 51, 0); // Marron
//            }
//
//            // Change temporairement la couleur si tu veux distinguer chaque ligne
//            historiqueArea.setForeground(textColor);
//
//            historiqueArea.append(String.format("[%s] %s : %.2f DH\n", 
//                date.substring(0, 16), typeTexte, montant));
//        }
        
        
        JTextArea historiqueArea = new JTextArea(10, 40);
      historiqueArea.setEditable(false);
      historiqueArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Police uniforme, taille augmentée
      historiqueArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      historiqueArea.setBackground(Color.WHITE);
      historiqueArea.setText("🕘 Dernières transactions:\n\n");
      
      while (rs.next()) {
          String type = rs.getString("type");
          double montant = rs.getDouble("montant");
          String date = rs.getString("date_transaction");

          String typeTexte;
          Color textColor;

          if (type.equals("depot")) {
              typeTexte = "➕ Dépôt";
              textColor = new Color(0, 128, 0); // Vert foncé
          } else if (type.equals("retrait")) {
              typeTexte = "➖ Retrait";
              textColor = new Color(178, 34, 34); // Rouge foncé
          } else {
              typeTexte = "🔄 Transfert";
              textColor = new Color(102, 51, 0); // Marron
          }
          // Change temporairement la couleur si tu veux distinguer chaque ligne
          historiqueArea.setForeground(textColor);

          historiqueArea.append(String.format("[%s] %s : %.2f DH\n", 
              date.substring(0, 16), typeTexte, montant));
      }
      

//        
//        JTextArea historiqueArea = new JTextArea(10, 40);
//        historiqueArea.setEditable(false);
//        historiqueArea.setFont(new Font("Consolas", Font.PLAIN, 12));
//        historiqueArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//        historiqueArea.setBackground(Color.WHITE);
//        historiqueArea.append("Dernières transactions:\n\n");
//        
//        while (rs.next()) {
//            String type = rs.getString("type");
//            double montant = rs.getDouble("montant");
//            String date = rs.getString("date_transaction");
//            
//            Color textColor = type.equals("depot") ? new Color(0, 100, 0) : 
//                            type.equals("retrait") ? new Color(139, 0, 0) : 
//                            new Color(102, 51, 0);
//            
//            historiqueArea.setForeground(textColor);
//            historiqueArea.append(String.format("[%s] %s: %.2f DH\n", 
//                date.substring(0, 16), 
//                type.equals("depot") ? "Dépot" : 
//                type.equals("retrait") ? "Retrait" : "Transfert", 
//                montant));
//        }
        
        // Ajout des composants
        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(operationsPanel, BorderLayout.CENTER);
        panel.add(new JScrollPane(historiqueArea), BorderLayout.SOUTH);
        
        // Actions des boutons
        consulterBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Solde actuel: " + new DecimalFormat("#,##0.00 DH").format(solde),
                "Solde du compte", JOptionPane.INFORMATION_MESSAGE);
        });
        
        verserBtn.addActionListener(e -> showOperationDialog("Versement", "Montant à verser:", compteId, "depot", solde));
        
        retirerBtn.addActionListener(e -> showOperationDialog("Retrait", "Montant à retirer:", compteId, "retrait", solde));
        
        if (typeCompte.equals("courant")) {
            transfertBtn.addActionListener(e -> showTransferDialog(compteId, solde));
        }
        
        // Action pour le bouton Imprimer relevé
        imprimerBtn.addActionListener(e -> {
            try {
                // Récupérer toutes les transactions pour le relevé
                String sqlReleve = "SELECT * FROM transactions WHERE id_compte_source = ? ORDER BY date_transaction DESC";
                PreparedStatement stmtReleve = conn.prepareStatement(sqlReleve);
                stmtReleve.setInt(1, compteId);
                ResultSet rsReleve = stmtReleve.executeQuery();
                
                // Créer le contenu du relevé
                StringBuilder releveContent = new StringBuilder();
                releveContent.append("RELEVE BANCAIRE\n\n");
                releveContent.append("Numéro de compte: ").append(numeroCompte).append("\n");
                releveContent.append("Type de compte: ").append(typeCompte.equals("courant") ? "Courant" : "Épargne").append("\n");
                releveContent.append("Solde actuel: ").append(new DecimalFormat("#,##0.00 DH").format(solde)).append("\n\n");
                releveContent.append("HISTORIQUE DES TRANSACTIONS\n");
                releveContent.append("----------------------------------------\n");
                
                while (rsReleve.next()) {
                    String type = rsReleve.getString("type");
                    double montant = rsReleve.getDouble("montant");
                    String date = rsReleve.getString("date_transaction");
                    
                    releveContent.append(String.format("[%s] %s: %.2f DH\n", 
                        date.substring(0, 16), 
                        type.equals("depot") ? "Dépot" : 
                        type.equals("retrait") ? "Retrait" : "Transfert", 
                        montant));
                }
                
                // Afficher le relevé dans une boîte de dialogue avec option d'impression
                JTextArea textArea = new JTextArea(releveContent.toString());
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                
                JButton printButton = new JButton("Imprimer");
                printButton.addActionListener(ev -> {
                    try {
                        textArea.print();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Erreur lors de l'impression: " + ex.getMessage(),
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                });
                
                JPanel panelReleve = new JPanel(new BorderLayout());
                panelReleve.add(new JScrollPane(textArea), BorderLayout.CENTER);
                panelReleve.add(printButton, BorderLayout.SOUTH);
                
                JOptionPane.showMessageDialog(this, panelReleve, 
                    "Relevé bancaire - " + numeroCompte, JOptionPane.PLAIN_MESSAGE);
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la génération du relevé: " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        return panel;
    }
    
    private void addStyledLabel(JPanel panel, String labelText, String valueText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(primaryColor);
        panel.add(label);
        
        JLabel value = new JLabel(valueText);
        value.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(value);
    }
    


    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(buttonTextColor); // Texte en bleu
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effet hover
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void showOperationDialog(String title, String message, int compteId, String operationType, double solde) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel label = new JLabel(message);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label, BorderLayout.NORTH);
        
        JTextField montantField = new JTextField();
        montantField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(montantField, BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(this, panel, title, 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                double montant = Double.parseDouble(montantField.getText());
                
                if (operationType.equals("retrait") && montant > solde) {
                    JOptionPane.showMessageDialog(this, "Solde insuffisant", 
                                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Mise à jour du solde
                String sql = "UPDATE comptes SET solde = solde " + 
                    (operationType.equals("depot") ? "+" : "-") + " ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setDouble(1, montant);
                stmt.setInt(2, compteId);
                stmt.executeUpdate();
                
                // Enregistrement de la transaction
                String sql2 = "INSERT INTO transactions (type, montant, id_compte_source) VALUES (?, ?, ?)";
                PreparedStatement stmt2 = conn.prepareStatement(sql2);
                stmt2.setString(1, operationType);
                stmt2.setDouble(2, montant);
                stmt2.setInt(3, compteId);
                stmt2.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Opération effectuée avec succès!");
                dispose();
                new ClientInterface(clientId).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Montant invalide", 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showTransferDialog(int compteId, double solde) {
        JPanel transfertPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        transfertPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel compteDestLabel = new JLabel("Numéro compte destinataire:");
        compteDestLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        transfertPanel.add(compteDestLabel);
        
        JTextField compteDestField = new JTextField();
        transfertPanel.add(compteDestField);
        
        JLabel montantLabel = new JLabel("Montant:");
        montantLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        transfertPanel.add(montantLabel);
        
        JTextField montantField = new JTextField();
        transfertPanel.add(montantField);
        
        int result = JOptionPane.showConfirmDialog(this, transfertPanel, 
            "Transfert", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                double montant = Double.parseDouble(montantField.getText());
                String compteDest = compteDestField.getText();
                
                if (montant > solde) {
                    JOptionPane.showMessageDialog(this, "Solde insuffisant", 
                                              "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Vérifier que le compte destinataire existe
                String sql = "SELECT id FROM comptes WHERE numero_compte = ? AND type_compte = 'courant'";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, compteDest);
                ResultSet rs = stmt.executeQuery();
                
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Compte destinataire invalide", 
                                              "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int compteDestId = rs.getInt("id");
                
                // Démarrer la transaction
                conn.setAutoCommit(false);
                
                try {
                    // Débiter le compte source
                    String sql2 = "UPDATE comptes SET solde = solde - ? WHERE id = ?";
                    PreparedStatement stmt2 = conn.prepareStatement(sql2);
                    stmt2.setDouble(1, montant);
                    stmt2.setInt(2, compteId);
                    stmt2.executeUpdate();
                    
                    // Créditer le compte destinataire
                    String sql3 = "UPDATE comptes SET solde = solde + ? WHERE id = ?";
                    PreparedStatement stmt3 = conn.prepareStatement(sql3);
                    stmt3.setDouble(1, montant);
                    stmt3.setInt(2, compteDestId);
                    stmt3.executeUpdate();
                    
                    // Enregistrer la transaction
                    String sql4 = "INSERT INTO transactions (type, montant, id_compte_source, id_compte_destination) " +
                          "VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt4 = conn.prepareStatement(sql4);
                    stmt4.setString(1, "transfert");
                    stmt4.setDouble(2, montant);
                    stmt4.setInt(3, compteId);
                    stmt4.setInt(4, compteDestId);
                    stmt4.executeUpdate();
                    
                    conn.commit();
                    JOptionPane.showMessageDialog(this, "Transfert effectué avec succès!");
                    dispose();
                    new ClientInterface(clientId).setVisible(true);
                } catch (SQLException ex) {
                    conn.rollback();
                    throw ex;
                } finally {
                    conn.setAutoCommit(true);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), 
                                          "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
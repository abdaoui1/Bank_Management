package pack1;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;

class ClientInterface extends JFrame {
    private int clientId;
    private Connection conn;
    
    public ClientInterface(int clientId) {
        this.clientId = clientId;
        setTitle("Espace Client");
        setSize(600, 400);
        setLocationRelativeTo(null);
        
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
        
        // Onglet Compte Courant
        JPanel courantPanel = createComptePanel("courant");
        tabbedPane.addTab("Compte Courant", courantPanel);
        
        // Onglet Compte Épargne
        JPanel epargnePanel = createComptePanel("epargne");
        tabbedPane.addTab("Compte Épargne", epargnePanel);
        
        add(tabbedPane);
    }
    
    private JPanel createComptePanel(String typeCompte) throws SQLException {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Vérifier si le compte existe
        String sql = "SELECT * FROM comptes WHERE id_client = ? AND type_compte = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, clientId);
        stmt.setString(2, typeCompte);
        
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            panel.add(new JLabel("Vous n'avez pas de compte " + typeCompte, JLabel.CENTER), BorderLayout.CENTER);
            return panel;
        }
        
        int compteId = rs.getInt("id");
        String numeroCompte = rs.getString("numero_compte");
        double solde = rs.getDouble("solde");
        
        // Affichage des infos du compte
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        infoPanel.add(new JLabel("Numéro de compte:"));
        infoPanel.add(new JLabel(numeroCompte));
        infoPanel.add(new JLabel("Type de compte:"));
        infoPanel.add(new JLabel(typeCompte.equals("courant") ? "Courant" : "Épargne"));
        infoPanel.add(new JLabel("Solde:"));
        infoPanel.add(new JLabel(new DecimalFormat("#,##0.00 DH").format(solde)));
        
        // Opérations
        JPanel operationsPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        JButton consulterBtn = new JButton("Consulter");
        JButton verserBtn = new JButton("Verser");
        JButton retirerBtn = new JButton("Retirer");
        JButton transfertBtn = new JButton("Transfert");
        
        operationsPanel.add(consulterBtn);
        operationsPanel.add(verserBtn);
        operationsPanel.add(retirerBtn);
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
        
        JTextArea historiqueArea = new JTextArea(10, 40);
        historiqueArea.setEditable(false);
        historiqueArea.append("Dernières transactions:\n");
        
        while (rs.next()) {
            String type = rs.getString("type");
            double montant = rs.getDouble("montant");
            String date = rs.getString("date_transaction");
            
            historiqueArea.append(String.format("[%s] %s: %.2f DH\n", 
                date.substring(0, 16), 
                type.equals("depot") ? "Dépôt" : 
                type.equals("retrait") ? "Retrait" : "Transfert", 
                montant));
        }
        
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
        
        verserBtn.addActionListener(e -> {
            String montantStr = JOptionPane.showInputDialog(this, "Montant à verser:");
            try {
                double montant = Double.parseDouble(montantStr);
                
                // Mise à jour du solde
                String sql2 = "UPDATE comptes SET solde = solde + ? WHERE id = ?";
                PreparedStatement stmt2 = conn.prepareStatement(sql2);
                stmt2.setDouble(1, montant);
                stmt2.setInt(2, compteId);
                stmt2.executeUpdate();
                
                // Enregistrement de la transaction
                String sql3 = "INSERT INTO transactions (type, montant, id_compte_source) VALUES (?, ?, ?)";
                PreparedStatement stmt3 = conn.prepareStatement(sql3);
                stmt3.setString(1, "depot");
                stmt3.setDouble(2, montant);
                stmt3.setInt(3, compteId);
                stmt3.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Dépôt effectué avec succès!");
                dispose();
                new ClientInterface(clientId).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Montant invalide", 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        retirerBtn.addActionListener(e -> {
            String montantStr = JOptionPane.showInputDialog(this, "Montant à retirer:");
            try {
                double montant = Double.parseDouble(montantStr);
                
                if (montant > solde) {
                    JOptionPane.showMessageDialog(this, "Solde insuffisant", 
                                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Mise à jour du solde
                String sql3 = "UPDATE comptes SET solde = solde - ? WHERE id = ?";
                PreparedStatement stmt3 = conn.prepareStatement(sql3);
                stmt3.setDouble(1, montant);
                stmt3.setInt(2, compteId);
                stmt3.executeUpdate();
                
                // Enregistrement de la transaction
                String sql4 = "INSERT INTO transactions (type, montant, id_compte_source) VALUES (?, ?, ?)";
                PreparedStatement stmt4 = conn.prepareStatement(sql4);
                stmt4.setString(1, "retrait");
                stmt4.setDouble(2, montant);
                stmt4.setInt(3, compteId);
                stmt4.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Retrait effectué avec succès!");
                dispose();
                new ClientInterface(clientId).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Montant invalide", 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        if (typeCompte.equals("courant")) {
            transfertBtn.addActionListener(e -> {
                JPanel transfertPanel = new JPanel(new GridLayout(3, 2, 5, 5));
                JTextField compteDestField = new JTextField();
                JTextField montantField = new JTextField();
                
                transfertPanel.add(new JLabel("Numéro compte destinataire:"));
                transfertPanel.add(compteDestField);
                transfertPanel.add(new JLabel("Montant:"));
                transfertPanel.add(montantField);
                
                int result = JOptionPane.showConfirmDialog(this, transfertPanel, 
                    "Transfert", JOptionPane.OK_CANCEL_OPTION);
                
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
                        String sql5 = "SELECT id FROM comptes WHERE numero_compte = ? AND type_compte = 'courant'";
                        PreparedStatement stmt5 = conn.prepareStatement(sql5);
                        stmt5.setString(1, compteDest);
                        ResultSet rs2 = stmt5.executeQuery();
                        
                        if (!rs2.next()) {
                            JOptionPane.showMessageDialog(this, "Compte destinataire invalide", 
                                                        "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        int compteDestId = rs2.getInt("id");
                        
                        // Démarrer la transaction
                        conn.setAutoCommit(false);
                        
                        try {
                            // Débiter le compte source
                            String sql6 = "UPDATE comptes SET solde = solde - ? WHERE id = ?";
                            PreparedStatement stmt6 = conn.prepareStatement(sql6);
                            stmt6.setDouble(1, montant);
                            stmt6.setInt(2, compteId);
                            stmt6.executeUpdate();
                            
                            // Créditer le compte destinataire
                            String sql7 = "UPDATE comptes SET solde = solde + ? WHERE id = ?";
                            PreparedStatement stmt7 = conn.prepareStatement(sql7);
                            stmt7.setDouble(1, montant);
                            stmt7.setInt(2, compteDestId);
                            stmt7.executeUpdate();
                            
                            // Enregistrer la transaction
                            String sql8 = "INSERT INTO transactions (type, montant, id_compte_source, id_compte_destination) " +
                                  "VALUES (?, ?, ?, ?)";
                            PreparedStatement stmt8 = conn.prepareStatement(sql8);
                            stmt8.setString(1, "transfert");
                            stmt8.setDouble(2, montant);
                            stmt8.setInt(3, compteId);
                            stmt8.setInt(4, compteDestId);
                            stmt8.executeUpdate();
                            
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
            });
        }
        
        return panel;
    }
}

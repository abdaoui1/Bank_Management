package pack1;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;

class EmployeInterface extends JFrame {
    private int employeId;
    private Connection conn;
	private String sql2;
    
    public EmployeInterface(int employeId) {
        this.employeId = employeId;
        setTitle("Espace Employé");
        setSize(800, 600);
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
        
        // Onglet Gestion Clients
        JPanel clientsPanel = new JPanel(new BorderLayout());
        JTable clientsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(clientsTable);
        
        // Charger les clients
        String sql = "SELECT id, nom, prenom, cin FROM utilisateurs WHERE type = 'client'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        clientsTable.setModel(DbUtils.resultSetToTableModel(rs));
        
        // Boutons
        JPanel buttonsPanel = new JPanel();
        JButton addClientBtn = new JButton("Ajouter Client");
        JButton addCompteBtn = new JButton("Créer Compte");
        JButton deleteClientBtn = new JButton("Supprimer Client");
        
        buttonsPanel.add(addClientBtn);
        buttonsPanel.add(addCompteBtn);
        buttonsPanel.add(deleteClientBtn);
        
        clientsPanel.add(scrollPane, BorderLayout.CENTER);
        clientsPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Gestion Clients", clientsPanel);
        
        // Onglet Gestion Comptes
        JPanel comptesPanel = new JPanel(new BorderLayout());
        JTable comptesTable = new JTable();
        JScrollPane comptesScroll = new JScrollPane(comptesTable);
        
        // Charger les comptes
        sql = "SELECT c.id, c.numero_compte, c.type_compte, c.solde, u.nom, u.prenom " +
              "FROM comptes c JOIN utilisateurs u ON c.id_client = u.id";
        stmt = conn.createStatement();
        rs = stmt.executeQuery(sql);
        comptesTable.setModel(DbUtils.resultSetToTableModel(rs));
        
        comptesPanel.add(comptesScroll, BorderLayout.CENTER);
        tabbedPane.addTab("Gestion Comptes", comptesPanel);
        
        add(tabbedPane);
        
        // Actions des boutons
        // abdessamad modification  Begin
        deleteClientBtn.addActionListener(e -> {
            int selectedRow = clientsTable.getSelectedRow();
            
            if ( selectedRow == -1 ) {
                JOptionPane.showMessageDialog(this,"Sélectionnez un client à supprimer", "Erreur", JOptionPane.WARNING_MESSAGE);
            }
            
            int idClientToDelete = (int) clientsTable.getValueAt(selectedRow, 0);
            
            int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer cet client ?", "Confirmer la suppression", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try ( Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quarta", "root", "") ) {
                    
                    
                    String reqDeleteComptes = "DELETE FROM comptes  WHERE id_client=? ;";
                    PreparedStatement stmtDeleteComptes = conn.prepareStatement(reqDeleteComptes);
                    stmtDeleteComptes.setInt(1 , idClientToDelete);
                    stmtDeleteComptes.executeUpdate();
                    
                    String reqDeleteClient = "DELETE FROM utilisateurs  WHERE id=? AND type='client' ;";
                    PreparedStatement stmtDeleteClient = conn.prepareStatement(reqDeleteClient);
                    stmtDeleteClient.setInt(1 , idClientToDelete);
                    
                    
                    stmtDeleteClient.executeUpdate();
                    // Rafraîchir la table
                    Statement stmt2 = conn.createStatement();
                    ResultSet rs2 = stmt2.executeQuery("SELECT id, nom, prenom, cin FROM utilisateurs WHERE type = 'client'");
                    clientsTable.setModel(DbUtils.resultSetToTableModel(rs2));
                    
                    // Rafraîchir la table des comptes
                    Statement stmt4 = conn.createStatement();
                    ResultSet rs5 = stmt4.executeQuery("SELECT c.id, c.numero_compte, c.type_compte, c.solde, u.nom, u.prenom " +
                                          "FROM comptes c JOIN utilisateurs u ON c.id_client = u.id");
                    comptesTable.setModel(DbUtils.resultSetToTableModel(rs5));
                
                
                } catch (SQLException er) {
                    JOptionPane.showMessageDialog(this, "Erreur: " + er.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        
        });
        
        
        // abdessamad modification  End
        
        
        addClientBtn.addActionListener(e -> {
            JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
            
            JTextField nomField = new JTextField();
            JTextField prenomField = new JTextField();
            JTextField cinField = new JTextField();
            JTextField loginField = new JTextField();
            JPasswordField passwordField = new JPasswordField();
            
            formPanel.add(new JLabel("Nom:"));
            formPanel.add(nomField);
            formPanel.add(new JLabel("Prénom:"));
            formPanel.add(prenomField);
            formPanel.add(new JLabel("CIN:"));
            formPanel.add(cinField);
            formPanel.add(new JLabel("Login:"));
            formPanel.add(loginField);
            formPanel.add(new JLabel("Mot de passe:"));
            formPanel.add(passwordField);
            
            int result = JOptionPane.showConfirmDialog(this, formPanel, 
                "Nouveau Client", JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION) {
                try {
                    sql2 = "INSERT INTO utilisateurs (type, nom, prenom, cin, login, password) " +
                          "VALUES ('client', ?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(sql2);
                    pstmt.setString(1, nomField.getText());
                    pstmt.setString(2, prenomField.getText());
                    pstmt.setString(3, cinField.getText());
                    pstmt.setString(4, loginField.getText());
                    pstmt.setString(5, new String(passwordField.getPassword()));
                    
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Client ajouté avec succès!");
                    
                    // Rafraîchir la table
                    Statement stmt2 = conn.createStatement();
                    ResultSet rs2 = stmt2.executeQuery("SELECT id, nom, prenom, cin FROM utilisateurs WHERE type = 'client'");
                    clientsTable.setModel(DbUtils.resultSetToTableModel(rs2));
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), 
                                                "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        addCompteBtn.addActionListener(e -> {
            int selectedRow = clientsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client", 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int clientId = (Integer) clientsTable.getValueAt(selectedRow, 0);
            String clientNom = (String) clientsTable.getValueAt(selectedRow, 1);
            String clientPrenom = (String) clientsTable.getValueAt(selectedRow, 2);
            
            JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            
            JComboBox<String> typeCompteBox = new JComboBox<>(new String[]{"courant", "epargne"});
            JTextField numeroCompteField = new JTextField();
            JTextField soldeField = new JTextField("0.00");
            
            formPanel.add(new JLabel("Client:"));
            formPanel.add(new JLabel(clientPrenom + " " + clientNom));
            formPanel.add(new JLabel("Type de compte:"));
            formPanel.add(typeCompteBox);
            formPanel.add(new JLabel("Numéro de compte:"));
            formPanel.add(numeroCompteField);
            formPanel.add(new JLabel("Solde initial:"));
            formPanel.add(soldeField);
            
            int result = JOptionPane.showConfirmDialog(this, formPanel, 
                "Créer un compte", JOptionPane.OK_CANCEL_OPTION);
            
            if (result == JOptionPane.OK_OPTION) {
                try {
                    String typeCompte = (String) typeCompteBox.getSelectedItem();
                    String numeroCompte = numeroCompteField.getText();
                    double solde = Double.parseDouble(soldeField.getText());
                    
                    // Vérifier que le client n'a pas déjà ce type de compte
                    String sql3 = "SELECT id FROM comptes WHERE id_client = ? AND type_compte = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql3);
                    pstmt.setInt(1, clientId);
                    pstmt.setString(2, typeCompte);
                    ResultSet rs3 = pstmt.executeQuery();
                    
                    if (rs3.next()) {
                        JOptionPane.showMessageDialog(this, "Ce client a déjà un compte " + typeCompte, 
                                                    "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Créer le compte
                    String sql4 = "INSERT INTO comptes (numero_compte, type_compte, solde, id_client) " +
                          "VALUES (?, ?, ?, ?)";
                    pstmt = conn.prepareStatement(sql4);
                    pstmt.setString(1, numeroCompte);
                    pstmt.setString(2, typeCompte);
                    pstmt.setDouble(3, solde);
                    pstmt.setInt(4, clientId);
                    
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Compte créé avec succès!");
                    
                    // Rafraîchir la table des comptes
                    Statement stmt4 = conn.createStatement();
                    ResultSet rs5 = stmt4.executeQuery("SELECT c.id, c.numero_compte, c.type_compte, c.solde, u.nom, u.prenom " +
                                          "FROM comptes c JOIN utilisateurs u ON c.id_client = u.id");
                    comptesTable.setModel(DbUtils.resultSetToTableModel(rs5));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), 
                                                "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
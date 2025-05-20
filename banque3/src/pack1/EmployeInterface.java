package pack1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import secondary.Styles;

class EmployeInterface extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Connection conn;
    private String sql2;
    // Updated primary color to match DirecteurInterface


    
    private JTabbedPane tabbedPane;

    public EmployeInterface(int employeId) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new NimbusLookAndFeel());
        setTitle("Espace Employé ");
        setSize(900, 650);
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
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Styles.BACKGROUND_COLOR);
        // Set font to plain to match DirecteurInterface
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 

        createClientsTab();
        createComptesTab();

        // Apply the same style to both tabs
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            tabbedPane.setBackgroundAt(i, Styles.PRIMARY_COLOR);
            tabbedPane.setForegroundAt(i, Color.WHITE);
        }

        add(tabbedPane);
    }
    
    private void createClientsTab() throws SQLException {
        JPanel clientsPanel = new JPanel(new BorderLayout(15, 15));
        clientsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        clientsPanel.setBackground(Styles.BACKGROUND_COLOR);
        
     // Titre du panneau
        JLabel titleLabel = new JLabel("Gestion des Clients");
        titleLabel.setFont(Styles.TITLE_FONT);
        titleLabel.setForeground(Styles.PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Styles.BACKGROUND_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);
//        headerPanel.add(searchPanel, BorderLayout.EAST);
        clientsPanel.add(headerPanel, BorderLayout.NORTH);
        
        
        
        JTable clientsTable = new JTable();
//        clientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        
  
        
        JScrollPane scrollPane = new JScrollPane(clientsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder() );
        
        String sql = "SELECT id, nom, prenom, cin FROM utilisateurs WHERE type = 'client'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        clientsTable.setModel(DbUtils.resultSetToTableModel(rs));
        
        clientsTable.setFont(Styles.REGULAR_FONT);
        clientsTable.setRowHeight(30);
        clientsTable.setShowGrid(false);
        clientsTable.setIntercellSpacing(new Dimension(0, 0));
        clientsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        clientsTable.getTableHeader().setBackground(Styles.PRIMARY_COLOR);
        clientsTable.getTableHeader().setForeground(Color.BLACK);
        clientsTable.setSelectionBackground(new Color(Styles.PRIMARY_COLOR.getRed(), Styles.PRIMARY_COLOR.getGreen(), Styles.PRIMARY_COLOR.getBlue(), 100));
        
        // Définir des largeurs de colonnes appropriées
        clientsTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        clientsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Nom
        clientsTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Prénom
        clientsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // CIN
        
        // Boutons pour les clients
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonsPanel.setBackground(Styles.BACKGROUND_COLOR);
        
        JButton addClientBtn = Styles.createStyledButton("Ajouter Client", Styles.SUCCESS_COLOR ,"add.png");
        JButton addCompteBtn = Styles.createStyledButton("Créer Compte", Styles.PRIMARY_COLOR,"accounts.png");
        JButton deleteClientBtn = Styles.createStyledButton("Supprimer Client", Styles.DANGER_COLOR,"trash_icon.png");
        
        buttonsPanel.add(addClientBtn);
        buttonsPanel.add(addCompteBtn);
        buttonsPanel.add(deleteClientBtn);
        
        clientsPanel.add(scrollPane, BorderLayout.CENTER);
        clientsPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Gestion Clients", Styles.createIcon("clients.png",20), clientsPanel, "Gérer les clients");
        
        // Actions des boutons clients
        addClientBtn.addActionListener(e -> showAddClientDialog(clientsTable));
        addCompteBtn.addActionListener(e -> showAddAccountDialog(clientsTable, getComptesTable()));
        deleteClientBtn.addActionListener(e -> deleteClient(clientsTable, getComptesTable()));
    }
    
    private void createComptesTab() throws SQLException {
        JPanel comptesPanel = new JPanel(new BorderLayout(15, 15));
        comptesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        comptesPanel.setBackground(Styles.BACKGROUND_COLOR);
        
        
     // Titre du panneau
        JLabel titleLabel = new JLabel("Gestion des comptes");
        titleLabel.setFont(Styles.TITLE_FONT);
        titleLabel.setForeground(Styles.PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Styles.BACKGROUND_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);
//        headerPanel.add(searchPanel, BorderLayout.EAST);
        comptesPanel.add(headerPanel, BorderLayout.NORTH);
        
        JTable comptesTable = new JTable();
//        comptesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane comptesScroll = new JScrollPane(comptesTable);
        comptesScroll.setBorder(BorderFactory.createEmptyBorder() );
        
        String sql = "SELECT c.id, c.numero_compte, c.type_compte, c.solde, u.nom, u.prenom " +
              "FROM comptes c JOIN utilisateurs u ON c.id_client = u.id";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        comptesTable.setModel(DbUtils.resultSetToTableModel(rs));
        
        comptesTable.setFont(Styles.REGULAR_FONT);
        comptesTable.setRowHeight(30);
        comptesTable.setShowGrid(false);
        comptesTable.setIntercellSpacing(new Dimension(0, 0));
        comptesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        comptesTable.getTableHeader().setBackground(Styles.PRIMARY_COLOR);
        comptesTable.getTableHeader().setForeground(Color.BLACK);
        comptesTable.setSelectionBackground(new Color(Styles.PRIMARY_COLOR.getRed(), Styles.PRIMARY_COLOR.getGreen(), Styles.PRIMARY_COLOR.getBlue(), 100));
        
        // Définir des largeurs de colonnes appropriées
        comptesTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        comptesTable.getColumnModel().getColumn(1).setPreferredWidth(150); // numero compte
        comptesTable.getColumnModel().getColumn(2).setPreferredWidth(150); // type compte
        comptesTable.getColumnModel().getColumn(3).setPreferredWidth(100); // solde
        comptesTable.getColumnModel().getColumn(4).setPreferredWidth(150); // nom
        comptesTable.getColumnModel().getColumn(5).setPreferredWidth(150); // prenom
        
        // Bouton Supprimer Compte
        JPanel compteButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        compteButtonsPanel.setBackground(Styles.BACKGROUND_COLOR);
        JButton deleteCompteBtn = Styles.createStyledButton("Supprimer Compte", Styles.DANGER_COLOR,"trash_icon.png");
        compteButtonsPanel.add(deleteCompteBtn);
        
        comptesPanel.add(comptesScroll, BorderLayout.CENTER);
        comptesPanel.add(compteButtonsPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Gestion Comptes", Styles.createIcon("gestion_comptes.png",20), comptesPanel, "Gérer les comptes bancaires");
        
        // Action du bouton supprimer compte
        deleteCompteBtn.addActionListener(e -> deleteCompte(comptesTable));
    }
    
    private JTable getComptesTable() {
        JPanel clientsPanel = (JPanel) tabbedPane.getComponentAt(1);

        for (Component comp : clientsPanel.getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                Component view = scrollPane.getViewport().getView();
                if (view instanceof JTable) {
                    return (JTable) view;
                }
            }
        }

        // If not found, return null or throw exception
        return null;
    }

    
//    private JTable getComptesTable() {
//        JPanel comptesPanel = (JPanel) tabbedPane.getComponentAt(1);
//        JScrollPane scrollPane = (JScrollPane) comptesPanel.getComponent(0);
//        return (JTable) scrollPane.getViewport().getView();
//    }
    
    private void deleteClient(JTable clientsTable, JTable comptesTable) {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un client à supprimer", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idClientToDelete = (int) clientsTable.getValueAt(selectedRow, 0);
        String clientNom = (String) clientsTable.getValueAt(selectedRow, 1);
        String clientPrenom = (String) clientsTable.getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer le client " + clientPrenom + " " + clientNom + "?\n" +
            "Tous ses comptes seront également supprimés.",
            "Confirmer la suppression", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                conn.setAutoCommit(false);
                
                // 1. Supprimer d'abord les transactions liées aux comptes du client
                String deleteTransactionsSql = "DELETE t FROM transactions t " +
                                            "JOIN comptes c ON t.id_compte_source = c.id OR t.id_compte_destination = c.id " +
                                            "WHERE c.id_client = ?";
                PreparedStatement deleteTransactionsStmt = conn.prepareStatement(deleteTransactionsSql);
                deleteTransactionsStmt.setInt(1, idClientToDelete);
                deleteTransactionsStmt.executeUpdate();
                
                // 2. Ensuite supprimer les comptes du client
                String deleteAccountsSql = "DELETE FROM comptes WHERE id_client = ?";
                PreparedStatement deleteAccountsStmt = conn.prepareStatement(deleteAccountsSql);
                deleteAccountsStmt.setInt(1, idClientToDelete);
                deleteAccountsStmt.executeUpdate();
                
                // 3. Enfin supprimer le client
                String deleteClientSql = "DELETE FROM utilisateurs WHERE id = ? AND type = 'client'";
                PreparedStatement deleteClientStmt = conn.prepareStatement(deleteClientSql);
                deleteClientStmt.setInt(1, idClientToDelete);
                int rowsAffected = deleteClientStmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    conn.commit();
                    JOptionPane.showMessageDialog(this, 
                        "Client et ses comptes supprimés avec succès!", 
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                    refreshTables(clientsTable, comptesTable);
                } else {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la suppression du client", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                JOptionPane.showMessageDialog(this, 
                    "Erreur: " + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void deleteCompte(JTable comptesTable) {
        int selectedRow = comptesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un compte à supprimer", 
                "Aucune sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Récupération des données du compte sélectionné
        int idCompte = Integer.parseInt(comptesTable.getValueAt(selectedRow, 0).toString());
        String numeroCompte = comptesTable.getValueAt(selectedRow, 1).toString();
        String typeCompte = comptesTable.getValueAt(selectedRow, 2).toString();
        double solde = Double.parseDouble(comptesTable.getValueAt(selectedRow, 3).toString());
        String clientNom = comptesTable.getValueAt(selectedRow, 4).toString();
        String clientPrenom = comptesTable.getValueAt(selectedRow, 5).toString();

        // Confirmation de suppression
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer le compte " + typeCompte + 
            " n°" + numeroCompte + " de " + clientPrenom + " " + clientNom + 
            " (solde: " + solde + " DH)?\n" +
            "Toutes les transactions associées seront également supprimées.",
            "Confirmation de suppression", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                conn.setAutoCommit(false); // Démarrer une transaction
                
                // 1. Supprimer les transactions liées au compte (comme source ou destination)
                String deleteTransactionsSql = "DELETE FROM transactions " +
                                           "WHERE id_compte_source = ? OR id_compte_destination = ?";
                PreparedStatement deleteTransactionsStmt = conn.prepareStatement(deleteTransactionsSql);
                deleteTransactionsStmt.setInt(1, idCompte);
                deleteTransactionsStmt.setInt(2, idCompte);
                deleteTransactionsStmt.executeUpdate();

                // 2. Supprimer le compte
                String deleteCompteSql = "DELETE FROM comptes WHERE id = ?";
                PreparedStatement deleteCompteStmt = conn.prepareStatement(deleteCompteSql);
                deleteCompteStmt.setInt(1, idCompte);
                int rowsDeleted = deleteCompteStmt.executeUpdate();

                if (rowsDeleted > 0) {
                    conn.commit(); // Valider la transaction
                    JOptionPane.showMessageDialog(this, 
                        "Compte et transactions associées supprimés avec succès!", 
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Rafraîchir les tables
                    refreshTables(getClientsTable(), comptesTable);
                } else {
                    conn.rollback(); // Annuler la transaction
                    JOptionPane.showMessageDialog(this, 
                        "Aucun compte n'a été supprimé", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                try {
                    conn.rollback(); // En cas d'erreur, annuler la transaction
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la suppression: " + ex.getMessage(), 
                    "Erreur SQL", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    conn.setAutoCommit(true); // Rétablir le mode auto-commit
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private JTable getClientsTable() {
        JPanel clientsPanel = (JPanel) tabbedPane.getComponentAt(0);

        for (Component comp : clientsPanel.getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                Component view = scrollPane.getViewport().getView();
                if (view instanceof JTable) {
                    return (JTable) view;
                }
            }
        }

        // If not found, return null or throw exception
        return null;
    }

    
//    private JTable getClientsTable() {
//        JPanel clientsPanel = (JPanel) tabbedPane.getComponentAt(0);
//        JScrollPane scrollPane = (JScrollPane) clientsPanel.getComponent(0);
//        return (JTable) scrollPane.getViewport().getView();
//    }
    
    private void refreshTables(JTable clientsTable, JTable comptesTable) {
        try {
            // Rafraîchir la table clients
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, nom, prenom, cin FROM utilisateurs WHERE type = 'client'");
            clientsTable.setModel(DbUtils.resultSetToTableModel(rs));
            
            // Rafraîchir la table comptes
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT c.id, c.numero_compte, c.type_compte, c.solde, u.nom, u.prenom " +
                                  "FROM comptes c JOIN utilisateurs u ON c.id_client = u.id");
            comptesTable.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors du rafraîchissement des données: " + ex.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddClientDialog(JTable clientsTable) {
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField nomField = new JTextField();
        JTextField prenomField = new JTextField();
        JTextField cinField = new JTextField();
        JTextField loginField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        
        addFormField(formPanel, "Nom:", nomField);
        addFormField(formPanel, "Prénom:", prenomField);
        addFormField(formPanel, "CIN:", cinField);
        addFormField(formPanel, "Login:", loginField);
        addFormField(formPanel, "Mot de passe:", passwordField);
        
        int result = JOptionPane.showConfirmDialog(this, formPanel, 
            "Nouveau Client", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                sql2 = "INSERT INTO utilisateurs (type, nom, prenom, cin, login, password) " +
                      "VALUES ('client', ?, ?, ?, ?, ? )";
                
               
             
                
                PreparedStatement pstmt = conn.prepareStatement(sql2);
                pstmt.setString(1, nomField.getText());
                pstmt.setString(2, prenomField.getText());
                pstmt.setString(3, cinField.getText());
                pstmt.setString(4, loginField.getText());
                
                // Modification START 
                // Hashing the password : 
                String password = new String ( passwordField.getPassword() );
                String hashedPassword = PasswordUtils.hashPassword( password );
                pstmt.setString(5, hashedPassword );
                // Modification END 
                
                
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Client ajouté avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                
                refreshTables(clientsTable, getComptesTable());
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showAddAccountDialog(JTable clientsTable, JTable comptesTable) {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client", 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int clientId = (Integer) clientsTable.getValueAt(selectedRow, 0);
        String clientNom = (String) clientsTable.getValueAt(selectedRow, 1);
        String clientPrenom = (String) clientsTable.getValueAt(selectedRow, 2);
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JComboBox<String> typeCompteBox = new JComboBox<>(new String[]{"courant", "epargne"});
        typeCompteBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JTextField numeroCompteField = new JTextField();
        JTextField soldeField = new JTextField("0.00");
        
        addFormField(formPanel, "Client:", new JLabel(clientPrenom + " " + clientNom));
        addFormField(formPanel, "Type de compte:", typeCompteBox);
        addFormField(formPanel, "Numéro de compte:", numeroCompteField);
        addFormField(formPanel, "Solde initial:", soldeField);
        
        int result = JOptionPane.showConfirmDialog(this, formPanel, 
            "Créer un compte", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String typeCompte = (String) typeCompteBox.getSelectedItem();
                String numeroCompte = numeroCompteField.getText();
                double solde = Double.parseDouble(soldeField.getText());
                
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
                
                String sql4 = "INSERT INTO comptes (numero_compte, type_compte, solde, id_client) " +
                      "VALUES (?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql4);
                pstmt.setString(1, numeroCompte);
                pstmt.setString(2, typeCompte);
                pstmt.setDouble(3, solde);
                pstmt.setInt(4, clientId);
                
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Compte créé avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                
                refreshTables(clientsTable, comptesTable);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void addFormField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(Styles.PRIMARY_COLOR);
        panel.add(label);
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        if (field instanceof JTextField) {
            ((JTextField)field).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
        }
        panel.add(field);
    }
}
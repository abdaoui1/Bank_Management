package pack1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

class EmployeInterface extends JFrame {
    
    
    
   
    private static final long serialVersionUID = -3930344573291586087L;
    private Connection conn;
    private String sql2;
    private Color primaryColor = new Color(0, 102, 153); // Bleu professionnel
    private Color secondaryColor = new Color(245, 245, 245); // Gris clair
    private Color buttonTextColor = new Color(0, 70, 140); // Bleu foncé pour le texte
    private JTabbedPane tabbedPane;
    public EmployeInterface(int employeId) {
        setTitle("Espace Employé - Système Bancaire");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                UIManager.put("TabbedPane.selected", primaryColor);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
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
        tabbedPane.setBackground(secondaryColor);
        tabbedPane.setForeground(Color.DARK_GRAY);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        createClientsTab();
        createComptesTab();
        
        add(tabbedPane);
    }
    
    private void createClientsTab() throws SQLException {
        JPanel clientsPanel = new JPanel(new BorderLayout(10, 10));
        clientsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        clientsPanel.setBackground(secondaryColor);
        
        JTable clientsTable = new JTable();
        clientsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        clientsTable.setRowHeight(25);
        clientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(clientsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(primaryColor, 1), 
            "Liste des Clients"));
        
        String sql = "SELECT id, nom, prenom, cin FROM utilisateurs WHERE type = 'client'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        clientsTable.setModel(DbUtils.resultSetToTableModel(rs));
        
        // Boutons pour les clients
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonsPanel.setBackground(secondaryColor);
        
        JButton addClientBtn = createStyledButton("Ajouter Client", new Color(220, 255, 220));
        JButton addCompteBtn = createStyledButton("Créer Compte", new Color(220, 230, 255));
        JButton deleteClientBtn = createStyledButton("Supprimer Client", new Color(255, 220, 220));
        
        buttonsPanel.add(addClientBtn);
        buttonsPanel.add(addCompteBtn);
        buttonsPanel.add(deleteClientBtn);
        
        clientsPanel.add(scrollPane, BorderLayout.CENTER);
        clientsPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Gestion Clients", new ImageIcon("client_icon.png"), clientsPanel, "Gérer les clients");
        
        // Actions des boutons clients
        addClientBtn.addActionListener(e -> showAddClientDialog(clientsTable));
        addCompteBtn.addActionListener(e -> showAddAccountDialog(clientsTable, getComptesTable()));
        deleteClientBtn.addActionListener(e -> deleteClient(clientsTable, getComptesTable()));
    }
    
    private void createComptesTab() throws SQLException {
        JPanel comptesPanel = new JPanel(new BorderLayout(10, 10));
        comptesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        comptesPanel.setBackground(secondaryColor);
        
        JTable comptesTable = new JTable();
        comptesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comptesTable.setRowHeight(25);
        comptesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane comptesScroll = new JScrollPane(comptesTable);
        comptesScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(primaryColor, 1), 
            "Liste des Comptes"));
        
        String sql = "SELECT c.id, c.numero_compte, c.type_compte, c.solde, u.nom, u.prenom " +
              "FROM comptes c JOIN utilisateurs u ON c.id_client = u.id";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        comptesTable.setModel(DbUtils.resultSetToTableModel(rs));
        
        // Bouton Supprimer Compte
        JPanel compteButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        compteButtonsPanel.setBackground(secondaryColor);
        JButton deleteCompteBtn = createStyledButton("Supprimer Compte", new Color(255, 200, 200));
        compteButtonsPanel.add(deleteCompteBtn);
        
        comptesPanel.add(comptesScroll, BorderLayout.CENTER);
        comptesPanel.add(compteButtonsPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Gestion Comptes", new ImageIcon("/imgs/account_icon.png") , comptesPanel, "Gérer les comptes bancaires");
        
        // Action du bouton supprimer compte
        deleteCompteBtn.addActionListener(e -> deleteCompte(comptesTable));
    }
    
    private JTable getComptesTable() {
        JPanel comptesPanel = (JPanel) tabbedPane.getComponentAt(1);
        JScrollPane scrollPane = (JScrollPane) comptesPanel.getComponent(0);
        return (JTable) scrollPane.getViewport().getView();
    }
    
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
                                            "JOIN comptes c ON t.id_compte_source = c.id " +
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
            JOptionPane.showMessageDialog(this, "Sélectionnez un compte à supprimer", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String numeroCompte = (String) comptesTable.getValueAt(selectedRow, 1);
        String typeCompte = (String) comptesTable.getValueAt(selectedRow, 2);
        double solde = (Double) comptesTable.getValueAt(selectedRow, 3);
        String clientNom = (String) comptesTable.getValueAt(selectedRow, 4);
        String clientPrenom = (String) comptesTable.getValueAt(selectedRow, 5);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer le compte " + typeCompte + 
            " n°" + numeroCompte + " de " + clientPrenom + " " + clientNom + 
            " (solde: " + solde + " DH)?",
            "Confirmation de suppression", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                conn.setAutoCommit(false);
                
                // 1. Supprimer d'abord les transactions liées au compte
                String deleteTransactionsSql = "DELETE FROM transactions WHERE id_compte_source = " +
                                            "(SELECT id FROM comptes WHERE numero_compte = ?)";
                PreparedStatement deleteTransactionsStmt = conn.prepareStatement(deleteTransactionsSql);
                deleteTransactionsStmt.setString(1, numeroCompte);
                deleteTransactionsStmt.executeUpdate();
                
                // 2. Ensuite supprimer le compte
                String deleteCompteSql = "DELETE FROM comptes WHERE numero_compte = ?";
                PreparedStatement deleteCompteStmt = conn.prepareStatement(deleteCompteSql);
                deleteCompteStmt.setString(1, numeroCompte);
                int rowsAffected = deleteCompteStmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    conn.commit();
                    JOptionPane.showMessageDialog(this, 
                        "Compte supprimé avec succès!", 
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                    
                    // Rafraîchir la table des comptes
                    refreshTables(getClientsTable(), comptesTable);
                } else {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de la suppression du compte", 
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
    
    private JTable getClientsTable() {
        JPanel clientsPanel = (JPanel) tabbedPane.getComponentAt(0);
        JScrollPane scrollPane = (JScrollPane) clientsPanel.getComponent(0);
        return (JTable) scrollPane.getViewport().getView();
    }
    
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
                      "VALUES ('client', ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql2);
                pstmt.setString(1, nomField.getText());
                pstmt.setString(2, prenomField.getText());
                pstmt.setString(3, cinField.getText());
                pstmt.setString(4, loginField.getText());
                pstmt.setString(5, new String(passwordField.getPassword()));
                
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
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(buttonTextColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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
    
    private void addFormField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(primaryColor);
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
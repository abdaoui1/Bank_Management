package pack1;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

class DirecteurInterface extends EmployeInterface {
    private DefaultTableModel empTableModel;
    private JTable empTable;
    private JLabel lblNbClients, lblNbEmployes, lblNbComptes, lblSoldeTotal;

    public DirecteurInterface(int directeurId) {
        super(directeurId);
        setTitle("Espace Directeur");

        JTabbedPane tabbedPane = (JTabbedPane) getContentPane().getComponent(0);

        // Onglet Gestion des employés
        JPanel empPanel = new JPanel(new BorderLayout());

        empTableModel = new DefaultTableModel(new Object[]{"ID", "Nom", "Prénom", "CIN", "Login"}, 0);
        empTable = new JTable(empTableModel);
        JScrollPane empScrollPane = new JScrollPane(empTable);

        empPanel.add(empScrollPane, BorderLayout.CENTER);

        JPanel empButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnAddEmployee = new JButton("Ajouter un employé");
        btnAddEmployee.addActionListener(e -> ajouterEmploye());
        empButtonPanel.add(btnAddEmployee);

        JButton btnEditEmployee = new JButton("Modifier un employé");
        btnEditEmployee.addActionListener(e -> modifierEmploye());
        empButtonPanel.add(btnEditEmployee);

        JButton btnDeleteEmployee = new JButton("Supprimer un employé");
        btnDeleteEmployee.addActionListener(e -> supprimerEmploye());
        empButtonPanel.add(btnDeleteEmployee);

        empPanel.add(empButtonPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Gestion des employés", empPanel);

        // Onglet Statistiques
     // Onglet Statistiques
        JPanel statsPanel = new JPanel(new BorderLayout());
        
        // Exemple de statistiques simples
        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/quarta", "root", "");
            
            // Nombre de clients
            String sql = "SELECT COUNT(*) FROM utilisateurs WHERE type = 'client'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            int nbClients = rs.getInt(1);
            
            // Nombre de comptes
            sql = "SELECT COUNT(*) FROM comptes";
            rs = stmt.executeQuery(sql);
            rs.next();
            int nbComptes = rs.getInt(1);
            
            // Solde total
            sql = "SELECT SUM(solde) FROM comptes";
            rs = stmt.executeQuery(sql);
            rs.next();
            double soldeTotal = rs.getDouble(1);
            
            JTextArea statsArea = new JTextArea();
            statsArea.append("Statistiques de la banque:\n\n");
            statsArea.append("Nombre de clients: " + nbClients + "\n");
            statsArea.append("Nombre de comptes: " + nbComptes + "\n");
            statsArea.append("Solde total: " + new DecimalFormat("#,##0.00 DH").format(soldeTotal) + "\n");
            statsArea.setEditable(false);
            
            statsPanel.add(new JScrollPane(statsArea), BorderLayout.CENTER);
            chargerEmployes();
            chargerStatistiques();
            tabbedPane.addTab("Statistiques", statsPanel);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
//        JPanel statsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
//        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//
//        lblNbClients = new JLabel();
//        lblNbEmployes = new JLabel();
//        lblNbComptes = new JLabel();
//        lblSoldeTotal = new JLabel();
//
//        statsPanel.add(new JLabel("Nombre de Clients :"));
//        statsPanel.add(lblNbClients);
//        statsPanel.add(new JLabel("Nombre d'Employés :"));
//        statsPanel.add(lblNbEmployes);
//        statsPanel.add(new JLabel("Nombre de Comptes :"));
//        statsPanel.add(lblNbComptes);
//        statsPanel.add(new JLabel("Solde Total des Comptes :"));
//        statsPanel.add(lblSoldeTotal);
//
//        tabbedPane.addTab("Statistiques", statsPanel);

        // Charger les données
       // chargerEmployes();
       // chargerStatistiques();
    

    private void ajouterEmploye() {
        JTextField nomField = new JTextField(15);
        JTextField prenomField = new JTextField(15);
        JTextField cinField = new JTextField(15);
        JTextField loginField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Nom:"));
        panel.add(nomField);
        panel.add(new JLabel("Prénom:"));
        panel.add(prenomField);
        panel.add(new JLabel("CIN:"));
        panel.add(cinField);
        panel.add(new JLabel("Login:"));
        panel.add(loginField);
        panel.add(new JLabel("Mot de passe:"));
        panel.add(passwordField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Ajouter un employé", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quarta", "root", "")) {
                String sql = "INSERT INTO utilisateurs (nom, prenom, cin, login, password, type) VALUES (?, ?, ?, ?, ?, 'employe')";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, nomField.getText());
                pst.setString(2, prenomField.getText());
                pst.setString(3, cinField.getText());
                pst.setString(4, loginField.getText());
                pst.setString(5, new String(passwordField.getPassword()));
                pst.executeUpdate();

                chargerEmployes();
                chargerStatistiques();
                JOptionPane.showMessageDialog(this, "Employé ajouté avec succès !");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifierEmploye() {
        int selectedRow = empTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un employé à modifier", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int empId = (int) empTableModel.getValueAt(selectedRow, 0);

        JTextField nomField = new JTextField((String) empTableModel.getValueAt(selectedRow, 1), 15);
        JTextField prenomField = new JTextField((String) empTableModel.getValueAt(selectedRow, 2), 15);
        JTextField cinField = new JTextField((String) empTableModel.getValueAt(selectedRow, 3), 15);
        JTextField loginField = new JTextField((String) empTableModel.getValueAt(selectedRow, 4), 15);
        JPasswordField passwordField = new JPasswordField(15);

        JPanel panel = new JPanel(new GridLayout(6, 2));
        panel.add(new JLabel("Nom:"));
        panel.add(nomField);
        panel.add(new JLabel("Prénom:"));
        panel.add(prenomField);
        panel.add(new JLabel("CIN:"));
        panel.add(cinField);
        panel.add(new JLabel("Login:"));
        panel.add(loginField);
        panel.add(new JLabel("Mot de passe:"));
        panel.add(passwordField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Modifier un employé", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quarta", "root", "")) {
                String sql = "UPDATE utilisateurs SET nom = ?, prenom = ?, cin = ?, login = ?, password = ? WHERE id = ? AND type = 'employe'";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, nomField.getText());
                pst.setString(2, prenomField.getText());
                pst.setString(3, cinField.getText());
                pst.setString(4, loginField.getText());
                pst.setString(5, new String(passwordField.getPassword()));
                pst.setInt(6, empId);
                pst.executeUpdate();

                chargerEmployes();
                chargerStatistiques();
                JOptionPane.showMessageDialog(this, "Employé modifié avec succès !");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void supprimerEmploye() {
        int selectedRow = empTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un employé à supprimer", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int empId = (int) empTableModel.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer cet employé ?", "Confirmer la suppression", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quarta", "root", "")) {
                String sql = "DELETE FROM utilisateurs WHERE id = ? AND type = 'employe'";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setInt(1, empId);
                pst.executeUpdate();

                chargerEmployes();
                chargerStatistiques();
                JOptionPane.showMessageDialog(this, "Employé supprimé avec succès !");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void chargerEmployes() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quarta", "root", "")) {
            String sql = "SELECT * FROM utilisateurs WHERE type = 'employe'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            empTableModel.setRowCount(0);

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String cin = rs.getString("cin");
                String login = rs.getString("login");
                empTableModel.addRow(new Object[]{id, nom, prenom, cin, login});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chargerStatistiques() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quarta", "root", "")) {
//            Modification Begin 
            // Nombre de clients
            String sql = "SELECT COUNT(*) FROM utilisateurs WHERE type = 'client'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            int nbClients = rs.getInt(1);
            
            // Nombre de comptes
            sql = "SELECT COUNT(*) FROM comptes";
            rs = stmt.executeQuery(sql);
            rs.next();
            int nbComptes = rs.getInt(1);
            
            // Solde total
            sql = "SELECT SUM(solde) FROM comptes";
            rs = stmt.executeQuery(sql);
            rs.next();
            double soldeTotal = rs.getDouble(1);
            
            JTextArea statsArea = new JTextArea();
            statsArea.append("Statistiques de la banque:\n\n");
            statsArea.append("Nombre de clients: " + nbClients + "\n");
            statsArea.append("Nombre de comptes: " + nbComptes + "\n");
            statsArea.append("Solde total: " + new DecimalFormat("#,##0.00 DH").format(soldeTotal) + "\n");
            statsArea.setEditable(false);
            
            // Modification End
            
            
            
            // Nombre de clients
//            String sqlClients = "SELECT COUNT(*) AS nb_clients FROM utilisateurs WHERE type = 'client'";
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery(sqlClients);
//            if (rs.next()) {
//                lblNbClients.setText(String.valueOf(rs.getInt("nb_clients")));
//            }
//
//            // Nombre d'employés
//            String sqlEmployes = "SELECT COUNT(*) AS nb_employes FROM utilisateurs WHERE type = 'employe'";
//            rs = stmt.executeQuery(sqlEmployes);
//            if (rs.next()) {
//                lblNbEmployes.setText(String.valueOf(rs.getInt("nb_employes")));
//            }
//
//            // Nombre de comptes
//            String sqlComptes = "SELECT COUNT(*) AS nb_comptes FROM comptes";
//            rs = stmt.executeQuery(sqlComptes);
//            if (rs.next()) {
//                lblNbComptes.setText(String.valueOf(rs.getInt("nb_comptes")));
//            }
//
//            // Solde total
//            String sqlSolde = "SELECT SUM(solde) AS solde_total FROM comptes";
//            rs = stmt.executeQuery(sqlSolde);
//            if (rs.next()) {
//                lblSoldeTotal.setText(rs.getBigDecimal("solde_total") != null ? rs.getBigDecimal("solde_total").toString() + " DH" : "0 DH");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
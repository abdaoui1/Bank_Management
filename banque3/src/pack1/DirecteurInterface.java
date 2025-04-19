package pack1;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.DecimalFormat;

class DirecteurInterface extends EmployeInterface {
    public DirecteurInterface(int directeurId) {
        super(directeurId);
        setTitle("Espace Directeur");
        
        // Ajouter des fonctionnalités spécifiques au directeur
        JTabbedPane tabbedPane = (JTabbedPane) getContentPane().getComponent(0);
        
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
            
            tabbedPane.addTab("Statistiques", statsPanel);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), 
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}


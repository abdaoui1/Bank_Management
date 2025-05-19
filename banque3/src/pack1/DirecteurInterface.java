package pack1;

import java.awt.*;
import java.sql.*;
import java.text.DecimalFormat;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class DirecteurInterface extends EmployeInterface {
    private static final long serialVersionUID = 1L;
    
    // Constantes pour les couleurs
    private static final Color PRIMARY_COLOR = new Color(25, 118, 210);
    private static final Color SECONDARY_COLOR = new Color(66, 66, 66);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color SUCCESS_COLOR = new Color(46, 125, 50);
    private static final Color WARNING_COLOR = new Color(237, 108, 2);
    private static final Color DANGER_COLOR = new Color(211, 47, 47);
    
    // Polices
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    
    private DefaultTableModel empTableModel;
    private JTable empTable;
    private JLabel statusLabel;

    public DirecteurInterface(int directeurId) throws UnsupportedLookAndFeelException {
        super(directeurId);
        UIManager.setLookAndFeel(new NimbusLookAndFeel());
        setTitle("Espace Directeur | Tableau de Bord");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Configurer le panneau principal
        JTabbedPane tabbedPane = (JTabbedPane) getContentPane().getComponent(0);
        tabbedPane.setFont(REGULAR_FONT);
        tabbedPane.setBackground(BACKGROUND_COLOR);
        
        // Créer la barre d'état
        statusLabel = new JLabel(" Système prêt", createIcon("check.png", 16), JLabel.LEFT);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
        getContentPane().add(statusLabel, BorderLayout.SOUTH);

        // === Onglet Gestion des employés ===
        JPanel empPanel = createEmployesPanel();
        tabbedPane.addTab("Gestion des employés", createIcon("users.png", 20), empPanel);

        // === Onglet Statistiques ===
        JPanel statsPanel = createStatistiquesPanel();
        tabbedPane.addTab("Statistiques", createIcon("chart.png", 20), statsPanel);

        // Charger les données au démarrage
        SwingUtilities.invokeLater(this::chargerEmployes);
        
        // Style des onglets
        tabbedPane.setBackgroundAt(tabbedPane.indexOfTab("Gestion des employés"), PRIMARY_COLOR);
        tabbedPane.setForegroundAt(tabbedPane.indexOfTab("Gestion des employés"), Color.WHITE);
    }
    
    private JPanel createEmployesPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // Titre du panneau
        JLabel titleLabel = new JLabel("Gestion des Employés");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Zone de recherche
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(BACKGROUND_COLOR);
        JTextField searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Rechercher un employé...");
        searchField.setFont(REGULAR_FONT);
        JButton searchButton = createIconButton("search.png", "Rechercher");
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Table des employés avec style moderne
        empTableModel = new DefaultTableModel(new Object[]{"ID", "Nom", "Prénom", "CIN", "Login"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        empTable = new JTable(empTableModel);
        empTable.setFont(REGULAR_FONT);
        empTable.setRowHeight(30);
        empTable.setShowGrid(false);
        empTable.setIntercellSpacing(new Dimension(0, 0));
        empTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        empTable.getTableHeader().setBackground(PRIMARY_COLOR);
        empTable.getTableHeader().setForeground(Color.WHITE);
        empTable.setSelectionBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 100));
        
        // Définir des largeurs de colonnes appropriées
        empTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        empTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Nom
        empTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Prénom
        empTable.getColumnModel().getColumn(3).setPreferredWidth(100); // CIN
        empTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Login
        
        // Ajouter double-clic pour modifier
        empTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    modifierEmploye();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(empTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panneau de boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton btnAdd = createStyledButton("Ajouter", SUCCESS_COLOR, "plus.png");
        btnAdd.addActionListener(e -> ajouterEmploye());
        
        JButton btnEdit = createStyledButton("Modifier", PRIMARY_COLOR, "edit.png");
        btnEdit.addActionListener(e -> modifierEmploye());
        
        JButton btnDelete = createStyledButton("Supprimer", DANGER_COLOR, "trash.png");
        btnDelete.addActionListener(e -> supprimerEmploye());
        
        JButton btnRefresh = createStyledButton("Actualiser", SECONDARY_COLOR, "refresh.png");
        btnRefresh.addActionListener(e -> chargerEmployes());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createStatistiquesPanel() {
        JPanel statsPanel = new JPanel(new BorderLayout(15, 15));
        statsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        statsPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel titleLabel = new JLabel("Statistiques de la Banque");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        statsPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Panneau principal pour les statistiques
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setBackground(BACKGROUND_COLOR);
        
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quarta", "root", "")) {
            Statement stmt = conn.createStatement();
            
            // Statistiques clients
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM utilisateurs WHERE type='client'");
            rs.next();
            int nbClients = rs.getInt(1);
            JPanel clientCard = createStatCard("Clients", String.valueOf(nbClients), "clients.png", new Color(3, 155, 229));
            cardsPanel.add(clientCard);
            
            // Statistiques comptes
            rs = stmt.executeQuery("SELECT COUNT(*) FROM comptes");
            rs.next();
            int nbComptes = rs.getInt(1);
            JPanel comptesCard = createStatCard("Comptes", String.valueOf(nbComptes), "accounts.png", new Color(0, 150, 136));
            cardsPanel.add(comptesCard);
            
            // Statistiques employés
            rs = stmt.executeQuery("SELECT COUNT(*) FROM utilisateurs WHERE type='employe'");
            rs.next();
            int nbEmployes = rs.getInt(1);
            JPanel employesCard = createStatCard("Employés", String.valueOf(nbEmployes), "employee.png", new Color(156, 39, 176));
            cardsPanel.add(employesCard);
            
            // Solde total
            rs = stmt.executeQuery("SELECT SUM(solde) FROM comptes");
            rs.next();
            double soldeTotal = rs.getDouble(1);
            String formattedSolde = new DecimalFormat("#,##0.00 DH").format(soldeTotal);
            JPanel soldeCard = createStatCard("Solde Total", formattedSolde, "money.png", new Color(67, 160, 71));
            cardsPanel.add(soldeCard);
            
            // Détails supplémentaires
            JPanel detailsPanel = new JPanel(new BorderLayout());
            detailsPanel.setBackground(Color.WHITE);
            detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(0, 0, 0, 30), 1, true),
                    new EmptyBorder(15, 15, 15, 15)
            ));
            
            JTextArea detailsArea = new JTextArea();
            detailsArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            detailsArea.setEditable(false);
            detailsArea.setLineWrap(true);
            detailsArea.setWrapStyleWord(true);
            detailsArea.setBackground(Color.WHITE);
            
            // Ajouter plus de détails statistiques si disponibles
            rs = stmt.executeQuery("SELECT AVG(solde) FROM comptes");
            rs.next();
            double soldeMoyen = rs.getDouble(1);
            
            detailsArea.append("• Solde moyen par compte: " + new DecimalFormat("#,##0.00 DH").format(soldeMoyen) + "\n\n");
            detailsArea.append("• Nombre moyen de comptes par client: " + String.format("%.2f", (double)nbComptes/nbClients) + "\n\n");
            detailsArea.append("• Ratio employés/clients: 1:" + String.format("%.1f", (double)nbClients/nbEmployes));
            
            detailsPanel.add(new JLabel("Détails Supplémentaires"), BorderLayout.NORTH);
            detailsPanel.add(detailsArea, BorderLayout.CENTER);
            
            JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
            contentPanel.setBackground(BACKGROUND_COLOR);
            contentPanel.add(cardsPanel, BorderLayout.NORTH);
            contentPanel.add(detailsPanel, BorderLayout.CENTER);
            
            statsPanel.add(contentPanel, BorderLayout.CENTER);
            
        } catch (SQLException e) {
            JPanel errorPanel = new JPanel(new BorderLayout());
            errorPanel.setBackground(new Color(DANGER_COLOR.getRed(), DANGER_COLOR.getGreen(), DANGER_COLOR.getBlue(), 30));
            errorPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            
            JLabel errorLabel = new JLabel("Erreur lors du chargement des statistiques: " + e.getMessage());
            errorLabel.setForeground(DANGER_COLOR);
            errorLabel.setFont(REGULAR_FONT);
            errorPanel.add(errorLabel, BorderLayout.CENTER);
            
            statsPanel.add(errorPanel, BorderLayout.CENTER);
        }
        
        JButton refreshButton = createStyledButton("Actualiser les statistiques", PRIMARY_COLOR, "refresh.png");
        refreshButton.addActionListener(e -> refreshStatistics(statsPanel));
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.add(refreshButton);
        
        statsPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        return statsPanel;
    }
    
    private JPanel createStatCard(String title, String value, String iconName, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(0, 0, 0, 30), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel iconLabel = new JLabel(createIcon(iconName, 32));
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        iconLabel.setVerticalAlignment(JLabel.CENTER);
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 40));
        iconPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        iconPanel.add(iconLabel, BorderLayout.CENTER);
        iconPanel.setPreferredSize(new Dimension(60, 60));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(SECONDARY_COLOR);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(titleLabel);
        textPanel.add(valueLabel);
        
        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void refreshStatistics(JPanel statsPanel) {
        Container parent = statsPanel.getParent();
        int index = -1;
        
        if (parent instanceof JTabbedPane) {
            JTabbedPane tabbedPane = (JTabbedPane) parent;
            index = tabbedPane.indexOfComponent(statsPanel);
            tabbedPane.removeTabAt(index);
            tabbedPane.insertTab("Statistiques", createIcon("chart.png", 20), createStatistiquesPanel(), null, index);
            tabbedPane.setSelectedIndex(index);
        }
        
        updateStatus("Statistiques actualisées");
    }
    
    private void ajouterEmploye() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 5, 15));

        JTextField nom = new JTextField();
        JTextField prenom = new JTextField();
        JTextField cin = new JTextField();
        JTextField login = new JTextField();
        JPasswordField password = new JPasswordField();

        // Style des champs
        nom.putClientProperty("JTextField.placeholderText", "Entrez le nom");
        prenom.putClientProperty("JTextField.placeholderText", "Entrez le prénom");
        cin.putClientProperty("JTextField.placeholderText", "Exemple: AB123456");
        login.putClientProperty("JTextField.placeholderText", "Identifiant de connexion");
        password.putClientProperty("JTextField.placeholderText", "Mot de passe sécurisé");

        panel.add(createFieldLabel("Nom:"));
        panel.add(nom);
        panel.add(createFieldLabel("Prénom:"));
        panel.add(prenom);
        panel.add(createFieldLabel("CIN:"));
        panel.add(cin);
        panel.add(createFieldLabel("Login:"));
        panel.add(login);
        panel.add(createFieldLabel("Mot de passe:"));
        panel.add(password);

        JDialog dialog = new JDialog(this, "Ajouter un employé", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = createStyledButton("Enregistrer", SUCCESS_COLOR, "check.png");
        saveButton.addActionListener(e -> {
            if (nom.getText().trim().isEmpty() || prenom.getText().trim().isEmpty() || 
                cin.getText().trim().isEmpty() || login.getText().trim().isEmpty() || 
                password.getPassword().length == 0) {
                showErrorMessage("Tous les champs sont obligatoires");
                return;
            }
            
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quarta", "root", "")) {
                String sql = "INSERT INTO utilisateurs (nom, prenom, cin, login, password, type) VALUES (?, ?, ?, ?, ?, 'employe')";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, nom.getText());
                pst.setString(2, prenom.getText());
                pst.setString(3, cin.getText());
                pst.setString(4, login.getText());

                String hashedPassword = PasswordUtils.hashPassword(new String(password.getPassword()));
                pst.setString(5, hashedPassword);
                pst.executeUpdate();

                dialog.dispose();
                chargerEmployes();
                showSuccessMessage("Employé ajouté avec succès");
                updateStatus("Nouvel employé ajouté: " + prenom.getText() + " " + nom.getText());
            } catch (SQLException ex) {
                showErrorMessage("Erreur: " + ex.getMessage());
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void modifierEmploye() {
        int row = empTable.getSelectedRow();
        if (row == -1) {
            showWarningMessage("Veuillez sélectionner un employé à modifier");
            return;
        }

        int empId = (int) empTableModel.getValueAt(row, 0);
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 5, 15));

        JTextField nom = new JTextField((String) empTableModel.getValueAt(row, 1));
        JTextField prenom = new JTextField((String) empTableModel.getValueAt(row, 2));
        JTextField cin = new JTextField((String) empTableModel.getValueAt(row, 3));
        JTextField login = new JTextField((String) empTableModel.getValueAt(row, 4));
        JPasswordField password = new JPasswordField();
        password.putClientProperty("JTextField.placeholderText", "Laisser vide pour conserver l'ancien");

        panel.add(createFieldLabel("Nom:"));
        panel.add(nom);
        panel.add(createFieldLabel("Prénom:"));
        panel.add(prenom);
        panel.add(createFieldLabel("CIN:"));
        panel.add(cin);
        panel.add(createFieldLabel("Login:"));
        panel.add(login);
        panel.add(createFieldLabel("Mot de passe:"));
        panel.add(password);

        JDialog dialog = new JDialog(this, "Modifier employé - ID: " + empId, true);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = createStyledButton("Enregistrer", PRIMARY_COLOR, "check.png");
        saveButton.addActionListener(e -> {
            if (nom.getText().trim().isEmpty() || prenom.getText().trim().isEmpty() || 
                cin.getText().trim().isEmpty() || login.getText().trim().isEmpty()) {
                showErrorMessage("Les champs nom, prénom, CIN et login sont obligatoires");
                return;
            }
            
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quarta", "root", "")) {
                StringBuilder sql = new StringBuilder("UPDATE utilisateurs SET nom=?, prenom=?, cin=?, login=?");
                String hashedPassword = null;
                if (password.getPassword().length > 0) {
                    sql.append(", password=?");
                    hashedPassword = PasswordUtils.hashPassword(new String(password.getPassword()));
                }
                sql.append(" WHERE id=? AND type='employe'");

                PreparedStatement pst = conn.prepareStatement(sql.toString());
                pst.setString(1, nom.getText());
                pst.setString(2, prenom.getText());
                pst.setString(3, cin.getText());
                pst.setString(4, login.getText());

                if (hashedPassword != null) {
                    pst.setString(5, hashedPassword);
                    pst.setInt(6, empId);
                } else {
                    pst.setInt(5, empId);
                }

                pst.executeUpdate();
                dialog.dispose();
                chargerEmployes();
                showSuccessMessage("Employé modifié avec succès");
                updateStatus("Employé modifié: " + prenom.getText() + " " + nom.getText());
            } catch (SQLException ex) {
                showErrorMessage("Erreur: " + ex.getMessage());
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void supprimerEmploye() {
        int row = empTable.getSelectedRow();
        if (row == -1) {
            showWarningMessage("Veuillez sélectionner un employé à supprimer");
            return;
        }

        int empId = (int) empTableModel.getValueAt(row, 0);
        String nomComplet = empTableModel.getValueAt(row, 2) + " " + empTableModel.getValueAt(row, 1);
        
        // Dialogue de confirmation avec style personnalisé
        JPanel confirmPanel = new JPanel(new BorderLayout(10, 10));
        confirmPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        JLabel warningIcon = new JLabel(createIcon("warning.png", 32));
        
        JLabel confirmMessage = new JLabel("<html>Êtes-vous sûr de vouloir supprimer l'employé:<br/><b>" + 
                                         nomComplet + "</b> (ID: " + empId + ")?<br/><br/>" +
                                         "<font color='#C62828'>Cette action est irréversible.</font></html>");
        confirmMessage.setFont(REGULAR_FONT);
        
        confirmPanel.add(warningIcon, BorderLayout.WEST);
        confirmPanel.add(confirmMessage, BorderLayout.CENTER);
        
        JDialog confirmDialog = new JDialog(this, "Confirmation de suppression", true);
        confirmDialog.setLayout(new BorderLayout());
        confirmDialog.add(confirmPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Annuler");
        cancelButton.addActionListener(e -> confirmDialog.dispose());
        
        JButton deleteButton = createStyledButton("Supprimer", DANGER_COLOR, "trash.png");
        deleteButton.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quarta", "root", "")) {
                PreparedStatement pst = conn.prepareStatement("DELETE FROM utilisateurs WHERE id=? AND type='employe'");
                pst.setInt(1, empId);
                int result = pst.executeUpdate();
                
                confirmDialog.dispose();
                
                if (result > 0) {
                    chargerEmployes();
                    showSuccessMessage("Employé supprimé avec succès");
                    updateStatus("Employé supprimé: " + nomComplet);
                } else {
                    showWarningMessage("Aucun employé n'a été supprimé");
                }
            } catch (SQLException ex) {
                showErrorMessage("Erreur: " + ex.getMessage());
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(deleteButton);
        confirmDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        confirmDialog.pack();
        confirmDialog.setSize(400, 200);
        confirmDialog.setLocationRelativeTo(this);
        confirmDialog.setVisible(true);
    }

    private void chargerEmployes() {
        empTableModel.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/quarta", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nom, prenom, cin, login FROM utilisateurs WHERE type='employe'")) {

            while (rs.next()) {
                empTableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("cin"),
                    rs.getString("login")
                });
            }
            
            updateStatus("Liste des employés chargée: " + empTableModel.getRowCount() + " employés");
        } catch (SQLException e) {
            showErrorMessage("Erreur lors du chargement des employés: " + e.getMessage());
        }
    }
    
    // === Méthodes utilitaires pour l'interface utilisateur ===
    
    private JButton createStyledButton(String text, Color color, String iconName) {
        JButton btn = new JButton(text);
        if (iconName != null) {
            btn.setIcon(createIcon(iconName, 16));
        }
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(8, 15, 8, 15));
        
        // Effet au survol
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(
                    (int)(color.getRed() * 0.8),
                    (int)(color.getGreen() * 0.8),
                    (int)(color.getBlue() * 0.8)
                ));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });
        
        return btn;
    }
    
    private JButton createIconButton(String iconName, String toolTip) {
        JButton btn = new JButton(createIcon(iconName, 16));
        btn.setToolTipText(toolTip);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private Icon createIcon(String name, int size) {
        // Placeholder pour les icônes - dans une vraie application, vous chargeriez des icônes réelles
        return new ImageIcon();
        
        // Dans une vraie implémentation:
        // return new ImageIcon(getClass().getResource("/icons/" + name));
    }
    
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }
    
    private void showSuccessMessage(String message) {
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = optionPane.createDialog(this, "Succès");
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showErrorMessage(String message) {
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.ERROR_MESSAGE);
        JDialog dialog = optionPane.createDialog(this, "Erreur");
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showWarningMessage(String message) {
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.WARNING_MESSAGE);
        JDialog dialog = optionPane.createDialog(this, "Attention");
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void updateStatus(String message) {
        statusLabel.setText(" " + message);
        Timer timer = new Timer(5000, e -> statusLabel.setText(" Système prêt"));
        timer.setRepeats(false);
        timer.start();
    }
    
    // Classe utilitaire pour les rendus améliorés dans la table
    private class CustomTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
            
            if (!isSelected) {
                if (row % 2 == 0) {
                    c.setBackground(Color.WHITE);
                } else {
                    c.setBackground(new Color(245, 245, 250));
                }
            }
            
            setBorder(noFocusBorder);
            return c;
        }
    }
}
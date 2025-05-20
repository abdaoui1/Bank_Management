package secondary;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Styles {
    
    // Constantes pour les couleurs
    public static final Color PRIMARY_COLOR = new Color(25, 118, 210);
    public static final Color SECONDARY_COLOR = new Color(66, 66, 66);
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    public static final Color SUCCESS_COLOR = new Color(46, 125, 50);
    public static final Color WARNING_COLOR = new Color(237, 108, 2);
    public static final Color DANGER_COLOR = new Color(211, 47, 47);
    public static final Color PINK_COLOR = new Color(90, 30, 190);
    public static final Color LIGHT_GRIS = new Color(240, 240, 240);
    
    
    // Polices
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public static JButton createStyledButton(String text, Color color, String iconName) {
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
    
    public static Icon createIcon(String name, int size) {
        String imagePath = "src/imgs/" + name;
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image scaledImage = originalIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
}
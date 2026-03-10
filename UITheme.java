package essay;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class UITheme {

    static final Color HEADER_BG = new Color(27, 79, 114);
    static final Color HEADER_TEXT = new Color(236, 240, 241);
    static final Color BODY_BG = new Color(236, 240, 241);
    static final Color CARD_BG = Color.WHITE;
    static final Color ACCENT = new Color(41, 128, 185);
    static final Color ACCENT_HOVER = new Color(52, 152, 219);
    static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    static final Color BORDER_COLOR = new Color(189, 195, 199);
    static final Color TOOLBAR_BG = new Color(214, 219, 223);
    static final Color SCORE_COLOR = new Color(39, 174, 96);

    static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 18);
    static final Font BRAND_FONT = new Font("SansSerif", Font.PLAIN, 12);
    static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 26);
    static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 14);
    static final Font MONO_FONT = new Font("Monospaced", Font.PLAIN, 13);
    static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 13);
    static final Font ESSAY_FONT = new Font("Serif", Font.PLAIN, 16);
    static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 11);
    static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 13);
    static final Font SCORE_FONT = new Font("SansSerif", Font.BOLD, 36);
    static final Font SECTION_FONT = new Font("SansSerif", Font.BOLD, 14);

    static JPanel createHeaderPanel(String title) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(HEADER_TEXT);
        header.add(titleLabel, BorderLayout.WEST);

        JLabel brandLabel = new JLabel("Automated Essay Grading");
        brandLabel.setFont(BRAND_FONT);
        brandLabel.setForeground(new Color(149, 165, 166));
        header.add(brandLabel, BorderLayout.EAST);

        return header;
    }

    static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(BUTTON_FONT);
        btn.setForeground(Color.WHITE);
        btn.setBackground(ACCENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT);
            }
        });
        return btn;
    }

    static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(BUTTON_FONT);
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(TOOLBAR_BG);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        return btn;
    }

    static JScrollPane createStyledScrollPane(JTextArea textArea, boolean monospaced) {
        textArea.setFont(monospaced ? MONO_FONT : ESSAY_FONT);
        textArea.setForeground(TEXT_PRIMARY);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setMargin(new Insets(14, 14, 14, 14));
        textArea.setBackground(CARD_BG);

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scroll.getViewport().setBackground(CARD_BG);
        return scroll;
    }

    static JPanel createFooterPanel(JButton... buttons) {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        footer.setBackground(BODY_BG);
        footer.setBorder(BorderFactory.createEmptyBorder(14, 24, 18, 24));
        for (JButton btn : buttons) {
            footer.add(btn);
        }
        return footer;
    }

    static void setupFrame(JFrame frame, String title, int width, int height) {
        frame.setTitle(title);
        frame.setSize(width, height);
        frame.setMinimumSize(new Dimension(700, 500));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    static JPanel wrapInCard(Component content) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        card.add(content, BorderLayout.CENTER);
        return card;
    }
}

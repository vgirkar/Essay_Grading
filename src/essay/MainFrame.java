package essay;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import javax.swing.*;

public class MainFrame extends javax.swing.JFrame {

    Details dt = new Details();
    private JTextArea textArea;

    public MainFrame() {
        UITheme.setupFrame(this, "Automated Essay Grading", 900, 650);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BODY_BG);

        root.add(UITheme.createHeaderPanel("Training Module"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setBackground(UITheme.BODY_BG);
        body.setBorder(BorderFactory.createEmptyBorder(20, 24, 0, 24));

        JPanel topSection = new JPanel(new BorderLayout(0, 10));
        topSection.setBackground(UITheme.BODY_BG);

        JLabel stepLabel = new JLabel("Step 1 \u2014 Load & Review Training Essays");
        stepLabel.setFont(UITheme.SECTION_FONT);
        stepLabel.setForeground(UITheme.TEXT_PRIMARY);
        topSection.add(stepLabel, BorderLayout.NORTH);

        JLabel infoLabel = new JLabel(
            "<html>Load the training essay dataset to begin the grading pipeline. "
            + "The system will preprocess, extract features, and build a prediction model.</html>"
        );
        infoLabel.setFont(UITheme.LABEL_FONT);
        infoLabel.setForeground(UITheme.TEXT_SECONDARY);
        topSection.add(infoLabel, BorderLayout.SOUTH);

        body.add(topSection, BorderLayout.NORTH);

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scroll = UITheme.createStyledScrollPane(textArea, true);
        body.add(scroll, BorderLayout.CENTER);

        root.add(body, BorderLayout.CENTER);

        JButton loadBtn = UITheme.createSecondaryButton("Load Training Essay Set");
        JButton preprocessBtn = UITheme.createPrimaryButton("Preprocess \u25B6");

        loadBtn.addActionListener(e -> loadTrainingSet());
        preprocessBtn.addActionListener(e -> preprocess());

        root.add(UITheme.createFooterPanel(loadBtn, preprocessBtn), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void loadTrainingSet() {
        try {
            File fe = new File(dt.trainPath);
            FileInputStream fis = new FileInputStream(fe);
            byte bt[] = new byte[fis.available()];
            fis.read(bt);
            fis.close();

            String g1 = new String(bt);
            String g2[] = g1.split("\n");

            dt.org = new String[g2.length - 1][4];
            for (int i = 1; i < g2.length; i++) {
                String t1[] = g2[i].trim().split("\t");
                for (int j = 0; j < t1.length; j++)
                    dt.org[i - 1][j] = t1[j];
            }

            textArea.setText(g1);
            textArea.setCaretPosition(0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading training essays: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void preprocess() {
        try {
            String txt = textArea.getText().trim();
            if (txt.equals("")) {
                JOptionPane.showMessageDialog(this, "Please load the training essay set first.",
                    "No Data", JOptionPane.WARNING_MESSAGE);
            } else {
                dt.preEss = new String[dt.org.length];
                dt.senEss = new java.util.ArrayList[dt.org.length];
                dt.wordEss = new java.util.ArrayList[dt.org.length];
                dt.CWScnt = new double[dt.org.length][3];

                Preprocess pr = new Preprocess();
                String re = pr.process();

                ProcessJFrame pf = new ProcessJFrame();
                pf.setVisible(true);
                pf.jTextArea1.setText(re);
                pf.jTextArea1.setCaretPosition(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

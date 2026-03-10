package essay;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.*;

public class ProcessJFrame extends javax.swing.JFrame {

    Details dt = new Details();
    public JTextArea jTextArea1;

    public ProcessJFrame() {
        UITheme.setupFrame(this, "Preprocess", 900, 620);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BODY_BG);

        root.add(UITheme.createHeaderPanel("Step 2 \u2014 Preprocessed Essays"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(UITheme.BODY_BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 24, 0, 24));

        JLabel info = new JLabel(
            "<html>Below are the preprocessed training essays after stop-word removal and POS tagging. "
            + "Proceed to extract TF-IDF features.</html>"
        );
        info.setFont(UITheme.LABEL_FONT);
        info.setForeground(UITheme.TEXT_SECONDARY);
        body.add(info, BorderLayout.NORTH);

        jTextArea1 = new JTextArea();
        jTextArea1.setEditable(false);
        body.add(UITheme.createStyledScrollPane(jTextArea1, true), BorderLayout.CENTER);

        root.add(body, BorderLayout.CENTER);

        JButton feBtn = UITheme.createPrimaryButton("Feature Extraction \u25B6");
        feBtn.addActionListener(e -> extractFeatures());
        root.add(UITheme.createFooterPanel(feBtn), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void extractFeatures() {
        try {
            FeatureExtraction fe = new FeatureExtraction();
            dt.featLt = fe.extractTerm();
            dt.tfidf1 = fe.findtfidf();

            String res = "Chara\tWord\tSentence";
            DecimalFormat df = new DecimalFormat("#.####");

            for (int i = 0; i < 10; i++)
                res = res + "\t" + dt.featLt.get(i);

            for (int i = 0; i < dt.preEss.length; i++) {
                res = res + "\n" + dt.CWScnt[i][0] + "\t" + dt.CWScnt[i][1] + "\t" + dt.CWScnt[i][2];
                for (int j = 0; j < 10; j++)
                    res = res + "\t" + df.format(dt.tfidf1[i][j]);
            }

            FeatureFrame ff = new FeatureFrame();
            ff.setVisible(true);
            ff.jTextArea1.setText(res);
            ff.jTextArea1.setCaretPosition(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package essay;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.*;
import Jama.Matrix;

public class FeatureFrame extends javax.swing.JFrame {

    Details dt = new Details();
    public JTextArea jTextArea1;

    public FeatureFrame() {
        UITheme.setupFrame(this, "Features", 900, 620);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BODY_BG);

        root.add(UITheme.createHeaderPanel("Step 3 \u2014 TF-IDF Feature Matrix"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(UITheme.BODY_BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 24, 0, 24));

        JLabel info = new JLabel(
            "<html>The extracted TF-IDF features for each essay are displayed below. "
            + "Proceed with LSA to reduce dimensionality.</html>"
        );
        info.setFont(UITheme.LABEL_FONT);
        info.setForeground(UITheme.TEXT_SECONDARY);
        body.add(info, BorderLayout.NORTH);

        jTextArea1 = new JTextArea();
        jTextArea1.setEditable(false);
        body.add(UITheme.createStyledScrollPane(jTextArea1, true), BorderLayout.CENTER);

        root.add(body, BorderLayout.CENTER);

        JButton lsaBtn = UITheme.createPrimaryButton("Latent Semantic Analysis \u25B6");
        lsaBtn.addActionListener(e -> performLSA());
        root.add(UITheme.createFooterPanel(lsaBtn), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void performLSA() {
        try {
            Matrix mt = new Matrix(dt.tfidf1);
            double term[][] = mt.transpose().getArray();
            LSA lsa = new LSA();
            double neww[][] = lsa.dimreduction(term);

            dt.selFeat = lsa.lt;

            String res = "Total Features = " + dt.tfidf1[0].length;
            res = res + "\nReduced Features = " + neww[0].length;

            DecimalFormat df = new DecimalFormat("#.###");

            String sg = "ch1,wd1,sen1";
            for (int i = 0; i < neww[0].length; i++)
                sg = sg + ",att_" + i;
            sg = sg + ",cls";

            for (int i = 0; i < dt.preEss.length; i++) {
                res = res + "\n" + dt.CWScnt[i][0] + "\t" + dt.CWScnt[i][1] + "\t" + dt.CWScnt[i][2];
                sg = sg + "\n" + dt.CWScnt[i][0] + "," + dt.CWScnt[i][1] + "," + dt.CWScnt[i][2];
                for (int j = 0; j < neww[0].length; j++) {
                    res = res + "\t" + df.format(neww[i][j]);
                    sg = sg + "," + df.format(neww[i][j]);
                }
                sg = sg + "," + dt.org[i][3];
            }

            File fe = new File("data1.csv");
            FileOutputStream fos = new FileOutputStream(fe);
            fos.write(sg.trim().getBytes());
            fos.close();

            LSAFrame lf = new LSAFrame();
            lf.setVisible(true);
            lf.jTextArea1.setText(res);
            lf.jTextArea1.setCaretPosition(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

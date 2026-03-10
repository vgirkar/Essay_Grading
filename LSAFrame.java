package essay;

import java.awt.*;
import java.io.File;
import java.util.Random;
import javax.swing.*;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.classifiers.functions.SMOreg;
import weka.classifiers.Evaluation;

public class LSAFrame extends javax.swing.JFrame {

    public JTextArea jTextArea1;

    public LSAFrame() {
        UITheme.setupFrame(this, "Feature Reduction", 900, 620);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BODY_BG);

        root.add(UITheme.createHeaderPanel("Step 4 \u2014 LSA Dimensionality Reduction"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(UITheme.BODY_BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 24, 0, 24));

        JLabel info = new JLabel(
            "<html>Features have been reduced using Latent Semantic Analysis. "
            + "Click <b>Build Model &amp; Predict</b> to train the SVR classifier and evaluate performance.</html>"
        );
        info.setFont(UITheme.LABEL_FONT);
        info.setForeground(UITheme.TEXT_SECONDARY);
        body.add(info, BorderLayout.NORTH);

        jTextArea1 = new JTextArea();
        jTextArea1.setEditable(false);
        body.add(UITheme.createStyledScrollPane(jTextArea1, true), BorderLayout.CENTER);

        root.add(body, BorderLayout.CENTER);

        JButton predictBtn = UITheme.createPrimaryButton("Build Model & Predict \u25B6");
        predictBtn.addActionListener(e -> predict());
        root.add(UITheme.createFooterPanel(predictBtn), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void predict() {
        try {
            CSVLoader csv = new CSVLoader();
            csv.setSource(new File("data1.csv"));

            Instances ins = csv.getDataSet();
            ins.setClassIndex(ins.numAttributes() - 1);
            SMOreg svr = new SMOreg();
            svr.buildClassifier(ins);

            String res = "";
            int cr = 0;
            for (int i = 0; i < ins.numInstances(); i++) {
                double x1[] = ins.instance(i).toDoubleArray();
                double pt = svr.classifyInstance(ins.instance(i));
                res = res + pt + "\n";
                if (Math.ceil(pt) == Math.ceil(x1[x1.length - 1]))
                    cr++;
            }

            Evaluation ev = new Evaluation(ins);
            ev.crossValidateModel(svr, ins, 10, new Random(1));

            res = res + "\n" + ev.toSummaryString();

            ResultFrame rf = new ResultFrame();
            rf.setVisible(true);
            rf.jTextArea1.setText(res);
            rf.jTextArea1.setCaretPosition(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

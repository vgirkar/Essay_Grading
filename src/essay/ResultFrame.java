package essay;

import java.awt.*;
import javax.swing.*;

public class ResultFrame extends javax.swing.JFrame {

    public JTextArea jTextArea1;

    public ResultFrame() {
        UITheme.setupFrame(this, "Prediction Results", 900, 620);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BODY_BG);

        root.add(UITheme.createHeaderPanel("Step 5 \u2014 Model Evaluation & Results"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(UITheme.BODY_BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 24, 0, 24));

        JLabel info = new JLabel(
            "<html>Cross-validation results and predicted scores for the training set are shown below. "
            + "You can now test the model on a new essay.</html>"
        );
        info.setFont(UITheme.LABEL_FONT);
        info.setForeground(UITheme.TEXT_SECONDARY);
        body.add(info, BorderLayout.NORTH);

        jTextArea1 = new JTextArea();
        jTextArea1.setEditable(false);
        body.add(UITheme.createStyledScrollPane(jTextArea1, true), BorderLayout.CENTER);

        root.add(body, BorderLayout.CENTER);

        JButton testBtn = UITheme.createPrimaryButton("Test New Essay \u25B6");
        testBtn.addActionListener(e -> {
            TestFrame tf = new TestFrame();
            tf.setVisible(true);
        });
        root.add(UITheme.createFooterPanel(testBtn), BorderLayout.SOUTH);

        setContentPane(root);
    }
}

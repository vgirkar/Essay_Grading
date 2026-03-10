package essay;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import weka.classifiers.functions.SMOreg;
import weka.core.Instances;
import weka.core.Instance;
import weka.core.DenseInstance;
import weka.core.converters.CSVLoader;
import java.text.DecimalFormat;

public class TestFrame extends javax.swing.JFrame {

    Details dt = new Details();
    private JTextArea essayArea;
    private JLabel wordCountLabel;
    private JLabel charCountLabel;
    private JLabel scoreLabel;
    private JButton gradeBtn;

    public TestFrame() {
        UITheme.setupFrame(this, "Essay Assessment", 960, 700);
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BODY_BG);

        root.add(UITheme.createHeaderPanel("Analytical Writing Assessment"), BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 0));
        body.setBackground(UITheme.BODY_BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 24, 0, 24));

        JPanel promptPanel = new JPanel(new BorderLayout());
        promptPanel.setBackground(UITheme.CARD_BG);
        promptPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        JLabel promptTitle = new JLabel("Directions");
        promptTitle.setFont(UITheme.SECTION_FONT);
        promptTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel promptText = new JLabel(
            "<html>Write your essay in the area below, or load an essay from a text file using the toolbar. "
            + "When you are finished, click <b>Grade Essay</b> to receive your predicted score.</html>"
        );
        promptText.setFont(UITheme.LABEL_FONT);
        promptText.setForeground(UITheme.TEXT_SECONDARY);
        promptText.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        promptPanel.add(promptTitle, BorderLayout.NORTH);
        promptPanel.add(promptText, BorderLayout.CENTER);
        body.add(promptPanel, BorderLayout.NORTH);

        JPanel editorSection = new JPanel(new BorderLayout(0, 0));
        editorSection.setBackground(UITheme.BODY_BG);
        editorSection.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(UITheme.TOOLBAR_BG);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 0, 1, UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));

        JPanel toolbarLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        toolbarLeft.setOpaque(false);

        JButton cutBtn = makeToolbarBtn("Cut");
        JButton copyBtn = makeToolbarBtn("Copy");
        JButton pasteBtn = makeToolbarBtn("Paste");
        JButton undoBtn = makeToolbarBtn("Undo");
        JButton loadBtn = makeToolbarBtn("Open File...");

        cutBtn.addActionListener(e -> essayArea.cut());
        copyBtn.addActionListener(e -> essayArea.copy());
        pasteBtn.addActionListener(e -> essayArea.paste());
        undoBtn.addActionListener(e -> essayArea.setText(""));
        loadBtn.addActionListener(e -> loadEssayFromFile());

        toolbarLeft.add(cutBtn);
        toolbarLeft.add(copyBtn);
        toolbarLeft.add(pasteBtn);
        toolbarLeft.add(makeSeparator());
        toolbarLeft.add(undoBtn);
        toolbarLeft.add(makeSeparator());
        toolbarLeft.add(loadBtn);

        toolbar.add(toolbarLeft, BorderLayout.WEST);
        editorSection.add(toolbar, BorderLayout.NORTH);

        essayArea = new JTextArea();
        essayArea.setEditable(true);
        essayArea.setFont(UITheme.ESSAY_FONT);
        essayArea.setForeground(UITheme.TEXT_PRIMARY);
        essayArea.setLineWrap(true);
        essayArea.setWrapStyleWord(true);
        essayArea.setMargin(new Insets(16, 18, 16, 18));
        essayArea.setBackground(UITheme.CARD_BG);

        essayArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateCounts(); }
            public void removeUpdate(DocumentEvent e) { updateCounts(); }
            public void changedUpdate(DocumentEvent e) { updateCounts(); }
        });

        JScrollPane scroll = new JScrollPane(essayArea);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_COLOR));
        scroll.getViewport().setBackground(UITheme.CARD_BG);
        editorSection.add(scroll, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(UITheme.TOOLBAR_BG);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 1, 1, 1, UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(5, 14, 5, 14)
        ));

        wordCountLabel = new JLabel("Words: 0");
        wordCountLabel.setFont(UITheme.SMALL_FONT);
        wordCountLabel.setForeground(UITheme.TEXT_SECONDARY);

        charCountLabel = new JLabel("Characters: 0");
        charCountLabel.setFont(UITheme.SMALL_FONT);
        charCountLabel.setForeground(UITheme.TEXT_SECONDARY);

        JPanel countsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        countsPanel.setOpaque(false);
        countsPanel.add(wordCountLabel);
        countsPanel.add(charCountLabel);
        statusBar.add(countsPanel, BorderLayout.WEST);

        editorSection.add(statusBar, BorderLayout.SOUTH);
        body.add(editorSection, BorderLayout.CENTER);

        root.add(body, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(UITheme.BODY_BG);
        footer.setBorder(BorderFactory.createEmptyBorder(12, 24, 18, 24));

        scoreLabel = new JLabel(" ");
        scoreLabel.setFont(UITheme.SCORE_FONT);
        scoreLabel.setForeground(UITheme.SCORE_COLOR);
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel scorePanel = new JPanel(new BorderLayout());
        scorePanel.setOpaque(false);
        scorePanel.add(scoreLabel, BorderLayout.CENTER);

        gradeBtn = UITheme.createPrimaryButton("Grade Essay");
        gradeBtn.addActionListener(e -> gradeEssay());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(gradeBtn);

        footer.add(scorePanel, BorderLayout.CENTER);
        footer.add(btnPanel, BorderLayout.EAST);

        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);

        SwingUtilities.invokeLater(() -> essayArea.requestFocusInWindow());
    }

    private JButton makeToolbarBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.SMALL_FONT);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(3, 8, 3, 8));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JSeparator makeSeparator() {
        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setPreferredSize(new Dimension(2, 20));
        return sep;
    }

    private void updateCounts() {
        String text = essayArea.getText().trim();
        int chars = text.length();
        int words = text.isEmpty() ? 0 : text.split("\\s+").length;
        wordCountLabel.setText("Words: " + words);
        charCountLabel.setText("Characters: " + chars);
    }

    private void loadEssayFromFile() {
        try {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File("."));
            fc.setFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    return f.getName().toLowerCase().endsWith(".txt") || f.isDirectory();
                }
                public String getDescription() {
                    return "Text Files (*.txt)";
                }
            });
            int ch = fc.showOpenDialog(this);
            if (ch == JFileChooser.APPROVE_OPTION) {
                String path = fc.getSelectedFile().getAbsolutePath();
                FileInputStream fis = new FileInputStream(new File(path));
                byte bt[] = new byte[fis.available()];
                fis.read(bt);
                fis.close();
                essayArea.setText(new String(bt));
                essayArea.setCaretPosition(0);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void gradeEssay() {
        try {
            String txt = essayArea.getText().trim();
            if (txt.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please write or load an essay before grading.",
                    "No Essay", JOptionPane.WARNING_MESSAGE);
                return;
            }

            scoreLabel.setText("Grading...");
            scoreLabel.setForeground(UITheme.TEXT_SECONDARY);
            gradeBtn.setEnabled(false);

            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
                protected String doInBackground() throws Exception {
                    dt.testPre = "";
                    dt.testSen = new ArrayList();
                    dt.testWord = new ArrayList();
                    dt.testCW = new double[3];

                    String g1 = process(txt);

                    double term1[] = new double[dt.selFeat.size()];
                    for (int j = 0; j < dt.selFeat.size(); j++) {
                        String f1 = dt.selFeat.get(j).toString();
                        double tm = findTermFre(f1, g1);
                        term1[j] = tm;
                    }

                    double in1[] = findInverse(dt.selFeat);
                    double tfidf1[] = new double[term1.length];
                    for (int i = 0; i < term1.length; i++) {
                        tfidf1[i] = term1[i] * in1[i];
                    }

                    CSVLoader csv = new CSVLoader();
                    csv.setSource(new File("data1.csv"));
                    Instances ins = csv.getDataSet();
                    ins.setClassIndex(ins.numAttributes() - 1);

                    SMOreg svr = new SMOreg();
                    svr.buildClassifier(ins);

                    Instance ins1 = new DenseInstance(ins.numAttributes());
                    for (int i = 0; i < dt.testCW.length; i++)
                        ins1.setValue(i, dt.testCW[i]);
                    for (int i = 0; i < tfidf1.length; i++)
                        ins1.setValue(i + 4, tfidf1[i]);
                    ins1.setDataset(ins);

                    double r1 = svr.classifyInstance(ins1);
                    DecimalFormat df = new DecimalFormat("#.#");
                    return df.format(r1);
                }

                protected void done() {
                    try {
                        String result = get();
                        scoreLabel.setText("Score: " + result + " / 6.0");
                        scoreLabel.setForeground(UITheme.SCORE_COLOR);
                    } catch (Exception ex) {
                        scoreLabel.setText("Error during grading");
                        scoreLabel.setForeground(Color.RED);
                        ex.printStackTrace();
                    }
                    gradeBtn.setEnabled(true);
                }
            };
            worker.execute();
        } catch (Exception e) {
            gradeBtn.setEnabled(true);
            e.printStackTrace();
        }
    }

    public String process(String g1) {
        String res = "";
        try {
            String reg = "(?<=\\w[\\w\\)\\]][\\.\\?\\!]\\s)";
            ArrayList<String> stop = read_stopwd();
            String taggerPath = "models" + File.separator + "left3words-wsj-0-18.tagger";
            MaxentTagger ob = new MaxentTagger(taggerPath);
            String sen[] = g1.split(reg);
            ArrayList at1 = new ArrayList();

            for (int j = 0; j < sen.length; j++) {
                String g2 = sen[j].replaceAll("[^a-zA-Z]", " ");
                at1.add(g2);
            }
            dt.testSen = at1;

            String wd[] = g1.split("\\s+");
            ArrayList at2 = new ArrayList();
            int ch = 0;
            for (int j = 0; j < wd.length; j++) {
                String g2 = wd[j].replaceAll("[^a-zA-Z]", " ").trim();
                ch = ch + g2.length();
                at2.add(g2);
            }
            dt.testWord = at2;

            String g2 = g1.replaceAll("[^a-zA-Z]", " ");
            String s2[] = g2.trim().toLowerCase().split(" ");
            String sm = "";
            for (int j = 0; j < s2.length; j++) {
                if (!stop.contains(s2[j].trim())) {
                    if (!s2[j].trim().equals("")) {
                        String ret = ob.tagString(s2[j]).trim();
                        if (ret.endsWith("/NN") || ret.endsWith("/NNS") || ret.endsWith("/NNP")) {
                            if (s2[j].trim().length() > 2)
                                sm = sm + s2[j].trim() + " ";
                        }
                    }
                }
            }
            dt.testPre = sm;
            dt.testCW[0] = ch;
            dt.testCW[1] = at2.size();
            dt.testCW[2] = at1.size();
            res = sm;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    static ArrayList<String> read_stopwd() throws Exception {
        ArrayList<String> st = new ArrayList<String>();
        Scanner scan1 = new Scanner(new BufferedReader(new FileReader("stopwords1.txt")));
        while (scan1.hasNext())
            st.add(scan1.next());
        return st;
    }

    public double findTermFre(String tm, String cn) {
        double tr = 0;
        try {
            double cc = 0;
            String g1[] = cn.split(" ");
            for (int i = 0; i < g1.length; i++) {
                if (g1[i].equals(tm))
                    cc++;
            }
            tr = cc / (double) g1.length;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tr;
    }

    public double[] findInverse(ArrayList ft) {
        double de[] = new double[ft.size()];
        try {
            for (int i = 0; i < ft.size(); i++) {
                String g1 = ft.get(i).toString();
                double cn1 = 0;
                if (dt.testPre.contains(g1))
                    cn1 = 1;
                if (cn1 == 0) {
                    de[i] = 1;
                } else {
                    double in1 = Math.log10(1.0);
                    de[i] = in1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return de;
    }
}

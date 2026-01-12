import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import java.util.*;
import java.awt.font.*;
import javax.swing.event.*;

public class Editor2015 extends JFrame implements ActionListener, KeyListener {
    JFrame jf;
    JMenuBar mb;
    JMenu mFile, mEdit, mFormat, mRun, mHelp;
    JMenuItem mFileMenuItem[] = new JMenuItem[6];
    String StrFileMenuItem[] = {"New", "Open", "Save", "Save As...", "Print..", "Exit"};
    JMenuItem mEditMenuItem[] = new JMenuItem[6];
    String StrEditMenuItem[] = {"Cut", "Copy", "Paste", "Delete", "Select All", "Time & Date"};
    JMenuItem mFormatMenuItem[] = new JMenuItem[1];
    String strFormatMenuItem[] = {"Color"};
    JMenuItem mRunMenuItem[] = new JMenuItem[2];
    String strRunMenuItem[] = {"Compile", "Run.."};
    JTextArea ta, ta1;
    JScrollPane sp, sp1;
    FileDialog fd;
    File currentFile;
    boolean textChanged;
    JColorChooser cc;

    public Editor2015() {
        jf = new JFrame();
        jf.setLayout(null);
        mb = new JMenuBar();
        jf.setJMenuBar(mb);

        // Menus
        mFile = new JMenu("File"); mb.add(mFile);
        mEdit = new JMenu("Edit"); mb.add(mEdit);
        mFormat = new JMenu("Format"); mb.add(mFormat);
        mRun = new JMenu("Run"); mb.add(mRun);
        mHelp = new JMenu("Help"); mb.add(mHelp);

        JMenuItem mHelpMenuItem = new JMenuItem("About Editor");
        mHelp.add(mHelpMenuItem);
        mHelpMenuItem.addActionListener(this);

        // Menu Items
        for (int x = 0; x < 6; x++) {
            mFileMenuItem[x] = new JMenuItem(StrFileMenuItem[x]);
            if (x == 4 || x == 5) mFile.addSeparator();
            mFile.add(mFileMenuItem[x]);
            mFileMenuItem[x].addActionListener(this);
        }
        for (int x = 0; x < 6; x++) {
            mEditMenuItem[x] = new JMenuItem(StrEditMenuItem[x]);
            if (x == 4) mEdit.addSeparator();
            mEdit.add(mEditMenuItem[x]);
            mEditMenuItem[x].addActionListener(this);
        }
        for (int x = 0; x < 1; x++) {
            mFormatMenuItem[x] = new JMenuItem(strFormatMenuItem[x]);
            mFormat.add(mFormatMenuItem[x]);
            mFormatMenuItem[x].addActionListener(this);
        }
        for (int x = 0; x < 2; x++) {
            mRunMenuItem[x] = new JMenuItem(strRunMenuItem[x]);
            mRun.add(mRunMenuItem[x]);
            mRunMenuItem[x].addActionListener(this);
        }

        // TextAreas
        ta = new JTextArea(50, 50);
        ta1 = new JTextArea(50, 50);
        ta.setFont(new Font("Courier New", Font.PLAIN, 20));
        ta1.setFont(new Font("Courier New", Font.PLAIN, 16));
        ta1.setEditable(false);

        sp = new JScrollPane(ta);
        sp1 = new JScrollPane(ta1);
        sp.setBounds(0, 0, 1350, 520);
        sp1.setBounds(0, 530, 1350, 135);
        jf.add(sp);
        jf.add(sp1);

        ta.addKeyListener(this);
        textChanged = false;

        jf.setTitle("Editor2015: Untitled");
        jf.setDefaultCloseOperation(EXIT_ON_CLOSE);
        jf.setSize(1500, 740);
        jf.setVisible(true);
    }

    public void keyPressed(KeyEvent e) {
        if (e.getSource() == ta) textChanged = true;
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public void actionPerformed(ActionEvent ae) {
        try {
            String cmd = ae.getActionCommand();

            if (cmd.equals("New")) newFile();
            else if (cmd.equals("Open")) openFile();
            else if (cmd.equals("Save")) saveFile();
            else if (cmd.equals("Save As...")) saveFileAs();
            else if (cmd.equals("Exit")) exitEditor();
            else if (cmd.equals("Cut")) ta.cut();
            else if (cmd.equals("Copy")) ta.copy();
            else if (cmd.equals("Paste")) ta.paste();
            else if (cmd.equals("Select All")) ta.selectAll();
            else if (cmd.equals("Delete")) ta.replaceSelection("");
            else if (cmd.equals("Time & Date")) ta.insert("" + new Date(), ta.getSelectionStart());
            else if (cmd.equals("Color")) changeColor();
            else if (cmd.equals("Compile")) compileJava();
            else if (cmd.equals("Run..")) runJava();
            else if (cmd.equals("About Editor")) aboutEditor();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }

    // ------------------- File Operations -------------------
    private void newFile() {
        if (textChanged) {
            int ask = JOptionPane.showConfirmDialog(this, "Text has been changed. Save it?", "Changes Save", JOptionPane.YES_NO_CANCEL_OPTION);
            if (ask == JOptionPane.YES_OPTION) saveFile();
            else if (ask == JOptionPane.CANCEL_OPTION) return;
        }
        String className = JOptionPane.showInputDialog(this, "Enter class name:");
        if (className == null || className.trim().isEmpty()) className = "MyClass";

        ta.setText("public class " + className + " {\n" +
                   "    public static void main(String[] args) {\n" +
                   "        \n" +
                   "    }\n}");
        currentFile = null;
        jf.setTitle("Editor2015: Untitled");
        textChanged = false;
    }

    private void openFile() throws Exception {
        fd = new FileDialog(this, "Open a File", FileDialog.LOAD);
        fd.setVisible(true);
        if (fd.getFile() == null) return;

        currentFile = new File(fd.getDirectory() + fd.getFile());
        BufferedReader br = new BufferedReader(new FileReader(currentFile));
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) sb.append(line).append("\n");
        br.close();
        ta.setText(sb.toString());
        jf.setTitle("Editor2015: " + fd.getFile());
        textChanged = false;
    }

    private void saveFile() {
        try {
            if (currentFile == null) {
                saveFileAs();
                return;
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(currentFile));
            bw.write(ta.getText());
            bw.close();
            textChanged = false;
            ta1.setText("File saved successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Cannot save file: " + e);
        }
    }

    private void saveFileAs() {
        try {
            fd = new FileDialog(this, "Save As", FileDialog.SAVE);
            fd.setVisible(true);
            if (fd.getFile() == null) return;
            currentFile = new File(fd.getDirectory() + fd.getFile());
            saveFile();
            jf.setTitle("Editor2015: " + fd.getFile());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Cannot save file: " + e);
        }
    }

    private void exitEditor() {
        if (textChanged) {
            int ask = JOptionPane.showConfirmDialog(this, "Text has been changed. Save it?", "Changes Save", JOptionPane.YES_NO_CANCEL_OPTION);
            if (ask == JOptionPane.YES_OPTION) saveFile();
            else if (ask == JOptionPane.CANCEL_OPTION) return;
        }
        System.exit(0);
    }

    private void changeColor() {
        cc = new JColorChooser();
        Color newColor = cc.showDialog(this, "Select Color", Color.black);
        if (newColor != null) ta.setForeground(newColor);
    }

    private void aboutEditor() {
        JOptionPane.showMessageDialog(this, "Mini Java IDE Editor2015\nCreated by Mohd Usaid.");
    }

    // ------------------- Compile & Run -------------------
    private void compileJava() {
        try {
            if (currentFile == null) {
                ta1.setText("Please save your file before compiling.");
                return;
            }
            saveFile(); // save before compile

            String filePath = currentFile.getAbsolutePath();
            ProcessBuilder pb = new ProcessBuilder("javac", filePath);
            pb.redirectErrorStream(true);
            Process p = pb.start();

            BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = output.readLine()) != null) sb.append(line).append("\n");
            output.close();

            if (sb.toString().isEmpty()) ta1.setText("Compilation successful.");
            else ta1.setText(sb.toString());
        } catch (Exception e) {
            ta1.setText("Compilation error: " + e);
        }
    }

    private void runJava() {
        try {
            if (currentFile == null) {
                ta1.setText("Please save and compile your file first.");
                return;
            }
            String fileName = currentFile.getName();
            if (!fileName.endsWith(".java")) {
                ta1.setText("Invalid Java file.");
                return;
            }
            String className = fileName.substring(0, fileName.lastIndexOf('.'));

            // Run using the same directory
            ProcessBuilder pb = new ProcessBuilder("java", className);
            pb.directory(currentFile.getParentFile());
            pb.redirectErrorStream(true);
            Process p = pb.start();

            BufferedReader output = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = output.readLine()) != null) sb.append(line).append("\n");
            output.close();

            ta1.setText(sb.toString());
        } catch (Exception e) {
            ta1.setText("Runtime error: " + e);
        }
    }

    public static void main(String[] args) {
        new Editor2015();
    }
}

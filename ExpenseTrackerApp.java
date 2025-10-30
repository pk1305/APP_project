// ExpenseTrackerApp.java
// Single-file Java Swing Expense Tracker (no SQL). Persisted locally via serialization.
// Compile: javac ExpenseTrackerApp.java
// Run:     java ExpenseTrackerApp

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

/**
 * ExpenseTrackerApp
 * - All Swing (no DB). Stores expenses locally in expenses.dat using Java serialization.
 * - Overspend rule: total > 90% of salary => "Overspent", else "Savings Made".
 */
public class ExpenseTrackerApp extends JFrame {
    // UI components
    private JTextField monthField, salaryField;
    private JTextField electricityField, waterField, gasField;
    private JTextField foodField, travelField, shoppingField, miscField;
    private JLabel messageLabel;
    private ExpenseManager manager;

    public ExpenseTrackerApp() {
        super("💰 Smart Expense Tracker (Swing-only)");
        manager = new ExpenseManager(); // loads existing data if present

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(880, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top: Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(20, 110, 180));
        header.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel title = new JLabel("💰 Smart Expense Tracker");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JLabel subtitle = new JLabel("Plan • Track • Save");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(220, 235, 255));
        header.add(subtitle, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Center: cards
        JPanel center = new JPanel(new GridBagLayout());
        center.setBorder(new EmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // Income card
        JPanel incomeCard = cardPanel("Income");
        monthField = new JTextField();
        salaryField = new JTextField();
        addLabeled(incomeCard, "Month (e.g., Oct-2025):", monthField);
        addLabeled(incomeCard, "Monthly Salary (₹):", salaryField);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5; gbc.gridwidth = 1;
        center.add(incomeCard, gbc);

        // Fixed expenses card
        JPanel fixedCard = cardPanel("Fixed Expenses");
        electricityField = new JTextField("0");
        waterField = new JTextField("0");
        gasField = new JTextField("0");
        addLabeled(fixedCard, "Electricity (₹):", electricityField);
        addLabeled(fixedCard, "Water (₹):", waterField);
        addLabeled(fixedCard, "Gas Cylinder (₹):", gasField);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        center.add(fixedCard, gbc);

        // Variable expenses card (spanning both columns)
        JPanel varCard = cardPanel("Variable Expenses");
        foodField = new JTextField("0");
        travelField = new JTextField("0");
        shoppingField = new JTextField("0");
        miscField = new JTextField("0");
        addLabeled(varCard, "Food (₹):", foodField);
        addLabeled(varCard, "Travel (₹):", travelField);
        addLabeled(varCard, "Shopping (₹):", shoppingField);
        addLabeled(varCard, "Miscellaneous (₹):", miscField);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weighty = 1.0;
        center.add(varCard, gbc);

        add(center, BorderLayout.CENTER);

        // Bottom panel: buttons and message
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(new EmptyBorder(10, 12, 12, 12));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 8));
        JButton addBtn = styledButton("Add Expenses");
        JButton viewBtn = styledButton("View Summary");
        JButton exportBtn = styledButton("Export CSV");
        JButton exitBtn = styledButton("Exit");

        buttonRow.add(addBtn);
        buttonRow.add(viewBtn);
        buttonRow.add(exportBtn);
        buttonRow.add(exitBtn);

        bottom.add(buttonRow, BorderLayout.NORTH);

        messageLabel = new JLabel(" ");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bottom.add(messageLabel, BorderLayout.SOUTH);

        add(bottom, BorderLayout.SOUTH);

        // Actions
        addBtn.addActionListener(e -> addExpensesAction());
        viewBtn.addActionListener(e -> showSummaryDialog());
        exportBtn.addActionListener(e -> exportCsvAction());
        exitBtn.addActionListener(e -> System.exit(0));

        // small niceties
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        setVisible(true);
    }

    private JPanel cardPanel(String heading) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(new LineBorder(new Color(200,200,200), 1, true),
                new EmptyBorder(12,12,12,12)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(4,4,10,4);
        JLabel h = new JLabel(heading);
        h.setFont(new Font("Segoe UI", Font.BOLD, 16));
        p.add(h, gbc);
        return p;
    }

    private void addLabeled(JPanel parent, String labelText, JComponent field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // count components to place rows below heading
        int row = parent.getComponentCount() / 2; // rough heuristic for layout
        gbc.gridx = 0; gbc.gridy = row + 1; gbc.weightx = 0.35;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        parent.add(lbl, gbc);

        gbc.gridx = 1; gbc.gridy = row + 1; gbc.weightx = 0.65;
        if (field instanceof JTextField) ((JTextField) field).setColumns(12);
        field.setPreferredSize(new Dimension(200, 28));
        parent.add(field, gbc);
    }

    private JButton styledButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(160, 36));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBorder(new LineBorder(new Color(120,120,200), 1, true));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); }
            public void mouseExited(MouseEvent e) { b.setCursor(Cursor.getDefaultCursor()); }
        });
        return b;
    }

    private void addExpensesAction() {
        try {
            String month = monthField.getText().trim();
            if (month.isEmpty()) {
                showMessage("Please enter a month (e.g., Oct-2025).", Color.RED);
                return;
            }
            double salary = parseDouble(salaryField.getText(), "Salary");
            double elec = parseDouble(electricityField.getText(), "Electricity");
            double water = parseDouble(waterField.getText(), "Water");
            double gas = parseDouble(gasField.getText(), "Gas Cylinder");
            double food = parseDouble(foodField.getText(), "Food");
            double travel = parseDouble(travelField.getText(), "Travel");
            double shop = parseDouble(shoppingField.getText(), "Shopping");
            double misc = parseDouble(miscField.getText(), "Miscellaneous");

            Expense exp = new Expense(month, salary, elec, water, gas, food, travel, shop, misc);
            manager.addExpense(exp); // persists to file

            DecimalFormat df = new DecimalFormat("##,##0.00");
            if (exp.total > exp.salary * 0.9) {
                showMessage("⚠️ This month you overspent! Total: ₹" + df.format(exp.total), new Color(170,30,30));
            } else {
                showMessage("🎉 Savings made this month! Total: ₹" + df.format(exp.total), new Color(20,120,60));
            }

            clearInputsExceptMonthAndSalary(); // keep month & salary if user wants to add multiple entries
        } catch (NumberFormatException nfe) {
            // parseDouble already shows message
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Unexpected error: " + ex.getMessage(), Color.RED);
        }
    }

    private void clearInputsExceptMonthAndSalary() {
        electricityField.setText("0");
        waterField.setText("0");
        gasField.setText("0");
        foodField.setText("0");
        travelField.setText("0");
        shoppingField.setText("0");
        miscField.setText("0");
    }

    private double parseDouble(String s, String fieldName) throws NumberFormatException {
        if (s == null) s = "";
        s = s.replaceAll("[,₹\\s]", "").trim();
        if (s.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ex) {
            showMessage("Invalid number in " + fieldName + ".", Color.RED);
            throw ex;
        }
    }

    private void showMessage(String msg, Color color) {
        messageLabel.setText(msg);
        messageLabel.setForeground(color);
        // optional dialog
        JOptionPane.showMessageDialog(this, msg);
    }
    private void showMessage(String msg) { showMessage(msg, Color.DARK_GRAY); }

    // Summary dialog (reads from manager)
    private void showSummaryDialog() {
        JDialog d = new JDialog(this, "Expense Summary", true);
        d.setSize(920, 420);
        d.setLocationRelativeTo(this);

        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        model.addColumn("ID");
        model.addColumn("Month");
        model.addColumn("Salary");
        model.addColumn("Electricity");
        model.addColumn("Water");
        model.addColumn("Gas");
        model.addColumn("Food");
        model.addColumn("Travel");
        model.addColumn("Shopping");
        model.addColumn("Misc");
        model.addColumn("Total");
        model.addColumn("Remarks");
        model.addColumn("Saved At");

        java.util.List<Expense> list = manager.getAllExpenses();
        DecimalFormat df = new DecimalFormat("##,##0.00");
        int id = 1;
        for (Expense e : list) {
            Vector<Object> row = new Vector<>();
            row.add(id++);
            row.add(e.month);
            row.add(df.format(e.salary));
            row.add(df.format(e.electricity));
            row.add(df.format(e.water));
            row.add(df.format(e.gas));
            row.add(df.format(e.food));
            row.add(df.format(e.travel));
            row.add(df.format(e.shopping));
            row.add(df.format(e.miscellaneous));
            row.add(df.format(e.total));
            row.add(e.remarks);
            row.add(e.savedAt);
            model.addRow(row);
        }

        JScrollPane sp = new JScrollPane(table);
        d.add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton close = new JButton("Close");
        JButton deleteAll = new JButton("Delete All Records");
        bottom.add(deleteAll);
        bottom.add(close);
        d.add(bottom, BorderLayout.SOUTH);

        close.addActionListener(ev -> d.dispose());
        deleteAll.addActionListener(ev -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Delete all saved records? This cannot be undone.", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                manager.clearAll();
                model.setRowCount(0);
                showMessage("All records deleted.", Color.ORANGE);
            }
        });

        d.setVisible(true);
    }

    // Export CSV action
    private void exportCsvAction() {
        java.util.List<Expense> list = manager.getAllExpenses();
        if (list.isEmpty()) {
            showMessage("No records to export.", Color.GRAY);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save CSV");
        chooser.setSelectedFile(new File("expenses_export.csv"));
        int sel = chooser.showSaveDialog(this);
        if (sel != JFileChooser.APPROVE_OPTION) return;
        File out = chooser.getSelectedFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(out))) {
            pw.println("Month,Salary,Electricity,Water,Gas,Food,Travel,Shopping,Miscellaneous,Total,Remarks,SavedAt");
            DecimalFormat df = new DecimalFormat("##.00");
            for (Expense e : list) {
                pw.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                        escapeCSV(e.month),
                        df.format(e.salary),
                        df.format(e.electricity),
                        df.format(e.water),
                        df.format(e.gas),
                        df.format(e.food),
                        df.format(e.travel),
                        df.format(e.shopping),
                        df.format(e.miscellaneous),
                        df.format(e.total),
                        e.remarks,
                        e.savedAt);
            }
            pw.flush();
            showMessage("Exported to " + out.getAbsolutePath(), Color.BLUE);
        } catch (IOException ex) {
            ex.printStackTrace();
            showMessage("Failed to export: " + ex.getMessage(), Color.RED);
        }
    }

    private String escapeCSV(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }

    // ---------- Main ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ExpenseTrackerApp::new);
    }

    // ----------------- Model and Manager classes -----------------

    /**
     * Expense model (Serializable) to persist locally.
     */
    static class Expense implements Serializable {
        private static final long serialVersionUID = 1L;

        String month;
        double salary;
        double electricity;
        double water;
        double gas;
        double food;
        double travel;
        double shopping;
        double miscellaneous;
        double total;
        String remarks;
        String savedAt; // human readable timestamp

        Expense(String month, double salary,
                double electricity, double water, double gas,
                double food, double travel, double shopping, double miscellaneous) {
            this.month = month;
            this.salary = salary;
            this.electricity = electricity;
            this.water = water;
            this.gas = gas;
            this.food = food;
            this.travel = travel;
            this.shopping = shopping;
            this.miscellaneous = miscellaneous;
            this.total = electricity + water + gas + food + travel + shopping + miscellaneous;
            this.remarks = (this.total > salary * 0.9) ? "Overspent" : "Savings Made";
            this.savedAt = java.time.LocalDateTime.now().toString().replace('T',' ');
        }
    }

    /**
     * ExpenseManager: handles in-memory list + persistence to file (expenses.dat)
     */
    static class ExpenseManager {
        private final File store = new File("expenses.dat");
        private final ArrayList<Expense> list = new ArrayList<>();

        ExpenseManager() {
            load();
        }

        synchronized void addExpense(Expense e) {
            list.add(0, e); // add newest at front
            save();
        }

        synchronized java.util.List<Expense> getAllExpenses() {
            return new ArrayList<>(list);
        }

        synchronized void clearAll() {
            list.clear();
            if (store.exists()) store.delete();
        }

        private void save() {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(store))) {
                oos.writeObject(list);
            } catch (IOException ex) {
                ex.printStackTrace();
                // can't show UI here; caller will handle
            }
        }

        @SuppressWarnings("unchecked")
        private void load() {
            if (!store.exists()) return;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(store))) {
                Object obj = ois.readObject();
                if (obj instanceof ArrayList) {
                    list.clear();
                    list.addAll((ArrayList<Expense>) obj);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                // if load fails, start fresh (do not crash)
            }
        }
    }
}
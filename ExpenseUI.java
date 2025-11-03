import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class ExpenseTrackerUI extends JFrame {
    private JTextField monthField, salaryField;
    private JTextField electricityField, waterField, gasField;
    private JTextField foodField, travelField, shoppingField, miscField;
    private JLabel messageLabel;

    public ExpenseTrackerUI() {
        setTitle("💰 Smart Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Top panel
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(new Color(30, 144, 255));
        top.setBorder(new EmptyBorder(12,12,12,12));
        JLabel title = new JLabel("💰 Smart Expense Tracker");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.white);
        top.add(title, BorderLayout.WEST);
        JLabel subtitle = new JLabel("Plan • Track • Save");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(230,230,250));
        top.add(subtitle, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // Center panel
        JPanel center = new JPanel(new GridBagLayout());
        center.setBorder(new EmptyBorder(16, 16, 16, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.BOTH;

        // Salary Card
        JPanel salaryCard = createCardPanel("Income", 2);
        monthField = new JTextField();
        salaryField = new JTextField();
        addLabeledField(salaryCard, "Month (e.g., Oct-2025)", monthField);
        addLabeledField(salaryCard, "Monthly Salary (₹)", salaryField);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.weightx = 0.5;
        center.add(salaryCard, gbc);

        // Fixed Expenses
        JPanel fixedCard = createCardPanel("Fixed Expenses", 3);
        electricityField = new JTextField("0");
        waterField = new JTextField("0");
        gasField = new JTextField("0");
        addLabeledField(fixedCard, "Electricity (₹)", electricityField);
        addLabeledField(fixedCard, "Water (₹)", waterField);
        addLabeledField(fixedCard, "Gas Cylinder (₹)", gasField);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        center.add(fixedCard, gbc);

        // Variable Expenses
        JPanel varCard = createCardPanel("Variable Expenses", 3);
        foodField = new JTextField("0");
        travelField = new JTextField("0");
        shoppingField = new JTextField("0");
        miscField = new JTextField("0");
        addLabeledField(varCard, "Food (₹)", foodField);
        addLabeledField(varCard, "Travel (₹)", travelField);
        addLabeledField(varCard, "Shopping (₹)", shoppingField);
        addLabeledField(varCard, "Miscellaneous (₹)", miscField);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weighty = 1.0;
        center.add(varCard, gbc);

        add(center, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(new EmptyBorder(12,12,12,12));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));

        JButton addBtn = createRoundedButton("Add Expenses");
        JButton viewBtn = createRoundedButton("View Summary");
        JButton exitBtn = createRoundedButton("Exit");

        buttons.add(addBtn);
        buttons.add(viewBtn);
        buttons.add(exitBtn);
        bottom.add(buttons, BorderLayout.NORTH);

        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottom.add(messageLabel, BorderLayout.SOUTH);

        add(bottom, BorderLayout.SOUTH);

        // Button actions
        addBtn.addActionListener(e -> onAddExpenses());
        viewBtn.addActionListener(e -> showSummaryDialog());
        exitBtn.addActionListener(e -> System.exit(0));

        // Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        setVisible(true);
    }

    private JPanel createCardPanel(String title, int cols) {
        JPanel card = new JPanel();
        card.setLayout(new GridBagLayout());
        card.setBorder(new CompoundBorder(new LineBorder(new Color(200,200,200), 1, true),
                new EmptyBorder(10,10,10,10)));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(6,0,10,0);
        card.add(lbl, gbc);
        return card;
    }

    private void addLabeledField(JPanel parent, String labelText, JComponent field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = parent.getComponentCount();
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        gbc.weightx = 0.4;
        parent.add(label, gbc);

        gbc.gridx = 1; gbc.weightx = 0.6;
        field.setPreferredSize(new Dimension(160, 28));
        if (field instanceof JTextField) ((JTextField) field).setColumns(12);
        parent.add(field, gbc);
    }

    private JButton createRoundedButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBorder(new LineBorder(new Color(120,120,200), 1, true));
        b.setPreferredSize(new Dimension(150, 36));
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { b.setCursor(new Cursor(Cursor.HAND_CURSOR)); }
            public void mouseExited(java.awt.event.MouseEvent evt) { b.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); }
        });
        return b;
    }

    // --- Expense Logic ---
    private void onAddExpenses() {
        try {
            String month = monthField.getText().trim();
            if (month.isEmpty()) {
                showMessage("Please enter month (e.g., Oct-2025).", Color.RED);
                return;
            }

            double salary = parseDouble(salaryField.getText(), "Salary");
            double electricity = parseDouble(electricityField.getText(), "Electricity");
            double water = parseDouble(waterField.getText(), "Water");
            double gas = parseDouble(gasField.getText(), "Gas Cylinder");
            double food = parseDouble(foodField.getText(), "Food");
            double travel = parseDouble(travelField.getText(), "Travel");
            double shopping = parseDouble(shoppingField.getText(), "Shopping");
            double misc = parseDouble(miscField.getText(), "Miscellaneous");

            Expense expense = new Expense(month, salary, electricity, water, gas, food, travel, shopping, misc);

            saveExpenseToDB(expense);

            if (expense.total > salary * 0.9) {
                showMessage("⚠️ Overspent! Total: ₹" + String.format("%.2f", expense.total), new Color(185, 50, 50));
            } else {
                showMessage("🎉 Savings made! Total: ₹" + String.format("%.2f", expense.total), new Color(20,120,60));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Error: " + ex.getMessage(), Color.RED);
        }
    }

    private double parseDouble(String text, String fieldName) throws NumberFormatException {
        text = text.replaceAll("[,₹\\s]", "");
        if (text.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            showMessage("Invalid number in " + fieldName + ".", Color.RED);
            throw ex;
        }
    }

    private void showMessage(String msg, Color color) {
        messageLabel.setText(msg);
        messageLabel.setForeground(color);
        JOptionPane.showMessageDialog(this, msg);
    }

    private void saveExpenseToDB(Expense e) {
        String sql = "INSERT INTO expenses (month, salary, electricity_bill, water_bill, gas_cylinder, food, travel, shopping, miscellaneous, total_expense, remarks) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, e.month);
            pst.setDouble(2, e.salary);
            pst.setDouble(3, e.electricity);
            pst.setDouble(4, e.water);
            pst.setDouble(5, e.gas);
            pst.setDouble(6, e.food);
            pst.setDouble(7, e.travel);
            pst.setDouble(8, e.shopping);
            pst.setDouble(9, e.miscellaneous);
            pst.setDouble(10, e.total);
            pst.setString(11, e.remarks);
            pst.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            showMessage("DB Error: " + ex.getMessage(), Color.RED);
        }
    }

    private void showSummaryDialog() {
        JDialog dialog = new JDialog(this, "Expense Summary", true);
        dialog.setSize(900, 400);
        dialog.setLocationRelativeTo(this);

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
        model.addColumn("Created At");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM expenses ORDER BY created_at DESC");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("id"));
                row.add(rs.getString("month"));
                row.add(rs.getDouble("salary"));
                row.add(rs.getDouble("electricity_bill"));
                row.add(rs.getDouble("water_bill"));
                row.add(rs.getDouble("gas_cylinder"));
                row.add(rs.getDouble("food"));
                row.add(rs.getDouble("travel"));
                row.add(rs.getDouble("shopping"));
                row.add(rs.getDouble("miscellaneous"));
                row.add(rs.getDouble("total_expense"));
                row.add(rs.getString("remarks"));
                row.add(rs.getTimestamp("created_at"));
                model.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load summary: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }

        JScrollPane sp = new JScrollPane(table);
        dialog.add(sp);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExpenseTrackerUI());
    }
}

// Helper class
class Expense {
    String month, remarks;
    double salary, electricity, water, gas, food, travel, shopping, miscellaneous, total;

    public Expense(String month, double salary, double electricity, double water, double gas, double food, double travel, double shopping, double misc) {
        this.month = month;
        this.salary = salary;
        this.electricity = electricity;
        this.water = water;
        this.gas = gas;
        this.food = food;
        this.travel = travel;
        this.shopping = shopping;
        this.miscellaneous = misc;
        this.total = electricity + water + gas + food + travel + shopping + misc;
        this.remarks = (total > salary * 0.9) ? "Overspent" : "Saved Well";
    }
}

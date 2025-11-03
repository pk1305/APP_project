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

    // --- THEME COLORS ---
    private static final Color COLOR_PRIMARY = new Color(220, 53, 69); // Bright Red
    private static final Color COLOR_BACKGROUND_DARK = new Color(30, 30, 30);
    private static final Color COLOR_BACKGROUND_LIGHT = new Color(50, 50, 50);
    private static final Color COLOR_TEXT_LIGHT = new Color(230, 230, 230);
    private static final Color COLOR_BORDER = new Color(80, 80, 80);
    private static final Color COLOR_ACCENT_GREEN = new Color(40, 180, 70);

    // --- FONTS ---
    private static final Font FONT_REGULAR = new Font("Times New Roman", Font.PLAIN, 15);
    private static final Font FONT_BOLD = new Font("Times New Roman", Font.BOLD, 15);
    private static final Font FONT_TITLE = new Font("Times New Roman", Font.BOLD, 30);
    private static final Font FONT_CARD_TITLE = new Font("Times New Roman", Font.BOLD, 20);

    public ExpenseTrackerUI() {
        setTitle("💰 Smart Expense Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_BACKGROUND_DARK);

        // --- HEADER ---
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.BLACK);
        top.setBorder(new CompoundBorder(new LineBorder(COLOR_PRIMARY.darker()), new EmptyBorder(15, 20, 15, 20)));
        JLabel title = new JLabel("💰 Smart Expense Tracker");
        title.setFont(FONT_TITLE);
        title.setForeground(COLOR_PRIMARY);
        top.add(title, BorderLayout.WEST);
        JLabel subtitle = new JLabel("Plan • Track • Save");
        subtitle.setFont(FONT_REGULAR);
        subtitle.setForeground(COLOR_TEXT_LIGHT);
        top.add(subtitle, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // --- CENTER ---
        JPanel center = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, Color.BLACK, 0, getHeight(), COLOR_BACKGROUND_DARK);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        center.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // --- CARDS ---
        JPanel salaryCard = createCardPanel("Income");
        monthField = new JTextField();
        salaryField = new JTextField();
        addLabeledField(salaryCard, "Month", monthField);
        addLabeledField(salaryCard, "Monthly Salary (₹)", salaryField);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.weightx = 0.5;
        center.add(salaryCard, gbc);

        JPanel fixedCard = createCardPanel("Fixed Expenses");
        electricityField = new JTextField("0");
        waterField = new JTextField("0");
        gasField = new JTextField("0");
        addLabeledField(fixedCard, "Electricity (₹)", electricityField);
        addLabeledField(fixedCard, "Water (₹)", waterField);
        addLabeledField(fixedCard, "Gas Cylinder (₹)", gasField);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        center.add(fixedCard, gbc);

        JPanel varCard = createCardPanel("Variable Expenses");
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

        // --- BOTTOM PANEL ---
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(new EmptyBorder(10, 20, 15, 20));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttons.setOpaque(false);

        JButton addBtn = createRoundedButton("Add Expenses", COLOR_PRIMARY, Color.BLACK, null);
        JButton viewBtn = createRoundedButton("View Summary", COLOR_BACKGROUND_LIGHT, COLOR_TEXT_LIGHT, null);
        JButton exitBtn = createRoundedButton("Exit", Color.BLACK, COLOR_PRIMARY, null);

        buttons.add(addBtn);
        buttons.add(viewBtn);
        buttons.add(exitBtn);
        bottom.add(buttons, BorderLayout.CENTER);

        messageLabel = new JLabel(" ");
        messageLabel.setFont(FONT_BOLD);
        messageLabel.setForeground(COLOR_TEXT_LIGHT);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottom.add(messageLabel, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> onAddExpenses());
        viewBtn.addActionListener(e -> showSummaryDialog());
        exitBtn.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private JPanel createCardPanel(String title) {
        JPanel card = new JPanel();
        card.setBackground(COLOR_BACKGROUND_LIGHT);
        card.setLayout(new GridBagLayout());
        card.setBorder(new CompoundBorder(new LineBorder(COLOR_BORDER, 1), new EmptyBorder(15, 15, 15, 15)));
        JLabel lbl = new JLabel(title);
        lbl.setFont(FONT_CARD_TITLE);
        lbl.setForeground(COLOR_PRIMARY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        card.add(lbl, gbc);
        return card;
    }

    private void addLabeledField(JPanel parent, String labelText, JComponent field) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = parent.getComponentCount();

        JLabel label = new JLabel(labelText);
        label.setFont(FONT_BOLD);
        label.setForeground(COLOR_PRIMARY);
        gbc.weightx = 0.4;
        gbc.anchor = GridBagConstraints.WEST;
        parent.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.6;
        field.setPreferredSize(new Dimension(160, 32));
        field.setFont(FONT_REGULAR);

        if (field instanceof JTextField) {
            JTextField tf = (JTextField) field;
            tf.setBackground(COLOR_BACKGROUND_DARK);
            tf.setForeground(COLOR_TEXT_LIGHT);
            tf.setCaretColor(COLOR_PRIMARY);
            tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER),
                new EmptyBorder(5, 5, 5, 5)
            ));
            addFocusHighlight(tf);
        }

        parent.add(field, gbc);
    }

    private JButton createRoundedButton(String text, Color bgColor, Color fgColor, Icon icon) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(FONT_BOLD);
        b.setForeground(fgColor);
        b.setBackground(bgColor);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        return b;
    }

    private void addFocusHighlight(JTextField textField) {
        Border defaultBorder = textField.getBorder();
        Border focusBorder = BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_PRIMARY, 2),
            new EmptyBorder(4, 4, 4, 4)
        );
        textField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { textField.setBorder(focusBorder); }
            public void focusLost(FocusEvent e) { textField.setBorder(defaultBorder); }
        });
    }

    private void onAddExpenses() {
        try {
            String month = monthField.getText().trim();
            if (month.isEmpty()) {
                showMessage("Please enter month (e.g., Oct-2025).", COLOR_PRIMARY);
                return;
            }
            double salary = parseDouble(salaryField.getText());
            double electricity = parseDouble(electricityField.getText());
            double water = parseDouble(waterField.getText());
            double gas = parseDouble(gasField.getText());
            double food = parseDouble(foodField.getText());
            double travel = parseDouble(travelField.getText());
            double shopping = parseDouble(shoppingField.getText());
            double misc = parseDouble(miscField.getText());

            Expense expense = new Expense(month, salary, electricity, water, gas, food, travel, shopping, misc);
            saveExpenseToDB(expense);

            if (expense.total > salary * 0.9 && salary > 0)
                showMessage("⚠ Overspent! Total: ₹" + String.format("%.2f", expense.total), COLOR_PRIMARY);
            else
                showMessage("🎉 Expenses added! Total: ₹" + String.format("%.2f", expense.total), COLOR_ACCENT_GREEN);

        } catch (Exception ex) {
            ex.printStackTrace();
            showMessage("Error: " + ex.getMessage(), COLOR_PRIMARY);
        }
    }

    private double parseDouble(String text) {
        text = text.replaceAll("[,₹\\s]", "");
        if (text.isEmpty()) return 0.0;
        return Double.parseDouble(text);
    }

    private void showMessage(String msg, Color color) {
        messageLabel.setText(msg);
        messageLabel.setForeground(color);
    }

    private void saveExpenseToDB(Expense e) {
        String query = """
            INSERT INTO expenses
            (month, salary, electricity_bill, water_bill, gas_cylinder, food, travel, shopping, miscellaneous, total_expense, remarks, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, e.month);
            ps.setDouble(2, e.salary);
            ps.setDouble(3, e.electricity);
            ps.setDouble(4, e.water);
            ps.setDouble(5, e.gas);
            ps.setDouble(6, e.food);
            ps.setDouble(7, e.travel);
            ps.setDouble(8, e.shopping);
            ps.setDouble(9, e.miscellaneous);
            ps.setDouble(10, e.total);
            ps.setString(11, e.remarks);
            ps.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void showSummaryDialog() {
        JDialog dialog = new JDialog(this, "Expense Summary", true);
        dialog.setSize(900, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(COLOR_BACKGROUND_DARK);

        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        table.setBackground(COLOR_BACKGROUND_LIGHT);
        table.setForeground(COLOR_TEXT_LIGHT);
        table.setGridColor(COLOR_PRIMARY.darker());
        table.setSelectionBackground(COLOR_PRIMARY);
        table.setSelectionForeground(Color.BLACK);
        table.setFont(FONT_REGULAR);
        table.getTableHeader().setBackground(Color.BLACK);
        table.getTableHeader().setForeground(COLOR_PRIMARY);
        table.getTableHeader().setFont(FONT_BOLD);

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
        }

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(COLOR_BACKGROUND_DARK);
        sp.setBorder(new LineBorder(COLOR_BORDER));
        dialog.add(sp);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExpenseTrackerUI());
    }
}

// --- EXPENSE CLASS ---
class Expense {
    String month, remarks = "";
    double salary, electricity, water, gas, food, travel, shopping, miscellaneous, total;

    Expense(String month, double salary, double electricity, double water, double gas,
             double food, double travel, double shopping, double misc) {
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
        if (total > salary) remarks = "Over budget!";
    }
}

// --- DB CONNECTION CLASS ---
class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/expense_tracker";
    private static final String USER = "root";
    private static final String PASSWORD = "sri123"; // Add your password if any

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

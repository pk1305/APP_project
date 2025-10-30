// Expense.java
public class Expense {
    public String month;
    public double salary;
    public double electricity;
    public double water;
    public double gas;
    public double food;
    public double travel;
    public double shopping;
    public double miscellaneous;
    public double total;
    public String remarks;

    public Expense(String month, double salary, double electricity, double water, double gas,
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
        // Business rule: overspent if total > 90% of salary
        this.remarks = (this.total > salary * 0.9) ? "Overspent" : "Savings Made";
    }
}
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Step 4: Interactive Input Configuration Window
 * Member 4's deliverable.
 *
 * Collects simulation parameters (customer count, IAT range, ST range),
 * validates them, and triggers the simulation engine via the
 * SimulationConfig interface so this class can be developed and tested
 * independently of Member 1 & 2's branches.
 */
public class InputConfigPanel extends JFrame {

    private JTextField customerCountField;
    private JTextField iatMinField, iatMaxField;
    private JTextField stMinField, stMaxField;
    private JButton runButton;

    public InputConfigPanel() {
        setTitle("Bank Queue Simulation - Configuration");
        setSize(420, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel headingLabel = new JLabel("Bank Queue Simulation Configuration", SwingConstants.CENTER);
        headingLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(headingLabel, BorderLayout.NORTH);

        JPanel fieldsPanel = new JPanel(new GridLayout(6, 2, 8, 8));
        add(fieldsPanel, BorderLayout.CENTER);

        customerCountField = new JTextField("100");
        iatMinField = new JTextField("1");
        iatMaxField = new JTextField("8");
        stMinField = new JTextField("1");
        stMaxField = new JTextField("6");
        runButton = new JButton("Run Simulation");

        fieldsPanel.add(new JLabel("Target Customer Count:"));
        fieldsPanel.add(customerCountField);
        fieldsPanel.add(new JLabel("IAT Min (minutes):"));
        fieldsPanel.add(iatMinField);
        fieldsPanel.add(new JLabel("IAT Max (minutes):"));
        fieldsPanel.add(iatMaxField);
        fieldsPanel.add(new JLabel("Service Time Min (minutes):"));
        fieldsPanel.add(stMinField);
        fieldsPanel.add(new JLabel("Service Time Max (minutes):"));
        fieldsPanel.add(stMaxField);
        fieldsPanel.add(new JLabel());
        fieldsPanel.add(runButton);

        runButton.addActionListener(this::onRunClicked);
    }

    private void onRunClicked(ActionEvent e) {
        try {
            int count = Integer.parseInt(customerCountField.getText().trim());
            double iatMin = Double.parseDouble(iatMinField.getText().trim());
            double iatMax = Double.parseDouble(iatMaxField.getText().trim());
            double stMin = Double.parseDouble(stMinField.getText().trim());
            double stMax = Double.parseDouble(stMaxField.getText().trim());

            if (count <= 0 || iatMin < 0 || iatMax <= iatMin || stMin < 0 || stMax <= stMin) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid positive ranges (max must be greater than min).",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SimulationConfig config = new BasicSimulationConfig(count, iatMin, iatMax, stMin, stMax);

            // Per the lecturer's spec, IAT is fixed Uniform(1,8) and ST is fixed Uniform(1,6) -
            // RandomGenerator.generateCustomerBatch(int) already hardcodes these correctly, so no
            // overload is needed. The IAT/ST fields above stay in the GUI for display/defaults,
            // but only customer count is actually passed through to the generator.
            RandomGenerator generator = new RandomGenerator();
            List<Customer> customers = generator.generateCustomerBatch(count);

            // TODO (integration step, once Member 2's SimulationEngine exists):
            // SimulationEngine engine = new SimulationEngine(customers);
            // engine.run();
            // new OutputMetricsPanel(engine.getResults()).setVisible(true);

            JOptionPane.showMessageDialog(this,
                    "Generated " + customers.size() + " customers (Step 1 only - engine not yet available).\n" +
                            "Customer Count: " + config.getCustomerCount() +
                            "\nIAT and ST use fixed ranges [1,8] and [1,6] per spec.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "All fields must contain valid numbers.",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InputConfigPanel().setVisible(true));
    }
}

/**
 * Shared contract so Member 4's GUI doesn't have to wait on
 * Member 1/2's concrete classes to be merged before this compiles and runs.
 * Agree on this interface as a team early — it can live in a shared
 * "common" package everyone branches from.
 */
interface SimulationConfig {
    int getCustomerCount();
    double getIatMin();
    double getIatMax();
    double getStMin();
    double getStMax();
}

class BasicSimulationConfig implements SimulationConfig {
    private final int customerCount;
    private final double iatMin, iatMax, stMin, stMax;

    public BasicSimulationConfig(int customerCount, double iatMin, double iatMax,
                                  double stMin, double stMax) {
        this.customerCount = customerCount;
        this.iatMin = iatMin;
        this.iatMax = iatMax;
        this.stMin = stMin;
        this.stMax = stMax;
    }

    public int getCustomerCount() { return customerCount; }
    public double getIatMin() { return iatMin; }
    public double getIatMax() { return iatMax; }
    public double getStMin() { return stMin; }
    public double getStMax() { return stMax; }
}
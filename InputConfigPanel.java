import analytics.SimulationAnalytics;
import engine.SimulationEngine;
import model.Customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Step 4: Interactive Input Configuration Window
 * Integrated with Steps 1–5.
 */
public class InputConfigPanel extends JFrame {

    private JTextField customerCountField;
    private JTextField iatMinField, iatMaxField;
    private JTextField stMinField, stMaxField;
    private JButton runButton;

    public InputConfigPanel() {

        setTitle("Bank Queue Simulation - Configuration");
        setSize(420, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ==========================
        // HEADING
        // ==========================
        JLabel headingLabel = new JLabel(
                "Bank Queue Simulation Configuration",
                SwingConstants.CENTER
        );

        headingLabel.setFont(
                new Font("SansSerif", Font.BOLD, 18)
        );

        add(headingLabel, BorderLayout.NORTH);

        // ==========================
        // INPUT FIELDS
        // ==========================
        JPanel fieldsPanel = new JPanel(
                new GridLayout(6, 2, 8, 8)
        );

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

    /**
     * Executes when the user presses
     * the Run Simulation button.
     */
    private void onRunClicked(ActionEvent e) {

        try {

            int count = Integer.parseInt(
                    customerCountField.getText().trim()
            );

            double iatMin = Double.parseDouble(
                    iatMinField.getText().trim()
            );

            double iatMax = Double.parseDouble(
                    iatMaxField.getText().trim()
            );

            double stMin = Double.parseDouble(
                    stMinField.getText().trim()
            );

            double stMax = Double.parseDouble(
                    stMaxField.getText().trim()
            );

            // ==========================
            // INPUT VALIDATION
            // ==========================
            if (count <= 0 ||
                    iatMin < 0 ||
                    iatMax <= iatMin ||
                    stMin < 0 ||
                    stMax <= stMin) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter valid positive ranges.\n" +
                                "Maximum values must be greater than minimum values.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            // ==========================
            // STEP 2: RUN SIMULATION
            // ==========================
            SimulationEngine engine =
                    new SimulationEngine(count);

            List<Customer> customers =
                    engine.runSimulation();

            // ==========================
            // STEP 3: ANALYTICS
            // ==========================
            SimulationAnalytics analytics =
                    new SimulationAnalytics();

            analytics.analyse(customers);

            // ==========================
            // STEP 5: DISPLAY RESULTS
            // ==========================
            new ResultsWindow(customers, analytics);

        }

        catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "All fields must contain valid numbers.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Simulation Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(
                () -> new InputConfigPanel().setVisible(true)
        );
    }
}


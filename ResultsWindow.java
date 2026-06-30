import analytics.SimulationAnalytics;
import model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ResultsWindow extends JFrame {

    public ResultsWindow(List<Customer> customers,
                         SimulationAnalytics analytics) {

        setTitle("Bank Queue Simulation Results");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ==========================
        // TABLE COLUMNS
        // ==========================
        String[] columns = {
                "ID",
                "IAT",
                "AT",
                "SST",
                "WT",
                "ST",
                "SET",
                "TS"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0);

        // ==========================
        // POPULATE TABLE
        // ==========================
        for (Customer c : customers) {

            model.addRow(new Object[]{
                    c.getId(),

                    String.format("%.2f",
                            c.getInterArrivalTime()),

                    String.format("%.2f",
                            c.getArrivalTime()),

                    String.format("%.2f",
                            c.getServiceStartTime()),

                    String.format("%.2f",
                            c.getWaitingTime()),

                    String.format("%.2f",
                            c.getServiceTime()),

                    String.format("%.2f",
                            c.getServiceEndTime()),

                    String.format("%.2f",
                            c.getTimeInSystem())
            });
        }

        JTable table = new JTable(model);

        table.setEnabled(false);

        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);

        // ==========================
        // METRICS PANEL
        // ==========================
        JPanel metricsPanel = new JPanel();
        metricsPanel.setLayout(new GridLayout(3, 1, 5, 5));
        metricsPanel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        JLabel avgWaitingLabel = new JLabel(
                "Average Waiting Time: "
                        + String.format("%.2f",
                        analytics.getAverageWaitingTime())
                        + " mins"
        );

        JLabel avgSystemLabel = new JLabel(
                "Average Time In System: "
                        + String.format("%.2f",
                        analytics.getAverageTimeInSystem())
                        + " mins"
        );

        JLabel idleLabel = new JLabel(
                "Teller Idle Percentage: "
                        + String.format("%.2f",
                        analytics.getTellerIdlePercentage())
                        + "%"
        );

        avgWaitingLabel.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );

        avgSystemLabel.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );

        idleLabel.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );

        metricsPanel.add(avgWaitingLabel);
        metricsPanel.add(avgSystemLabel);
        metricsPanel.add(idleLabel);

        add(metricsPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}

package analytics;

import model.Customer;

import java.util.List;

public class SimulationAnalytics {

    private double averageWaitingTime;
    private double averageTimeInSystem;
    private double tellerIdlePercentage;

    private double totalWaitingTime;
    private double totalTimeInSystem;
    private double totalIdleTime;
    private double totalSimulationTime;

    private int customersWhoWaited;
    private boolean hasAnalysed;

    public SimulationAnalytics() {
        this.hasAnalysed = false;
    }

    public void analyse(List<Customer> customers) {
        if (hasAnalysed) {
            throw new IllegalStateException(
                "SimulationAnalytics has already been run. Create a new instance to analyse a different simulation."
            );
        }

        if (customers == null || customers.isEmpty()) {
            throw new IllegalArgumentException("Customer list must not be null or empty.");
        }

        double sumWaitingTime = 0.0;
        double sumTimeInSystem = 0.0;
        double sumIdleTime = 0.0;
        int waitCount = 0;

        // Before the first customer, previous service end time is 0.
        double previousServiceEndTime = 0.0;

        for (Customer customer : customers) {
            double at = customer.getArrivalTime();
            double wt = customer.getWaitingTime();
            double ts = customer.getTimeInSystem();
            double set = customer.getServiceEndTime();

            sumWaitingTime += wt;
            sumTimeInSystem += ts;

            if (wt > 0.0) {
                waitCount++;
            }

            double idleGap = at - previousServiceEndTime;
            if (idleGap > 0.0) {
                sumIdleTime += idleGap;
            }

            previousServiceEndTime = set;
        }

        int n = customers.size();

        averageWaitingTime = sumWaitingTime / n;
        averageTimeInSystem = sumTimeInSystem / n;

        double firstArrival = customers.get(0).getArrivalTime();
        double lastDeparture = customers.get(n - 1).getServiceEndTime();
        totalSimulationTime = lastDeparture - firstArrival;

        if (totalSimulationTime > 0.0) {
            tellerIdlePercentage = (sumIdleTime / totalSimulationTime) * 100.0;
        } else {
            tellerIdlePercentage = 0.0;
        }

        totalWaitingTime = sumWaitingTime;
        totalTimeInSystem = sumTimeInSystem;
        totalIdleTime = sumIdleTime;
        customersWhoWaited = waitCount;

        hasAnalysed = true;
    }

    public double getAverageWaitingTime() {
        guardCheck();
        return averageWaitingTime;
    }

    public double getAverageTimeInSystem() {
        guardCheck();
        return averageTimeInSystem;
    }

    public double getTellerIdlePercentage() {
        guardCheck();
        return tellerIdlePercentage;
    }

    public double getTotalWaitingTime() {
        guardCheck();
        return totalWaitingTime;
    }

    public double getTotalTimeInSystem() {
        guardCheck();
        return totalTimeInSystem;
    }

    public double getTotalIdleTime() {
        guardCheck();
        return totalIdleTime;
    }

    public double getTotalSimulationTime() {
        guardCheck();
        return totalSimulationTime;
    }

    public int getCustomersWhoWaited() {
        guardCheck();
        return customersWhoWaited;
    }

    public boolean hasAnalysed() {
        return hasAnalysed;
    }

    @Override
    public String toString() {
        if (!hasAnalysed) {
            return "[SimulationAnalytics - not yet run. Call analyse() first.]";
        }

        return String.format(
            "%n" +
            "  ===========================================%n" +
            "   SIMULATION ANALYTICS SUMMARY%n" +
            "  ===========================================%n" +
            "   Average Waiting Time       : %8.4f mins%n" +
            "   Average Time in System     : %8.4f mins%n" +
            "   Teller Idle Percentage     : %8.4f %%%n" +
            "  -------------------------------------------%n" +
            "   Total Waiting Time         : %8.4f mins%n" +
            "   Total Time in System       : %8.4f mins%n" +
            "   Total Teller Idle Time     : %8.4f mins%n" +
            "   Total Simulation Duration  : %8.4f mins%n" +
            "   Customers Who Waited       : %d%n" +
            "  ===========================================%n",
            averageWaitingTime,
            averageTimeInSystem,
            tellerIdlePercentage,
            totalWaitingTime,
            totalTimeInSystem,
            totalIdleTime,
            totalSimulationTime,
            customersWhoWaited
        );
    }

    private void guardCheck() {
        if (!hasAnalysed) {
            throw new IllegalStateException("Metrics not available. Call analyse(customers) first.");
        }
    }
}

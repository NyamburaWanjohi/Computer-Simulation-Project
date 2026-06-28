import engine.SimulationEngine;
import model.Customer;
import analytics.SimulationAnalytics;
import java.util.List;

/**
 * ============================================================
 *  Purpose:
 *  Verify that SimulationEngine produces correct output against
 *  Ivy's actual Customer and RandomGenerator files,
 *  BEFORE handing results to Samuel's GUI and Bridget 5's display.
 *
 *  What to verify in the output table:
 *    1. AT values are strictly increasing  (meaning each customer arrives later)
 *    2. SST >= AT ;at all times  (meaning service never begins before the customer arrives)
 *    3. WT = 0  when the teller was free  (SST == AT in that row)
 *    4. WT > 0  when the customer had to wait  (SST > AT in that row)
 *    5. TS = WT + ST  for every row  (verify a few by hand)
 * ============================================================
 */
public class Main {

    public static void main(String[] args) {

        // Configurations

        int customerCount = 100;


        // Creating & running the engine

        SimulationEngine engine = new SimulationEngine(customerCount);

        System.out.println("=".repeat(78));
        System.out.println("  BANK QUEUE DISCRETE-EVENT SIMULATION");
        System.out.println("  Timon  Step 2: Simulation Engine Test Output");
        System.out.println("  Customers: " + customerCount
                + "  |  IAT ~ Uniform(1, 8)  |  ST ~ Uniform(1, 6)");
        System.out.println("  [Random values produced by Member 1's RandomGenerator]");
        System.out.println("=".repeat(78));

        List<Customer> customers = engine.runSimulation();


        // Results Table
        //  Column order:
        //    Customer ID | IAT | AT | SST | WT | ST | SET | TS

        // Header row
        System.out.printf("%-6s  %-7s  %-8s  %-8s  %-7s  %-7s  %-8s  %-7s%n",
                "C.ID", "IAT", "AT", "SST", "WT", "ST", "SET", "TS");
        System.out.println("-".repeat(68));

        // Data rows  one per customer
        for (Customer c : customers) {
            System.out.printf("%-6d  %-7.2f  %-8.2f  %-8.2f  %-7.2f  %-7.2f  %-8.2f  %-7.2f%n",
                    c.getId(),
                    c.getInterArrivalTime(),
                    c.getArrivalTime(),
                    c.getServiceStartTime(),
                    c.getWaitingTime(),
                    c.getServiceTime(),
                    c.getServiceEndTime(),
                    c.getTimeInSystem()
            );
        }

        System.out.println("-".repeat(68));


        // Self-checks

        System.out.println("\n  EQUATION SELF-CHECK:");
        System.out.println("  " + "-".repeat(44));

        int errorCount = 0;

        for (Customer c : customers) {
            double at  = c.getArrivalTime();
            double sst = c.getServiceStartTime();
            double set = c.getServiceEndTime();
            double wt  = c.getWaitingTime();
            double st  = c.getServiceTime();
            double ts  = c.getTimeInSystem();

            // Check 1: SST >= AT  (service cannot start before the customer arrives)
            if (sst < at - 0.0001) {
                System.out.printf("  [FAIL] C#%d  SST (%.2f) < AT (%.2f)%n",
                        c.getId(), sst, at);
                errorCount++;
            }

            // Check 2: WT == SST - AT  (waiting time must match the gap we computed)
            if (Math.abs(wt - (sst - at)) > 0.0001) {
                System.out.printf("  [FAIL] C#%d  WT stored=%.2f, expected=%.2f%n",
                        c.getId(), wt, sst - at);
                errorCount++;
            }

            // Check 3: SET == SST + ST  (service end must equal start plus duration)
            if (Math.abs(set - (sst + st)) > 0.0001) {
                System.out.printf("  [FAIL] C#%d  SET stored=%.2f, expected=%.2f%n",
                        c.getId(), set, sst + st);
                errorCount++;
            }

            // Check 4: TS == SET - AT  (total time must match arrival-to-departure gap)
            if (Math.abs(ts - (set - at)) > 0.0001) {
                System.out.printf("  [FAIL] C#%d  TS stored=%.2f, expected=%.2f%n",
                        c.getId(), ts, set - at);
                errorCount++;
            }

            // Check 5: TS == WT + ST  (total = waiting + service; cross-verify)
            if (Math.abs(ts - (wt + st)) > 0.0001) {
                System.out.printf("  [FAIL] C#%d  TS (%.2f) != WT+ST (%.2f)%n",
                        c.getId(), ts, wt + st);
                errorCount++;
            }
        }

        if (errorCount == 0) {
            System.out.println("  [PASS] All " + customers.size()
                    + " customers passed every equation check.");
        } else {
            System.out.println("  " + errorCount + " check(s) FAILED  review flagged rows above.");
        }

        System.out.println("\n  Total customers processed : " + customers.size());
        System.out.println("=".repeat(78));

        SimulationAnalytics analytics = new SimulationAnalytics();
        analytics.analyse(customers);
        System.out.println(analytics);
        System.out.println("=".repeat(78));
    }
}
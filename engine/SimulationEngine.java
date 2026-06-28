package engine;

import model.Customer;
import util.RandomGenerator;

import java.util.Collections;
import java.util.List;

/**
 * ============================================================
 *
 *  WHAT THIS CLASS DOES
 *  --------------------
 *  Runs the discrete-event bank queue simulation.
 *  It processes a list of pre-generated customers in time order,
 *  computing exactly when each one arrives, when service starts,
 *  and when they leave.
 *
 *  HOW IT WORKS WITH IVY'S FILES
 *  ------------------------------------
 *  Ivy's RandomGenerator.generateCustomerBatch() already handles:
 *    → Creating all Customer objects
 *    → Assigning each customer their ID
 *    → Sampling IAT  ~  Uniform(1, 8) via getUniformRandom()
 *    → Sampling ST   ~  Uniform(1, 6) via getUniformRandom()
 *
 *  So this engine's job is purely the event loop:
 *    → Receive that pre-loaded List<Customer> from Ivy's generator
 *    → Walk through it in order, customer by customer
 *    → Compute and store AT, SST, SET, WT, TS for each customer
 *      using Ivy's setters (setArrivalTime, setServiceStartTime, etc.)
 *
 *  This is a two-phase approach:
 *    Phase 1 (Ivy): Generate the raw data  →  List<Customer> with IAT + ST set
 *    Phase 2 (Me: Timon): Simulate the timeline  →  Fill in AT, SST, SET, WT, TS
 *
 *
 *  THE SINGLE-TELLER BANK QUEUE MODEL
 *  ------------------------------------
 *  - There is ONE teller (single-server queue).
 *  - Customers arrive one at a time, each separated by IAT minutes.
 *  - If the teller is FREE when a customer arrives:
 *        → Service starts immediately on arrival.  (Teller was idling.)
 *  - If the teller is BUSY when a customer arrives:
 *        → Customer waits in line until the teller finishes.
 *
 *
 *  CHRONOLOGICAL EQUATIONS  (from the project specification)
 *  ----------------------------------------------------------
 *
 *    AT_i  =  AT_{i-1}  +  IAT_i           ← Arrival Time
 *    SST_i =  max(AT_i,    SET_{i-1})       ← Service Start Time
 *    SET_i =  SST_i      +  ST_i            ← Service End Time
 *    WT_i  =  SST_i      -  AT_i            ← Waiting Time in queue
 *    TS_i  =  SET_i      -  AT_i            ← Total Time in System
 *
 *  Starting condition (before the first customer):
 *    AT_0  =  0    (no previous customer; clock starts at zero)
 *    SET_0 =  0    (teller is free and idle at the very start)
 *
 *
 *  TEAM INTEGRATION NOTES
 *  -----------------------
 *  Continuation of Ivy's Step 1 Work:
 *    Ivy  →  Customer.java, RandomGenerator.java
 *
 *  Output goes to:
 *    Obuya  →  List<Customer> via getCustomers()   [for analytics]
 *    Bridget  →  List<Customer> via getCustomers()   [for JTable display]
 *
 *  NOTE ON IAT/ST BOUNDS:
 *    The distribution bounds [1,8] for IAT and [1,6] for ST are currently
 *    hardcoded inside RandomGenerator.generateCustomerBatch().
 *    Member 4's GUI should pass the customer count to this engine.
 *    If Ivy later makes the bounds configurable in their generator,
 *    this engine can be updated to pass them through.
 * ============================================================
 */


public class SimulationEngine {

    // fields

    /** How many customers to simulate.*/
    private final int customerCount;

    /**
     * Ivy's random generator.
     * Used to produce the pre-loaded List<Customer> with IAT and ST already set.
     */
    private final RandomGenerator randomGenerator;

    /** Holds the fully processed customer records*/
    private List<Customer> customers;

    /**
     * Prevents runSimulation() from being called twice on the same engine instance.
     * Running twice would corrupt results since the random generator's internal
     * state would have already advanced past its starting position.
     */
    private boolean hasRun;


    // constructor

    /**
     * Creates a SimulationEngine ready to simulate the given number of customers.
     *
     * @param customerCount  number of customers to simulate (e.g. 100)
     */
    public SimulationEngine(int customerCount) {
        if (customerCount <= 0) {
            throw new IllegalArgumentException(
                    "Customer count must be a positive integer. Received: " + customerCount
            );
        }
        this.customerCount   = customerCount;
        this.randomGenerator = new RandomGenerator(); // Ivy's generator
        this.customers       = null;
        this.hasRun          = false;
    }

    // main simulation method

    /**
     * Runs the full discrete-event simulation.
     *
     * Executes in two clear phases:
     *
     *   PHASE 1 — Data Generation (Done by Ivy):
     *     Calls RandomGenerator.generateCustomerBatch() to obtain a list of
     *     Customer objects that already have their ID, IAT, and ST set.
     *     This engine does not create Customer objects or sample random values.
     *
     *   PHASE 2 — Timeline Computation (I am doing this in this code):
     *     Iterates through the pre-loaded customer list in sequence.
     *     For each customer, computes AT, SST, SET, WT, and TS using the
     *     chronological equations from the project specification, then writes
     *     the results back to the Customer object using Ivy's setters.
     *
     *     @return  an unmodifiable view of the fully processed customer list
     */
    public List<Customer> runSimulation() {
        // Guard preventing double-running
        if (hasRun) {
            throw new IllegalStateException(
                    "This SimulationEngine has already been run.\n" +
                            "Create a new SimulationEngine instance to start a fresh simulation."
            );
        }

        // Phase 1 - Receives the pre-generated customer batch
        //
        // Ivy's generateCustomerBatch() does the following internally:
        //   - Creates customerCount Customer objects
        //   - Assigns each a sequential ID (1, 2, 3, ..., N)
        //   - Samples IAT ~ Uniform(1, 8) for each customer
        //   - Samples ST  ~ Uniform(1, 6) for each customer
        //   - Returns them as a list of customers
        //
        // When we receive this list, each customer has:
        //   ✓  id                 — set
        //   ✓  interArrivalTime   — set (accessible via getInterArrivalTime())
        //   ✓  serviceTime        — set (accessible via getServiceTime())
        //   ✗  arrivalTime        — 0.0 (populated in Phase 2)
        //   ✗  serviceStartTime   — 0.0  ''
        //   ✗  serviceEndTime     — 0.0  ''
        //   ✗  waitingTime        — 0.0  ''
        //   ✗  timeInSystem       — 0.0  ''

        customers = randomGenerator.generateCustomerBatch(customerCount);

        // Phase 2: looping the list & computing each customer's timeline
        //
        //   previousArrivalTime    = AT_{i-1}
        //   previousServiceEndTime = SET_{i-1}
        //
        // Before the first customer, both are 0:
        //   AT_0  = 0   (no customer has arrived yet)
        //   SET_0 = 0   (teller is free and waiting at the start of the day)

        double previousArrivalTime    = 0.0;
        double previousServiceEndTime = 0.0;

        for (Customer customer : customers) {

            // Read the randomly generated values Ivy already stored
            double iat = customer.getInterArrivalTime();  // IAT_i — gap since last arrival
            double st  = customer.getServiceTime();        // ST_i  — how long service takes

            // Arrival Time  (AT_i)
            //  Formula:  AT_i = AT_{i-1} + IAT_i
            //
            //  Each customer arrives exactly IAT_i minutes after the
            //  previous one. Arrival times are therefore strictly increasing.
            //
            //  For the first customer: AT_1 = 0 + IAT_1 = IAT_1
            //  (The first customer arrives IAT_1 minutes after the bank opens.)
            double arrivalTime = previousArrivalTime + iat;
            customer.setArrivalTime(arrivalTime);

            // Service Start Time  (SST_i)
            //  Formula:  SST_i = max(AT_i, SET_{i-1})
            double serviceStartTime = Math.max(arrivalTime, previousServiceEndTime);
            customer.setServiceStartTime(serviceStartTime);

            // Service End Time  (SET_i)
            //  Formula:  SET_i = SST_i + ST_i
            double serviceEndTime = serviceStartTime + st;
            customer.setServiceEndTime(serviceEndTime);

            // Waiting Time  (WT_i)
            //  Formula:  WT_i = SST_i - AT_i
            double waitingTime = serviceStartTime - arrivalTime;
            customer.setWaitingTime(waitingTime);

            // Total Time in System  (TS_i)
            //  Formula:  TS_i = SET_i - AT_i
            double timeInSystem = serviceEndTime - arrivalTime;
            customer.setTimeInSystem(timeInSystem);

            // Clock Advance
            //  Stores this customer's values so the NEXT iteration can
            //  reference them as AT_{i-1} and SET_{i-1}.
            // ----------------------------------------------------------
            previousArrivalTime    = arrivalTime;
            previousServiceEndTime = serviceEndTime;
        }

        hasRun = true;
        return customers;
    }

    // Public Accessors

    /**
     * Returns the complete, processed list of Customer records.
     *
     * Every customer in this list has all eight fields populated:
     *   ID, IAT, ST  →  set by Ivy's generateCustomerBatch()
     *   AT, SST, SET, WT, TS  →  set by this engine's runSimulation()
     *
     * @return  unmodifiable list of all Customer records in arrival order
     * @throws  IllegalStateException if runSimulation() has not been called yet
     */

    public List<Customer> getCustomers() {
        if (!hasRun) {
            throw new IllegalStateException(
                    "No results available yet. Call runSimulation() first."
            );
        }
        return Collections.unmodifiableList(customers);
    }

    /**
     * Returns the number of customers this engine was configured to simulate.
     */
    public int getCustomerCount() {
        return customerCount;
    }

    /**
     * Returns true if runSimulation() has already completed successfully.
     */
    public boolean hasRun() {
        return hasRun;
    }
}
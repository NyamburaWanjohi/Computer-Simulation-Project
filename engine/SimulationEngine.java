package engine;

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
 *    Obuya  →  List<Customer>   [for analytics]
 *    Bridget  →  List<Customer>   [for JTable display]
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
}
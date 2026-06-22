import java.util.List;

public class MainTest {
    public static void main(String[] args) {
        // Create an instance of RandomGenerator
        RandomGenerator generator = new RandomGenerator();

        // Generate a batch of customers (10 customers)
        List<Customer> testList = generator.generateCustomerBatch(10);

        // Print the generated customers' details
        System.out.println("--- STEP 1 GENERATION TEST ---");
        for (Customer c : testList) {
            System.out.printf("Customer ID: %d | IAT: %.2f mins | Service Time: %.2f mins%n", 
                c.getId(), c.getInterArrivalTime(), c.getServiceTime());
        }
    }
    
}

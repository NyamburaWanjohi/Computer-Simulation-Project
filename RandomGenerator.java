import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomGenerator {
   private Random rand; 

   public RandomGenerator() {
       this.rand = new Random();
   }

   public double getUniformRandom(double min, double max) {
        return min + (max - min) * rand.nextDouble();
    }
    public List<Customer> generateCustomerBatch(int totalCustomers) {
        List<Customer> customerList = new ArrayList<>();
        for (int i = 1; i <= totalCustomers; i++) {
            // Customer 1 establishes the starting clock, so their IAT is typically 0 or a base value.
            // But following strict distribution rules, we sample for everyone:
            double iat = getUniformRandom(1.0, 8.0);
            double st = getUniformRandom(1.0, 6.0);

            // Construct the customer object with an ID and our random times
            Customer customer = new Customer(i, iat, st);
            customerList.add(customer);
        }

        return customerList;
    }
}

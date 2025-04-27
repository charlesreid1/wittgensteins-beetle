import java.lang.Math;
import java.util.Scanner; // Import Scanner for user input

/**
 * Calculates the age difference between twins for the Twins Paradox
 * scenario (Chapter 20), based on Special Relativity's time dilation.
 * Takes the traveler's speed and experienced duration as input.
 */

public class Chapter20 {

    // Speed of light in m/s (approximate) - for context, though we use fractions of c
    static final double SPEED_OF_LIGHT_MPS = 299792458.0;
    // Days in a year (average)
    static final double DAYS_IN_YEAR = 365.25;

    public static void main(String[] args) {

        Scanner inputScanner = new Scanner(System.in);

        System.out.println("--- Twins Paradox Age Difference Calculator (Chapter 20) ---");

        // --- Get Inputs ---
        double travelerVelocityFraction = 0.0;
        while (travelerVelocityFraction <= 0 || travelerVelocityFraction >= 1.0) {
            System.out.print("Enter traveler's velocity as a fraction of speed of light (e.g., 0.9 for 90% c): ");
            if (inputScanner.hasNextDouble()) {
                 travelerVelocityFraction = inputScanner.nextDouble();
                 if (travelerVelocityFraction <= 0 || travelerVelocityFraction >= 1.0) {
                      System.out.println("Error: Velocity must be greater than 0 and less than 1.0.");
                 }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                inputScanner.next(); // Consume invalid input
            }
        }


        double travelerTimeDays = 0.0;
        while (travelerTimeDays <= 0) {
            System.out.print("Enter duration of trip experienced by traveler (in days): ");
             if (inputScanner.hasNextDouble()) {
                 travelerTimeDays = inputScanner.nextDouble();
                  if (travelerTimeDays <= 0) {
                      System.out.println("Error: Duration must be positive.");
                 }
            } else {
                 System.out.println("Invalid input. Please enter a number.");
                 inputScanner.next(); // Consume invalid input
            }
        }
        inputScanner.close(); // Close the scanner


        // --- Calculations ---

        // 1. Calculate Lorentz Factor (gamma)
        // gamma = 1 / sqrt(1 - (v^2 / c^2))
        double v_over_c_squared = Math.pow(travelerVelocityFraction, 2);
        double lorentzFactor = 1.0 / Math.sqrt(1.0 - v_over_c_squared);

        // 2. Calculate Time Elapsed on Earth
        // time_earth = time_traveler * gamma
        double earthTimeDays = travelerTimeDays * lorentzFactor;

        // 3. Convert times to years for comparison
        double travelerTimeYears = travelerTimeDays / DAYS_IN_YEAR;
        double earthTimeYears = earthTimeDays / DAYS_IN_YEAR;

        // 4. Calculate Age Difference
        double ageDifferenceYears = earthTimeYears - travelerTimeYears;

        // --- Output Results ---
        System.out.println("\n--- Results ---");
        System.out.printf("Traveler's Velocity: %.3f c %n", travelerVelocityFraction);
        System.out.printf("Lorentz Factor (gamma): %.4f %n", lorentzFactor);
        System.out.printf("Time Experienced by Traveler: %.2f days (%.3f years)%n", travelerTimeDays, travelerTimeYears);
        System.out.printf("Time Elapsed on Earth:        %.2f days (%.3f years)%n", earthTimeDays, earthTimeYears);
        System.out.printf("Age Difference upon Return:   %.3f years%n", ageDifferenceYears);

    } // End main method

}

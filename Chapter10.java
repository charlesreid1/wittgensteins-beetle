import java.lang.Math;

/**
 * Simulates the Poincare thought experiment (Chapter 10) of a "Jeometer"
 * on a gaseous planet where beings and tools shrink as they move away
 * from the hot center towards the cold edge (absolute zero).
 * Demonstrates how their perceived distance climbed can become infinite
 * even though the planet is finite.
 */
public class Chapter10 {

    // --- Planet Parameters ---
    static final double PLANET_RADIUS = 1000.0; // Arbitrary radius of the finite planet
    static final double CENTER_POS = 0.0;       // Position of the center

    /**
     * Calculates the scaling factor based on distance from the center.
     * Scale is 1.0 at the center and approaches 0.0 near the planet's radius.
     * Using a simple linear decrease for this simulation.
     * Added max(epsilon) to prevent true zero scale causing division issues later
     * if we needed to divide by scale.
     * @param distanceFromCenter Current actual distance from the planet's center.
     * @return The scaling factor (between ~0 and 1).
     */
    public static double calculateScale(double distanceFromCenter) {
        if (distanceFromCenter >= PLANET_RADIUS) {
            return 0.00001; // Effectively zero scale at/beyond the edge
        }
         // Linear scaling: scale = 1 - (distance / Radius)
         double scale = 1.0 - (distanceFromCenter / PLANET_RADIUS);
         return Math.max(0.00001, scale); // Prevent scale from being exactly zero or negative
    }

    public static void main(String[] args) {

        // --- Simulation Setup ---
        double actualDistanceFromCenter = 0.0; // Start at the center
        double perceivedDistanceClimbed = 0.0;
        double baseStepSize = 1.0; // The length of one ladder step *at the center*
        int maxSteps = 10_000;       // Maximum steps to simulate
        int reportInterval = 500;  // How often to print status

        System.out.println("--- Poincare's Shrinking Jeometer Simulation (Chapter 10) ---");
        System.out.printf("Planet Radius: %.1f%n", PLANET_RADIUS);
        System.out.printf("Base Step Size (at center): %.1f%n", baseStepSize);
        System.out.println("\nSimulating climb...");
        System.out.println("Step | Actual Dist from Center | Current Scale | Perceived Dist Climbed");
        System.out.println("---------------------------------------------------------------------");

        // Initial state print
        System.out.printf("%8d | %23.5f | %13.5f | %22.1f%n",
                          0, actualDistanceFromCenter, calculateScale(actualDistanceFromCenter), perceivedDistanceClimbed);


        // --- Simulation Loop ---
        for (int step = 1; step <= maxSteps; step++) {

            // 1. Calculate current scale based on actual position
            double currentScale = calculateScale(actualDistanceFromCenter);

            // 2. Calculate the *actual* distance this step covers
            // The "1 unit" ladder step is now scaled down
            double actualDistanceMoved = baseStepSize * currentScale;

            // 3. Update actual position
            actualDistanceFromCenter += actualDistanceMoved;

            // Safety check: prevent exceeding radius physically
            if (actualDistanceFromCenter >= PLANET_RADIUS) {
                actualDistanceFromCenter = PLANET_RADIUS - 0.00001; // Stay just inside
                currentScale = calculateScale(actualDistanceFromCenter); // Recalculate scale at edge
                 // We could break here, but let simulation continue to show perceived distance growing
            }


            // 4. Calculate the *perceived* distance climbed
            // The Jeometer measures with their equally shrunk "ruler" (the step size *relative* to them).
            // Perceived Step = Actual Distance Moved / Current Scale
            // perceivedStep = (baseStepSize * currentScale) / currentScale = baseStepSize
            // So, they always perceive they climb the 'baseStepSize'.
            perceivedDistanceClimbed += baseStepSize;


            // 5. Print status periodically
            if (step % reportInterval == 0 || step == maxSteps) {
                System.out.printf("%8d | %23.5f | %13.5f | %22.1f%n",
                                  step, actualDistanceFromCenter, currentScale, perceivedDistanceClimbed);
            }

             // Optional: Break if scale becomes extremely small
             if (currentScale < 0.0001 && actualDistanceFromCenter < PLANET_RADIUS - 0.001) {
                 // Print final state if breaking early
                 if (step % reportInterval != 0) {
                      System.out.printf("%8d | %23.5f | %13.5f | %22.1f%n",
                                  step, actualDistanceFromCenter, currentScale, perceivedDistanceClimbed);
                 }
                 // break; // Uncomment to stop early
             }

        } // End simulation loop

        System.out.println("---------------------------------------------------------------------");
        System.out.println("Simulation Complete.");
        System.out.printf("Final Actual Distance from Center: %.5f (out of %.1f)%n", actualDistanceFromCenter, PLANET_RADIUS);
        System.out.printf("Final Perceived Distance Climbed: %.1f%n", perceivedDistanceClimbed);

        // --- Verification ---
        System.out.println("\n--- Verification ---");
        if (actualDistanceFromCenter < PLANET_RADIUS && perceivedDistanceClimbed > PLANET_RADIUS * 3) { // Check if perceived >> actual radius
            System.out.println("Result matches Poincare's scenario:");
            System.out.println("- Actual distance approaches the finite radius.");
            System.out.println("- Perceived distance climbed grows much larger, appearing infinite.");
        } else if (actualDistanceFromCenter >= PLANET_RADIUS - 0.001) {
             System.out.println("Result partially matches: Actual distance reached (or got extremely close to) the finite radius.");
             System.out.println("Ensure perceived distance also grew significantly large for full effect.");
        }
        else {
            System.out.println("Result does not clearly match Poincare's scenario.");
            System.out.println("(Check parameters or simulation steps if actual distance didn't approach radius)");
        }

    } // End main method

} // End class PoincareJeometer

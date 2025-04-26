import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class simulates a simplified Maxwell's Demon scenario.
 * Attempts to sort particles based on energy between two chambers,
 * potentially creating a temperature difference and violating the
 * Second Law of Thermodynamics in this idealized model.
 */
// Ensure filename is Chapter04.java
public class Chapter04 {

    // Represents a simple particle with an energy level
    static class Particle {
        double energy;

        Particle(double energy) {
            this.energy = energy;
        }

        @Override
        public String toString() {
            return String.format("%.2f", energy);
        }
    } // End of inner Particle class

    // Instance variables for the simulation
    private List<Particle> chamberLeft = new ArrayList<>(); // Target state: "Cold"
    private List<Particle> chamberRight = new ArrayList<>(); // Target state: "Hot"
    private Random random = new Random();
    private int particleCount;
    private double overallInitialAvgEnergy; // Store the initial average

    // Constructor - Name MUST match class name above
    public Chapter04(int numParticles, double initialTemp) {
        this.particleCount = numParticles;
        // Initialize particles
        for (int i = 0; i < numParticles; i++) {
            double energy = Math.max(0.1, initialTemp + (random.nextDouble() - 0.5) * initialTemp * 0.8); // Ensure positive energy, add some variance
            // Start with random distribution
            if (random.nextBoolean()) {
                 chamberLeft.add(new Particle(energy));
            } else {
                 chamberRight.add(new Particle(energy));
            }
        }
         // Ensure initial distribution isn't completely empty on one side if possible
         if (chamberLeft.isEmpty() && !chamberRight.isEmpty()) {
              chamberLeft.add(chamberRight.remove(0));
         } else if (chamberRight.isEmpty() && !chamberLeft.isEmpty()) {
              chamberRight.add(chamberLeft.remove(0));
         }
         // Recalculate particle count based on actual distribution
         this.particleCount = chamberLeft.size() + chamberRight.size();
         this.overallInitialAvgEnergy = calculateCurrentTotalAverageEnergy(); // Store initial average


        System.out.println("Initial Setup:");
        System.out.printf("Overall Initial Avg Energy: %.3f%n", this.overallInitialAvgEnergy);
        printTemperatures();
    } // End of constructor

    // Calculates average energy (proxy for temperature) in a specific chamber
    private double calculateAverageEnergy(List<Particle> chamber) {
        if (chamber.isEmpty()) {
            return 0.0;
        }
        double totalEnergy = 0;
        for (Particle p : chamber) {
            totalEnergy += p.energy;
        }
        return totalEnergy / chamber.size();
    }

    // Calculates the current overall average energy across both chambers
    private double calculateCurrentTotalAverageEnergy() {
         int currentTotalCount = chamberLeft.size() + chamberRight.size();
         if (currentTotalCount == 0) return 0.0;

         double totalEnergyLeft = calculateAverageEnergy(chamberLeft) * chamberLeft.size();
         double totalEnergyRight = calculateAverageEnergy(chamberRight) * chamberRight.size();
         // Ensure total energy is conserved (or close due to potential floating point inaccuracies)
         // double totalEnergy = totalEnergyLeft + totalEnergyRight;
         return (totalEnergyLeft + totalEnergyRight) / currentTotalCount;
    }


    // The Demon's logic for attempting to sort particles
    private void demonAction() {
        boolean checkLeft = random.nextBoolean(); // Decide which chamber to potentially take a particle from
        List<Particle> sourceChamber = checkLeft ? chamberLeft : chamberRight;
        List<Particle> targetChamber = checkLeft ? chamberRight : chamberLeft;

        if (sourceChamber.isEmpty()) {
            return;
        }

        int particleIndex = random.nextInt(sourceChamber.size());
        Particle particle = sourceChamber.get(particleIndex);

        // Demon Rule: Move particle if it helps separate temperatures
        double avgLeft = calculateAverageEnergy(chamberLeft);
        double avgRight = calculateAverageEnergy(chamberRight);

        // Handle case where one chamber is empty to avoid nonsensical comparisons or division by zero
        boolean leftEmpty = chamberLeft.isEmpty();
        boolean rightEmpty = chamberRight.isEmpty();

        boolean shouldMove = false;
        if (checkLeft) { // Particle in Left (Cold Side Target) -> Consider moving to Right (Hot Side Target)
            // Move if particle is hotter than the average of the *source* (Left) chamber.
            if (!leftEmpty && particle.energy > avgLeft) {
                 shouldMove = true;
             }
        } else { // Particle in Right (Hot Side Target) -> Consider moving to Left (Cold Side Target)
            // Move if particle is colder than the average of the *source* (Right) chamber.
             if (!rightEmpty && particle.energy < avgRight) {
                 shouldMove = true;
             }
        }


        if (shouldMove) {
            targetChamber.add(sourceChamber.remove(particleIndex));
        }
    } // End of demonAction method

    // Run the simulation for a number of steps
    public void runSimulation(int steps) {
        System.out.println("\nRunning Simulation...");
        for (int i = 0; i < steps; i++) {
            int actionsPerStep = particleCount; // Let demon attempt many sorts per step
            for (int j = 0; j < actionsPerStep; j++) {
                demonAction();
            }

            // Print status periodically
            int reportInterval = steps / 20;
            if (reportInterval == 0) reportInterval = 1;
            if ((i + 1) % reportInterval == 0 || i == steps - 1) {
                System.out.printf("--- Step %d ---%n", i + 1);
                printTemperatures();
            }
        }
        System.out.println("\nSimulation Complete.");
    } // End of runSimulation method

    // Print current average energies (temperatures) and counts
    private void printTemperatures() {
        double tempLeft = calculateAverageEnergy(chamberLeft);
        double tempRight = calculateAverageEnergy(chamberRight);
        System.out.printf("Left Chamber (Count: %d, Avg Energy: %.3f) | Right Chamber (Count: %d, Avg Energy: %.3f)%n",
                chamberLeft.size(), tempLeft, // No need for NaN check if we handle empty lists in calculateAverageEnergy
                chamberRight.size(), tempRight);
    } // End of printTemperatures method


    // --- Verification Method ---
    public void verifyResult() {
        System.out.println("\n--- Verification ---");
        double finalTempLeft = calculateAverageEnergy(chamberLeft);
        double finalTempRight = calculateAverageEnergy(chamberRight);
        double finalOverallAverage = calculateCurrentTotalAverageEnergy();

        System.out.printf("Initial Overall Avg Energy: %.3f%n", this.overallInitialAvgEnergy);
        System.out.printf("Final Left Avg Energy:      %.3f%n", finalTempLeft);
        System.out.printf("Final Right Avg Energy:     %.3f%n", finalTempRight);
        System.out.printf("Final Overall Avg Energy:   %.3f%n", finalOverallAverage); // Should be close to initial

        // Check if the 'hot' side is indeed hotter than the 'cold' side
        // Allow for a small tolerance due to randomness & simulation limits
        double tolerance = this.overallInitialAvgEnergy * 0.01; // e.g., 1% tolerance

        if (finalTempRight > finalTempLeft + tolerance) {
            System.out.println("Verification PASSED: A temperature difference was created/maintained (Right > Left).");
            // Optional stricter check: Right is hotter AND Left is colder than initial average
             if (finalTempRight > overallInitialAvgEnergy + tolerance && finalTempLeft < overallInitialAvgEnergy - tolerance){
                 System.out.println("Verification Detail: Right chamber is hotter and Left chamber is colder than the initial average.");
             } else {
                  System.out.println("Verification Detail: Chambers have separated, but may not both have diverged significantly from initial average.");
             }
        } else if (Math.abs(finalTempRight - finalTempLeft) <= tolerance) {
             System.out.println("Verification RESULT: No significant temperature difference observed.");
        } else {
            System.out.println("Verification FAILED: The temperature difference is reversed (Left > Right).");
        }
    }


    // Main method to run the simulation and verification
    public static void main(String[] args) {
        int numberOfParticles = 1000;
        double initialTemperature = 50.0; // Represents average initial energy
        int simulationSteps = 100000; // Increased steps further for better sorting

        Chapter04 sim = new Chapter04(numberOfParticles, initialTemperature);
        sim.runSimulation(simulationSteps);
        sim.verifyResult(); // Call verification after simulation
    } // End of main method

} // End of class Chapter04

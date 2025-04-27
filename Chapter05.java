import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Collections;

/**
 * Simulates the spread of an advantageous trait ('F') in a population
 * initially dominated by a less advantageous trait ('S'), addressing
 * the core problem discussed in Chapter 5 (Evolution).
 * Models basic Mendelian inheritance, selection based on fitness,
 * and random mutation.
 */
public class Chapter05 {

    // Represents an individual with a simple genotype (two alleles)
    static class Individual {
        char allele1;
        char allele2;
        double fitness; // Represents survival/reproductive advantage

        // Constructor
        Individual(char a1, char a2) {
            this.allele1 = a1;
            this.allele2 = a2;
            this.fitness = calculateFitness(a1, a2);
        }

        // Determine fitness based on genotype (F is advantageous and dominant)
        private double calculateFitness(char a1, char a2) {
            // Example fitness values: F gives a 10% advantage
            final double fitnessAdvantage = 1.1;
            final double baselineFitness = 1.0;

            if (a1 == 'F' || a2 == 'F') { // Dominant F allele
                return fitnessAdvantage;
            } else { // SS genotype
                return baselineFitness;
            }
        }

        // Get one allele randomly for reproduction
        char getRandomAllele(Random rand) {
            return rand.nextBoolean() ? allele1 : allele2;
        }

        @Override
        public String toString() {
            return "" + allele1 + allele2 + " (Fit:" + String.format("%.1f", fitness) + ")";
        }
    } // End Individual class

    // Manages the population and simulation steps
    static class Population {
        List<Individual> individuals;
        Random random = new Random();
        double mutationRate;

        // Constructor to initialize the population
        Population(int initialSize, int initialMutants, double mutRate) {
            individuals = new ArrayList<>(initialSize);
            this.mutationRate = mutRate;

            // Add initial mutants (heterozygous SF)
            for (int i = 0; i < initialMutants; i++) {
                 // Start mutants as heterozygous SF for simplicity
                 if(random.nextBoolean()){
                      individuals.add(new Individual('S', 'F'));
                 } else {
                      individuals.add(new Individual('F', 'S'));
                 }
            }
            // Fill the rest with the baseline SS genotype
            for (int i = initialMutants; i < initialSize; i++) {
                individuals.add(new Individual('S', 'S'));
            }
        }

        // Calculate the frequency of the 'F' allele in the current population
        double getFAlleleFrequency() {
            if (individuals.isEmpty()) {
                return 0.0;
            }
            int fCount = 0;
            int totalAlleles = individuals.size() * 2;
            for (Individual ind : individuals) {
                if (ind.allele1 == 'F') fCount++;
                if (ind.allele2 == 'F') fCount++;
            }
            return (double) fCount / totalAlleles;
        }

        // Simulate one generation: selection + reproduction + mutation
        void simulateGeneration() {
            if (individuals.isEmpty()) return;

            // --- Selection Phase ---
            // Select individuals for reproduction based on fitness (Roulette Wheel)
            List<Individual> breedingPool = selectForBreeding();
            if (breedingPool.isEmpty()) {
                 // Population extinction or edge case
                 individuals.clear();
                 return;
            }


            // --- Reproduction Phase ---
            List<Individual> nextGeneration = new ArrayList<>(individuals.size());
            int currentPopSize = individuals.size(); // Maintain population size roughly

            for (int i = 0; i < currentPopSize; i++) {
                // Select two parents randomly from the breeding pool
                Individual parent1 = breedingPool.get(random.nextInt(breedingPool.size()));
                Individual parent2 = breedingPool.get(random.nextInt(breedingPool.size()));

                // Get one allele from each parent
                char alleleFrom1 = parent1.getRandomAllele(random);
                char alleleFrom2 = parent2.getRandomAllele(random);

                // --- Mutation Phase ---
                alleleFrom1 = mutate(alleleFrom1);
                alleleFrom2 = mutate(alleleFrom2);

                // Create offspring
                nextGeneration.add(new Individual(alleleFrom1, alleleFrom2));
            }

            // Replace old generation with the new one
            individuals = nextGeneration;
        }

        // Simple Roulette Wheel Selection
        private List<Individual> selectForBreeding() {
             List<Individual> breedingPool = new ArrayList<>();
             double totalFitness = 0;
             for(Individual ind : individuals) {
                  totalFitness += ind.fitness;
             }

             if (totalFitness <= 0) return breedingPool; // Avoid division by zero if all fitness is 0

             // Create relative fitness probabilities
             List<Double> cumulativeFitness = new ArrayList<>();
             double runningTotal = 0;
             for(Individual ind : individuals) {
                  runningTotal += ind.fitness / totalFitness;
                  cumulativeFitness.add(runningTotal);
             }

             // Spin the wheel populationSize times
             int popSize = individuals.size();
             for(int i = 0; i < popSize; i++){
                  double roll = random.nextDouble();
                  // Find which individual corresponds to the roll
                  for(int j=0; j < individuals.size(); j++){
                       if(roll < cumulativeFitness.get(j)){
                            breedingPool.add(individuals.get(j));
                            break;
                       }
                  }
             }
             // Shuffle pool to better simulate random mating from selected individuals
             Collections.shuffle(breedingPool);
             return breedingPool;
        }


        // Apply mutation based on rate
        private char mutate(char allele) {
            if (random.nextDouble() < mutationRate) {
                // Flip the allele
                return (allele == 'S') ? 'F' : 'S';
            }
            return allele; // No mutation
        }

         // Get current population size
         int getSize() {
              return individuals.size();
         }

    } // End Population class


    // Main simulation execution
    public static void main(String[] args) {
        // --- Simulation Parameters ---
        int populationSize = 500;      // Size of the population
        int initialMutants = 2;         // Start with 2 individuals carrying the 'F' allele
        double mutationRate = 0.001;    // 0.1% chance per allele transmission
        int generations = 500;         // How many generations to simulate
        int reportInterval = 10;       // How often to print the frequency

        // --- Setup ---
        Population population = new Population(populationSize, initialMutants, mutationRate);

        System.out.println("--- Population Genetics Simulation (Chapter 5) ---");
        System.out.printf("Parameters: Pop Size=%d, Initial Mutants=%d, Mutation Rate=%.4f, Generations=%d%n",
                          populationSize, initialMutants, mutationRate, generations);
        System.out.println("Tracking frequency of advantageous 'F' allele...");
        System.out.println("--------------------------------------------------");

        // --- Run Simulation ---
        System.out.printf("Gen %d: F Freq = %.4f (Pop Size: %d)%n", 0, population.getFAlleleFrequency(), population.getSize());

        for (int gen = 1; gen <= generations; gen++) {
            population.simulateGeneration();
            if (population.getSize() == 0) {
                 System.out.printf("Gen %d: Population Extinct.%n", gen);
                 break;
            }
            if (gen % reportInterval == 0 || gen == generations) {
                System.out.printf("Gen %d: F Freq = %.4f (Pop Size: %d)%n", gen, population.getFAlleleFrequency(), population.getSize());
            }
        }

        // --- Final Result ---
        System.out.println("--------------------------------------------------");
        double finalFrequency = population.getFAlleleFrequency();
        System.out.printf("Simulation Complete. Final 'F' Allele Frequency: %.4f%n", finalFrequency);

        // --- Verification ---
        if (finalFrequency > 0.9) {
             System.out.println("Verification: Advantageous allele 'F' became dominant.");
        } else if (finalFrequency < 0.01 && initialMutants > 0) {
             System.out.println("Verification: Advantageous allele 'F' likely died out (as Jenkins worried).");
        } else if (initialMutants == 0 && finalFrequency > 0.01) {
            System.out.println("Verification: Advantageous allele 'F' arose through mutation and spread.");
        }
         else {
             System.out.println("Verification: Advantageous allele 'F' is present but hasn't reached dominance or died out.");
        }
    } // End main method

} // End Chapter05 class

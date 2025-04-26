import java.lang.Math;

/**
 * This class calculates the one-way travel time for a hypothetical 
 * frictionless train moving through a straight tunnel inside a uniform 
 * spherical Earth, powered only by gravity. The calculation assumes 
 * Simple Harmonic Motion based on Earth's radius and surface gravity.
 */

public class GravityTrain {

    public static void main(String[] args) {

        // --- Constants ---
        // Gravitational acceleration at Earth's surface (m/s^2)
        final double g = 9.81; 
        // Average radius of the Earth (meters)
        final double R = 6371000.0; 

        // --- Assumptions ---
        // 1. Earth is a perfect sphere with uniform density.
        // 2. Tunnel is a straight line.
        // 3. Motion is frictionless (no air resistance).
        // 4. Earth is not rotating.
        // 5. Only gravity acts on the train.

        // --- Calculation ---
        // Calculate the period of oscillation (T = 2 * pi * sqrt(R/g))
        double periodSeconds = 2.0 * Math.PI * Math.sqrt(R / g);

        // Travel time is half the period
        double travelTimeSeconds = periodSeconds / 2.0;

        // Convert travel time to minutes
        double travelTimeMinutes = travelTimeSeconds / 60.0;

        // --- Output ---
        System.out.println("Calculating travel time for a gravity train through a uniform Earth...");
        System.out.printf("Earth Radius (R): %.0f meters%n", R);
        System.out.printf("Surface Gravity (g): %.2f m/s^2%n", g);
        System.out.printf("Calculated travel time: %.2f seconds%n", travelTimeSeconds);
        System.out.printf("Calculated travel time: %.2f minutes%n", travelTimeMinutes);
    }
}

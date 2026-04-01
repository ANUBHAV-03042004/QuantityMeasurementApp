package com.quantitymeasurementapp;


public class QuantityMeasurementApp {

    public static class Feet {

        
        private final double value;

        public Feet(double value) {
            this.value = value;
        }

       
        @Override
        public boolean equals(Object obj) {

            if (this == obj) return true;

             if (obj == null) return false;

           
            if (this.getClass() != obj.getClass()) return false;

           
            Feet other = (Feet) obj;

            return Double.compare(this.value, other.value) == 0;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(this.value);
        }

        @Override
        public String toString() {
            return "Feet{value=" + value + "}";
        }
    }

    public static void main(String[] args) {

     
        Feet feet1 = new Feet(1.0);
        Feet feet2 = new Feet(1.0);
       
        System.out.println("1.0 ft == 1.0 ft  → " + feet1.equals(feet2)); 

        // Test Case 2: Different values
        Feet feet3 = new Feet(2.0);
        System.out.println("1.0 ft == 2.0 ft  → " + feet1.equals(feet3)); 
        // Test Case 3: Null comparison
        System.out.println("1.0 ft == null    → " + feet1.equals(null));  

        // Test Case 4: Same reference
        System.out.println("feet1 == feet1    → " + feet1.equals(feet1)); 

        // Test Case 5: Different type
        System.out.println("1.0 ft == 'abc'   → " + feet1.equals("abc"));
    }
}
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
    
    public static class Inches {

        private final double value;

  
        public Inches(double value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
        
            if (this == obj) return true;

        
            if (obj == null) return false;

          
            if (this.getClass() != obj.getClass()) return false;

        
            Inches other = (Inches) obj;
            return Double.compare(this.value, other.value) == 0;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(this.value);
        }

        @Override
        public String toString() {
            return "Inches{value=" + value + "}";
        }
    }
    
    public static boolean compareFeet(double value1, double value2) {
        Feet feet1 = new Feet(value1);
        Feet feet2 = new Feet(value2);
        return feet1.equals(feet2);
    }

    public static boolean compareInches(double value1, double value2) {
        Inches inches1 = new Inches(value1);
        Inches inches2 = new Inches(value2);
        return inches1.equals(inches2);
    }

    public static void main(String[] args) {

     
    	System.out.println("1.0 ft == 1.0 ft  → " + compareFeet(1.0, 1.0));   // true
        System.out.println("1.0 ft == 2.0 ft  → " + compareFeet(1.0, 2.0));   // false

        System.out.println("1.0 in == 1.0 in  → " + compareInches(1.0, 1.0)); // true
        System.out.println("1.0 in == 2.0 in  → " + compareInches(1.0, 2.0)); // false

        Feet   feetObj   = new Feet(1.0);
        Inches inchesObj = new Inches(1.0);

        System.out.println("1.0 ft  == null   → " + feetObj.equals(null));        // false
        System.out.println("1.0 in  == null   → " + inchesObj.equals(null));      // false
        System.out.println("1.0 ft  == feetObj (self)   → " + feetObj.equals(feetObj));   // true
        System.out.println("1.0 in  == inchObj (self)   → " + inchesObj.equals(inchesObj)); // true
        System.out.println("1.0 ft  == 1.0 in (cross)  → " + feetObj.equals(inchesObj)); // false (different types)

    }
}
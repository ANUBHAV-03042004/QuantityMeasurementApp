package com.quantitymeasurementapp;
import com.quantitymeasurementapp.Quantity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QuantityMeasurementAppTest {

    private static final double EPSILON = 1e-4;


    @Test
    void testEquality_LitreToLitre_SameValue() {
        Quantity<VolumeUnit> v1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 = new Quantity<>(1.0, VolumeUnit.LITRE);
        assertEquals(v1, v2);
    }

    @Test
    void testEquality_LitreToMillilitre_EquivalentValue() {
        Quantity<VolumeUnit> litre = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> ml = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        assertEquals(litre, ml);
    }

    @Test
    void testEquality_LitreToGallon_EquivalentValue() {
        Quantity<VolumeUnit> litre = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> gallon =
                new Quantity<>(0.264172, VolumeUnit.GALLON);
        assertEquals(litre, gallon);
    }

    @Test
    void testEquality_VolumeVsLength_Incompatible() {
        Quantity<VolumeUnit> volume =
                new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<LengthUnit> length =
                new Quantity<>(1.0, LengthUnit.FEET);

        assertNotEquals(volume, length);
    }

    @Test
    void testEquality_ZeroValue() {
        Quantity<VolumeUnit> v1 =
                new Quantity<>(0.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 =
                new Quantity<>(0.0, VolumeUnit.MILLILITRE);

        assertEquals(v1, v2);
    }

   
    @Test
    void testConversion_LitreToMillilitre() {
        Quantity<VolumeUnit> litre =
                new Quantity<>(1.0, VolumeUnit.LITRE);

        Quantity<VolumeUnit> result =
                litre.convertTo(VolumeUnit.MILLILITRE);

        assertEquals(1000.0, result.getValue(), EPSILON);
    }

    @Test
    void testConversion_GallonToLitre() {
        Quantity<VolumeUnit> gallon =
                new Quantity<>(1.0, VolumeUnit.GALLON);

        Quantity<VolumeUnit> result =
                gallon.convertTo(VolumeUnit.LITRE);

        assertEquals(3.78541, result.getValue(), EPSILON);
    }

    @Test
    void testConversion_RoundTrip() {
        Quantity<VolumeUnit> original =
                new Quantity<>(1.5, VolumeUnit.LITRE);

        Quantity<VolumeUnit> converted =
                original.convertTo(VolumeUnit.MILLILITRE)
                        .convertTo(VolumeUnit.LITRE);

        assertEquals(original.getValue(),
                converted.getValue(), EPSILON);
    }

        @Test
        void testSubtraction_SameUnit_FeetMinusFeet() {

            Quantity<LengthUnit> l1 =
                    new Quantity<>(10.0, LengthUnit.FEET);

            Quantity<LengthUnit> l2 =
                    new Quantity<>(5.0, LengthUnit.FEET);

            Quantity<LengthUnit> result = l1.subQuantity(l2);

            assertEquals(new Quantity<>(5.0, LengthUnit.FEET), result);
        }


        @Test
        void testSubtraction_CrossUnit_FeetMinusInches() {

            Quantity<LengthUnit> l1 =
                    new Quantity<>(10.0, LengthUnit.FEET);

            Quantity<LengthUnit> l2 =
                    new Quantity<>(6.0, LengthUnit.INCHES);

            Quantity<LengthUnit> result = l1.subQuantity(l2);

            assertEquals(new Quantity<>(9.5, LengthUnit.FEET), result);
        }


        @Test
        void testSubtraction_ResultingInZero() {

            Quantity<LengthUnit> l1 =
                    new Quantity<>(10.0, LengthUnit.FEET);

            Quantity<LengthUnit> l2 =
                    new Quantity<>(120.0, LengthUnit.INCHES);

            Quantity<LengthUnit> result = l1.subQuantity(l2);

            assertEquals(new Quantity<>(0.0, LengthUnit.FEET), result);
        }
        
        
        @Test
        void testDivision_SameUnit_FeetDividedByFeet() {

            Quantity<LengthUnit> l1 =
                    new Quantity<>(10.0, LengthUnit.FEET);

            Quantity<LengthUnit> l2 =
                    new Quantity<>(2.0, LengthUnit.FEET);

            Quantity<LengthUnit> result = l1.divQuantity(l2);

            assertEquals(new Quantity<>(5.0, LengthUnit.FEET), result);
        }


        @Test
        void testDivision_CrossUnit_FeetDividedByInches() {

            Quantity<LengthUnit> l1 =
                    new Quantity<>(24.0, LengthUnit.INCHES);

            Quantity<LengthUnit> l2 =
                    new Quantity<>(2.0, LengthUnit.FEET);

            Quantity<LengthUnit> result = l1.divQuantity(l2);

            assertEquals(new Quantity<>(1.0, LengthUnit.INCHES), result);
        }


        @Test
        void testDivision_RatioLessThanOne() {

            Quantity<LengthUnit> l1 =
                    new Quantity<>(5.0, LengthUnit.FEET);

            Quantity<LengthUnit> l2 =
                    new Quantity<>(10.0, LengthUnit.FEET);

            Quantity<LengthUnit> result = l1.divQuantity(l2);

            assertEquals(new Quantity<>(0.5, LengthUnit.FEET), result);
        }
        
        @Test
        void testSubtraction_NullOperand() {

            Quantity<LengthUnit> l1 =
                    new Quantity<>(10.0, LengthUnit.FEET);

            assertThrows(IllegalArgumentException.class,
                    () -> l1.subQuantity(null));
        }


        @Test
        void testDivision_NullOperand() {

            Quantity<LengthUnit> l1 =
                    new Quantity<>(10.0, LengthUnit.FEET);

            assertThrows(IllegalArgumentException.class,
                    () -> l1.divQuantity(null));
        }
    }
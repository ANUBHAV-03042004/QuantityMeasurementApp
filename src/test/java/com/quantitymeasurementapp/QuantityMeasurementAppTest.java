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
    void testAddition_SameUnit_LitrePlusLitre() {
        Quantity<VolumeUnit> v1 =
                new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 =
                new Quantity<>(2.0, VolumeUnit.LITRE);

        Quantity<VolumeUnit> result =
                v1.addQuantity(v2, VolumeUnit.LITRE);

        assertEquals(3.0, result.getValue(), EPSILON);
    }

    @Test
    void testAddition_CrossUnit_LitrePlusMillilitre() {
        Quantity<VolumeUnit> litre =
                new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> ml =
                new Quantity<>(1000.0, VolumeUnit.MILLILITRE);

        Quantity<VolumeUnit> result =
                litre.addQuantity(ml, VolumeUnit.LITRE);

        assertEquals(2.0, result.getValue(), EPSILON);
    }

    @Test
    void testAddition_ExplicitTargetUnit_Gallon() {
        Quantity<VolumeUnit> v1 =
                new Quantity<>(3.78541, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 =
                new Quantity<>(3.78541, VolumeUnit.LITRE);

        Quantity<VolumeUnit> result =
                v1.addQuantity(v2, VolumeUnit.GALLON);

        assertEquals(2.0, result.getValue(), EPSILON);
    }

    @Test
    void testAddition_WithZero() {
        Quantity<VolumeUnit> v1 =
                new Quantity<>(5.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> v2 =
                new Quantity<>(0.0, VolumeUnit.MILLILITRE);

        Quantity<VolumeUnit> result =
                v1.addQuantity(v2, VolumeUnit.LITRE);

        assertEquals(5.0, result.getValue(), EPSILON);
    }

    @Test
    void testVolumeUnitEnum_LitreConstant() {
        assertEquals(1.0,
                VolumeUnit.LITRE.getConversionFactor());
    }

    @Test
    void testVolumeUnitEnum_MillilitreConstant() {
        assertEquals(0.001,
                VolumeUnit.MILLILITRE.getConversionFactor());
    }

    @Test
    void testVolumeUnitEnum_GallonConstant() {
        assertEquals(3.78541,
                VolumeUnit.GALLON.getConversionFactor());
    }

    @Test
    void testConvertToBaseUnit_MillilitreToLitre() {
        double result =
                VolumeUnit.MILLILITRE.convertToBaseUnit(1000.0);

        assertEquals(1.0, result, EPSILON);
    }

    @Test
    void testConvertFromBaseUnit_LitreToGallon() {
        double result =
                VolumeUnit.GALLON.convertFromBaseUnit(3.78541);

        assertEquals(1.0, result, EPSILON);
    }
}
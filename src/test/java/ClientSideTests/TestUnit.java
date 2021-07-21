package ClientSideTests;

import ClientSide.Asset;
import ClientSide.Exceptions.IllegalString;
import ClientSide.Exceptions.InvalidAmount;
import ClientSide.Unit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The following tests are used to test the functionality of the Unit class.
 * @author Scott Peachey
 */

public class TestUnit {

    HashMap<Asset, Integer> mockAssets = new HashMap<>();
    Unit mockUnit;
    Asset mockAsset1;
    Asset mockAsset2;
    Asset mockAsset3;

    // ============== BEFORE EACH TEST ==============
    /**
     * Before each test, create a unit with mock a asset
     * @throws IllegalString if unit name is not valid
     * @throws InvalidAmount if unit credits are negative
     */
    @BeforeEach
    @DisplayName("Before each... create new unit and assets. Assign one asset to the unit.")
    public void before() throws InvalidAmount, IllegalString {
        mockAsset1 = new Asset(111, "Test asset 1");
        mockAsset2 = new Asset(222, "Test asset 2");
        mockAsset3 = new Asset(222, "Test asset 2");
        mockAssets.put(mockAsset1, 10);
        mockUnit = new Unit("mockUnit", 100, mockAssets);
    }

    // ============== AFTER EACH TEST ==============
    /**
     * After each test, reset the mock unit and asset
     */
    @AfterEach
    @DisplayName("After each... delete mock unit and asset, clear assets HashMap")
    public void clearMockObjects() {
        mockUnit = null;
        mockAsset1 = null;
        mockAssets.clear();

    }

    // ============== TESTS ==============
    /**
     * Test that units are constructed correctly
     */
    @Test
    @DisplayName("Units constructor assigns the data correctly")
    public void testUnitConstructor() {
        assertAll(
                // the unit name is assigned correctly (lowercase)
                () -> assertEquals("mockunit", mockUnit.getName()),
                // the unit's credits are assigned correctly
                () -> assertEquals(100, mockUnit.getCredits()),
                // the unit's assets are assigned correctly
                () -> assertEquals(mockAssets, mockUnit.getAssets())
        );
    }

    /**
     * Test the methods for getting and setting the unit's assets
     */
    @Test
    public void getAndSetAssets() {
        mockUnit.addAssets(mockAsset2, 10); // adds asset to unit
        mockUnit.adjustAsset(mockAsset1, 20); // increases asset quantity within the unit by addQty
        mockUnit.adjustAsset(mockAsset3, 10); // adds asset to unit if it does not exist already
        assertAll(
                // the unit's asset objects are correct
                () -> assertEquals(mockAssets, mockUnit.getAssets()),
                // the unit's asset names are correct
                () -> assertEquals(mockAssets.keySet(), mockUnit.getAssetNames()),
                // the quantity of an asset has been increased correctly
                () -> assertEquals(30, mockUnit.getAssets().get(mockAsset1))
        );
    }

    /**
     * Test the methods for getting and setting the unit's credits
     * @throws InvalidAmount Throw an exception if the credits are not a valid amount
     */
    @Test
    public void getAndSetCredits() throws InvalidAmount {
        // set a new amount for the unit's credits
        mockUnit.setCredits(200);
        // the unit's credits have been adjusted correctly
        assertEquals(200, mockUnit.getCredits());
        // increase the amount of credits by 250
        mockUnit.adjustBalance(250); //
        // the unit's credits have been increased correctly
        assertEquals(450, mockUnit.getCredits());
    }

    /**
     * Test the method for changing the unit's name
     */
    @Test
    public void setUnitName() {
        // change the unit's name
        mockUnit.setName("newName");
        // the unit's name has been changed correctly
        assertEquals("newName", mockUnit.getName());
    }

    /**
     * Make sure InvalidAmount exception is thrown
     */
    @Test
    public void invalidAdjustBalance() {
        // check that the unit's credits dont go below zero
        assertThrows(InvalidAmount.class, () ->
                mockUnit.adjustBalance(-(mockUnit.getCredits() + 1)));
    }

    /**
     * Make sure IllegalString and InvalidAmount exceptions are thrown
     */
    @Test
    public void invalidConstruction() {
        assertAll(
                // unit name must not include numbers
                () -> assertThrows(IllegalString.class, () ->
                        mockUnit = new Unit("unit1", 10, mockAssets)),
                // unit name must not include spaces
                () -> assertThrows(IllegalString.class, () ->
                        mockUnit = new Unit("a unit", 10, mockAssets)),
                // unit name must not include special characters
                () -> assertThrows(IllegalString.class, () ->
                        mockUnit = new Unit("unit!", 10, mockAssets))
        );
    }
}

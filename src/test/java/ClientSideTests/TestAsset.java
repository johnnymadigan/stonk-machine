package ClientSideTests;

import ClientSide.Asset;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The following tests are used to test the functionality of the Assets class.
 *
 * The functionality tested is simply the constructor of the class, as the class
 * has no other methods. The class also throws no exceptions.
 *
 * @author Scott Peachey
 */
public class TestAsset {

    /**
     * Testing the Asset constructor works via the getters
     */
    @Test
    public void testAssetConstructor() {
        // Create mock asset
        Asset mockAsset = new Asset(123, "This is an asset.");

        assertAll(
                // The asset ID is assigned correctly
                () -> assertEquals(123, mockAsset.getId()),
                // The asset description is assigned correctly
                () -> assertEquals("This is an asset.", mockAsset.getDescription()),
                // The asset ID is converted to a strign correctly
                () -> assertEquals("123", mockAsset.getIdString())
                );
    }
}

package zoom;

import javafx.geometry.BoundingBox;
import org.junit.jupiter.api.Test;
import tde.maps.zoom.Layer;
import tde.maps.zoom.Pyramid;
import tde.maps.zoom.Tile;
import tde.model.Address;
import tde.model.Coordinates;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZoomPyramidTest {
    @Test
    void testLayerHasCorrectNumberOfTiles() {
        var bounds = new BoundingBox(0, 0, 100, 100);
        var pyramid = new Pyramid(4, bounds);

        var layer2 = pyramid.getLayers().get(2);
        assertEquals(4, layer2.tiles.length);
        assertEquals(4, layer2.tiles[0].length);
    }

    private Address createAddress(double east, double north) {
        return new Address(
                new Coordinates(east, north, 0),
                LocalDate.now().minusDays(1),
                "1",           // number
                true,          // official
                "status",      // status
                "category",    // category
                "buildingName",// buildingName
                "ZH",          // canton
                "Zürich",      // communityName
                "Strasse",     // streetName
                "8000 Zürich"  // zipLabel
        );
    }

    @Test
    void testLowestLayerContainsAllAddresses() {

        var bounds = new BoundingBox(0, 0, 100, 100);
        var pyramid = new Pyramid(4, bounds);

        List<Address> addresses = List.of(
                createAddress(10, 10),
                createAddress(50, 50),
                createAddress(90, 90)
        );

        pyramid.initializeTiles(addresses);

        Layer lowestLayer = pyramid.getLayers().get(3);
        int total = 0;
        for (Tile[] row : lowestLayer.tiles) {
            for (Tile tile : row) {
                total += tile.getAddresses().size();
            }
        }

        assertEquals(3, total);
    }
}

package zoom;

import javafx.geometry.BoundingBox;
import org.junit.jupiter.api.Test;
import tde.maps.zoom.Pyramid;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZoomPyramidTest {
    @Test
    void testLayerHasCorrectNumberOfTiles() {
        var bounds = new BoundingBox(0, 0, 100, 100);
        var pyramid = new Pyramid(4, bounds);

        var layer2 = pyramid.layers.get(2);
        assertEquals(4, layer2.tiles.length);
        assertEquals(4, layer2.tiles[0].length);
    }
}

package tde.maps.zoom;

import javafx.geometry.BoundingBox;
import tde.model.Address;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pyramid {
    private static final int CAPACITY = 25000;
    private List<Layer> layers = new ArrayList<>();
    private BoundingBox boundingBox;

    public Pyramid(int depth, BoundingBox bounds) {
        for (int level = 0; level < depth; level++) {
            var l = new Layer(level, CAPACITY,  bounds);
            layers.add(l);
        }
        this.boundingBox = bounds;
    }


    public void initializeTiles(List<Address> addresses) {
        var depth = layers.size();
        // lowest level
        Layer currentLayer = layers.get(depth - 1);
        var w = currentLayer.getTileWidth();
        var h = currentLayer.getTileHeight();

        int maxIndex = currentLayer.getLength() - 1;
        addresses.forEach(address -> {
            int x = Math.min((int) Math.floor((address.location().east() - boundingBox.getMinX()) / w), maxIndex);
            int y = Math.min((int) Math.floor((address.location().north() - boundingBox.getMinY()) / h), maxIndex);
            currentLayer.getTile(x, y).addAddress(address);
        });

        initilizeUpperLayers(addresses); // parameter really needed?
    }

    private void initilizeUpperLayers(List<Address> addresses) {
        // distill addresses for upper levels
        // start at 2nd lowest level: layers.size() - 2

        for (int layer = layers.size() - 2; layer >= 0; layer--) {
            var currentLayer = layers.get(layer);
            var len = currentLayer.getLength();  // size of tile matrix at this layer

            // visit all tiles in this layer
            for (int x = 0; x < len; x++) {
                for (int y = 0; y < len; y++) {
                    var fillingTile = currentLayer.getTile(x,y);
                    var allAddresses = new ArrayList<Address>();

                    // visit all four of the tiles beneath current tile
                    for (int i = 0; i < 2; i++) {
                        for (int j = 0; j < 2; j++) {
                            var beneathLayer = layers.get(layer + 1);
                            var beaneathTile = beneathLayer.getTile(2*x + i, 2*y + j);
                            allAddresses.addAll(beaneathTile.getAddresses());
                        }
                    }
                    Collections.shuffle(allAddresses);
                    fillingTile.addAddresses(allAddresses.stream().limit(CAPACITY).toList());

                }
            }
        }
    }

    public List<Layer> getLayers() {
        return layers;
    }
}

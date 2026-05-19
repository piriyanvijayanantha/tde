package tde.maps.zoom;

import javafx.geometry.BoundingBox;

import java.util.ArrayList;
import java.util.List;

public class Pyramid {
    private static final int CAPACITY = 20000;
    public List<Layer> layers = new ArrayList<>();

    public Pyramid(int depth, BoundingBox bounds) {
        for (int level = 0; level < depth; level++) {
            var l = new Layer(level, CAPACITY,  bounds);
            layers.add(l);
        }
    }
}

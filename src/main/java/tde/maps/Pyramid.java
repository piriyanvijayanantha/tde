package tde.maps;

import javafx.geometry.BoundingBox;

import java.util.ArrayList;
import java.util.List;

public class Pyramid {
    List<Layer> layers = new ArrayList<>();

    Pyramid(int depth, BoundingBox bounds) {
        for (int level = 0; level < depth; level++) {
            var l = new Layer(level, bounds);
            layers.add(l);
        }
    }
}

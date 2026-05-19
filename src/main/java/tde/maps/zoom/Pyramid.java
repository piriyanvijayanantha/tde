package tde.maps.zoom;

public class Pyramide {
    List<Layer> layers = new ArrayList<>();

    Pyramid(int depth, BoundingBox bounds) {
        for (int level = 0; level < depth; level++) {
            var l = new Layer(level, bounds);
            layers.add(l);
        }
    }
}

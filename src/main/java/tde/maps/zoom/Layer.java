package tde.maps.zoom;

import javafx.geometry.BoundingBox;

public class Layer {
    public Tile[][] tiles;
    int length;
    BoundingBox bounds;

    Layer(int level, int capacity, BoundingBox bounds) {
        length = (int) Math.pow(2, level);
        tiles = new Tile[length][length];

        this.bounds = bounds;
        var x = bounds.getMinX();
        var y = bounds.getMinY();
        var w = bounds.getWidth() / length;
        var h = bounds.getHeight() / length;

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                tiles[i][j] = new Tile(capacity,
                        x + i * w,
                        y + j * h,
                        w,
                        h);
            }
        }
    }
}

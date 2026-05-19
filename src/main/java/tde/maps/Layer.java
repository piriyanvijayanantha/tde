package tde.maps;

import javafx.geometry.BoundingBox;

public class Layer {

    Tile[][] tiles;
    int length;
    BoundingBox bounds;

    Layer(int level, BoundingBox bounds) {
        length = 2 ^ level;
        tiles = new Tile[length][length];

        this.bounds = bounds;
        var x = bounds.getCenterX();
        var y = bounds.getCenterY();
        var w = bounds.getWidth() / length;
        var h = bounds.getHeight() / length;

        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; i++) {
                tiles[i][j] = new Tile(
                        level,
                        i,
                        j,
                        x + x * w,
                        y + y * h,
                        w,
                        h
                );
            }
        }
    }
}

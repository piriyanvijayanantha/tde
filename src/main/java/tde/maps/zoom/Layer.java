package tde.maps.zoom;

import javafx.geometry.BoundingBox;

import java.util.Arrays;

public class Layer {
    public Tile[][] tiles;
    int length;
    BoundingBox bounds;
    double w;
    double h;

    Layer(int level, int capacity, BoundingBox bounds) {
        length = (int) Math.pow(2, level);
        tiles = new Tile[length][length];

        this.bounds = bounds;
        var x = bounds.getMinX();
        var y = bounds.getMinY();
         w = bounds.getWidth() / length;
         h = bounds.getHeight() / length;

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

    public double getTileWidth() {
        return w;
    }

    public double getTileHeight() {
        return  h;
    }

    public Tile getTile(int x, int y) {
        return tiles[x][y];
    }

    public int getLength() {
        return tiles.length;
    }
}

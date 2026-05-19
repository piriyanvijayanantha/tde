package tde.maps;

import tde.model.Address;

import java.util.ArrayList;
import java.util.List;

public class Tile {
    private static final int CAPACITY = 20000;

    private final int layer;       // Ebene in der Pyramide
    private final int col;         // Spaltenindex (x-Richtung)
    private final int row;         // Zeilenindex  (y-Richtung)
    private final double x;        // LV95 linke untere Ecke
    private final double y;
    private final double w;
    private final double h;
    private final List<Address> addresses = new ArrayList<>();


    Tile(int layer, int col, int row, double x, double y, double w, double h) {
        this.layer = layer;
        this.col   = col;
        this.row   = row;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    void addAddress(Address a)       { addresses.add(a); }
    List<Address> getAddresses()     { return addresses; }

    double getX()      { return x; }
    double getWidth()  { return w; }
    double getHeight() { return h; }
    int getLayer()     { return layer; }
    int getCol()       { return col; }
    int getRow()       { return row; }

    boolean contains(double east, double north) {
        return east >= x && east < x + w && north >= y && north < y + h;
    }
}

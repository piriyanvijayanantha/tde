package tde.maps.zoom;

import javafx.geometry.BoundingBox;
import tde.model.Address;

import java.util.ArrayList;
import java.util.List;

public class Tile {
    List<Address> addresses;
    BoundingBox bounds;

    Tile(int capacity, double east, double north, double width, double height) {
        addresses = new ArrayList<>(capacity);
        bounds = new BoundingBox(east, north, width, height);
    }

    BoundingBox getBounds() {
        return bounds;
    }

    public void addAddress(Address address) {
        addresses.add(address);
    }
    public void addAddresses(List<Address> newAddresses) {
        addresses.addAll(newAddresses);
    }

    public List<Address> getAddresses() {
        return addresses;
    }
}

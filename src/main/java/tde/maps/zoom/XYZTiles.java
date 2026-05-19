package tde.maps.zoom;

import javafx.geometry.BoundingBox;
import tde.model.Address;

import java.util.List;

import static java.lang.Math.ceil;
import static java.lang.Math.log;

public class XYZTiles<T extends Address> implements ZoomStrategy<T>{
    private static final int depth = 4;

    @Override
    public List<T> getZoomedObjects(List<T> objects) {
        var nofAddresses = objects.size();  // addresses contains all our addresses
        var capacity = 20_000;                // from observing zoom speed of LI buildings.
        int depth = (int) ceil(((log((double) nofAddresses /capacity)) / log(2))/2); // our formula for the depth of pyramid

        double minX = objects.stream().mapToDouble(a -> a.location().east()).min().orElseThrow();
        double maxX = objects.stream().mapToDouble(a -> a.location().east()).max().getAsDouble();
        double minY = objects.stream().mapToDouble(a -> a.location().north()).min().getAsDouble();
        double maxY = objects.stream().mapToDouble(a -> a.location().north()).max().getAsDouble();

        var bounds = new BoundingBox(minX, minY, maxX - minX, maxY - minY);
        Pyramid pyramid = new Pyramid(depth, bounds);
        return null;
    }
}

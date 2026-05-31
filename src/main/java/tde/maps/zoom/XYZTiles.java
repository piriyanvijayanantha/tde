package tde.maps.zoom;

import javafx.geometry.BoundingBox;
import tde.model.Address;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.Math.ceil;
import static java.lang.Math.log;

public class XYZTiles<T extends Address> implements ZoomStrategy<T> {
    private static final int depth = 4;
    private Pyramid pyramid;
    private List<T> objects;
    private BoundingBox pyramidBounds;

    public XYZTiles(List<T> objects) {
        this.objects = objects;
            int depth = 4;// our formula for the depth of pyramid

        double minX = objects.stream().mapToDouble(a -> a.location().east()).min().orElseThrow();
        double maxX = objects.stream().mapToDouble(a -> a.location().east()).max().getAsDouble();
        double minY = objects.stream().mapToDouble(a -> a.location().north()).min().getAsDouble();
        double maxY = objects.stream().mapToDouble(a -> a.location().north()).max().getAsDouble();

        pyramidBounds = new BoundingBox(minX, minY, maxX - minX, maxY - minY);

        pyramid = new Pyramid(depth, pyramidBounds);

        pyramid.initializeTiles((List<Address>) this.objects);
    }

    @Override
    public List<T> getZoomedObjects(BoundingBox boundingBox) {

        var layers = pyramid.getLayers();
        var zoomLayer = layers.getFirst();
        for (int i = 1; i < layers.size(); i++) {
            if (boundingBox.getWidth() < layers.get(i).getTileWidth()) {
                zoomLayer = layers.get(i - 1);
            }
        }
        Set<Tile> tilesToDraw = new HashSet<>();

        double[] xs = {boundingBox.getMinX(), boundingBox.getMaxX()};
        double[] ys = {boundingBox.getMinY(), boundingBox.getMaxY()};

        for (double px : xs) {
            for (double py : ys) {
                int x = (int) Math.max(0, Math.min(zoomLayer.getLength() - 1,
                        Math.floor((px - pyramidBounds.getMinX()) / zoomLayer.getTileWidth())));
                int y = (int) Math.max(0, Math.min(zoomLayer.getLength() - 1,
                        Math.floor((py - pyramidBounds.getMinY()) / zoomLayer.getTileHeight())));
                tilesToDraw.add(zoomLayer.getTile(x, y));
            }
        }

        return (List<T>) tilesToDraw.stream().flatMap(t -> t.addresses.stream()).toList();
    }


}

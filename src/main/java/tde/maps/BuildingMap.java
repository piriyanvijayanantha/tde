package tde.maps;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import tde.maps.zoom.XYZTiles;
import tde.maps.zoom.ZoomStrategy;
import tde.model.Address;

import java.util.List;

// PRÜFUNG: OCP + Generics – BuildingMap<T extends Address> erweitert AbstractMap ohne Änderung der Basis
// PRÜFUNG: Exception-Wrapping – NonInvertibleTransformException (checked) wird in draw() zu RuntimeException (unchecked)
public class BuildingMap<T extends Address> extends AbstractMap<T> {
    List<T> addresses;
    private final Pane pane;
    private final ZoomStrategy<T> zoomStrategy;


    public BuildingMap(String name, List<T> addresses, Pane pane) {
        setName(name);
        this.addresses = addresses;
        zoomStrategy = new XYZTiles<T>(addresses);
        this.pane = pane;
        setVisible(true);
    }

    @Override
    protected List<T> getObjects() {
        return addresses;
    }

    @Override
    public Rectangle getBoundingBox() {
        if (addresses == null || addresses.isEmpty()) {
            return new Rectangle();
        }

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (T address : addresses) {
            var b = address.location();
            minX = Math.min(minX, b.east());
            minY = Math.min(minY, b.north());
            maxX = Math.max(maxX, b.east() + Address.SHAPE_SIZE);
            maxY = Math.max(maxY, b.north() + Address.SHAPE_SIZE);
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    @Override
    public Node getTool() {
        return null;
    }

    @Override
    public Pane getPane() {
        return pane;
    }

    @Override
    public void draw(Transform trans) {
        Transform screenToLV95 = null;
        try {
            screenToLV95 = trans.createInverse();
        } catch (NonInvertibleTransformException e) {
            throw new RuntimeException(e); // PRÜFUNG: Exception-Wrapping – checked → unchecked RuntimeException
        }

        Point2D topLeft     = screenToLV95.transform(0, 0);
        Point2D bottomRight = screenToLV95.transform(pane.getWidth(), pane.getHeight());

        // Weil Y-Achse invertiert ist (LV95 vs. Screen), braucht man min/max:
        double minX = Math.min(topLeft.getX(), bottomRight.getX());
        double minY = Math.min(topLeft.getY(), bottomRight.getY());
        double width = Math.abs(bottomRight.getX() - topLeft.getX());
        double height = Math.abs(bottomRight.getY() - topLeft.getY());

        BoundingBox visibleBounds = new BoundingBox(minX, minY, width, height);
        addresses = zoomStrategy.getZoomedObjects(visibleBounds);
        super.draw(trans);
    }
}

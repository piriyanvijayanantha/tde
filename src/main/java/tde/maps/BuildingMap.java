package tde.maps;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import tde.model.Address;

import java.util.List;

public class BuildingMap<T extends Address> extends AbstractMap<T> {
    List<T> addresses;
    private final Pane pane;


    public BuildingMap(String name, List<T> addresses, Pane pane) {
        setName(name);
        this.addresses = addresses;
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
}

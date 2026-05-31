package tde.maps.zoom;

import javafx.geometry.BoundingBox;

import java.util.List;

// PRÜFUNG: OCP + Generics – ZoomStrategy<T> Interface; XYZTiles ist eine Implementierung; weitere können hinzugefügt werden
public interface ZoomStrategy<T> {
    List<T> getZoomedObjects(BoundingBox boundingBox);
}

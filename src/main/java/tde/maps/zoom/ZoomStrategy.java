package tde.maps.zoom;

import java.util.List;

public interface ZoomStrategy<T> {
    List<T> getZoomedObjects(List<T> objects);
}

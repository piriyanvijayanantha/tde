package tde.maps;

import tde.model.Address;

import java.util.List;

public interface ZoomStrategy {
     List<Address> getZoomObject(List<Address> addresses);
}

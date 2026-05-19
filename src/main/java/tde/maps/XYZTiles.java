package tde.maps;

import tde.model.Address;

import java.util.List;

public class XYZTiles<T extends Address> implements ZoomStrategy {
    private List<T> addresses;


    public XYZTiles(List<T> addresses) {
        this.addresses = addresses;
    }

    public void xyzTiles(){
        var nofAddresses = addresses.size();  // addresses contains all our addresses
        var capacity = 20_000;                // from observing zoom speed of LI buildings.
//        var depth = ceil(log2(N/capacity)/2); // our formula for the depth of pyramid
//        var bounds = addresses.getBounds();   // the bounding box around all buildings

//        Pyramid pyramid = new Pyramid(depth, bounds);
    }

    @Override
    public List<Address> getZoomObject(List<Address> addresses) {
        return List.of();
    }
}

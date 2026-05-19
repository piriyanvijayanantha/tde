package tde.maps;

import tde.Address1;
import tde.model.Address;

import java.util.List;

public class ZoomStrategy {
    public stat
    public ZoomStrategy(List<Address> addresses) {
    }

    public void xyzTiles(){
        var nofAddresses = addresses.size();  // addresses contains all our addresses
        var capacity = 20_000;                // from observing zoom speed of LI buildings.
        var depth = ceil(log2(N/capacity)/2); // our formula for the depth of pyramid
        var bounds = addresses.getBounds();   // the bounding box around all buildings

        Pyramid pyramid = new Pyramid(depth, bounds);
    }
}

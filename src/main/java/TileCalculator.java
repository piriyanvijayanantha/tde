public class TileCalculator {
    static void main() {
        System.out.println(computeTile(47.4818, 8.2117, 9));;
    }

    static String computeTile(double lat, double lon, int z) {
        double latRad = lat * Math.PI / 180;
        double x = Math.floor((lon + 180) / 360 * Math.pow(2, z));
        double y = Math.floor((1 - Math.log(Math.tan(latRad) + 1 / Math.cos(latRad)) / Math.PI) * Math.pow(2, z - 1));

        return "https://tile.openstreetmap.org/" + z + "/" + (int)x + "/" + (int)y + ".png";
    }
}

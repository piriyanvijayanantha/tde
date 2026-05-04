package tde.importers;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import tde.model.Coordinates;
import tde.model.Country;
import tde.model.LandArea;

import java.util.ArrayList;
import java.util.List;


public class XMLHandler extends DefaultHandler {
    private List<LandArea.Area> areas = new ArrayList<>();

    private StringBuilder sb = new StringBuilder();
    private static final String PRETAG = "swissBOUNDARIES3D_ili2_LV95_V1_5.TLM_GRENZEN.TLM_";
    private static final String HOHEITSGEBIET = PRETAG + "HOHEITSGEBIET";
    private static final String BEZIRKSGEBIET = PRETAG + "BEZIRKSGEBIET";
    private static final String KANTONSGEBIET = PRETAG + "KANTONSGEBIET";
    private static final String LANDESGEBIET = PRETAG + "LANDESGEBIET";

    private List<Country> landesgebiete = new ArrayList<>();
    private List<Country> kantonsgebiete = new ArrayList<>();
    private List<Country> bezirksgebiete = new ArrayList<>();
    private List<Country> hoheitsgebiete = new ArrayList<>();
    private List<Coordinates> coordinates = new ArrayList<>();
    private double c1 = 0.0;
    private double c2 = 0.0;
    private String name = "";
    private int population = 0;
    private double area = 0;




    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        sb.append(ch, start, length);
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        sb = new StringBuilder();

        switch (qName) {
            case "COORD" -> {
                c1 = 0.0;
                c2 = 0.0;
            }
            case HOHEITSGEBIET, BEZIRKSGEBIET, KANTONSGEBIET, LANDESGEBIET -> areas = new ArrayList<>();
            case "POLYLINE" -> coordinates = new ArrayList<>();
        }
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case "C1" -> c1 = Double.parseDouble(sb.toString());
            case "C2" -> c2 = Double.parseDouble(sb.toString());
            case "COORD" -> coordinates.add(new Coordinates(c1, c2, 0));
            case "Name" -> name = sb.toString().trim();
            case "Einwohnerzahl" -> population = Integer.parseInt(sb.toString().trim());
            case "Landesflaeche", "Kantonsflaeche", "Gem_Flaeche", "Bezirksflaeche" ->
                    area = Double.parseDouble(sb.toString().trim());
            case HOHEITSGEBIET -> hoheitsgebiete.add(new Country(name, population, area, new LandArea(areas)));
            case BEZIRKSGEBIET -> bezirksgebiete.add(new Country(name, population, area, new LandArea(areas)));
            case KANTONSGEBIET -> kantonsgebiete.add(new Country(name, population, area, new LandArea(areas)));
            case LANDESGEBIET -> landesgebiete.add(new Country(name, population, area, new LandArea(areas)));
            case "POLYLINE" -> areas.add(new LandArea.Area(List.of(new LandArea.Area.Boundaries(coordinates))));
        }
    }

    public List<Country> getLandesgebiete() {
        return landesgebiete;
    }

    public List<Country> getBezirksgebiete() {
        return bezirksgebiete;
    }

    public List<Country> getKantonsgebiete() {
        return kantonsgebiete;
    }

    public List<Country> getHoheitsgebiete() {
        return hoheitsgebiete;
    }
}

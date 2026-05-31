# TDE – Prüfungs-Referenz

Landkarte für die Programmieren-2-Prüfung. Gibt an, wo welches Muster im Code zu finden ist.
Suche im Code nach dem Stichwort `PRÜFUNG:` um direkt zur markierten Stelle zu springen.

---

## Projektübersicht: Packages und Klassen

```
tde/
├── TDE.java                     – main()-Methode, startet JavaFX
├── TDEApplication.java          – JavaFX Application (start())
├── TDEController.java           – FXML-Controller; löst Laden von XML/CSV aus
│
├── db/
│   ├── DataService.java         – Interface (OCP): getAllCountries, getAllAddresses, store…
│   └── SimpleDataService.java   – Implementierung von DataService (In-Memory)
│
├── importers/
│   ├── XMLHandler.java          – SAX-ContentHandler für swissBOUNDARIES3D XML
│   ├── CSVLoader.java           – CSV-Reader für Gebäudeadressen (IOException checked)
│   └── package-info.java
│
├── maps/
│   ├── Map.java                 – Interface<T>: Generics + OCP-Basis
│   ├── AbstractMap.java         – abstrakte Basisklasse<T extends Drawable>
│   ├── TerritoryMap.java        – konkrete Map<T extends Territory> (OCP, Generics)
│   ├── BuildingMap.java         – konkrete Map<T extends Address> + Exception-Wrapping
│   ├── MapsController.java      – verwaltet mehrere Maps, Zoom/Pan
│   │
│   └── zoom/
│       ├── ZoomStrategy.java    – Interface<T>: OCP für Zoom-Algorithmen
│       ├── XYZTiles.java        – Implementierung von ZoomStrategy<T extends Address>
│       ├── Pyramid.java         – Tile-Pyramide (Quad-Tree-Struktur)
│       ├── Layer.java           – eine Ebene der Pyramide (2^level × 2^level Tiles)
│       └── Tile.java            – ein einzelnes Tile mit Address-Liste
│
└── model/
    ├── Drawable.java            – Interface: draw() + contains()
    ├── Territory.java           – Interface extends Drawable
    ├── AbstractTerritory.java   – abstrakte Basis (Name, Population, Fläche, LandArea)
    ├── Country.java             – konkretes Territory (Farben)
    ├── LandArea.java            – Record: Gebiets-Polygone (Exklaven/Enklaven)
    ├── Address.java             – Record implements Drawable (Gebäudeadressen)
    └── Coordinates.java         – Record: east / north / altitude (LV95)
```

---

## 1. SAX-Parsing

**Klasse:** `src/main/java/tde/importers/XMLHandler.java`

| Was | Wo |
|---|---|
| Extends `DefaultHandler` (SAX-Basis) | Klassendeklaration Z. 14 |
| `StringBuilder sb` akkumuliert Text | Feld Z. 17; `characters()` Z. 39–41 |
| `startElement()` – StringBuilder leeren, Zustand setzen | Z. 45–56 |
| `endElement()` – akkumulierten Text auswerten, Objekte bauen | Z. 60–74 |
| Zustandsverwaltung (laufende Felder) | Felder Z. 29–33: `c1`, `c2`, `name`, `population`, `area` |
| SAX-Parser starten (in TDEController) | `TDEController.java` Z. 86–92 (`factory.newSAXParser()`, `saxParser.parse()`) |

**Aufruf-Kette:** `TDEController.onLoadBoundaries()` → `SAXParser.parse(file, saxHandler)` → XMLHandler-Callbacks

---

## 2. CSV-Einlesen

**Klasse:** `src/main/java/tde/importers/CSVLoader.java`

| Was | Wo |
|---|---|
| `readAddressData(File)` – Datei zeilenweise lesen | Z. 28–38 |
| `mapStringLineToAddress(String)` – Zeile → `Address`-Objekt | Z. 45–75 |
| Spalten-Konstanten (COL_EAST, COL_NORTH …) | Z. 14–25 |
| Quote-Escaping im CSV | Z. 46–59 |
| Wirft `IOException` (checked) | Signatur Z. 28 |

**Aufruf:** `TDEController.onLoadBuildings()` Z. 74: `csvLoader.readAddressData(file)` — IOException wird dort gefangen.

---

## 3. Exception-Handling / Loader-Kapselung

| Muster | Datei : Zeile |
|---|---|
| `IOException` checked – wird in Methode deklariert | `CSVLoader.java` Z. 28 (`throws IOException`) |
| `IOException` im Controller gefangen | `TDEController.java` Z. 76 (`catch (IOException ioe)`) |
| SAX/Parser-Exceptions gefangen | `TDEController.java` Z. 92 (`catch (IOException \| ParserConfigurationException \| SAXException e)`) |
| **Exception-Wrapping** checked → unchecked `RuntimeException` | `BuildingMap.java` Z. 72–74: `throw new RuntimeException(e)` |

---

## 4. Open/Closed-Principle in der Map-/Loader-Hierarchie

**Idee:** Klassen sind offen für Erweiterung (neue Subklassen) aber geschlossen für Änderung (kein `instanceof`/`if-else` auf Typen).

| Ebene | Datei |
|---|---|
| Interface `Map<T>` (Vertrag) | `maps/Map.java` |
| Abstrakte Basis `AbstractMap<T extends Drawable>` | `maps/AbstractMap.java` |
| Konkrete Erweiterung `TerritoryMap<T extends Territory>` | `maps/TerritoryMap.java` |
| Konkrete Erweiterung `BuildingMap<T extends Address>` | `maps/BuildingMap.java` |
| Interface `DataService` (Loader-Kapselung) | `db/DataService.java` |
| Implementierung `SimpleDataService` | `db/SimpleDataService.java` |
| Interface `ZoomStrategy<T>` | `maps/zoom/ZoomStrategy.java` |
| Implementierung `XYZTiles<T extends Address>` | `maps/zoom/XYZTiles.java` |
| Interface `Territory extends Drawable` | `model/Territory.java` |
| Abstrakte Basis `AbstractTerritory` | `model/AbstractTerritory.java` |
| Konkretes `Country extends AbstractTerritory` | `model/Country.java` |

**Polymorphie statt if/else:** `AbstractMap.draw()` ruft `getObjects().forEach(t -> t.draw(...))` — kein Typ-Check, jedes Drawable zeichnet sich selbst.

---

## 5. Generics

| Ausdruck | Datei : Zeile |
|---|---|
| `Map<T>` | `maps/Map.java` Z. 13 |
| `AbstractMap<T extends Drawable>` | `maps/AbstractMap.java` Z. 8 |
| `TerritoryMap<T extends Territory>` | `maps/TerritoryMap.java` Z. 10 |
| `BuildingMap<T extends Address>` | `maps/BuildingMap.java` Z. 16 |
| `ZoomStrategy<T>` | `maps/zoom/ZoomStrategy.java` Z. 7 |
| `XYZTiles<T extends Address>` | `maps/zoom/XYZTiles.java` Z. 13 |
| `List<Country>` in DataService | `db/DataService.java` Z. 18 |
| `List<Address>` in DataService | `db/DataService.java` Z. 37 |
| `Optional<Country>` | `db/DataService.java` Z. 26 |

---

## 6. i18n (ResourceBundle / Locale / FXML)

**FXML:** vorhanden – `src/main/resources/tde/tde-main.fxml`

**ResourceBundle / Locale:** **nicht implementiert** – es gibt keine `.properties`-Dateien, keinen `ResourceBundle.getBundle()`-Aufruf und keine `Locale`-Konfiguration im Projekt.

---

## Schnell-Suche: `PRÜFUNG:`-Marker im Code

| Stichwort | Datei |
|---|---|
| `PRÜFUNG: SAX` | `XMLHandler.java` |
| `PRÜFUNG: CSV` | `CSVLoader.java` |
| `PRÜFUNG: Exception-Wrapping` | `BuildingMap.java` |
| `PRÜFUNG: OCP` | `Map.java`, `AbstractMap.java`, `DataService.java`, `ZoomStrategy.java` |
| `PRÜFUNG: Generics` | `TerritoryMap.java`, `BuildingMap.java`, `XYZTiles.java` |

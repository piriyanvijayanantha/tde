package tde.importers;

import tde.model.Address;
import tde.model.Coordinates;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class CSVLoader {
    private static final int COL_EAST = 15;
    private static final int COL_NORTH = 16;
    private static final int COL_MODIFIED = 14;  // ADR_MODIFIED
    private static final int COL_NUMBER = 5;   // ADR_NUMBER
    private static final int COL_OFFICIAL = 13;  // ADR_OFFICIAL
    private static final int COL_STATUS = 12;  // ADR_STATUS
    private static final int COL_CATEGORY = 6;   // BDG_CATEGORY
    private static final int COL_BUILDING = 7;   // BDG_NAME
    private static final int COL_CANTON = 11;  // COM_CANTON
    private static final int COL_COMMUNITY = 10;  // COM_NAME
    private static final int COL_STREET = 4;   // STN_LABEL
    private static final int COL_ZIP = 8;   // ZIP_LABEL


    public List<Address> readAddressData(File file) throws IOException {
        var addresses = new ArrayList<Address>();
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            var line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                addresses.add(mapStringLineToAddress(line));
            }
        }
        return addresses;
    }

    private LocalDate parseStringToLocalDate(String strDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.parse(strDate, formatter);
    }

    private Address mapStringLineToAddress(String line) {
        if (line.contains("\"")){
            boolean inQuote = false;
            var chars = line.toCharArray();
            var sb = new StringBuilder();
            for (int i = 0; i < chars.length; i++){
                if (chars[i] == '\"'){
                    inQuote = !inQuote;
                }
                if (!(inQuote && chars[i] == ';')){
                    sb.append(chars[i]);
                }
            }
            line = sb.toString();
        }
        var stringArr = line.split(";");
        var location = new Coordinates(Double.parseDouble(stringArr[COL_EAST]), Double.parseDouble(stringArr[COL_NORTH]), 0);
        var localDate = parseStringToLocalDate(stringArr[COL_MODIFIED]);
        return new Address(
                location,
                localDate,
                stringArr[COL_NUMBER],
                Boolean.getBoolean(stringArr[COL_OFFICIAL]),
                stringArr[COL_STATUS],
                stringArr[COL_CATEGORY],
                stringArr[COL_BUILDING],
                stringArr[COL_CANTON],
                stringArr[COL_COMMUNITY],
                stringArr[COL_STREET],
                stringArr[COL_ZIP]);
    }
}



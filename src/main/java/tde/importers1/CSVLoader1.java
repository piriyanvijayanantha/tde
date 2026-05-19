package tde.importers1;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import tde.Address1;


import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class CSVLoader {
    private static final int STN_LABEL = 4;
    private static final int ADR_NUMBER = 5;
    private static final int BDG_CATEGORY = 6;
    private static final int BDG_NAME = 7;
    private static final int ZIP_LABEL = 8;
    private static final int COM_NAME = 10;
    private static final int COM_CANTON = 11;
    private static final int ADR_STATUS = 12;
    private static final int ADR_OFFICIAL = 13;
    private static final int ADR_MODIFIED = 14;
    private static final int ADR_EASTING = 15;
    private static final int ADR_NORTHING = 16;
    private static final int LINE_LENGTH = 17;

    private static final DateTimeFormatter CH_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // prevent instantiation
    private CSVLoader() { }

    public static List<Address1> readAddressData(File file) throws IOException {
//        return read_CSVFormat(new FileReader(file));
        return read_mapped(file);
    }

    public static List<Address1> read_CSVFormat(Reader in) throws IOException {
        List<Address1> result = new ArrayList<>(3_300_000);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder().setDelimiter(';').get().parse(in);
        boolean first = true;
        for (CSVRecord record: records) {
            if (first) {
                first = false;
                continue;
            }
            var status = Address1.AddressStatus.valueOf(record.get(ADR_STATUS).toUpperCase());
            var category = Address1.BuildingCategory.valueOf(record.get(BDG_CATEGORY).toUpperCase());
            Address1 adr = new Address1(
                    Double.parseDouble(record.get(ADR_EASTING)),
                    Double.parseDouble(record.get(ADR_NORTHING)),
                    LocalDate.parse(record.get(ADR_MODIFIED), CH_FORMATTER),
                    record.get(ADR_NUMBER),
                    "true".equals(record.get(ADR_OFFICIAL)),
                    status,
                    category,
                    record.get(BDG_NAME),
                    record.get(COM_CANTON),
                    record.get(COM_NAME),
                    record.get(STN_LABEL),
                    record.get(ZIP_LABEL));
            result.add(adr);
        }
        return result;
    }

    public static List<Address1> read_naive(Reader in) throws IOException {
        List<Address1> result = new ArrayList<>(3_300_000);

        var br = new BufferedReader(in);
        var line = br.readLine(); // SKIP first
        line = br.readLine();

        while(line != null) {

            // escape
            if(line.contains("\"")) {
                var split = line.split("\"");

                for(int i = 1; i < split.length - 1; ++i){
                    split[i] = split[i].replace(';', ':');
                }

                // only for showcase - hurts to do this :D
                var combiner = "";
                for(var s : split) {
                    combiner += s;
                }
                line = combiner;
            }
            var addressLine = line.split(";");

            assert addressLine.length == LINE_LENGTH : "unexpected line";

            Address1 adr = new Address1(
                    Double.parseDouble(addressLine[ADR_EASTING]),
                    Double.parseDouble(addressLine[ADR_NORTHING]),
                    LocalDate.parse(addressLine[ADR_MODIFIED], CH_FORMATTER),
                    addressLine[ADR_NUMBER],
                    "true".equals(addressLine[ADR_OFFICIAL]),
                    Address1.AddressStatus.valueOf(addressLine[ADR_STATUS].toUpperCase()),
                    Address1.BuildingCategory.valueOf(addressLine[BDG_CATEGORY].toUpperCase()),
                    addressLine[BDG_NAME],
                    addressLine[COM_CANTON],
                    addressLine[COM_NAME],
                    addressLine[STN_LABEL],
                    addressLine[ZIP_LABEL]);
            result.add(adr);

            line = br.readLine();
        }

        return result;
    }

    public static List<Address1> read_naive_opt(Reader in) throws IOException {
        List<Address1> result = new ArrayList<>(3_300_000);

        var br = new BufferedReader(in);
        var line = br.readLine(); // SKIP first
        line = br.readLine();

        while(line != null) {

            var addressLine = line.split(";");

            if(addressLine.length != LINE_LENGTH) {
                var split = line.split("\"");

                for(int i = 1; i < split.length - 1; ++i){
                    split[i] = split[i].replace(';', ':');
                }

                var sb = new StringBuilder();
                for(var s : split) {
                    sb.append(s);
                }
                addressLine = sb.toString().split(";");
            }

            Address1 adr = new Address1(
                    Double.parseDouble(addressLine[ADR_EASTING]),
                    Double.parseDouble(addressLine[ADR_NORTHING]),
                    LocalDate.parse(addressLine[ADR_MODIFIED], CH_FORMATTER),
                    addressLine[ADR_NUMBER],
                    "true".equals(addressLine[ADR_OFFICIAL]),
                    Address1.AddressStatus.valueOf(addressLine[ADR_STATUS].toUpperCase()),
                    Address1.BuildingCategory.valueOf(addressLine[BDG_CATEGORY].toUpperCase()),
                    addressLine[BDG_NAME],
                    addressLine[COM_CANTON],
                    addressLine[COM_NAME],
                    addressLine[STN_LABEL],
                    addressLine[ZIP_LABEL]);
            result.add(adr);

            line = br.readLine();
        }

        return result;
    }

    public static List<Address1> read_on_chars(Reader in) throws IOException {
        List<Address1> result = new ArrayList<>(3_300_000);

        var br = new BufferedReader(in);
        var line = br.readLine(); // SKIP first
        line = br.readLine();

        String[] split = new String[LINE_LENGTH];
        int splitC = 0;
        int from = 0;
        int next = 0;
        boolean inQuotes = false;

        while(line != null) {

            if (line.indexOf('"') < 0) {
                // fast path — JVM vectorized indexOf
                while ((next = line.indexOf(';', from)) >= 0) {
                    split[splitC++] = line.substring(from, next);
                    from = next + 1;
                }
                split[splitC] = line.substring(from);
            } else {
                // slow path - charAt scan
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if (c == '"') {
                        inQuotes = !inQuotes;
                    } else if (c == ';' && !inQuotes) {
                        split[splitC++] = line.substring(from, i);
                        from = i + 1;
                    }
                }
            }
            split[splitC] = line.substring(from);
            from = 0;
            splitC = 0;

            result.add(new Address1(
                    Double.parseDouble(split[ADR_EASTING]),
                    Double.parseDouble(split[ADR_NORTHING]),
                    LocalDate.parse(split[ADR_MODIFIED], CH_FORMATTER),
                    split[ADR_NUMBER],
                    "true".equals(split[ADR_OFFICIAL]),
                    Address1.AddressStatus.valueOf(split[ADR_STATUS].toUpperCase()),
                    Address1.BuildingCategory.valueOf(split[BDG_CATEGORY].toUpperCase()),
                    split[BDG_NAME],
                    split[COM_CANTON],
                    split[COM_NAME],
                    split[STN_LABEL],
                    split[ZIP_LABEL]));

            line = br.readLine();
        }

        return result;
    }

    public static List<Address1> read_mapped(File file) throws IOException {
        List<Address1> result = new ArrayList<>(3_300_000);

        try (FileChannel ch = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {
            long size = ch.size();
            if (size > Integer.MAX_VALUE) {
                throw new IOException("file > 2 GB; get() on MappedByteBuffer only supports int :) ");
            }
            MappedByteBuffer bb = ch.map(FileChannel.MapMode.READ_ONLY, 0, size);

            final int end = (int) size;
            final int[] fieldStart = new int[LINE_LENGTH + 1];
            final String[] split = new String[LINE_LENGTH];
            byte[] fieldBuf = new byte[256];

            // skip header
            int p = 0;
            while (p < end && bb.get(p) != '\n') p++;
            p++;

            while (p < end) {
                int f = 0;
                fieldStart[f++] = p;
                boolean inQuotes = false;

                while (p < end) {
                    byte b = bb.get(p);
                    if (b == '\n') {
                        break;
                    }
                    if (b == '"') {
                        inQuotes = !inQuotes;
                    }
                    else if (b == ';' && !inQuotes) {
                        fieldStart[f++] = p + 1;
                    }
                    p++;
                }

                // if CRLF, skip CR
                fieldStart[LINE_LENGTH] = bb.get(p - 1) == '\r' ? p - 1 : p;

                for (int i = 0; i < LINE_LENGTH; i++) {
                    int s = fieldStart[i];
                    int e = fieldStart[i + 1] - 1;
                    int len = e - s;
                    bb.get(s, fieldBuf, 0, len);
                    split[i] = new String(fieldBuf, 0, len, StandardCharsets.UTF_8);
                }

                result.add(new Address1(
                        Double.parseDouble(split[ADR_EASTING]),
                        Double.parseDouble(split[ADR_NORTHING]),
                        LocalDate.parse(split[ADR_MODIFIED], CH_FORMATTER),
                        split[ADR_NUMBER],
                        "true".equals(split[ADR_OFFICIAL]),
                        Address1.AddressStatus.valueOf(split[ADR_STATUS].toUpperCase()),
                        Address1.BuildingCategory.valueOf(split[BDG_CATEGORY].toUpperCase()),
                        split[BDG_NAME],
                        split[COM_CANTON],
                        split[COM_NAME],
                        split[STN_LABEL],
                        split[ZIP_LABEL]));

                p++; // skip \n
            }
        }

        return result;
    }
}

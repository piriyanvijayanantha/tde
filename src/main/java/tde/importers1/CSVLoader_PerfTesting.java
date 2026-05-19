package tde.importers1;

import tde.Address1;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class CSVLoader_PerfTesting {

    private static final int WARMUP_COUNT = 3;
    private static final int RUN_COUNT = 10;
    private static final String FILENAME_CH = "/amtliches-gebaeudeadressverzeichnis_ch_2056.csv";
    private static final String FILENAME_LI = "/amtliches-gebaeudeadressverzeichnis_li_2056.csv";

    @FunctionalInterface
    private interface MeasuredCall<T> {
        T run() throws Exception;
    }

    private static <T> T measuredRun(String label, MeasuredCall<T> call) throws Exception {
        long elapsedNs = 0;
        T result = null;

        for(int i = 0; i < RUN_COUNT; ++i ) {
            long start = System.nanoTime();
            result = call.run();
            elapsedNs += (System.nanoTime() - start);
        }

        System.out.printf("%-30s %6d ms%n", label, elapsedNs / (RUN_COUNT * 1_000_000));
        return result;
    }

    void main() throws Exception {
        var usedFile = FILENAME_CH;

        // Warm-up: run each enough times to trigger JIT
        for (int i = 0; i < WARMUP_COUNT; i++) {
            warmUpAll(usedFile);
        }

        var lA1 = readCSVFormat(usedFile);
        var lA2 = readNaive(usedFile);
        var lA3 = readNaiveOpt(usedFile);
        var lA4 = readOnChars(usedFile);
        var lA5 = readOnBytes(usedFile);

        IO.println(check(lA1, lA2, lA3, lA4, lA5));
        printRandomData(lA1, lA2, lA3, lA4, lA5);
    }

    private void warmUpAll(String fName){
        try (Reader r = new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(fName)),
                StandardCharsets.UTF_8)) {

            CSVLoader1.read_naive(r);
        } catch (Exception e) {
            IO.println("warmup read naive failed");
        }

        try (Reader r = new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(fName)),
                StandardCharsets.UTF_8)) {
            CSVLoader1.read_CSVFormat(r);
        } catch (Exception e) {
            IO.println("warmup read csv failed");
        }

        try (Reader r = new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(fName)),
                StandardCharsets.UTF_8)) {

            CSVLoader1.read_naive_opt(r);
        } catch (Exception e) {
            IO.println("warmup read naive opt failed");
        }

        try (Reader r = new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(fName)),
                StandardCharsets.UTF_8)) {

            CSVLoader1.read_on_chars(r);
        } catch (Exception e) {
            IO.println("warmup read on chars failed");
        }

        try {
            CSVLoader1.read_mapped(new File(Objects.requireNonNull(getClass().getResource(fName)).toURI()));
        }
        catch (Exception e) {
            IO.println("warmup read on mapped bytes failed");
        }
    }

    private List<Address1> readOnBytes(String fName) throws Exception {
        return measuredRun("Mapped Byte Reader",
                () -> CSVLoader1.read_mapped(new File(Objects.requireNonNull(getClass().getResource(fName)).toURI())));
    }

    private List<Address1> readOnChars(String fName) throws Exception {
        return measuredRun("Char Reader",
                () -> {
                    try (Reader r = new InputStreamReader(
                            Objects.requireNonNull(getClass().getResourceAsStream(fName)),
                            StandardCharsets.UTF_8)) {
                        return CSVLoader1.read_on_chars(r);
                    }
                });
    }

    private List<Address1> readNaiveOpt(String fName) throws Exception {
        return measuredRun("Naive Opt",
                () -> {
                    try (Reader r = new InputStreamReader(
                            Objects.requireNonNull(getClass().getResourceAsStream(fName)),
                            StandardCharsets.UTF_8)) {
                        return CSVLoader1.read_naive_opt(r);
                    }
                });
    }

    private List<Address1> readNaive(String fName) throws Exception {
        return measuredRun("Naive",
            () -> {
                try (Reader r = new InputStreamReader(
                        Objects.requireNonNull(getClass().getResourceAsStream(fName)),
                        StandardCharsets.UTF_8)) {
                    return CSVLoader1.read_naive(r);
                }
            });
    }

    private List<Address1> readCSVFormat(String fName) throws Exception {
        return measuredRun("CSVFormat",
            () -> {
                try (Reader r = new InputStreamReader(
                        Objects.requireNonNull(getClass().getResourceAsStream(fName)),
                        StandardCharsets.UTF_8)) {
                    return CSVLoader1.read_CSVFormat(r);
                }
            });
    }

    @SafeVarargs
    private void printRandomData(List<Address1>... lists) {
        for(var l : lists) {
            var randomIdx = (int) (Math.random() * l.size());
            IO.println(l.get(randomIdx));
        }
    }

    @SafeVarargs
    private boolean check(List<Address1>... lists){

        var fst = lists[0];
        var fstSize = fst.size();

        int i = 1;
        while(i < lists.length && fstSize == lists[i].size()){
            ++i;
        }

        if(i != lists.length) {
            return false;
        }

        i = 1;
        while(i < lists.length){
            int j = 0;
            while(j < fstSize && fst.get(j).equalsStreetNameInvariant(lists[i].get(j))) {
                ++j;
            }

            if(j != fstSize) {
                return false;
            }

            ++i;
        }

        return true;
    }
}

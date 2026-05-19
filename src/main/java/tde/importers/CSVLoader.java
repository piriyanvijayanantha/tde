package tde.importers;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface CSVLoader<T> {
    List<T> readData(File file);
}

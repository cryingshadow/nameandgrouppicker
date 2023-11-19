package nameandgrouppicker;

import java.io.*;
import java.util.*;

public class FrequencyMap extends LinkedHashMap<String, Integer> {

    private static final long serialVersionUID = 992872443877097612L;

    public FrequencyMap() {
        super();
    }

    public FrequencyMap(final BufferedReader reader) throws IOException {
        String line = reader.readLine();
        while (line != null) {
            if (!line.isBlank()) {
                final String[] split = line.split(",");
                if (split.length != 2) {
                    throw new IllegalArgumentException("Input file must be a CSV file with exactly two columns!");
                }
                this.put(split[0].strip(), Integer.parseInt(split[1]));
            }
            line = reader.readLine();
        }
    }

    public FrequencyMap(final Map<String, Integer> map) {
        super(map);
    }

    public int getMax() {
        return this.values().stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    public void increment(final String name) {
        this.merge(name, 1, Integer::sum);
    }

    public void save(final BufferedWriter writer) throws IOException {
        for (final Map.Entry<String, Integer> entry : this.entrySet()) {
            writer.write(entry.getKey());
            writer.write(",");
            writer.write(entry.getValue().toString());
            writer.write("\n");
        }
    }

}

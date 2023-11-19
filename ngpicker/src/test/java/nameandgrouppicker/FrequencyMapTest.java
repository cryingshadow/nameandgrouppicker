package nameandgrouppicker;

import java.io.*;

import org.testng.*;
import org.testng.annotations.Test;

public class FrequencyMapTest {

    @Test
    public void create() throws IOException {
        FrequencyMap frequencies;
        try (BufferedReader reader = new BufferedReader(new StringReader("Alice,2\nBob,3\nCharly,1\n"))) {
            frequencies = new FrequencyMap(reader);
        }
        Assert.assertEquals(frequencies.size(), 3);
        Assert.assertEquals(frequencies.getMax(), 3);
        Assert.assertEquals(frequencies.get("Alice"), Integer.valueOf(2));
        Assert.assertEquals(frequencies.get("Bob"), Integer.valueOf(3));
        Assert.assertEquals(frequencies.get("Charly"), Integer.valueOf(1));
    }

    @Test
    public void increment() {
        final FrequencyMap frequencies = new FrequencyMap();
        frequencies.put("Alice", 2);
        frequencies.increment("Bob");
        frequencies.increment("Alice");
        frequencies.increment("Charly");
        frequencies.increment("Bob");
        Assert.assertEquals(frequencies.size(), 3);
        Assert.assertEquals(frequencies.getMax(), 3);
        Assert.assertEquals(frequencies.get("Alice"), Integer.valueOf(3));
        Assert.assertEquals(frequencies.get("Bob"), Integer.valueOf(2));
        Assert.assertEquals(frequencies.get("Charly"), Integer.valueOf(1));
    }

    @Test
    public void save() throws IOException {
        final FrequencyMap frequencies = new FrequencyMap();
        frequencies.put("Alice", 2);
        frequencies.put("Bob", 3);
        frequencies.put("Charly", 1);
        final StringWriter result = new StringWriter();
        try (BufferedWriter writer = new BufferedWriter(result)) {
            frequencies.save(writer);
        }
        Assert.assertEquals(result.toString(), "Alice,2\nBob,3\nCharly,1\n");
    }

}

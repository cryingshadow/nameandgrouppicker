package nameandgrouppicker;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public class NameList extends ArrayList<String> {

    private static final long serialVersionUID = 377778425025832291L;

    private final Random random = new Random();

    public NameList() {
        super();
    }

    public NameList(final BufferedReader nameListReader) throws IOException {
        String line = nameListReader.readLine();
        while (line != null) {
            final String stripped = line.strip();
            if (!stripped.isBlank() && !stripped.startsWith("//")) {
                this.add(stripped);
            }
            line = nameListReader.readLine();
        }
    }

    public NameList(final Collection<String> names) {
        super(names);
    }

    public GroupList getRandomGroups(final int count) {
        return GroupList.createFromNameList(this, count);
    }

    public GroupList getRandomGroups(final int minGroupSize, final int maxGroupSize) {
        return GroupList.createFromNameList(this, minGroupSize, maxGroupSize);
    }

    public String getRandomName() {
        if (this.isEmpty()) {
            return "";
        }
        return this.get(this.random.nextInt(this.size()));
    }

    public String getRandomName(final FrequencyMap frequencies) {
        if (this.isEmpty()) {
            return "";
        }
        final List<String> multipleList = new ArrayList<String>();
        final int max = frequencies.getMax();
        for (final String name : this) {
            final int number = max - frequencies.getOrDefault(name, 0) + 1;
            for (int i = 0; i < number; i++) {
                multipleList.add(name);
            }
        }
        final String name = multipleList.get(this.random.nextInt(multipleList.size()));
        frequencies.increment(name);
        return name;
    }

    public String removeRandom() {
        return this.remove(this.random.nextInt(this.size()));
    }

    public NameList shuffle() {
        final NameList result = new NameList(this);
        Collections.shuffle(result);
        return result;
    }

    @Override
    public String toString() {
        return this.stream().collect(Collectors.joining("\n"));
    }

}

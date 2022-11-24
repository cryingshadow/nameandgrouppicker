package nameandgrouppicker;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public class GroupList extends ArrayList<NameList> {

    private static final long serialVersionUID = -3601654371182069051L;

    public static GroupList createFromNameList(final Collection<String> names, final int count) {
        final GroupList result = new GroupList();
        GroupList.moveNamesToGroupsEvenly(new NameList(names), count, result);
        return result;
    }

    public static GroupList createFromNameList(
        final Collection<String> names,
        final int minGroupSize,
        final int maxGroupSize
    ) {
        if (maxGroupSize < minGroupSize) {
            throw new IllegalArgumentException("Minimum group size cannot be bigger than maximum group size!");
        }
        final int numOfNames = names.size();
        final int remainder = numOfNames % maxGroupSize;
        if (maxGroupSize == minGroupSize && remainder != 0) {
            throw new IllegalArgumentException(
                String.format("Cannot build groups of exactly size %d with %d people!", minGroupSize, numOfNames)
            );
        }
        final int numOfMaxSizeGroups;
        final int numOfSmallerGroups;
        if (remainder != 0 && remainder < minGroupSize) {
            final double min = minGroupSize;
            // (x * max + rest) / (x + 1) >= min
            // x * max + rest >= (x + 1) * min
            // x * max / min + rest / min >= x + 1
            // x * (max / min - 1) >= 1 - rest / min
            // x >= (1 - rest / min) / (max / min - 1)
            numOfSmallerGroups = (int)Math.ceil(((1 - (remainder / min)) / ((maxGroupSize / min) - 1))) + 1;
            numOfMaxSizeGroups = (numOfNames / maxGroupSize) + 1 - numOfSmallerGroups;
            if (numOfMaxSizeGroups < 0) {
                throw new IllegalArgumentException(
                    String.format(
                        "Cannot build groups of sizes between %d and %d with %d people!",
                        minGroupSize,
                        maxGroupSize,
                        numOfNames
                    )
                );
            }
        } else {
            numOfMaxSizeGroups = numOfNames / maxGroupSize;
            numOfSmallerGroups = remainder == 0 ? 0 : 1;
        }
        final GroupList result = new GroupList(minGroupSize, maxGroupSize);
        final NameList remainingNames =
            GroupList.fillFixedSizeGroupsAndReturnRemainingNames(
                names,
                numOfMaxSizeGroups,
                maxGroupSize,
                result
            );
        GroupList.moveNamesToGroupsEvenly(remainingNames, numOfSmallerGroups, result);
        return result;
    }

    private static NameList fillFixedSizeGroupsAndReturnRemainingNames(
        final Collection<String> names,
        final int numOfGroups,
        final int groupSize,
        final GroupList result
    ) {
        final NameList nameList = new NameList(names);
        for (int i = 0; i < numOfGroups; i++) {
            final NameList group = new NameList();
            for (int j = 0; j < groupSize; j++) {
                group.add(nameList.removeRandom());
            }
            result.add(group);
        }
        return nameList;
    }

    private static void moveNamesToGroupsEvenly(final NameList names, final int numOfGroups, final GroupList result) {
        if (numOfGroups == 0) {
            if (!names.isEmpty()) {
                throw new IllegalArgumentException("Cannot move names to zero groups!");
            }
            return;
        }
        final int groupSize = names.size() / numOfGroups;
        for (int i = 0; i < numOfGroups; i++) {
            final NameList group = new NameList();
            for (int j = 0; j < groupSize; j++) {
                group.add(names.removeRandom());
            }
            result.add(group);
        }
        final int resultSize = result.size();
        int index = 0;
        while (!names.isEmpty()) {
            result.get(resultSize - numOfGroups + index).add(names.removeRandom());
            index++;
        }
    }

    private static List<List<String>> parseGroups(final BufferedReader reader) throws IOException {
        final List<List<String>> result = new LinkedList<List<String>>();
        List<String> currentGroup = Collections.emptyList();
        String line = reader.readLine();
        while (line != null) {
            final String stripped = line.strip();
            if (!stripped.isBlank() && !stripped.startsWith("//")) {
                if (stripped.matches("Gruppe \\d+:")) {
                    result.add(currentGroup);
                    currentGroup = new LinkedList<String>();
                } else {
                    currentGroup.add(stripped);
                }
            }
            line = reader.readLine();
        }
        result.add(currentGroup);
        result.remove(0);
        return result;
    }

    public final Optional<Integer> maxGroupSize;

    public final Optional<Integer> minGroupSize;

    private final Random random;

    public GroupList() {
        this(Collections.emptyList(), Optional.empty(), Optional.empty());
    }

    public GroupList(
        final BufferedReader groupsReader,
        final int minGroupSize,
        final int maxGroupSize
    ) throws IOException {
        this(GroupList.parseGroups(groupsReader), Optional.of(minGroupSize), Optional.of(maxGroupSize));
    }

    public GroupList(
        final Collection<? extends List<String>> groups,
        final int minGroupSize,
        final int maxGroupSize
    ) {
        this(groups, Optional.of(minGroupSize), Optional.of(maxGroupSize));
    }

    public GroupList(
        final Collection<? extends List<String>> groups,
        final Optional<Integer> minGroupSize,
        final Optional<Integer> maxGroupSize
    ) {
        if (maxGroupSize.isPresent() && minGroupSize.isPresent() && maxGroupSize.get() < minGroupSize.get()) {
            throw new IllegalArgumentException("Minimum group size cannot be bigger than maximum group size!");
        }
        if (minGroupSize.isPresent() && minGroupSize.get() < 1) {
            throw new IllegalArgumentException("Minimum group size must be positive!");
        }
        this.minGroupSize = minGroupSize;
        this.maxGroupSize = maxGroupSize;
        this.random = new Random();
        for (final List<String> group : groups) {
            final int groupSize = group.size();
            if (
                (minGroupSize.isPresent() && groupSize < minGroupSize.get())
                || (maxGroupSize.isPresent() && groupSize > maxGroupSize.get())
            ) {
                throw new IllegalArgumentException(
                    "Specified groups must respect the minimum and maximum group sizes!"
                );
            }
            this.add(new NameList(group));
        }
    }

    public GroupList(final int minGroupSize, final int maxGroupSize) {
        this(Collections.emptyList(), Optional.of(minGroupSize), Optional.of(maxGroupSize));
    }

    public NameList getRandomGroup() {
        return this.get(this.getRandomGroupIndex());
    }

    public int getRandomGroupIndex() {
        return this.random.nextInt(this.size());
    }

    @Override
    public String toString() {
        return IntStream.range(0, this.size())
            .mapToObj(i -> String.format("Gruppe %d:\n%s", i + 1, this.get(i).toString()))
            .collect(Collectors.joining("\n\n"));
    }

}

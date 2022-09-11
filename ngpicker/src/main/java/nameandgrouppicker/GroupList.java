package nameandgrouppicker;

import java.util.*;
import java.util.stream.*;

public class GroupList extends ArrayList<NameList> {

    private static final long serialVersionUID = -3601654371182069051L;

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
            GroupList.fillFixedSizeGroupsAndReturnRemainingNames(names, numOfMaxSizeGroups, maxGroupSize, result);
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

    public final int maxGroupSize;

    public final int minGroupSize;

    private final Random random;

    public GroupList(final Collection<? extends List<String>> groups, final int minGroupSize, final int maxGroupSize) {
        if (maxGroupSize < minGroupSize) {
            throw new IllegalArgumentException("Minimum group size cannot be bigger than maximum group size!");
        }
        if (minGroupSize < 1) {
            throw new IllegalArgumentException("Minimum group size must be positive!");
        }
        this.minGroupSize = minGroupSize;
        this.maxGroupSize = maxGroupSize;
        this.random = new Random();
        for (final List<String> group : groups) {
            final int groupSize = group.size();
            if (groupSize < minGroupSize || groupSize > maxGroupSize) {
                throw new IllegalArgumentException(
                    "Specified groups must respect the minimum and maximum group sizes!"
                );
            }
            this.add(new NameList(group));
        }
    }

    public GroupList(final int minGroupSize, final int maxGroupSize) {
        this(Collections.emptyList(), minGroupSize, maxGroupSize);
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

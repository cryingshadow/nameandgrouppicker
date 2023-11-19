package nameandgrouppicker;

import java.io.*;
import java.util.*;

import org.testng.*;
import org.testng.annotations.Test;

public class NameListTest {

    @Test
    public void constructNameListTest() {
        final NameList list = new NameList();
        Assert.assertTrue(list.isEmpty());
    }

    @Test
    public void constructNameListWithBufferedReaderTest() throws IOException {
        final List<String> testList = Arrays.asList("Alice", "Bob", "Charly");
        final String[] testInput =
            new String[] {
                "Alice\nBob\nCharly",
                " Alice\nBob \n Charly ",
                "Alice\nBob\n//comment\nCharly",
                "Alice \n Bob\n // comment \nCharly",
            };
        for (final String testString : testInput) {
            final NameList list = new NameList(new BufferedReader(new StringReader(testString)));
            Assert.assertEquals(list.size(), 3);
            Assert.assertTrue(list.containsAll(testList));
            Assert.assertTrue(testList.containsAll(list));
        }
    }

    @Test
    public void constructNameListWithCollectionTest() {
        final List<String> testList = Arrays.asList("Alice", "Bob", "Charly");
        final NameList list = new NameList(testList);
        Assert.assertEquals(list.size(), 3);
        Assert.assertTrue(list.containsAll(testList));
        Assert.assertTrue(testList.containsAll(list));
    }

    @Test
    public void getRandomGroupsTest() {
        final List<String> nameList =
            Arrays.asList(
                "Alice",
                "Bob",
                "Charly",
                "Jim",
                "Jack",
                "Johnnie",
                "Zaphod",
                "Trillian",
                "John Doe",
                "Dexter",
                "Trinity",
                "Rosemary's Baby",
                "Damian"
            );
        final GroupList list = new NameList(nameList).getRandomGroups(2, 3);
        Assert.assertEquals(list.size(), 5);
        Assert.assertEquals(list.minGroupSize.get(), Integer.valueOf(2));
        Assert.assertEquals(list.maxGroupSize.get(), Integer.valueOf(3));
        Assert.assertEquals(list.stream().mapToInt(List::size).sum(), nameList.size());
        final List<String> actualNames = list.stream().flatMap(List::stream).toList();
        Assert.assertTrue(actualNames.containsAll(nameList));
        Assert.assertTrue(nameList.containsAll(actualNames));
        Assert.assertTrue(list.stream().mapToInt(List::size).max().getAsInt() <= 3);
        Assert.assertTrue(list.stream().mapToInt(List::size).min().getAsInt() >= 2);
    }

    @Test
    public void getRandomNameTest() {
        final List<String> testList = Arrays.asList("Alice", "Bob", "Charly");
        final NameList list = new NameList(testList);
        final String name = list.getRandomName();
        Assert.assertEquals(list.size(), 3);
        Assert.assertTrue(testList.contains(name));
        Assert.assertTrue(list.containsAll(testList));
        Assert.assertTrue(testList.containsAll(list));
        final FrequencyMap frequencies = new FrequencyMap();
        frequencies.put("Alice", 2);
        frequencies.put("Bob", 3);
        frequencies.put("Charly", 1);
        final String name2 = list.getRandomName(frequencies);
        Assert.assertTrue(testList.contains(name2));
        Assert.assertEquals(frequencies.size(), 3);
        Assert.assertEquals(frequencies.get("Alice"), Integer.valueOf("Alice".equals(name2) ? 3 : 2));
        Assert.assertEquals(frequencies.get("Bob"), Integer.valueOf("Bob".equals(name2) ? 4 : 3));
        Assert.assertEquals(frequencies.get("Charly"), Integer.valueOf("Charly".equals(name2) ? 2 : 1));
    }

    @Test
    public void removeRandomTest() {
        final List<String> testList = Arrays.asList("Alice", "Bob", "Charly");
        final NameList list = new NameList(testList);
        final String name = list.removeRandom();
        Assert.assertEquals(list.size(), 2);
        Assert.assertTrue(testList.contains(name));
        Assert.assertTrue(testList.containsAll(list));
    }

    @Test
    public void shuffleTest() {
        final List<String> testList = Arrays.asList("Alice", "Bob", "Charly");
        final NameList list = new NameList(testList).shuffle();
        Assert.assertEquals(list.size(), 3);
        Assert.assertTrue(list.containsAll(testList));
        Assert.assertTrue(testList.containsAll(list));
    }

    @Test
    public void toStringTest() {
        final List<String> testList = Arrays.asList("Alice", "Bob", "Charly");
        final NameList list = new NameList(testList);
        Assert.assertEquals(list.toString(), "Alice\nBob\nCharly");
    }

}

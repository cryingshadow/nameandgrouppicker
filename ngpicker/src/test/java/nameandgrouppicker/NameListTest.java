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
        //TODO own test for frequencies
//        final String testFrequency = "Alice,2\nBob,3\nCharly,1";
//        new BufferedReader(new StringReader(testFrequency))
        for (final String testString : testInput) {
            final NameList list = new NameList(new BufferedReader(new StringReader(testString)));
            Assert.assertEquals(list.size(), 3);
            Assert.assertTrue(list.containsAll(testList));
            Assert.assertTrue(testList.containsAll(list));
//            Assert.assertEquals(list.frequencies.size(), 3);
//            Assert.assertEquals(list.frequencies.get("Alice"), Integer.valueOf(2));
//            Assert.assertEquals(list.frequencies.get("Bob"), Integer.valueOf(3));
//            Assert.assertEquals(list.frequencies.get("Charly"), Integer.valueOf(1));
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

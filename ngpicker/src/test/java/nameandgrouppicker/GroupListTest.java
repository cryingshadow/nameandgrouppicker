package nameandgrouppicker;

import java.util.*;

import org.testng.*;
import org.testng.annotations.Test;

public class GroupListTest {

    private static final List<List<String>> TEST_GROUPS =
        Arrays.asList(
            Arrays.asList("Alice", "Bob", "Charly"),
            Arrays.asList("Jim", "Jack", "Johnnie"),
            Arrays.asList("Zaphod", "Trillian")
        );

    @Test
    public void constructGroupListTest() {
        final int[][] testValues = new int[][] {
            {1,2},
            {2,3},
            {7,8},
            {3,9},
            {3,3},
            {2,2}
        };
        for (int i = 0; i < testValues.length; i++) {
            final int min = testValues[i][0];
            final int max = testValues[i][1];
            final GroupList list = new GroupList(min, max);
            Assert.assertEquals(list.size(), 0);
            Assert.assertEquals(list.minGroupSize, min);
            Assert.assertEquals(list.maxGroupSize, max);
        }
        Assert.assertThrows(IllegalArgumentException.class, () -> new GroupList(2, 1));
        Assert.assertThrows(IllegalArgumentException.class, () -> new GroupList(-1, 1));
        Assert.assertThrows(IllegalArgumentException.class, () -> new GroupList(0, 1));
    }

    @Test
    public void constructGroupListWithCollectionTest() {
        final GroupList list = new GroupList(GroupListTest.TEST_GROUPS, 2, 3);
        Assert.assertEquals(list.size(), 3);
        Assert.assertTrue(list.containsAll(GroupListTest.TEST_GROUPS));
        Assert.assertTrue(GroupListTest.TEST_GROUPS.containsAll(list));
        Assert.assertThrows(IllegalArgumentException.class, () -> new GroupList(GroupListTest.TEST_GROUPS, 2, 1));
        Assert.assertThrows(IllegalArgumentException.class, () -> new GroupList(GroupListTest.TEST_GROUPS, -1, 1));
        Assert.assertThrows(IllegalArgumentException.class, () -> new GroupList(GroupListTest.TEST_GROUPS, 0, 1));
        Assert.assertThrows(IllegalArgumentException.class, () -> new GroupList(GroupListTest.TEST_GROUPS, 3, 3));
        Assert.assertThrows(IllegalArgumentException.class, () -> new GroupList(GroupListTest.TEST_GROUPS, 2, 2));
        Assert.assertThrows(IllegalArgumentException.class, () -> new GroupList(GroupListTest.TEST_GROUPS, 1, 2));
        Assert.assertThrows(IllegalArgumentException.class, () -> new GroupList(GroupListTest.TEST_GROUPS, 3, 7));
    }

    @Test
    public void createFromNameListTest() {
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
        final int[][] testValues = new int[][] {
            {1,2,7},
            {2,3,5},
            {3,9,2},
            {5,9,2},
            {3,13,1},
            {5,15,1},
            {13,13,1},
            {13,17,1}
        };
        for (int i = 0; i < testValues.length; i++) {
            final int min = testValues[i][0];
            final int max = testValues[i][1];
            final int numOfGroups = testValues[i][2];
            final GroupList list = GroupList.createFromNameList(nameList, min, max);
            Assert.assertEquals(list.size(), numOfGroups);
            Assert.assertEquals(list.minGroupSize, min);
            Assert.assertEquals(list.maxGroupSize, max);
            Assert.assertEquals(list.stream().mapToInt(List::size).sum(), nameList.size());
            final List<String> actualNames = list.stream().flatMap(List::stream).toList();
            Assert.assertTrue(actualNames.containsAll(nameList));
            Assert.assertTrue(nameList.containsAll(actualNames));
            Assert.assertTrue(list.stream().mapToInt(List::size).max().getAsInt() <= max);
            Assert.assertTrue(list.stream().mapToInt(List::size).min().getAsInt() >= min);
        }
        Assert.assertThrows(IllegalArgumentException.class, () -> GroupList.createFromNameList(nameList, 7, 8));
        Assert.assertThrows(IllegalArgumentException.class, () -> GroupList.createFromNameList(nameList, 3, 3));
        Assert.assertThrows(IllegalArgumentException.class, () -> GroupList.createFromNameList(nameList, 2, 2));
    }

    @Test
    public void getRandomGroupIndexTest() {
        final GroupList list = new GroupList(GroupListTest.TEST_GROUPS, 2, 4);
        for (int i = 0; i < 10; i++) {
            final int index = list.getRandomGroupIndex();
            Assert.assertTrue(index < 3);
            Assert.assertTrue(0 <= index);
        }
    }

    @Test
    public void getRandomGroupTest() {
        final GroupList list = new GroupList(GroupListTest.TEST_GROUPS, 2, 4);
        for (int i = 0; i < 10; i++) {
            final NameList group = list.getRandomGroup();
            Assert.assertTrue(GroupListTest.TEST_GROUPS.contains(group));
        }
    }

    @Test
    public void toStringTest() {
        Assert.assertEquals(
            new GroupList(GroupListTest.TEST_GROUPS, 1, 3).toString(),
            "Gruppe 1:\nAlice\nBob\nCharly\n\nGruppe 2:\nJim\nJack\nJohnnie\n\nGruppe 3:\nZaphod\nTrillian"
        );
    }

}

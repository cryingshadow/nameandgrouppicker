package nameandgrouppicker;

import java.io.*;
import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

public class MainTest {

    private final PrintStream originalStdOut = System.out;
    private OutputStream testOut = new ByteArrayOutputStream();

    @AfterMethod
    public void cleanUp() {
        System.setOut(this.originalStdOut);
        this.testOut = new ByteArrayOutputStream();
    }

    @Test
    public void emptyArgsTest() {
        Main.main(new String[] {});
        Assert.assertEquals(this.testOut.toString(), Main.HELP_TEXT + System.lineSeparator());
    }

    @Test
    public void randomNameFromNameListTest() {
        Main.main(new String[] {"PICK", "-n", "C:\\Daten\\Test\\ngpicker\\nameList.txt"});
        final String name = this.testOut.toString().strip();
        Assert.assertTrue(Arrays.asList("Alice", "Bob", "Charly").contains(name));
    }

    @BeforeMethod
    public void setup() {
        System.setOut(new PrintStream(this.testOut));
    }

}

package nameandgrouppicker;

import java.io.*;
import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

public class MainTest { //TODO frequencies

    private static final NameList NAMES =
        new NameList(
            Arrays.asList(
                "Alice",
                "Bob",
                "Charly",
                "Donna",
                "Edward",
                "Frank",
                "Gina",
                "Hannah",
                "Ingrid",
                "Julia",
                "Karl",
                "Linus",
                "Melanie",
                "Nora",
                "Othello",
                "Peter",
                "Quentin",
                "Romeo",
                "Susanne",
                "Thomas",
                "Ulrich",
                "Viktor",
                "Wayne",
                "Xavier",
                "Yael",
                "Zander"
            )
        );

    private File frequenciesFile = null;

    private File nameListFile = null;

    private final PrintStream originalStdOut = System.out;

    private File testDir = null;

    private OutputStream testOut = new ByteArrayOutputStream();

    @AfterMethod
    public void cleanUp() {
        System.setOut(this.originalStdOut);
        this.testOut = new ByteArrayOutputStream();
        this.frequenciesFile.delete();
        this.nameListFile.delete();
        this.testDir.delete();
    }

    @Test
    public void emptyArgsTest() {
        Main.main(new String[] {});
        Assert.assertEquals(this.testOut.toString(), Main.HELP_TEXT + System.lineSeparator());
    }

    @Test
    public void randomGroupsFromNameListTest() throws IOException {
        Main.main(
            new String[] {
                "GROUPS",
                "-n", this.nameListFile.getAbsolutePath(),
                Main.toFlag(Main.MAX), "3",
                Main.toFlag(Main.MIN), "2"
            }
        );
        final List<String> names = new ArrayList<String>();
        try (final BufferedReader reader = new BufferedReader(new StringReader(this.testOut.toString()))) {
            String line = reader.readLine();
            int count = -1;
            while (line != null) {
                if (!line.isBlank()) {
                    final String stripped = line.strip();
                    if (stripped.startsWith("Gruppe")) {
                        Assert.assertTrue(count == -1 || (count <= 3 && count >= 2));
                        count = 0;
                    } else {
                        names.add(stripped);
                        count++;
                    }
                }
                line = reader.readLine();
            }
        }
        Assert.assertEquals(names.size(), MainTest.NAMES.size());
        Assert.assertTrue(names.containsAll(MainTest.NAMES));
        Assert.assertTrue(MainTest.NAMES.containsAll(names));
    }

    @Test
    public void randomNameFromNameListTest() {
        Main.main(
            new String[] {
                "PICK",
                "-n", this.nameListFile.getAbsolutePath()
            }
        );
        final String name = this.testOut.toString().strip();
        Assert.assertTrue(MainTest.NAMES.contains(name));
    }

    @BeforeMethod
    public void setup() throws IOException {
        System.setOut(new PrintStream(this.testOut));
        final File locateTmp = File.createTempFile("locate", "tmp");
        this.testDir = new File(locateTmp.getParentFile().getAbsolutePath(), "ngpicker");
        this.testDir.mkdir();
        locateTmp.delete();
        this.nameListFile = new File(this.testDir, "names.txt");
        this.frequenciesFile = new File(this.testDir, "frequencies.txt");
        this.frequenciesFile.createNewFile();
        try (
            BufferedWriter writer =
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.nameListFile), "UTF-8"))
        ) {
            for (final String name : MainTest.NAMES) {
                writer.write(name);
                writer.newLine();
            }
        }
    }

}

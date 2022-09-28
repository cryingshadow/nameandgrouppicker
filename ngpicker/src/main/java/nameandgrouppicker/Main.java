package nameandgrouppicker;

import java.io.*;

import net.sourceforge.argparse4j.*;
import net.sourceforge.argparse4j.inf.*;

public class Main {

    public static final String HELP_TEXT = "";

    public static void main(final String[] args) {
        if (args == null || args.length == 0) {
            System.out.println(Main.HELP_TEXT);
            return;
        }
        final ArgumentParser parser = ArgumentParsers.newFor("java -jar nameandgrouppicker.jar").build();
        parser.addArgument("-n", "--names")
            .help("Specify a file containing a list of names.");
        parser.addArgument("action")
            .type(Action.class)
            .help("Specify the action to be executed.");
        final Namespace space;
        try {
            space = parser.parseArgs(args);
        } catch (final ArgumentParserException e) {
            parser.handleError(e);
            return;
        }
        final File nameListFile = new File(space.<String>get("names"));
        try (
            BufferedReader reader =
                new BufferedReader(new InputStreamReader(new FileInputStream(nameListFile), "UTF-8"));
            BufferedWriter writer = Main.getOutput(space);
        ) {
            writer.write(new NameList(reader).getRandomName());
            writer.newLine();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedWriter getOutput(final Namespace space) throws UnsupportedEncodingException {
        return new BufferedWriter(new OutputStreamWriter(System.out, "Cp850"));
    }

}

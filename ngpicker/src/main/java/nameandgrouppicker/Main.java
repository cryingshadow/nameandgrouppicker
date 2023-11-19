package nameandgrouppicker;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import net.sourceforge.argparse4j.*;
import net.sourceforge.argparse4j.inf.*;

public class Main {

    public static final String HELP_TEXT = "";

    static final String COUNT = "count";

    static final String MAX = "max";

    static final String MIN = "min";

    private static final String ACTION = "action";

    private static final String FREQUENCIES = "frequencies";

    private static final String NAMES = "names";

    public static void main(final String[] args) {
        if (args == null || args.length == 0) {
            System.out.println(Main.HELP_TEXT);
            return;
        }
        final ArgumentParser parser = ArgumentParsers.newFor("java -jar nameandgrouppicker.jar").build();
        parser.addArgument("-n", Main.toFlag(Main.NAMES))
        .help("Specify a file containing a list of names.");
        parser.addArgument("-f", Main.toFlag(Main.FREQUENCIES))
        .help("Specify a CSV file containing a table of names and frequencies.");
        parser.addArgument(Main.ACTION)
            .type(Action.class)
            .help("Specify the action to be executed.");
        parser.addArgument(Main.toFlag(Main.MAX)).type(int.class).help("Specify the maximum number of group members.");
        parser.addArgument(Main.toFlag(Main.MIN)).type(int.class).help("Specify the minimum number of group members.");
        parser.addArgument(Main.toFlag(Main.COUNT)).type(int.class).help("Specify the number of groups.");
        final Namespace space;
        try {
            space = parser.parseArgs(args);
        } catch (final ArgumentParserException e) {
            parser.handleError(e);
            return;
        }
        try (BufferedWriter writer = Main.getOutput(space)) {
            switch (space.<Action>get(Main.ACTION)) {
            case GROUPS:
                Main.pickRandomGroups(space, writer);
                break;
            case PICK:
                Main.pickRandomName(space, writer);
                break;
            default:
                writer.write(
                    String.format(
                        "Unknown action. Choose from %s.",
                        Arrays.stream(Action.values()).map(Action::name).collect(Collectors.joining(", "))
                    )
                );
                break;
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    static String toFlag(final String flagName) {
        return "--" + flagName;
    }

    private static BufferedReader getFrequenciesReader(final Namespace space) throws IOException {
        return Main.getReader(space, Main.FREQUENCIES);
    }

    private static BufferedWriter getFrequenciesWriter(final Namespace space) throws IOException {
        return Main.getWriter(space, Main.FREQUENCIES);
    }

    private static BufferedReader getNamesReader(final Namespace space) throws IOException {
        return Main.getReader(space, Main.NAMES);
    }

    private static BufferedWriter getOutput(final Namespace space) throws UnsupportedEncodingException {
        return new BufferedWriter(new OutputStreamWriter(System.out, "Cp850"));
    }

    private static BufferedReader getReader(final Namespace space, final String flag) throws IOException {
        final File file = new File(space.<String>get(flag));
        file.createNewFile();
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
    }

    private static BufferedWriter getWriter(final Namespace space, final String flag) throws IOException {
        return new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(new File(space.<String>get(flag))), "UTF-8")
        );
    }

    private static void pickRandomGroups(final Namespace space, final BufferedWriter writer) throws IOException {
        final Integer max = space.getInt(Main.MAX);
        final Integer min = space.getInt(Main.MIN);
        final Integer count = space.getInt(Main.COUNT);
        try (final BufferedReader reader = Main.getNamesReader(space)) {
            final NameList names = new NameList(reader);
            final GroupList groups;
            if (count == null) {
                groups = names.getRandomGroups(min, max);
            } else {
                groups = names.getRandomGroups(count);
            }
            writer.write(groups.toString());
        }
    }

    private static void pickRandomName(
        final Namespace space,
        final BufferedWriter writer
    ) throws IOException {
        final String frequenciesPath = space.getString(Main.FREQUENCIES);
        if (frequenciesPath == null) {
            try (final BufferedReader namesReader = Main.getNamesReader(space)) {
                final NameList list = new NameList(namesReader);
                writer.write(list.getRandomName());
                writer.newLine();
            }
        } else {
            final FrequencyMap frequencies;
            final NameList list;
            try (
                final BufferedReader namesReader = Main.getNamesReader(space);
                final BufferedReader frequenciesReader = Main.getFrequenciesReader(space);
            ) {
                frequencies = new FrequencyMap(frequenciesReader);
                list = new NameList(namesReader);
            }
            writer.write(list.getRandomName(frequencies));
            writer.newLine();
            try (final BufferedWriter frequenciesWriter = Main.getFrequenciesWriter(space)) {
                frequencies.save(frequenciesWriter);
            }
        }
    }

}

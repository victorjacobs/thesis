import org.apache.commons.cli.*;

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class Cli {
    private Options options;

    private String outDir;
    private boolean showGui = false;
    private int repetitions = 10;
    private int threads = Runtime.getRuntime().availableProcessors();
    private boolean quickRun = false;

    public Cli(String[] args) {
        // Set up command line
        options = new Options();

        options.addOption(OptionBuilder
                .withArgName("resultsDir")
                .hasArg()
                .withDescription("Directory to output results to")
                .create("o"));
        options.addOption(OptionBuilder
                .withArgName("nbThreads")
                .hasArg()
                .withDescription("Number of threads, defaults to number of cores in system")
                .create("t"));
        options.addOption(OptionBuilder
                .withArgName("repetitions")
                .hasArg()
                .withDescription("Number of repetitions, defaults to 10")
                .create("r"));
        options.addOption(new Option("q", "Quick run: one repetition of one scenario"));
        options.addOption(new Option("help", "Print this message"));
        options.addOption(new Option("g", "Show gui"));

        CommandLineParser parser = new BasicParser();

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("help")) {
                printHelp();
                return;
            }

            if (!cmd.hasOption("q") && !cmd.hasOption("o")) {
                System.out.println("Missing option: o");
                System.out.println();
                printHelp();

                return;
            }

            quickRun = cmd.hasOption("q");
            if (cmd.hasOption("t")) {
                try {
                    threads = Integer.parseInt(cmd.getOptionValue("t"));
                } catch (NumberFormatException e) {
                    System.out.println("Warning: -t " + cmd.getOptionValue("t") + " not valid option");
                }
            }

            if (cmd.hasOption("r")) {
                try {
                    repetitions = Integer.parseInt(cmd.getOptionValue("r"));
                } catch (NumberFormatException e) {
                    System.out.println("Warning: -r " + cmd.getOptionValue("r") + " not valid option");
                }
            }

            if (showGui = cmd.hasOption("g")) {
                threads = 1;
            }

            outDir = cmd.getOptionValue("o");
        } catch (ParseException e) {
            System.out.println("Something went wrong parsing input");
            System.out.println();
            printHelp();
        }
    }

    public void printHelp() {
        (new HelpFormatter()).printHelp("java -jar Thesis.jar", options);
    }

    public boolean showGui() {
        return showGui;
    }

    public int repetitions() {
        return repetitions;
    }

    public int threads() {
        return threads;
    }

    public boolean quickrun() {
        return quickRun;
    }

    public String outDir() {
        return outDir;
    }
}

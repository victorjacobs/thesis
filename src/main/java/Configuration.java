import org.apache.commons.cli.*;
import rinde.sim.pdptw.gendreau06.Gendreau06Parser;

import java.io.File;

/**
 * Handles CLI input and configuration in general. Provides sensible defaults for everything.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class Configuration {
    private Options options;

    private boolean stop = false;

    private String outDir;
    private boolean showGui = false;
    private int repetitions = 12;
    private int threads = Runtime.getRuntime().availableProcessors();
    private boolean quickRun = false;
    private String scenarioDirectory = "files/scenarios/gendreau06/";

    public Configuration(String[] args) {
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
                .withDescription("Number of repetitions, defaults to 12")
                .create("r"));
        options.addOption(new Option("q", "Quick run: one repetition of one scenario"));
        options.addOption(new Option("help", "Print this message"));
        options.addOption(new Option("g", "Show gui"));

        CommandLineParser parser = new BasicParser();

        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("help")) {
                printHelp();
                stop = true;
            }

            if (!cmd.hasOption("q") && !cmd.hasOption("o")) {
                System.out.println("Missing option: o");
                System.out.println();
                printHelp();

                stop = true;
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

            if (!isValidScenarioDirectory()) {
                System.out.println("Invalid scenario directory " + scenarioDirectory());

                stop = true;
            }
        } catch (ParseException e) {
            System.out.println("Something went wrong parsing input");
            System.out.println();
            printHelp();
        }
    }

    /**
     * Prints CLI help.
     */
    public void printHelp() {
        (new HelpFormatter()).printHelp("java -jar Thesis.jar", options);
    }

    /**
     * Show a GUI? Defaults to false.
     *
     * @return Whether or not to show a gui
     */
    public boolean showGui() {
        return showGui;
    }

    /**
     * Number of repetitions for every configuration in the simulator. Defaults to 10.
     *
     * @return Number of repetitions of configurations
     */
    public int repetitions() {
        return repetitions;
    }

    /**
     * Number of threads to run the simulator on. Defaults to total number available in system.
     *
     * @return Number of threads to run
     */
    public int threads() {
        return threads;
    }

    /**
     * Toggles a quick run mode, in which only one scenario is tested and one repetition. Defaults to false.
     *
     * @return Whether or not a quick run should be run
     */
    public boolean quickrun() {
        return quickRun;
    }

    /**
     * Directory to output results to. No default.
     *
     * @return Directory where to results should be written
     */
    public String outDir() {
        return outDir;
    }

    /**
     * Should the program stop execution or not. This might occur when required option is missing or something went
     * wrong parsing the input.
     *
     * @return Should program end
     */
    public boolean stop() {
        return stop;
    }

    /**
     * Directory where scenario files are stored. Defaults to "files/scenarios/gendreau06/"
     *
     * @return Directory in which scenarios can be found
     */
    public String scenarioDirectory() {
        return scenarioDirectory;
    }

    private void setScenarioDirectory(String dir) {
        // Make sure it ends with a slash
        scenarioDirectory = dir;
    }

    /**
     * Checks whether a given scenario directory is a valid one. This is so when both exists and it contains some
     * valid scenario.
     *
     * @return Whether or not the scenario directory is valid
     */
    public boolean isValidScenarioDirectory() {
        File directory = new File(scenarioDirectory());
        File aScenario = new File(scenarioDirectory() + "req_rapide_1_240_24");

        if (directory.exists() && aScenario.exists()) {
            try {
                Gendreau06Parser.parse(aScenario);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }
}

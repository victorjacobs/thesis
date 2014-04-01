import com.google.common.collect.ImmutableList;
import common.auctioning.Auctioneer;
import common.baseline.SolverBidder;
import common.baseline.StubStateEvaluator;
import common.results.ParcelTrackerModel;
import common.results.ResultsPostProcessor;
import common.results.ResultsProcessor;
import common.truck.TruckConfiguration;
import common.truck.route.SolverRoutePlanner;
import org.apache.commons.cli.*;
import ra.evaluator.*;
import ra.parcel.AdaptiveSlackReAuctionableParcel;
import ra.parcel.ExponentialBackoffSlackReAuctionableParcel;
import ra.parcel.ReAuctionableParcel;
import rinde.logistics.pdptw.solver.MultiVehicleHeuristicSolver;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.pdptw.experiment.Experiment;
import rinde.sim.pdptw.gendreau06.Gendreau06ObjectiveFunction;
import rinde.sim.pdptw.gendreau06.Gendreau06Parser;
import rinde.sim.pdptw.gendreau06.Gendreau06Scenario;
import rinde.sim.pdptw.gendreau06.GendreauProblemClass;

import java.io.File;
import java.util.List;

import static com.google.common.collect.Lists.newLinkedList;

/**
 * Class that contains main method and testing setup.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
// TODO this needs to be refactored
public class Run {
	private static final String SCENARIOS_PATH = "files/scenarios/gendreau06/";
	private static final long SEED = 123L;

    private boolean quickRun = true; // Flag that toggles a fast run mode (only one repetition on one scenario) for debugging
    private int threads = Runtime.getRuntime().availableProcessors();
    private int repetitions = 10;
    private boolean showGui = false;

    public static void main(String[] args) throws Exception {
        // Set up command line
        Options opt = new Options();

        opt.addOption(OptionBuilder
                .withArgName("resultsDir")
                .hasArg()
                .withDescription("Directory to output results to")
                .create("o"));
        opt.addOption(OptionBuilder
                .withArgName("nbThreads")
                .hasArg()
                .withDescription("Number of threads, defaults to number of cores in system")
                .create("t"));
        opt.addOption(OptionBuilder
                .withArgName("repetitions")
                .hasArg()
                .withDescription("Number of repetitions, defaults to 10")
                .create("r"));
        opt.addOption(new Option("q", "Quick run: one repetition of one scenario"));
        opt.addOption(new Option("help", "Print this message"));
        opt.addOption(new Option("g", "Show gui"));

        CommandLineParser parser = new BasicParser();

        CommandLine cmd = parser.parse(opt, args);

        if (cmd.hasOption("help")) {
            (new HelpFormatter()).printHelp("java -jar Thesis.jar", opt);
            return;
        }

        if (!cmd.hasOption("q") && !cmd.hasOption("o")) {
            System.err.println("Missing option: o");

            (new HelpFormatter()).printHelp("java -jar Thesis.jar", opt);
            return;
        }

        new Run(cmd);
    }

	private Run(CommandLine cmd) throws Exception {
        // Extract some info from cli
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

        final long startTime = System.currentTimeMillis();

        ResultsProcessor result = performRAExperiment();
        //Experiment.ExperimentResults result = performRandomExperiments();
        //Experiment.ExperimentResults result = performAdaptiveSlackExperiment();
        //Experiment.ExperimentResults result = performAgentParcelExperiments();
        //ResultsProcessor result = performExponentialBackoffExperiments();

        System.out.println();

        if (quickRun) {
            System.out.println(result);
        } else {
            result.write(cmd.getOptionValue("o"));
        }

        System.out.println();
        System.out.println("Simulation took " + Math.round(((double) System.currentTimeMillis() - startTime) / 1000)
                + "s");
    }

    private ResultsProcessor performAdaptiveSlackExperiment() throws Exception {
        System.out.println("Doing adaptive slack experiment");

        final ObjectiveFunction objFunc = new Gendreau06ObjectiveFunction();
        Experiment.Builder builder = getExperimentBuilder(objFunc);

        // Do loop over int, than divide by 10 because floating point
        for (int i = 30; i >= 0; i -= 2) {
            builder = builder.addConfiguration(
                    new TruckConfiguration(
                            SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                            SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                            ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                            ImmutableList.of(AdaptiveSlackEvaluator.supplier((float) i / 10)),
                            ReAuctionableParcel.getCreator()
                    )
            );
        }

        return new ResultsProcessor(builder.perform());
    }

    private ResultsProcessor performRandomExperiments() throws Exception {
        System.out.println("Doing random experiment");

        final ObjectiveFunction objFunc = new Gendreau06ObjectiveFunction();
        Experiment.Builder builder = getExperimentBuilder(objFunc);

        for (int i = 0; i <= 50; i += 5) {
            builder = builder.addConfiguration(
                    new TruckConfiguration(
                            SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                            SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                            ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                            ImmutableList.of(RandomStateEvaluatorMultipleParcels.supplier(i)),
                            ReAuctionableParcel.getCreator()
                    )
            );
        }

        return new ResultsProcessor(builder.perform());
    }

    private ResultsProcessor performAgentParcelExperiments() throws Exception {
        System.out.println("Doing agent parcel experiment");

        final ObjectiveFunction objFunc = new Gendreau06ObjectiveFunction();
        Experiment.Builder builder = getExperimentBuilder(objFunc);

        // Do loop over int, than divide by 10 because floating point
        // Go through negative values, to force more re-auctions
        for (int i = 30; i >= -10; i -= 2) {
            builder = builder.addConfiguration(
                    new TruckConfiguration(
                            SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                            SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                            ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                            ImmutableList.of(AgentParcelSlackEvaluator.supplier()),
                            AdaptiveSlackReAuctionableParcel.getCreator((float) i / 10)
                    )
            );
        }

        return new ResultsProcessor(builder.perform());
    }

    private ResultsProcessor performExponentialBackoffExperiments() throws Exception {
        System.out.println("Doing agent exponential backoff experiments");

        final ObjectiveFunction objFunc = new Gendreau06ObjectiveFunction();
        Experiment.Builder builder = getExperimentBuilder(objFunc);

        // Do loop over int, than divide by 10 because floating point
        // Go through negative values, to force more re-auctions
        //for (int i = 20; i >= 10; i -= 1) {
            builder = builder.addConfiguration(
                    new TruckConfiguration(
                            SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                            SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                            ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                            ImmutableList.of(AgentParcelSlackEvaluator.supplier()),
                            ExponentialBackoffSlackReAuctionableParcel.getCreator(1, /*(float) i / 10*/ 0)
                    )
            );
        //}

        return new ResultsProcessor(builder.perform());
    }

	private ResultsProcessor performRAExperiment() throws Exception {
        final ObjectiveFunction objFunc = new Gendreau06ObjectiveFunction();
        Experiment.Builder builder = getExperimentBuilder(objFunc);

		builder = builder
				//.addScenario(Gendreau06Parser.parse(new File(SCENARIOS_PATH + "req_rapide_1_240_24")))
				/*.addConfiguration(
                        new TruckConfiguration(
                                SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                                ImmutableList.of(StubStateEvaluator.supplier()),
                                ReAuctionableParcel.getCreator()
                        )
                )
                /*.addConfiguration(
                        new TruckConfiguration(
                                SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                                ImmutableList.of(RandomStateEvaluator.supplier(10)),
                                ReAuctionableParcel.getCreator()
                        )
                )
                .addConfiguration(
                        new TruckConfiguration(
                                SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                                ImmutableList.of(RandomStateEvaluator.supplier(30)),
                                ReAuctionableParcel.getCreator()
                        )
                )*/
                /*.addConfiguration(
                        new TruckConfiguration(
                                SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                                ImmutableList.of(RandomStateEvaluatorMultipleParcels.supplier(10)),
                                ReAuctionableParcel.getCreator()
                        )
                )
                .addConfiguration(
                        new TruckConfiguration(
                                SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                                ImmutableList.of(RandomStateEvaluatorMultipleParcels.supplier(30)),
                                ReAuctionableParcel.getCreator()
                        )
                )
				/*.addConfiguration(
                        new TruckConfiguration(
                                SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                                ImmutableList.of(FixedSlackEvaluator.supplier()),
                                ReAuctionableParcel.getCreator()
                        )
                )*/
				/*.addConfiguration(
                        new TruckConfiguration(
                                SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                                ImmutableList.of(AdaptiveSlackEvaluator.supplier()),
                                ReAuctionableParcel.getCreator()
                        )
                )
                /*.addConfiguration(
                        new TruckConfiguration(
                                SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                                ImmutableList.of(AdaptiveSlackEvaluator.supplier()),
                                LimitedAuctionReAuctionableParcel.getCreator()
                        )
                )*/
                /*.addConfiguration(
                        new TruckConfiguration(
                                SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                                ImmutableList.of(AgentParcelSlackEvaluator.supplier()),
                                AdaptiveSlackReAuctionableParcel.getCreator()
                        )
                )
                /*.addConfiguration(
                        new TruckConfiguration(
                                SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                                ImmutableList.of(AgentParcelSlackEvaluatorUpdateOnChange.supplier()),
                                ImmutableList.of(AgentParcelSlackEvaluatorUpdateOnChange.supplier()),
                                AdaptiveSlackReAuctionableParcel.getCreator()
                        )
                )*/
                .addConfiguration(
                        new TruckConfiguration(
                                SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                                ImmutableList.of(AgentParcelSlackEvaluator.supplier()),
                                ExponentialBackoffSlackReAuctionableParcel.getCreator()
                        )
                );

        return new ResultsProcessor(builder.perform());
	}

    /**
     * Creates basic experiment builder, shared between different test setups. Takes a flag which toggles a shorter
     * run (only one scenario and one repetition).
     *
     * @param objFunc Objective function used in the experiments
     * @return Experiment builder object entirely setup, only need to add configurations and run it
     */
    private Experiment.Builder getExperimentBuilder(ObjectiveFunction objFunc) {
        final List<Gendreau06Scenario> onlineScenarios = Gendreau06Parser.parser()
                .addDirectory(SCENARIOS_PATH)
                .filter(GendreauProblemClass.SHORT_LOW_FREQ)
                .parse();

        Experiment.Builder builder = Experiment
                .build(objFunc)
                .withRandomSeed(SEED)
                .withThreads(threads)
                .usePostProcessor(new ResultsPostProcessor());

        if (quickRun) {
            System.out.println("Doing fast run");
            builder = builder
                    .addScenario(Gendreau06Parser.parse(new File(SCENARIOS_PATH + "req_rapide_1_240_24")))
                    .repeat(1);
            if (showGui) {
                builder = builder.showGui();
            }

            return builder;
        } else {
            return builder.addScenarios(onlineScenarios).repeat(repetitions);
        }
    }

}

import common.auctioning.Auctioneer;
import common.baseline.SolverBidder;
import common.baseline.StubStateEvaluator;
import common.results.ParcelTrackerModel;
import common.results.ResultDirectory;
import common.results.ResultsPostProcessor;
import common.results.ResultsProcessor;
import common.truck.TruckConfiguration;
import common.truck.route.SolverRoutePlanner;
import ra.evaluator.AdaptiveHeuristicEvaluator;
import ra.evaluator.AgentParcelHeuristicEvaluator;
import ra.evaluator.heuristic.SlackHeuristic;
import ra.parcel.ExponentialBackoffAdaptiveThresholdAgentParcel;
import ra.parcel.ReAuctionableParcel;
import rinde.logistics.pdptw.solver.MultiVehicleHeuristicSolver;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.pdptw.experiment.Experiment;
import rinde.sim.pdptw.gendreau06.Gendreau06ObjectiveFunction;
import rinde.sim.pdptw.gendreau06.Gendreau06Parser;
import rinde.sim.pdptw.gendreau06.Gendreau06Scenario;
import rinde.sim.pdptw.gendreau06.GendreauProblemClass;
import ui.DemoUICreator;

import java.io.File;
import java.util.List;


/**
 * Class that contains main method and testing setup.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
// TODO this needs to be refactored
public class Run {
	private static final long SEED = 123L;

    private final Configuration c;
    private final ObjectiveFunction objectiveFunction;

    // Top level results directory, add results to object
    private final ResultDirectory topDir;

    public static void main(String[] args) throws Exception {
        new Run(new Configuration(args));
    }

	private Run(Configuration c) throws Exception {
        objectiveFunction = new Gendreau06ObjectiveFunction();
        this.c = c;

        final long startTime = System.currentTimeMillis();
        topDir = new ResultDirectory(c.outDir());

        if (c.stop())
            return;

        if (!c.demoSetup().isPresent()) {
            System.err.println("This is a demo build, please supply demo setup");
            return;
        }

        Experiment.Builder b;

        switch (c.demoSetup().get()) {
            case 1:
                b = getExperimentBuilder("Parcel Exponential Backoff")
                        .addConfiguration(
                                getTruckConfigurationBuilder()
                                        .addStateEvaluator(AgentParcelHeuristicEvaluator.supplier(new SlackHeuristic()))
                                        .withParcelCreator(ExponentialBackoffAdaptiveThresholdAgentParcel.getCreator())
                                        .build()
                        );
                break;

            case 2:
                b = getExperimentBuilder("Everything disabled")
                        .addConfiguration(
                                getTruckConfigurationBuilder()
                                        .addStateEvaluator(StubStateEvaluator.supplier())
                                        .build()
                        );
                break;

            case 3:
                b = getExperimentBuilder("Truck adaptive")
                        .addConfiguration(
                                getTruckConfigurationBuilder()
                                        .addStateEvaluator(AdaptiveHeuristicEvaluator.supplier())
                                        .build()
                        );
                break;

            default:
                System.err.println("Invalid setup selector " + c.demoSetup().get());
                return;
        }

        topDir.addResult(new ResultsProcessor("main", b.perform()));

        System.out.println();

        if (c.outDir() == null) {
            System.out.println(topDir.toString());
        } else {
            topDir.write();
        }

        System.out.println();
        System.out.println("Simulation took " + Math.round(((double) System.currentTimeMillis() - startTime) / 1000)
                + "s");
    }

    /**
     * Creates basic experiment builder, shared between different test setups. Takes a flag which toggles a shorter
     * run (only one scenario and one repetition).
     *
     * @return Experiment builder object entirely setup, only need to add configurations and run it
     */
    private Experiment.Builder getExperimentBuilder() {
        return getExperimentBuilder("null");
    }

    private Experiment.Builder getExperimentBuilder(String setup) {
        final List<Gendreau06Scenario> onlineScenarios = Gendreau06Parser.parser()
                .addDirectory(c.scenarioDirectory())
                .filter(GendreauProblemClass.SHORT_LOW_FREQ)
                .parse();

        Experiment.Builder builder = Experiment
                .build(objectiveFunction)
                .withRandomSeed(SEED)
                .withThreads(c.threads())
                .usePostProcessor(new ResultsPostProcessor());

        if (c.quickrun() || c.demoSetup().isPresent()) {
            //System.out.println("Doing fast run");

            builder.addScenario(Gendreau06Parser.parse(new File(c.scenarioDirectory() + "req_rapide_1_240_24")))
                    .repeat(1);
            if (c.showGui() && c.demoSetup().isPresent()) {
                builder.showGui(new DemoUICreator(setup));
            }

            return builder;
        } else {
            return builder.addScenarios(onlineScenarios).repeat(c.repetitions());
        }
    }

    /**
     * Creates basic truck configuration builder that uses a {@link common.baseline.SolverBidder},
     * {@link common.truck.route.SolverRoutePlanner}, {@link ra.parcel.ReAuctionableParcel} and some shared models.
     *
     * @return Truck configuration builder with common settings set up
     */
    private TruckConfiguration.Builder getTruckConfigurationBuilder() {
        TruckConfiguration.Builder builder = new TruckConfiguration.Builder();

        builder.withBidder(SolverBidder.supplier(objectiveFunction, MultiVehicleHeuristicSolver.supplier(50, 1000)))
                .withRoutePlanner(SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)))
                .addModel(Auctioneer.supplier())
                .addModel(ParcelTrackerModel.supplier())
                .withParcelCreator(ReAuctionableParcel.getCreator());

        return builder;
    }

}

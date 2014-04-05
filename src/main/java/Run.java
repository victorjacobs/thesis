import common.auctioning.Auctioneer;
import common.baseline.SolverBidder;
import common.baseline.StubStateEvaluator;
import common.results.*;
import common.truck.TruckConfiguration;
import common.truck.route.SolverRoutePlanner;
import ra.evaluator.*;
import ra.parcel.AdaptiveSlackReAuctionableParcel;
import ra.parcel.ExponentialBackoffSlackReAuctionableParcel;
import ra.parcel.ReAuctionableParcel;
import rinde.logistics.pdptw.solver.MultiVehicleHeuristicSolver;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.pdptw.experiment.Experiment;
import rinde.sim.pdptw.gendreau06.Gendreau06ObjectiveFunction;
import rinde.sim.pdptw.gendreau06.Gendreau06Parser;
import rinde.sim.pdptw.gendreau06.Gendreau06Scenario;
import rinde.sim.pdptw.gendreau06.GendreauProblemClass;

import java.io.File;
import java.util.List;


/**
 * Class that contains main method and testing setup.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
// TODO this needs to be refactored
public class Run {
	private static final String SCENARIOS_PATH = "files/scenarios/gendreau06/";
	private static final long SEED = 123L;

    private final Cli c;
    private final ObjectiveFunction objectiveFunction;

    // Top level results directory, add results to object
    private final ResultDirectory topDir;

    public static void main(String[] args) throws Exception {
        new Run(new Cli(args));
    }

	private Run(Cli c) throws Exception {
        objectiveFunction = new Gendreau06ObjectiveFunction();
        this.c = c;

        final long startTime = System.currentTimeMillis();
        topDir = new ResultDirectory(c.outDir());

        //performRAExperiment();
        performRandomExperiments();
        performAdaptiveSlackExperiment();
        performAgentParcelExperiments();
        performExponentialBackoffExperiments();
        //performAllScenariosSeperated();

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

    private void performAdaptiveSlackExperiment() throws Exception {
        System.out.println("Doing adaptive slack experiment");

        Experiment.Builder builder = getExperimentBuilder();

        // Do loop over int, than divide by 10 because floating point
        for (int i = 300; i >= 200; i -= 5) {
            builder.addConfiguration(
                    getTruckConfigurationBuilder()
                        .addStateEvaluator(AdaptiveSlackEvaluator.supplier((float) i / 100))
                        .build()
            );
        }

        topDir.addResult(new ResultsProcessor("adaptiveVaryingThreshold", builder.perform()));
    }

    private void performRandomExperiments() throws Exception {
        System.out.println("Doing random experiment");

        Experiment.Builder builder = getExperimentBuilder();

        for (int i = 0; i <= 120; i += 6) {
            builder.addConfiguration(
                    getTruckConfigurationBuilder()
                        .addStateEvaluator(RandomStateEvaluatorMultipleParcels.supplier((float) i / 10))
                        .build()
            );
        }

        topDir.addResult(new ResultsProcessor("randomVaryingPercentages", builder.perform()));
    }

    private void performAgentParcelExperiments() throws Exception {
        System.out.println("Doing agent parcel experiment");

        Experiment.Builder builder = getExperimentBuilder();

        // Do loop over int, than divide by 10 because floating point
        // Go through negative values, to force more re-auctions
        for (int i = 300; i >= 40; i -= 13) {
            builder.addConfiguration(
                    getTruckConfigurationBuilder()
                        .addStateEvaluator(AgentParcelSlackEvaluator.supplier())
                        .withParcelCreator(AdaptiveSlackReAuctionableParcel.getCreator((float) i / 100))
                        .build()
            );
        }

        topDir.addResult(new ResultsProcessor("agentParcelVaryingThreshold", builder.perform()));
    }

    private void performExponentialBackoffExperiments() throws Exception {
        System.out.println("Doing agent exponential backoff experiments");

        Experiment.Builder builder = getExperimentBuilder();

        // Do loop over int, than divide by 10 because floating point
        // Go through negative values, to force more re-auctions
        for (int i = 30; i >= -10; i -= 2) {
            builder.addConfiguration(
                    getTruckConfigurationBuilder()
                            .addStateEvaluator(AgentParcelSlackEvaluator.supplier())
                            .withParcelCreator(ExponentialBackoffSlackReAuctionableParcel.getCreator((float) i / 10, 2))
                            .build()
            );
        }

        topDir.addResult(new ResultsProcessor("agentParcelExponentialBackoff", builder.perform()));
    }

    private void performAllScenariosSeperated() throws Exception {
        System.out.println("WARNING: this might take a while");

        Experiment.Builder builder;
        File d = new File(SCENARIOS_PATH);

        for (File scenarioFile : d.listFiles()) {
            System.out.println("Starting " + scenarioFile.getName());

            builder = Experiment
                    .build(objectiveFunction)
                    .withRandomSeed(SEED)
                    .withThreads(c.threads())
                    .usePostProcessor(new ResultsPostProcessor())
                    .addScenario(Gendreau06Parser.parse(scenarioFile))
                    .repeat(c.repetitions())
                    .addConfiguration(
                            getTruckConfigurationBuilder()
                                    .addStateEvaluator(StubStateEvaluator.supplier())
                                    .build()
                    )
                    .addConfiguration(
                            getTruckConfigurationBuilder()
                                    .withParcelCreator(ExponentialBackoffSlackReAuctionableParcel.getCreator())
                                    .addStateEvaluator(AgentParcelSlackEvaluator.supplier())
                                    .build()
                    );

            topDir.addResult(new ResultsProcessor(scenarioFile.getName(), builder.perform()));
        }
    }

	private void performRAExperiment() throws Exception {
		Experiment.Builder builder = getExperimentBuilder()
                /*.addConfiguration(
                        getTruckConfigurationBuilder(objFunc)
                            .addStateEvaluator(StubStateEvaluator.supplier())
                            .build()
                )*/
                /*.addConfiguration(
                        getTruckConfigurationBuilder(objFunc)
                                .addStateEvaluator(RandomStateEvaluatorMultipleParcels.supplier(5))
                                .build()
                )
                .addConfiguration(
                        getTruckConfigurationBuilder(objFunc)
                                .addStateEvaluator(RandomStateEvaluatorMultipleParcels.supplier(1))
                                .build()
                )
                .addConfiguration(
                        getTruckConfigurationBuilder(objFunc)
                                .addStateEvaluator(FixedSlackEvaluator.supplier())
                                .build()
                )
                .addConfiguration(
                        getTruckConfigurationBuilder(objFunc)
                                .addStateEvaluator(AdaptiveSlackEvaluator.supplier())
                                .build()
                )
                .addConfiguration(
                        getTruckConfigurationBuilder(objFunc)
                                .addStateEvaluator(AdaptiveSlackEvaluator.supplier())
                                .withParcelCreator(LimitedAuctionReAuctionableParcel.getCreator())
                                .build()
                )*/
                .addConfiguration(
                        getTruckConfigurationBuilder()
                                .addStateEvaluator(AgentParcelSlackEvaluator.supplier())
                                .withParcelCreator(AdaptiveSlackReAuctionableParcel.getCreator())
                                .build()
                )
                /*.addConfiguration(
                        getTruckConfigurationBuilder(objFunc)
                            .addStateEvaluator(AgentParcelSlackEvaluatorUpdateOnChange.supplier())
                            .addStateObserver(AgentParcelSlackEvaluatorUpdateOnChange.supplier())
                            .withParcelCreator(AdaptiveSlackReAuctionableParcel.getCreator())
                            .build()
                )*/
                /*.addConfiguration(
                        getTruckConfigurationBuilder(objFunc)
                            .addStateEvaluator(AgentParcelSlackEvaluator.supplier())
                            .withParcelCreator(ExponentialBackoffSlackReAuctionableParcel.getCreator())
                            .build()
                )*/
                /*.addConfiguration(
                        getTruckConfigurationBuilder(objFunc)
                            .addStateEvaluator(AgentParcelSlackEvaluator.supplier())
                            .withParcelCreator(ExponentialBackoffRandomSelectionReAuctionableParcel.getCreator(2, 2))
                            .build()
                )*/;

        topDir.addResult(new ResultsProcessor("main", builder.perform()));
	}

    /**
     * Creates basic experiment builder, shared between different test setups. Takes a flag which toggles a shorter
     * run (only one scenario and one repetition).
     *
     * @return Experiment builder object entirely setup, only need to add configurations and run it
     */
    private Experiment.Builder getExperimentBuilder() {
        final List<Gendreau06Scenario> onlineScenarios = Gendreau06Parser.parser()
                .addDirectory(SCENARIOS_PATH)
                .filter(GendreauProblemClass.SHORT_LOW_FREQ)
                .parse();

        Experiment.Builder builder = Experiment
                .build(objectiveFunction)
                .withRandomSeed(SEED)
                .withThreads(c.threads())
                .usePostProcessor(new ResultsPostProcessor());

        if (c.quickrun()) {
            System.out.println("Doing fast run");
            builder
                    .addScenario(Gendreau06Parser.parse(new File(SCENARIOS_PATH + "req_rapide_1_240_24")))
                    .repeat(1);
            if (c.showGui()) {
                builder.showGui();
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

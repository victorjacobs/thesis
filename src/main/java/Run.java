import com.google.common.collect.ImmutableList;
import common.auctioning.Auctioneer;
import common.baseline.SolverBidder;
import common.results.ParcelTrackerModel;
import common.results.ResultsProcessor;
import common.results.ResultsPostProcessor;
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

    public static void main(String[] args) throws Exception {
        new Run(new Cli(args));
    }

	private Run(Cli c) throws Exception {
        this.c = c;
        final long startTime = System.currentTimeMillis();

        ResultsProcessor result = performRAExperiment();
        //Experiment.ExperimentResults result = performRandomExperiments();
        //Experiment.ExperimentResults result = performAdaptiveSlackExperiment();
        //Experiment.ExperimentResults result = performAgentParcelExperiments();
        //ResultsProcessor result = performExponentialBackoffExperiments();

        System.out.println();

        if (c.quickrun()) {
            System.out.println(result);
        } else {
            result.write(c.outDir());
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

        return new ResultsProcessor("adaptiveDifferentThreshold", builder.perform());
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

        return new ResultsProcessor("randomDifferentPercentages", builder.perform());
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

        return new ResultsProcessor("agentParcelDifferentThreshold", builder.perform());
    }

    private ResultsProcessor performExponentialBackoffExperiments() throws Exception {
        System.out.println("Doing agent exponential backoff experiments");

        final ObjectiveFunction objFunc = new Gendreau06ObjectiveFunction();
        Experiment.Builder builder = getExperimentBuilder(objFunc);

        // Do loop over int, than divide by 10 because floating point
        // Go through negative values, to force more re-auctions
        for (int i = 20; i >= 10; i -= 1) {
            builder = builder.addConfiguration(
                    new TruckConfiguration(
                            SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                            SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                            ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                            ImmutableList.of(AgentParcelSlackEvaluator.supplier()),
                            ExponentialBackoffSlackReAuctionableParcel.getCreator(1, (float) i / 10)
                    )
            );
        }

        return new ResultsProcessor("agentParcelExponentialBackoff", builder.perform());
    }

	private ResultsProcessor performRAExperiment() throws Exception {
        final ObjectiveFunction objFunc = new Gendreau06ObjectiveFunction();

		Experiment.Builder builder = getExperimentBuilder(objFunc)
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
                                ImmutableList.of(RandomStateEvaluatorMultipleParcels.supplier(5)),
                                ReAuctionableParcel.getCreator()
                        )
                )
                .addConfiguration(
                        new TruckConfiguration(
                                SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                                ImmutableList.of(RandomStateEvaluatorMultipleParcels.supplier(1)),
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
                .addConfiguration(
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
                /*.addConfiguration(
                        new TruckConfiguration(
                                SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                                ImmutableList.of(AgentParcelSlackEvaluator.supplier()),
                                ExponentialBackoffSlackReAuctionableParcel.getCreator()
                        )
                )
                .addConfiguration(
                        new TruckConfiguration(
                                SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                                ImmutableList.of(AgentParcelSlackEvaluator.supplier()),
                                ExponentialBackoffRandomSelectionReAuctionableParcel.getCreator(2, 2)
                        )
                )*/;

        return new ResultsProcessor("main", builder.perform());
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
                .withThreads(c.threads())
                .usePostProcessor(new ResultsPostProcessor());

        if (c.quickrun()) {
            System.out.println("Doing fast run");
            builder = builder
                    .addScenario(Gendreau06Parser.parse(new File(SCENARIOS_PATH + "req_rapide_1_240_24")))
                    .repeat(1);
            if (c.showGui()) {
                builder = builder.showGui();
            }

            return builder;
        } else {
            return builder.addScenarios(onlineScenarios).repeat(c.repetitions());
        }
    }

}

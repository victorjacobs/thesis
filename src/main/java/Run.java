import com.google.common.collect.ImmutableList;
import common.auctioning.Auctioneer;
import common.baseline.SolverBidder;
import common.results.ParcelTrackerModel;
import common.results.ResultsPostProcessor;
import common.results.ResultsProcessor;
import common.truck.TruckConfiguration;
import common.truck.route.SolverRoutePlanner;
import ra.evaluator.*;
import ra.parcel.AdaptiveSlackReAuctionableParcel;
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
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class Run {

	private static final String SCENARIOS_PATH = "files/scenarios/gendreau06/";
	private static final long SEED = 123L;
    private static boolean FAST = true;

	private Run() {}

	public static void main(String[] args) throws Exception {
        final long startTime = System.currentTimeMillis();
        boolean localRun = args.length < 1;

        FAST = localRun;

		//Experiment.ExperimentResults result = performRAExperiment();
        //Experiment.ExperimentResults result = performRandomExperiments();
        Experiment.ExperimentResults result = performAdaptiveSlackExperiment();
        //Experiment.ExperimentResults result = performAgentParcelExperiments();

		System.out.println();

		ResultsProcessor processor = new ResultsProcessor(result);

        if (localRun) {
            System.out.println(processor);
        } else {
            processor.write(args[0]);
        }

        System.out.println();
        System.out.println("Simulation took " + Math.round(System.currentTimeMillis() - startTime / 1000));
	}

    private static Experiment.ExperimentResults performAdaptiveSlackExperiment() throws Exception {
        System.out.println("Doing adaptive slack experiment");

        final ObjectiveFunction objFunc = new Gendreau06ObjectiveFunction();
        Experiment.Builder builder = getExperimentBuilder(objFunc, FAST);

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

        return builder.perform();
    }

    private static Experiment.ExperimentResults performRandomExperiments() throws Exception {
        System.out.println("Doing random experiment");

        final ObjectiveFunction objFunc = new Gendreau06ObjectiveFunction();
        Experiment.Builder builder = getExperimentBuilder(objFunc, FAST);

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

        return builder.perform();
    }

    private static Experiment.ExperimentResults performAgentParcelExperiments() throws Exception {
        System.out.println("Doing agent parcel experiment");

        final ObjectiveFunction objFunc = new Gendreau06ObjectiveFunction();
        Experiment.Builder builder = getExperimentBuilder(objFunc, FAST);

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

        return builder.perform();
    }

	private static Experiment.ExperimentResults performRAExperiment() throws Exception {
        final ObjectiveFunction objFunc = new Gendreau06ObjectiveFunction();
        Experiment.Builder builder = getExperimentBuilder(objFunc, FAST);

		return builder
				//.addScenario(Gendreau06Parser.parse(new File(SCENARIOS_PATH + "req_rapide_1_240_24")))
				/*.addConfiguration(
                        new TruckConfiguration(
                                SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                                ImmutableList.of(StubStateEvaluator.supplier()),
                                ReAuctionableParcel.getCreator()
                        )
                )*/
                .addConfiguration(
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
                )
                .addConfiguration(
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
                .addConfiguration(
                        new TruckConfiguration(
                                SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                                ImmutableList.of(AdaptiveSlackEvaluatorApache.supplier()),
                                ReAuctionableParcel.getCreator()
                        )
                )
                /*.addConfiguration(
                        new TruckConfiguration(
                                SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                                ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                                ImmutableList.of(AdaptiveSlackEvaluator.supplier()),
                                FixedSlackReAuctionableParcel.getCreator()
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
                )*/
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
				.perform();
	}

    /**
     * Creates basic experiment builder, shared between different test setups. Takes a flag which toggles a shorter
     * run (only one scenario and one repetition).
     *
     * @param objFunc Objective function used in the experiments
     * @param fast Flag that toggles a fast run mode (only one repetition on one scenario) for debugging
     * @return Experiment builder object entirely setup, only need to add configurations and run it
     */
    private static Experiment.Builder getExperimentBuilder(ObjectiveFunction objFunc, boolean fast) {
        int threads = Runtime.getRuntime().availableProcessors();
        int repetitions = 10;

        final List<Gendreau06Scenario> onlineScenarios = Gendreau06Parser.parser()
                .addDirectory(SCENARIOS_PATH)
                .filter(GendreauProblemClass.SHORT_LOW_FREQ)
                .parse();

        Experiment.Builder builder = Experiment
                .build(objFunc)
                .withRandomSeed(SEED)
                .withThreads(threads)
                .usePostProcessor(new ResultsPostProcessor());

        if (fast) {
            System.out.println("Doing fast run");
            return builder
                    .addScenario(Gendreau06Parser.parse(new File(SCENARIOS_PATH + "req_rapide_1_240_24")))
                    .repeat(1);
        } else {
            return builder.addScenarios(onlineScenarios).repeat(repetitions);
        }
    }

}

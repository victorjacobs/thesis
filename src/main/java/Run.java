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

	private static final int THREADS = Runtime.getRuntime().availableProcessors();
	private static final int REPETITIONS = 10;
	private static final long SEED = 123L;

	private Run() {}

	public static void main(String[] args) throws Exception {
		String outputDirectory = (args.length < 1) ? "results/test" + System.currentTimeMillis() + "/" : args[0];

		Experiment.ExperimentResults result = performRAExperiment();
        //Experiment.ExperimentResults result = performRandomExperiments();

		System.out.println();

		ResultsProcessor processor = new ResultsProcessor(result);

		processor.write(outputDirectory);

		System.out.println();

	}

    private static Experiment.ExperimentResults performRandomExperiments() throws Exception {
        final ObjectiveFunction objFunc = new Gendreau06ObjectiveFunction();
        final List<Gendreau06Scenario> onlineScenarios = Gendreau06Parser.parser()
                .addDirectory(SCENARIOS_PATH)
                .filter(GendreauProblemClass.SHORT_LOW_FREQ)
                .parse();

        Experiment.Builder exp = Experiment
                .build(objFunc)
                .withRandomSeed(SEED)
                .repeat(REPETITIONS)
                .withThreads(THREADS)
                .addScenarios(onlineScenarios)
                .usePostProcessor(new ResultsPostProcessor());

        for (int i = 1; i < 10; i += 5) {
            exp = exp.addConfiguration(
                    new TruckConfiguration(
                            SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
                            SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
                            ImmutableList.of(Auctioneer.supplier(), ParcelTrackerModel.supplier()),
                            ImmutableList.of(RandomStateEvaluator.supplier(i)),
                            ReAuctionableParcel.getCreator()
                    )
            );
        }

        return exp.perform();
    }

	private static Experiment.ExperimentResults performRAExperiment() throws Exception {
		final ObjectiveFunction objFunc = new Gendreau06ObjectiveFunction();
		final List<Gendreau06Scenario> onlineScenarios = Gendreau06Parser.parser()
				.addDirectory(SCENARIOS_PATH)
				.filter(GendreauProblemClass.SHORT_LOW_FREQ)
                .parse();

		return Experiment
				.build(objFunc)
				.withRandomSeed(SEED)
				.repeat(REPETITIONS)
				.withThreads(THREADS)
				.addScenarios(onlineScenarios)
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
				.addConfiguration(
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
                )
				/*.addConfiguration(
						new TruckConfiguration(
								SolverRoutePlanner.supplier(CheapestInsertionHeuristic.supplier(objFunc)),
								SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
								ImmutableList.of(Auctioneer.supplier()),
								ImmutableList.of(RandomStateEvaluator.supplier())
						)
				)
				.addConfiguration(
						new TruckConfiguration(
								SolverRoutePlanner.supplier(CheapestInsertionHeuristic.supplier(objFunc)),
								SolverBidder.supplier(objFunc, CheapestInsertionHeuristic.supplier(objFunc)),
								ImmutableList.of(Auctioneer.supplier()),
								ImmutableList.of(LocalStateEvaluator.supplier())))
				.addConfiguration(
						new TruckConfiguration(
								SolverRoutePlanner.supplier(CheapestInsertionHeuristic.supplier(objFunc)),
								SolverBidder.supplier(objFunc, CheapestInsertionHeuristic.supplier(objFunc)),
								ImmutableList.of(Auctioneer.supplier()),
								ImmutableList.of(AdaptiveLocalStateEvaluator.supplier())))*/
				//.showGui()
				/*.addConfiguration(
						new TruckConfiguration(
								SolverRoutePlanner.supplier(CheapestInsertionHeuristic.supplier(objFunc)),
								SolverBidder.supplier(objFunc, CheapestInsertionHeuristic.supplier(objFunc)),
								ImmutableList.of(Auctioneer.supplier()),
								ImmutableList.<SupplierRng<? extends StateEvaluator>>of()))*/
				.usePostProcessor(new ResultsPostProcessor())
				.perform();
	}

}

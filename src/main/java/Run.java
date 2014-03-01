import com.google.common.collect.ImmutableList;
import common.ParcelTracker;
import common.ResultsProcessor;
import common.Stats;
import common.auctioning.Auctioneer;
import common.auctioning.ReAuctionableParcel;
import common.baseline.SolverBidder;
import common.truck.TruckConfiguration;
import common.truck.route.SolverRoutePlanner;
import ra.LocalStateEvaluator;
import ra.RandomStateEvaluator;
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

	private static final int THREADS = 2;
	private static final int REPETITIONS = 3;
	private static final long SEED = 123L;

	private Run() {}

	public static void main(String[] args) throws Exception {
		String outputDirectory = (args.length < 1) ? "results/test" + System.currentTimeMillis() + "/" : args[0];

		Experiment.ExperimentResults result = performRAExperiment();

		ResultsProcessor w = new ResultsProcessor(result);

		System.out.println(w.toString());

		//w.write(outputDirectory);

		System.out.println();

		Stats.print();

		System.out.println();

		// Temp
		for (String a : ParcelTracker.getParcels().keySet()) {
			int max = 0;
			System.out.println(a);

			for (ReAuctionableParcel p : ParcelTracker.getParcels().get(a)) {
				max = (p.getOwnerHistory().size() > max) ? p.getOwnerHistory().size() : max;
			}
			System.out.println("Max num of reauctions/parcel: " + max);

			System.out.println();
		}

	}

	private static Experiment.ExperimentResults performRAExperiment() throws Exception {
		final ObjectiveFunction objFunc = new Gendreau06ObjectiveFunction();
		final List<Gendreau06Scenario> onlineScenarios = Gendreau06Parser.parser()
				.addDirectory(SCENARIOS_PATH)
				.filter(GendreauProblemClass.SHORT_LOW_FREQ).parse();

		return Experiment
				.build(objFunc)
				.withRandomSeed(SEED)
				.repeat(REPETITIONS)
				.withThreads(THREADS)
				//.addScenarios(onlineScenarios)
				.addScenario(Gendreau06Parser.parse(new File(SCENARIOS_PATH + "req_rapide_1_240_24")))
				/*.addConfiguration(
						new TruckConfiguration(
								SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
								SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
								ImmutableList.of(Auctioneer.supplier()),
								ImmutableList.of(StubStateEvaluator.supplier())
						)
				)*/
				.addConfiguration(
						new TruckConfiguration(
								SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
								SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
								ImmutableList.of(Auctioneer.supplier()),
								ImmutableList.of(RandomStateEvaluator.supplier())
						)
				)
				.addConfiguration(
						new TruckConfiguration(
								SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
								SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
								ImmutableList.of(Auctioneer.supplier()),
								ImmutableList.of(LocalStateEvaluator.supplier())))
				/*.addConfiguration(
						new TruckConfiguration(
								SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
								SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
								ImmutableList.of(Auctioneer.supplier()),
								ImmutableList.of(AdaptiveLocalStateEvaluator.supplier())))
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
				.perform();
	}

}

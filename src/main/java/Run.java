import com.google.common.collect.ImmutableList;
import common.Auctioneer;
import common.baseline.SolverBidder;
import common.truck.Stats;
import common.truck.TruckConfiguration;
import common.truck.route.SolverRoutePlanner;
import ra.AdaptiveLocalStateEvaluator;
import ra.LocalStateEvaluator;
import ra.RandomStateEvaluator;
import rinde.logistics.pdptw.solver.CheapestInsertionHeuristic;
import rinde.logistics.pdptw.solver.MultiVehicleHeuristicSolver;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.pdptw.experiment.Experiment;
import rinde.sim.pdptw.gendreau06.Gendreau06ObjectiveFunction;
import rinde.sim.pdptw.gendreau06.Gendreau06Parser;
import rinde.sim.pdptw.gendreau06.Gendreau06Scenario;
import rinde.sim.pdptw.gendreau06.GendreauProblemClass;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: victor
 * Date: 14/10/13
 * Time: 15:56
 */
public class Run {

	private static final String SCENARIOS_PATH = "files/scenarios/gendreau06/";

	private static final int THREADS = 2;
	private static final int REPETITIONS = 1;
	private static final long SEED = 123L;

	private Run() {}

	public static void main(String[] args) throws Exception {
		final ObjectiveFunction objFunc = new Gendreau06ObjectiveFunction();

		Experiment.ExperimentResults result = performRAExperiment();

		System.out.println();

		String[] temp;

		for (Experiment.SimulationResult res : result.results) {
			temp = res.masConfiguration.toString().split("-");

			System.out.println(temp[temp.length - 1] + " Total overtime: " + res.stats.overTime);
			System.out.println(temp[temp.length - 1] + " Total distance: " + res.stats.totalDistance);
			System.out.println(temp[temp.length - 1] + " Objfunc: " + objFunc.computeCost(res.stats));
			System.out.println(temp[temp.length - 1] + " Computation time: " + res.stats.computationTime);
		}

		Stats.print();
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
				.addScenarios(onlineScenarios)
				/*.addScenario(Gendreau06Parser.parse(SCENARIOS_PATH + "req_rapide_1_240_24", 10))*/
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
				.addConfiguration(
						new TruckConfiguration(
								SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
								SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
								ImmutableList.of(Auctioneer.supplier()),
								ImmutableList.of(AdaptiveLocalStateEvaluator.supplier())))
				.addConfiguration(
						new TruckConfiguration(
								SolverRoutePlanner.supplier(CheapestInsertionHeuristic.supplier(objFunc)),
								SolverBidder.supplier(objFunc, CheapestInsertionHeuristic.supplier(objFunc)),
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
								ImmutableList.of(AdaptiveLocalStateEvaluator.supplier())))
				//.showGui()
				.perform();
	}

}

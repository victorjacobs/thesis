import com.google.common.collect.ImmutableList;
import common.Auctioneer;
import common.baseline.SolverBidder;
import common.truck.Stats;
import common.truck.TruckConfiguration;
import common.truck.route.SolverRoutePlanner;
import ra.AdaptiveLocalStateEvaluator;
import rinde.logistics.pdptw.solver.MultiVehicleHeuristicSolver;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.pdptw.experiment.Experiment;
import rinde.sim.pdptw.gendreau06.Gendreau06ObjectiveFunction;
import rinde.sim.pdptw.gendreau06.Gendreau06Parser;

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

	private Run() {
	}

	public static void main(String[] args) throws Exception {
		final ObjectiveFunction objFunc = new Gendreau06ObjectiveFunction();

//		final Gendreau06Scenarios onlineScenarios = new Gendreau06Scenarios(
//				SCENARIOS_PATH, true, GendreauProblemClass.SHORT_LOW_FREQ);


//		Experiment.ExperimentResults res = performCAExperiment();

		Experiment.ExperimentResults result = performRAExperiment();

		System.out.println();

		String[] temp;

		for (Experiment.SimulationResult res : result.results) {
			temp = res.masConfiguration.toString().split("-");

			System.out.println(temp[temp.length - 1] + " Total overtime: " + res.stats.overTime);
			System.out.println(temp[temp.length - 1] + " Total distance: " + res.stats.totalDistance);
		}

		Stats.print();
	}

	private static Experiment.ExperimentResults performRAExperiment() throws Exception {
		final ObjectiveFunction objFunc = new Gendreau06ObjectiveFunction();

		return Experiment
				.build(objFunc)
				.withRandomSeed(SEED)
				.repeat(REPETITIONS)
				.withThreads(THREADS)
				.addScenario(Gendreau06Parser.parse(SCENARIOS_PATH + "req_rapide_1_240_24", 10))
				/*.addConfiguration(
						new TruckConfiguration(
								SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
								SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
								ImmutableList.of(Auctioneer.supplier()),
								ImmutableList.of(StubStateEvaluator.supplier())
						)
				)
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
								ImmutableList.of(LocalStateEvaluator.supplier())))*/
				.addConfiguration(
						new TruckConfiguration(
								SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
								SolverBidder.supplier(objFunc, MultiVehicleHeuristicSolver.supplier(50, 1000)),
								ImmutableList.of(Auctioneer.supplier()),
								ImmutableList.of(AdaptiveLocalStateEvaluator.supplier())))
				//.showGui()
				.perform();
	}

}

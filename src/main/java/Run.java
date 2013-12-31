import com.google.common.collect.ImmutableList;
import common.Auctioneer;
import common.baseline.RandomBidder;
import common.truck.TruckConfiguration;
import common.truck.route.SolverRoutePlanner;
import ra.RandomStateEvaluator;
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

	private static final int THREADS = 1;
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

		for (Experiment.SimulationResult res : result.results) {
			System.out.println(res.toString());
		}
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
						new rinde.logistics.pdptw.mas.TruckConfiguration(rinde.logistics.pdptw.mas.route.SolverRoutePlanner
								.supplier(MultiVehicleHeuristicSolver.supplier(200, 50000)),
								InsertionCostBidder.supplier(objFunc), ImmutableList
								.of(AuctionCommModel.supplier())))*/
				.addConfiguration(
						new TruckConfiguration(
								SolverRoutePlanner.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
								RandomBidder.supplier(),
								ImmutableList.of(Auctioneer.supplier()),
								ImmutableList.of(RandomStateEvaluator.supplier())))
				//.showGui()
				.perform();
	}

	// TODO: prettify
//	private static Experiment.ExperimentResults performCAExperiment() throws Exception {
//		final ObjectiveFunction objFunc = new Gendreau06ObjectiveFunction();
//
//		return Experiment
//				.build(objFunc)
//				.withRandomSeed(SEED)
//				.repeat(REPETITIONS)
//				.withThreads(THREADS)
//				.addScenario(Gendreau06Parser.parse(SCENARIOS_PATH + "req_rapide_1_240_24", 10))
//				.addConfiguration(
//						new TruckConfiguration(SolverRoutePlanner
//								.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
//								SolverBidder.supplier(objFunc,
//										MultiVehicleHeuristicSolver.supplier(50, 100)),
//								ImmutableList.of(AuctionCommModel.supplier())))
//				.addConfiguration(
//						new TruckConfiguration(SolverRoutePlanner
//								.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
//								NaiveCombAuctionBidder.supplier(objFunc),
//								ImmutableList.of(CombAuctionCommModel.supplier())))
//				.showGui()
//				.perform();
//	}

}

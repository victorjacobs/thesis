package experimental;

import com.google.common.collect.ImmutableList;
import rinde.logistics.pdptw.mas.TruckConfiguration;
import rinde.logistics.pdptw.mas.comm.AuctionCommModel;
import rinde.logistics.pdptw.mas.comm.SolverBidder;
import rinde.logistics.pdptw.mas.route.SolverRoutePlanner;
import rinde.logistics.pdptw.solver.MultiVehicleHeuristicSolver;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.pdptw.experiment.Experiment;
import rinde.sim.pdptw.gendreau06.Gendreau06ObjectiveFunction;
import rinde.sim.pdptw.gendreau06.Gendreau06Parser;
import rinde.sim.pdptw.gendreau06.Gendreau06Scenarios;
import rinde.sim.pdptw.gendreau06.GendreauProblemClass;

/**
 * Created with IntelliJ IDEA.
 * User: victor
 * Date: 14/10/13
 * Time: 15:56
 */
public class Run {

	private static final String SCENARIOS_PATH = "files/scenarios/gendreau06/";

	private static final int THREADS = 2;
	private static final int REPETITIONS = 2;
	private static final long SEED = 123L;

	private Run() {}

	public static void main(String[] args) throws Exception {
		final ObjectiveFunction objFunc = new Gendreau06ObjectiveFunction();

		final Gendreau06Scenarios onlineScenarios = new Gendreau06Scenarios(
				SCENARIOS_PATH, true, GendreauProblemClass.SHORT_LOW_FREQ);
		final Experiment.ExperimentResults onlineResults = Experiment
				.build(objFunc)
				.withRandomSeed(SEED)
				.repeat(REPETITIONS)
				.withThreads(THREADS)
				.addScenario(Gendreau06Parser.parse(SCENARIOS_PATH + "req_rapide_1_240_24", 10))
				.addConfiguration(
						new TruckConfiguration(SolverRoutePlanner
								.supplier(MultiVehicleHeuristicSolver.supplier(50, 1000)),
								SolverBidder.supplier(objFunc,
										MultiVehicleHeuristicSolver.supplier(50, 100)),
								ImmutableList.of(AuctionCommModel.supplier())))
				//.showGui()
				.perform();
		System.out.println(onlineResults.results.get(0).stats);
	}

}

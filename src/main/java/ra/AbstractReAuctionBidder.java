package ra;

import common.SolverBidder;
import org.apache.commons.math3.random.MersenneTwister;
import rinde.logistics.pdptw.solver.MultiVehicleHeuristicSolver;
import rinde.sim.core.TickListener;
import rinde.sim.core.TimeLapse;
import rinde.sim.event.Event;
import rinde.sim.pdptw.central.Solver;
import rinde.sim.pdptw.gendreau06.Gendreau06ObjectiveFunction;

import java.util.Random;

/**
 * Provides base for all re-auction implementations, more specifically a method that allows the bidder to re-evaluate
 * the assigned parcels regularly.
 *
 * TODO: use tick() or afterTick()?
 * TODO: since Java lacks double dispatch + want to extend SolverBidder, the get bid functions return doubles and not Bids
 * 		-> Change Bidder interface to use Bids instead of doubles?
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class AbstractReAuctionBidder extends SolverBidder implements TickListener, ReAuctionBidder {

	protected ReAuctionCommModel commModel;
	protected int ticksUntilNextEvaluation;
	// Probably a lot of implementations will use an rng generator, provide one TODO: is this useful?
	protected Random rng;

	public AbstractReAuctionBidder() {
		super(new Gendreau06ObjectiveFunction(), (Solver) new MultiVehicleHeuristicSolver(new MersenneTwister(123), 50, 1000));
		rng = new Random();
		ticksUntilNextEvaluation = getDelay();
	}

	@Override
	public void tick(TimeLapse timeLapse) {
	}

	@Override
	public void afterTick(TimeLapse timeLapse) {
		if (--ticksUntilNextEvaluation == 0) {
			reEvaluateParcels();
			// Reset counter
			ticksUntilNextEvaluation = getDelay();
		}
	}

	@Override
	public void register(ReAuctionCommModel model) {
		commModel = model;
	}

	/**
	 * Notify the route planner that some change has happened
	 */
	protected void notifyChange() {
		eventDispatcher
				.dispatchEvent(new Event(CommunicatorEventType.CHANGE, this));
	}

	/**
	 * Get new ticksUntilNextEvaluation value
	 */
	protected abstract int getDelay();

	/**
	 * Describes behaviour what to do when bids should be re-evaluated
	 */
	protected abstract void reEvaluateParcels();
}

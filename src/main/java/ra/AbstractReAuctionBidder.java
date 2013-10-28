package ra;

import common.AbstractBidder;
import rinde.sim.core.TickListener;
import rinde.sim.core.TimeLapse;

import java.util.Random;

/**
 * Provides base for all re-auction implementations, more specifically a method that allows the bidder to re-evaluate
 * the assigned parcels regularly.
 *
 * TODO: use tick() or afterTick()?
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class AbstractReAuctionBidder extends AbstractBidder implements TickListener, ReAuctionBidder {

	protected ReAuctionCommModel commModel;
	protected int ticksUntilNextEvaluation;
	// Probably a lot of implementations will use an rng generator, provide one TODO: is this useful?
	protected Random rng;

	public AbstractReAuctionBidder() {
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
	 * Get new ticksUntilNextEvaluation value
	 */
	protected abstract int getDelay();

	/**
	 * Describes behaviour what to do when bids should be re-evaluated
	 */
	protected abstract void reEvaluateParcels();
}

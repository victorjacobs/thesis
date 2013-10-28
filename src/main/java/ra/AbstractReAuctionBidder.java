package ra;

import rinde.logistics.pdptw.mas.comm.AbstractBidder;
import rinde.sim.core.TickListener;
import rinde.sim.core.TimeLapse;

/**
 * Provides base for all re-auction implementations
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class AbstractReAuctionBidder extends AbstractBidder implements TickListener, ReAuctionBidder {

	@Override
	public void tick(TimeLapse timeLapse) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void afterTick(TimeLapse timeLapse) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	/**
	 * Describes behaviour what to do when bids should be re-evaluated
	 */
	abstract void reEvaluateParcels();
}

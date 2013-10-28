package ra;

import rinde.logistics.pdptw.mas.comm.AbstractCommModel;
import rinde.sim.pdptw.common.DefaultParcel;

/**
 * Created with IntelliJ IDEA.
 * User: victor
 * Date: 28/10/13
 * Time: 15:18
 */
public class ReAuctionCommModel extends AbstractCommModel<ReAuctionBidder> {

	@Override
	protected void receiveParcel(DefaultParcel p, long time) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public boolean register(ReAuctionBidder communicator) {
		// Make double binding happen
		communicator.register(this);

		return super.register(communicator);
	}
}

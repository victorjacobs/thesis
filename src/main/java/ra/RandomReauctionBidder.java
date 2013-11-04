package ra;

import common.Auctioneer;
import common.Bid;
import common.truck.Bidder;
import rinde.sim.pdptw.common.DefaultParcel;

/**
 * Created with IntelliJ IDEA.
 * User: victor
 * Date: 04/11/13
 * Time: 19:01
 */
public class RandomReauctionBidder extends Bidder {

	public RandomReauctionBidder(Auctioneer auctioneer) {
		super(auctioneer);
	}

	@Override
	public Bid getBidFor(DefaultParcel par, long time) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}

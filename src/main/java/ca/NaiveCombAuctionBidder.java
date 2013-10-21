package ca;

import rinde.sim.pdptw.common.DefaultParcel;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: victor
 * Date: 21/10/13
 * Time: 11:47
 */
public class NaiveCombAuctionBidder extends AbstractCombAuctionBidder {

	@Override
	public double getBidFor(List<DefaultParcel> p, long time) {
		return 0;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public void receiveParcels(List<DefaultParcel> p) {
		//To change body of implemented methods use File | Settings | File Templates.
	}
}

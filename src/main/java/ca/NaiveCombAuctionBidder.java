package ca;

import common.AbstractBidder;
import common.Bid;
import rinde.sim.core.graph.Point;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.util.SupplierRng;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements a naive combinatorial bidding strategy, based off {@link rinde.logistics.pdptw.mas.comm.InsertionCostBidder}.
 * For now just sums over the distance from agent to all parcels, doesn't re-auction
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class NaiveCombAuctionBidder extends AbstractBidder {

	private ObjectiveFunction objFunc;

	public NaiveCombAuctionBidder(ObjectiveFunction objFunc) {
		this.objFunc = objFunc;
	}

	@Override
	public Bid getBidFor(DefaultParcel p, long time) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public List<Bid> getBidsFor(List<DefaultParcel> p, long time) {
		// Naive, bid is total distance to self
		int totalDistance = 0;
		Point myPos = roadModel.get().getPosition(vehicle.get());

		for (DefaultParcel curP : p) {
			totalDistance += Point.distance(myPos, curP.getDestination());
		}

		ArrayList<Bid> ret = new ArrayList<Bid>();
		ret.add(new Bid(this, p, totalDistance));

		return ret;
	}

	public static SupplierRng<NaiveCombAuctionBidder> supplier(final ObjectiveFunction objFunc) {
		return new SupplierRng.DefaultSupplierRng<NaiveCombAuctionBidder>() {
			@Override
			public NaiveCombAuctionBidder get(long seed) {
				return new NaiveCombAuctionBidder(objFunc);
			}
		};
	}
}

/**
 *
 */
package ca;

import ca.wdp.NaiveParcelAllocator;
import ca.wdp.ParcelAllocator;
import rinde.logistics.pdptw.mas.comm.AbstractCommModel;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.util.SupplierRng;
import rinde.sim.util.SupplierRng.DefaultSupplierRng;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * A communication model that supports combinatorial auctions.
 * Based off {@link rinde.logistics.pdptw.mas.comm.AuctionCommModel}
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class CombAuctionCommModel extends AbstractCommModel<CombAuctionBidder> {

	/**
	 * New instance.
	 */
	public CombAuctionCommModel() {}

	private static final int QUEUE_TIME_WINDOW = 100;
	private static final int QUEUE_MAX_LENGTH = 3;

	private List<DefaultParcel> queuedParcels = new LinkedList<DefaultParcel>();
	private int startWindow = 0;

	@Override
	protected void receiveParcel(DefaultParcel p, long time) {
		checkState(!communicators.isEmpty(), "there are no bidders..");

		queuedParcels.add(p);

		if (queuedParcels.size() != QUEUE_MAX_LENGTH)
			return;		// Don't do anything for now

		// Solve WDP
		ParcelAllocator allocator = new NaiveParcelAllocator();

		// Do bidding round
		final Iterator<CombAuctionBidder> it = communicators.iterator();
		CombAuctionBidder bidder;

		while (it.hasNext()) {
			bidder = it.next();

			allocator.addAllBids(bidder.getBidsFor(queuedParcels, time));
		}

		// Solve
		allocator.distributeParcels();

		// Reset queue
		queuedParcels = new LinkedList<DefaultParcel>();


//		// Do bidding round
//		final Iterator<CombAuctionBidder> it = communicators.iterator();
//		CombAuctionBidder bestBidder = it.next();
//		// if there are no other bidders, there is no need to organize an
//		// auction at all (mainly used in test cases)
//		if (it.hasNext()) {
//			// Winner determination problem
//			double bestValue = bestBidder.getBidsFor(queuedParcels, time);
//
//			while (it.hasNext()) {
//				final CombAuctionBidder cur = it.next();
//				final double curValue = cur.getBidsFor(queuedParcels, time);
//				if (curValue < bestValue) {
//					bestValue = curValue;
//					bestBidder = cur;
//				}
//			}
//		}
//		// For now best bidder wins everything
//		bestBidder.receiveParcels(queuedParcels);
	}

	public static SupplierRng<CombAuctionCommModel> supplier() {
		return new DefaultSupplierRng<CombAuctionCommModel>() {
			@Override
			public CombAuctionCommModel get(long seed) {
				return new CombAuctionCommModel();
			}
		};
	}
}

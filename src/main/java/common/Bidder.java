package common;

import rinde.logistics.pdptw.mas.comm.Communicator;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.List;

/**
 * Implementations of this interface can participate in auctions. This differs from rinde.logistics.pdptw.mas.comm
 * .Bidder in that uses a Bid object to represent bids.
 *
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public interface Bidder extends Communicator {

	/**
	 * Should compute the 'bid value' for the specified {@link DefaultParcel}. It
	 * can be assumed that this method is called only once for each
	 * {@link DefaultParcel}, the caller is responsible for any caching if
	 * necessary.
	 * @param p The {@link DefaultParcel} that needs to be handled.
	 * @param time The current time.
	 * @return The bid value, the lower the better (i.e. cheaper).
	 */
	Bid getBidFor(DefaultParcel p, long time);
	List<Bid> getBidsFor(List<DefaultParcel> p, long time);

	/**
	 * When an auction has been won by this {@link Bidder}, the
	 * {@link DefaultParcel} is received via this method.
	 * @param p The {@link DefaultParcel} that is won.
	 */
	void receiveParcel(DefaultParcel p);
	void receiveParcels(List<DefaultParcel> p);

}

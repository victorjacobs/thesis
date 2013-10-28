/**
 *
 */
package ca;

import common.Bid;
import common.Bidder;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.List;

/**
 * Implementations of this interface can participate in combinatorial auctions.
 * Based off {@link rinde.logistics.pdptw.mas.comm.Bidder}
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public interface CombAuctionBidder extends Bidder {
	List<Bid> getBidsFor(List<DefaultParcel> p, long time);
	void receiveParcels(List<DefaultParcel> p);
}

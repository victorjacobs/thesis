package common.truck;

import com.google.common.collect.ImmutableSet;
import common.auctioning.Auctioneer;
import rinde.sim.pdptw.common.DefaultParcel;

import static com.google.common.base.Preconditions.checkState;

/**
 * Base class for all bidders. Basically provides the glue between a {@link common.truck.Truck} instance
 * and a {@link common.auctioning.Auctioneer}. Specific implementations of this abstract class provide different
 * bidding strategies.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class Bidder {
	protected Truck truck;
	protected Auctioneer auctioneer;

    /**
     * Binds an {@link common.auctioning.Auctioneer} object to this bidder. This is needed to make bids
     *
     * @param auctioneer Auctioneer bound to this bidder
     */
	public void bindAuctioneer(Auctioneer auctioneer) {
		checkState(this.auctioneer == null);
		this.auctioneer = auctioneer;
	}

    /**
     * Bind a {@link common.truck.Truck to this bidder}.
     *
     * @param truck Truck to be bound
     */
	public void bindTruck(Truck truck) {
		this.truck = truck;
	}

    /**
     * If bidder wins the bid for a certain parcel, this method is called to actually give the parcels to the Truck.
     *
     * @param pars List of parcels to be allocated to the truck
     */
	public void receiveParcels(ImmutableSet<DefaultParcel> pars) {
		for (DefaultParcel par : pars) {
            truck.addParcel(par);
		}
	}

    /**
     * Method to be overridden in subclasses. It calculates the bid for a certain parcel, given a certain time.
     *
     * @param par Parcel for which the agent will need to make a bid
     * @param time Current simulation time
     * @return A bid for the parcel
     */
	public abstract Bid getBidFor(DefaultParcel par, long time);

	@Override
	public String toString() {
		return truck.toString() + " bidder";
	}
}

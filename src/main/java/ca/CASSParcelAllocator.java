package ca;

import common.Bid;

import java.util.Set;

/**
 * Implements the Combinatorial Auction Structured Search (CASS) winner determination algorithm detailed in "Taming
 * the computational complexity of combinatorial auctions: Optimal and approximate approaches"
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class CASSParcelAllocator extends ParcelAllocator {

	@Override
	public boolean distributeParcels() {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	Set<Bid> solve() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}

package ca;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: victor
 * Date: 21/10/13
 * Time: 16:45
 */
public interface ParcelAllocator {
	void addBid(Bid newBid);

	void addAllBids(List<Bid> newBids);

	boolean distributeParcels();
}

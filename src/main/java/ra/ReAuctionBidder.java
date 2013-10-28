package ra;

import common.Bidder;

/**
 * Adds An extra method to a Bidder to allow for double binding to the model
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public interface ReAuctionBidder extends Bidder {
	void register(ReAuctionCommModel model);
}

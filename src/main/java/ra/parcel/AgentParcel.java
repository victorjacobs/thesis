package ra.parcel;

import common.auctioning.ReAuctionableParcel;
import rinde.sim.pdptw.common.ParcelDTO;

/**
 * Provides the parcel with some methods to allow it to make decisions.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class AgentParcel extends ReAuctionableParcel {
    public AgentParcel(ParcelDTO pDto) {
        super(pDto);
    }

    public abstract void update(double slack);
}

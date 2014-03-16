package ra.parcel;

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

    @Override
    // Force subclasses to implement this
    public abstract boolean shouldChangeOwner();

    /**
     * Update the parcel with information from the state evaluator
     * TODO might make this more general to support all kinds of heuristics
     *
     * @param slack Slack calculated by the state evaluator
     */
    public abstract void update(double slack);
}

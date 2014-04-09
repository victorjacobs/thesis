package ra.parcel;

import com.google.common.collect.ImmutableList;
import rinde.sim.pdptw.common.ParcelDTO;

import java.util.List;

import static com.google.common.collect.Lists.newLinkedList;

/**
 * Provides the parcel with some methods to allow it to make decisions.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class AgentParcel extends ReAuctionableParcel {
    private List<Double> slackHistory;

    public AgentParcel(ParcelDTO pDto) {
        super(pDto);
        slackHistory = newLinkedList();
    }

    @Override
    // Force subclasses to implement this
    public abstract boolean shouldChangeOwner();

    /**
     * Update the parcel with slack information from the state evaluator.
     *
     * @param slack Slack calculated by the state evaluator
     */
    public void update(double slack) {
        slackHistory.add(slack);
    }

    /**
     * Gets the history of all slacks this parcel saw.
     *
     * @return List of all slacks seen by the parcel
     */
    public ImmutableList<Double> getSlackHistory() {
        return ImmutableList.copyOf(slackHistory);
    }
}

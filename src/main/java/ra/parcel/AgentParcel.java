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
    private int updatesReceived = 0;

    public AgentParcel(ParcelDTO pDto) {
        super(pDto);
        slackHistory = newLinkedList();
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
    // TODO maybe change back to abstract
    public void update(double slack) {
        slackHistory.add(slack);
        updatesReceived++;
    }

    public ImmutableList<Double> getSlackHistory() {
        return ImmutableList.copyOf(slackHistory);
    }
}

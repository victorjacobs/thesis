package ra.parcel;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import common.auctioning.Auctioneer;
import common.truck.Bidder;
import rinde.sim.core.Simulator;
import rinde.sim.pdptw.common.AddParcelEvent;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.DynamicPDPTWProblem;
import rinde.sim.pdptw.common.ParcelDTO;

import java.util.*;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newLinkedList;

/**
 * Extends {@link DefaultParcel} with a reference to an {@link common.auctioning.Auctioneer}. This allows the parcel to change owners.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ReAuctionableParcel extends DefaultParcel {
	private Optional<Auctioneer> auctioneer;
    protected List<Bidder> ownerHistory;

	public ReAuctionableParcel(ParcelDTO pDto) {
		super(pDto);
		auctioneer = Optional.absent();
		ownerHistory = newLinkedList();
	}

    /**
     * Set Auctioneer that will be used in reauctioning.
     *
     * @param auct Auctioneer that owns the parcel
     */
	public final void setAuctioneer(Auctioneer auct) {
		checkState(!auctioneer.isPresent(), "Auctioneer already set");

		auctioneer = Optional.of(auct);
	}

    public final void setOwner(Bidder owner) {
        ownerHistory.add(owner);
    }

    /**
     * Is the parcel owned by an auctioneer?
     *
     * @return The parcel is owned by an auctioneer
     */
	public final boolean hasAuctioneer() {
		return auctioneer.isPresent();
	}

    /**
     * This method allows the parcel to have a say in re-auctioning. This is called after it being selected by an
     * implementation of {@link common.truck.StateEvaluator}. If you want the parcel to have full deciding power,
     * couple it with a state evaluator that returns all parcels.
     *
     * @return Whether or not the parcel wants to be re-auctioned
     */
    public boolean shouldChangeOwner() {
        return true;
    }

    /**
     * Attempt to change owner of the parcel.
     * NOTE: FIRST update local state BEFORE calling this.
     *
     * @param time Simulation time
     * @return Whether re-auction is allowed or not (decided by the parcel itself)
     */
	public final boolean changeOwner(long time) {
        // TODO FIX THIS
        /*if (this.reAuctionPrevented || !shouldChangeOwner()) {
            this.reAuctionPrevented = true;
            return false;
        }*/

		checkState(auctioneer.isPresent(), "Auctioneer needed to change owner");
		auctioneer.get().auction(this, time);

        return true;
	}

    /**
     * Returns the edge list representing the owner graph.
     *
     * @return Linked list multimap containing all edges in the owner graph. The keys are sorted in order of occurence.
     */
    public final Multimap<String, Integer> getEdgeList() {
        // Loop over owner history
        Multimap<String, Integer> edgeList = LinkedListMultimap.create();
        Bidder currentLocation = null;
        String edgeKey;

        for (Bidder b : getOwnerHistory()) {
            if (currentLocation == null) {
                currentLocation = b;
                continue;
            }

            edgeKey = currentLocation.hashCode() + "-" + b.hashCode();
            edgeList.put(edgeKey, -1);
            currentLocation = b;
        }

        return edgeList;
    }

    /**
     * Returns a list of weighed edges representing the owner graph. The weight is the number of times the edge
     * occurs in the graph (how many times the re-auction between the agents in question occurs).
     *
     * @return Map representing the owner graph. The key is in the form of v1-v2,
     * with v1 and v2 string representations of two vertices. The value is the weight of the edge.
     */
    public final Multimap<String, Integer> getWeighedEdgeListOwnerGraph() {
        // Collapse multimap into new multimap
        Multimap<String, Integer> edgeList = getEdgeList();
        Multimap<String, Integer> collapsed = LinkedListMultimap.create();

        for (String k : edgeList.keySet()) {
            collapsed.put(k, edgeList.get(k).size());
        }

        return collapsed;
    }

    @Override
    public String toString() {
        return "[ReAuctionableParcel " + dto + "]";
    }

    public static DynamicPDPTWProblem.Creator<AddParcelEvent> getCreator() {
		return new DynamicPDPTWProblem.Creator<AddParcelEvent>() {
			@Override
			public boolean create(Simulator sim, AddParcelEvent event) {
				sim.register(new ReAuctionableParcel(event.parcelDTO));
				return true;
			}

            @Override
            public String toString() {
                return "ReAuctionableParcel";
            }
        };
	}

    // Stats
    public final ImmutableList<Bidder> getOwnerHistory() {
        return ImmutableList.copyOf(ownerHistory);
    }
}

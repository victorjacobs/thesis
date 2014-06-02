package ra.parcel;

import rinde.sim.core.Simulator;
import rinde.sim.pdptw.common.AddParcelEvent;
import rinde.sim.pdptw.common.DynamicPDPTWProblem;
import rinde.sim.pdptw.common.ParcelDTO;

/**
 * Parcel based off the adaptive slack parcel that simply inhibits re-auctions afer a loop has occurred. Just for
 * benchmarking and testing, not a very useful strategy
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class InhibitAfterLoopAgentParcel extends AgentParcel {
    private boolean inhibit = false;

    public InhibitAfterLoopAgentParcel(ParcelDTO pDto) {
        super(pDto);
    }

    @Override
    public boolean shouldChangeOwner() {
        if (inhibit)
            return false;

        int last = getOwnerHistory().size() - 1;
        if (getOwnerHistory().size() != 1 && getOwnerHistory().get(last) == getOwnerHistory().get(last - 1)) {
            inhibit = true;
            return false;
        }

        return true;
    }

    public static DynamicPDPTWProblem.Creator<AddParcelEvent> getCreator() {
        return new DynamicPDPTWProblem.Creator<AddParcelEvent>() {
            @Override
            public boolean create(Simulator sim, AddParcelEvent event) {
                sim.register(new InhibitAfterLoopAgentParcel(event.parcelDTO));
                return true;
            }

            @Override
            public String toString() {
                return "InhibitAfterLoop";
            }
        };
    }
}

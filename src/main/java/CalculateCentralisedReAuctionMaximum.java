import rinde.sim.core.model.pdp.PDPModel;
import rinde.sim.core.model.pdp.PDPScenarioEvent;
import rinde.sim.pdptw.common.AddParcelEvent;
import rinde.sim.pdptw.gendreau06.Gendreau06Parser;
import rinde.sim.pdptw.gendreau06.Gendreau06Scenario;
import rinde.sim.pdptw.gendreau06.GendreauProblemClass;
import rinde.sim.scenario.TimedEvent;

import java.util.*;

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class CalculateCentralisedReAuctionMaximum {

    public static void main(String[] args) {
        final List<Gendreau06Scenario> onlineScenarios = Gendreau06Parser.parser()
                .addDirectory("files/scenarios/gendreau06/")
                .filter(GendreauProblemClass.SHORT_LOW_FREQ)
                .parse();

        List<AddParcelEvent> addParcelEvents, currentReAuctionableParcels;
        long currentTime, total;
        Iterator<AddParcelEvent> it;
        AddParcelEvent curEvent;

        for (Gendreau06Scenario sc : onlineScenarios) {
            addParcelEvents = new LinkedList<AddParcelEvent>();

            for (TimedEvent ev : sc.asList()) {
                if (ev.getEventType() == PDPScenarioEvent.ADD_PARCEL) {
                    addParcelEvents.add((AddParcelEvent) ev);
                }
            }

            // Sort on order announcement
            Collections.sort(addParcelEvents, new Comparator<AddParcelEvent>() {
                @Override
                public int compare(AddParcelEvent o1, AddParcelEvent o2) {
                    if (o1.parcelDTO.orderArrivalTime < o2.parcelDTO.orderArrivalTime)
                        return -1;
                    if (o1.parcelDTO.orderArrivalTime == o2.parcelDTO.orderArrivalTime)
                        return 0;
                    if (o1.parcelDTO.orderArrivalTime > o2.parcelDTO.orderArrivalTime)
                        return 1;

                    throw new IllegalStateException("This shouldn't be reached");
                }
            });

            // Do calculation
            currentReAuctionableParcels = new LinkedList<AddParcelEvent>();
            total = 0;

            for (AddParcelEvent ev : addParcelEvents) {
                currentReAuctionableParcels.add(ev);
                currentTime = ev.parcelDTO.orderArrivalTime;
                it = currentReAuctionableParcels.iterator();

                // Remove from currentReAuctionableParcels
                while (it.hasNext()) {
                    curEvent = it.next();
                    if (curEvent.parcelDTO.pickupTimeWindow.end < currentTime)
                        it.remove();
                }

                total += currentReAuctionableParcels.size();
            }

            System.out.println(Float.toString((float) total / addParcelEvents.size()));
        }
    }

}

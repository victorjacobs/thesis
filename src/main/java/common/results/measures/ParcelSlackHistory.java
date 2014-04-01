package common.results.measures;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import common.results.CSVWriter;
import common.results.Result;
import ra.parcel.AgentParcel;
import ra.parcel.ReAuctionableParcel;
import rinde.sim.pdptw.experiment.Experiment;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This only works on AgentParcels
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ParcelSlackHistory extends Measure<String> {
    public ParcelSlackHistory() {
        super("parcelSlackHistory", null);
    }

    // TODO this needs a better selector thingamajig
    @Override
    public Result<String> evaluate(Map<String, List<Experiment.SimulationResult>> resultBins) {
        try {
            CSVWriter<String> csv = new CSVWriter<String>(getName());

            AgentParcel ap;

            Iterator<String> it = resultBins.keySet().iterator();
            Experiment.SimulationResult result = resultBins.get(it.next()).get(0);
            List<ReAuctionableParcel> pars = getParcelsFromRun(result);

            // Select some random parcels
            Collections.shuffle(pars);

            for (ReAuctionableParcel par : pars.subList(0, 4)) {
                ap = (AgentParcel) par;
                csv.addColumn(par.toString(), Lists.transform(ap.getSlackHistory(), new Function<Double, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable Double aDouble) {
                        return aDouble.toString();
                    }
                }));
            }

            return csv;
        } catch (ClassCastException e) {
            System.out.println("Warning: trying to use ParcelSlackHistory on non-agentParcel");
            return null;
        }
    }
}

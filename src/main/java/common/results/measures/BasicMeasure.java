package common.results.measures;

import common.results.CSVWriter;
import common.results.Result;
import common.truck.Bidder;
import ra.parcel.ReAuctionableParcel;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.pdptw.experiment.Experiment;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newLinkedList;

/**
 * Defines a Measure that will be evaluated for every result.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class BasicMeasure<E> extends Measure<E> {
    public BasicMeasure(String name, ObjectiveFunction objectiveFunction) {
        super(name, objectiveFunction);
    }

    /**
     * Calculate measure for a single simulation result. This might return a list for values for a certain result.
     *
     * @param result Simulation result for which the measure should be calculated
     * @return List of values that are calculated from the result
     */
    protected abstract List<E> calculate(Experiment.SimulationResult result);

    @Override
    public final Result<E> evaluate(Map<String, List<Experiment.SimulationResult>> resultBins) {
        CSVWriter<E> csv = new CSVWriter<E>(getName());

        for (String runName : resultBins.keySet()) {
            for (Experiment.SimulationResult simRes : resultBins.get(runName)) {
                csv.addToColumn(runName, calculate(simRes));
            }
        }

        return csv;
    }

    /**
     * Returns the fitness for a simulation result.
     */
    public static class Fitness extends BasicMeasure<String> {
        public Fitness(ObjectiveFunction objectiveFunction) {
            super("fitness", objectiveFunction);
        }

        @Override
        protected List<String> calculate(Experiment.SimulationResult result) {
            return Collections.singletonList(Double.toString(getObjectiveFunction().computeCost(result.stats)));
        }
    }

    /**
     * Returns the computation time for a simulation result.
     */
    public static class ComputationTime extends BasicMeasure<String> {
        public ComputationTime() {
            super("computationtime", null);
        }

        @Override
        protected List<String> calculate(Experiment.SimulationResult result) {
            return Collections.singletonList(Long.toString(result.stats.computationTime));
        }
    }

    /**
     * Returns the total re-auctions done in a certain configuration.
     */
    public static class TotalReAuctions extends BasicMeasure<String> {
        public TotalReAuctions() {
            super("totalReauctions", null);
        }

        @Override
        protected List<String> calculate(Experiment.SimulationResult result) {
            int total = 0;
            for (ReAuctionableParcel par : getParcelsFromRun(result)) {
                total += par.getOwnerHistory().size();
            }

            return Collections.singletonList(Integer.toString(total));
        }
    }

    /**
     * Returns the number of re-auctions per parcel for a result.
     */
    public static class NumberReAuctions extends BasicMeasure<String> {
        public NumberReAuctions() {
            super("nbReauctions", null);
        }

        @Override
        protected List<String> calculate(Experiment.SimulationResult result) {
            List<String> ret = newLinkedList();

            for (ReAuctionableParcel par : getParcelsFromRun(result))
                ret.add(Integer.toString(par.getOwnerHistory().size()));

            return ret;
        }
    }

    /**
     * Returns the ratio of #re-auctions/#distinct owners per parcel for a result.
     */
    public static class AuctionOwnerRatio extends BasicMeasure<String> {
        public AuctionOwnerRatio() {
            super("auctionOwnerRatio", null);
        }

        @Override
        protected List<String> calculate(Experiment.SimulationResult result) {
            List<String> ret = newLinkedList();
            int distinctOwners;

            for (ReAuctionableParcel par : getParcelsFromRun(result)) {
                distinctOwners = (new HashSet<Bidder>(par.getOwnerHistory())).size();
                ret.add(Double.toString(par.getOwnerHistory().size() / distinctOwners));
            }

            return ret;
        }
    }
}

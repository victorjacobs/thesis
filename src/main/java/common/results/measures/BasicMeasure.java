package common.results.measures;

import common.results.CSVWriter;
import ra.parcel.ReAuctionableParcel;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.pdptw.experiment.Experiment;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newLinkedList;

/**
 * Defines a Measure that will be evaluated for every configuration.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class BasicMeasure<E> extends Measure<E> {
    public BasicMeasure(String name, ObjectiveFunction objectiveFunction) {
        super(name, objectiveFunction);
    }

    protected abstract List<E> calculate(Experiment.SimulationResult result);

    @Override
    public final CSVWriter<E> evaluate(Map<String, List<Experiment.SimulationResult>> resultBins) {
        for (String runName : resultBins.keySet()) {
            for (Experiment.SimulationResult simRes : resultBins.get(runName)) {
                csv.addToColumn(runName, calculate(simRes));
            }
        }

        return csv;
    }

    // TODO maybe to interface?
    public static class Fitness extends BasicMeasure<String> {
        public Fitness(ObjectiveFunction objectiveFunction) {
            super("fitness", objectiveFunction);
        }

        @Override
        protected List<String> calculate(Experiment.SimulationResult result) {
            return Collections.singletonList(Double.toString(getObjectiveFunction().computeCost(result.stats)));
        }
    }

    public static class ComputationTime extends BasicMeasure<String> {
        public ComputationTime() {
            super("computationtime", null);
        }

        @Override
        protected List<String> calculate(Experiment.SimulationResult result) {
            return Collections.singletonList(Long.toString(result.stats.computationTime));
        }
    }

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
}

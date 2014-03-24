package common.results.measures;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import common.results.CSVWriter;
import ra.parcel.ReAuctionableParcel;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.pdptw.experiment.Experiment;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

/**
 * Defines a measure to be evaluated on a list of simulation results
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class Measure<E> {
    private final Optional<ObjectiveFunction> objectiveFunction;
    protected final CSVWriter<E> csv;

    public Measure(String name, @Nullable ObjectiveFunction objectiveFunction) {
        this.objectiveFunction = Optional.fromNullable(objectiveFunction);
        this.csv = new CSVWriter<E>(name);
    }

    public ObjectiveFunction getObjectiveFunction() {
        checkState(objectiveFunction.isPresent(), "No objective function set");

        return objectiveFunction.get();
    }

    // Place this here to do some extensive checking whether simulationData is what we hope it is
    @SuppressWarnings("unchecked")
    public final List<ReAuctionableParcel> getParcelsFromRun(Experiment.SimulationResult result) {
        checkState(result.simulationData != null, "Simulation data is null, did you add a post processor?");
        List<ReAuctionableParcel> ret;

        try {
            ret = (List<ReAuctionableParcel>) result.simulationData;
        } catch(ClassCastException e) {
            System.err.println("WARNING: simulation data couldn't be cast properly, returning empty list");
            ret = ImmutableList.of();
        }

        return ret;
    }

    public abstract CSVWriter<E> evaluate(Map<String, List<Experiment.SimulationResult>> resultBins);

}

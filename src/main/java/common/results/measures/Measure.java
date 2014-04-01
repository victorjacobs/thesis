package common.results.measures;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import common.results.CSVWriter;
import common.results.Result;
import ra.parcel.ReAuctionableParcel;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.pdptw.experiment.Experiment;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

/**
 * Defines a measure to be evaluated on a list of simulation results.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public abstract class Measure<E> {
    private final Optional<ObjectiveFunction> objectiveFunction;
    private final String name;

    public Measure(String name, @Nullable ObjectiveFunction objectiveFunction) {
        this.objectiveFunction = Optional.fromNullable(objectiveFunction);
        this.name = name;
    }

    public ObjectiveFunction getObjectiveFunction() {
        checkState(objectiveFunction.isPresent(), "No objective function set");

        return objectiveFunction.get();
    }

    public String getName() {
        return name;
    }

    /**
     * Extract the parcels from a simulation result, gathered through {@link common.results.ParcelTrackerModel}.
     *
     * @param result Simulation result to get the parcels from
     * @return List of parcels that were used in the simulation
     */
    @SuppressWarnings("unchecked")
    public final List<ReAuctionableParcel> getParcelsFromRun(Experiment.SimulationResult result) {
        checkState(result.simulationData != null, "Simulation data is null, did you add a post processor?");
        List<ReAuctionableParcel> ret;

        try {
            ret = new LinkedList<ReAuctionableParcel>((List<ReAuctionableParcel>) result.simulationData);
        } catch(ClassCastException e) {
            System.err.println("WARNING: simulation data couldn't be cast properly, returning empty list");
            ret = ImmutableList.of();
        }

        return ret;
    }

    /**
     * Evaluate the measure on a list of simulation results bins. It returns a {@link common.results.CSVWriter}
     * containing the results in format desired for outputting.
     *
     * @param resultBins Bins containing the simulation results
     * @return A writer containing the measure result
     */
    public abstract Result<E> evaluate(Map<String, List<Experiment.SimulationResult>> resultBins);

}

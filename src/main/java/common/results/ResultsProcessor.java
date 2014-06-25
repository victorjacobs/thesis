package common.results;

import common.results.measures.*;
import rinde.sim.pdptw.experiment.Experiment;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newLinkedList;

/**
 * Calculates the {@link common.results.measures.Measure}s added through {@link #addMeasure(common.results.measures.Measure)}
 * on an {@link rinde.sim.pdptw.experiment.Experiment.ExperimentResults} set. The result is then written to a
 * directory with a given name.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ResultsProcessor extends ResultDirectory {
	private List<Measure<String>> measures;

	/**
	 * Creates empty ResultsProcessor. Add own Measures and then call load(). For now, don't let anyone use it
	 */
	private ResultsProcessor(String experimentName) {
        super(experimentName);
		measures = newLinkedList();
	}

	/**
	 * Constructs default ResultsProcessor with standard result measures (objective value and computation time)
	 *
     * @param experimentName Name of the experiment for which this processor will process the results
	 * @param data ExperimentResults to be processed
	 */
	public ResultsProcessor(String experimentName, Experiment.ExperimentResults data) {
		this(experimentName);

		// What data to extract
        addMeasure(new BasicMeasure.FitnessToAuctionsPerParcel(data.objectiveFunction));
        /*addMeasure(new BasicMeasure.Fitness(data.objectiveFunction));
        addMeasure(new BasicMeasure.ComputationTime());
        addMeasure(new BasicMeasure.TotalReAuctions());
        addMeasure(new BasicMeasure.NumberReAuctions());
        addMeasure(new BasicMeasure.AverageAuctionsPerParcel());
        addMeasure(new MaxEdgesOwnerGraph());
        addMeasure(new ParcelSlackHistory());
        //addMeasure(new AllWeighedOwnerGraph());
        addMeasure(new BasicMeasure.PercentageUsefulReAuctionsOverall());
        addMeasure(new BasicMeasure.PercentageUsefulReAuctionsPerParcel());*/

		load(data);
	}

	/**
	 * Add measure to be evaluated on experiment results.
	 *
	 * @param m Measure to be evaluated
	 */
	public void addMeasure(Measure<String> m) {
		measures.add(m);
	}

	/**
	 * Load list of experimental results
	 *
	 * @param data List to be loaded
	 */
	public void load(Experiment.ExperimentResults data) {
		checkState(isEmpty(), "Data already loaded");
		checkState(!measures.isEmpty(), "I need some measures to evaluate");

		// Create some stuff
		Map<String, List<Experiment.SimulationResult>> dtoBins = new LinkedHashMap<String, List<Experiment.SimulationResult>>();

		// Bin SimulationResults
		for (Experiment.SimulationResult res : data.results) {
			if (!dtoBins.containsKey(res.masConfiguration.toString()))
				dtoBins.put(res.masConfiguration.toString(), new LinkedList<Experiment.SimulationResult>());

			dtoBins.get(res.masConfiguration.toString()).add(res);
		}

		// Calculate measures
		for (Measure<String> m : measures)
            addResult(m.evaluate(dtoBins));
	}

    @Override
    public String toString() {
        return prettyPrint();
    }
}

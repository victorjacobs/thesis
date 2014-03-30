package common.results;

import common.results.measures.*;
import rinde.sim.pdptw.experiment.Experiment;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newLinkedList;

/**
 * Class that writes out ExperimentResults from an experiment run to csv files. On one hand,
 * it interpretes data from ExperimentResults and on the other hand data from static fields in stats objects.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
// TODO maybe change how Measures that return multiple values work
public class ResultsProcessor {
	private List<CSVWriter<String>> processedData;	// CSV writers containing measures
	private List<Measure<String>> measures;

	/**
	 * Creates empty ResultsProcessor. Add own Measures and then call load(). For now, don't let anyone use it
	 */
	private ResultsProcessor() {
		processedData = new LinkedList<CSVWriter<String>>();
		measures = newLinkedList();
	}

	/**
	 * Constructs default ResultsProcessor with standard result measures (objective value and computation time)
	 *
	 * @param data ExperimentResults to be processed
	 */
	public ResultsProcessor(Experiment.ExperimentResults data) {
		this();

		// What data to extract
        addMeasure(new BasicMeasure.Fitness(data.objectiveFunction));
        addMeasure(new BasicMeasure.ComputationTime());
        addMeasure(new BasicMeasure.TotalReAuctions());
        addMeasure(new BasicMeasure.NumberReAuctions());
        addMeasure(new BasicMeasure.AuctionOwnerRatio());
        addMeasure(new WeighedOwnerGraphMeasure());
        addMeasure(new OwnerGraphMeasure());
        addMeasure(new ParcelSlackHistoryMeasure());

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
    // TODO: immutable map?
	public void load(Experiment.ExperimentResults data) {
		checkState(processedData.isEmpty(), "Data already loaded");
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
        CSVWriter<String> w;
		for (Measure<String> m : measures) {
            if ((w = m.evaluate(dtoBins)) != null) processedData.add(w);
		}
	}

	/**
	 * Write processed data to a set of CSV files in given directory.
	 *
	 * @param directory Directory where to write processed data
	 * @throws IOException IO broke
	 */
    @SuppressWarnings("all")    // Compiler complains that we ignore return value of mkdir()
	public void write(String directory) throws IOException {
		// Create result directory if it doesn't exist
		File dir = new File(directory);
		if (!dir.exists()) dir.mkdir();

		for (CSVWriter<String> w : processedData) {
			w.write(directory);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (CSVWriter<String> w : processedData) {
			sb.append('\n').append(w.getName()).append(".csv\n");
			sb.append(w.toString());
		}

		return sb.toString();
	}

}

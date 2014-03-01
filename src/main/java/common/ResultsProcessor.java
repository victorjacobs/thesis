package common;

import common.auctioning.ReAuctionableParcel;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.pdptw.common.StatisticsDTO;
import rinde.sim.pdptw.experiment.Experiment;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newLinkedList;

/**
 * Class that writes out ExperimentResults from an experiment run to csv files. On one hand,
 * it interpretes data from ExperimentResults and on the other hand data from static fields in stats objects. (e.g.
 * {@link common.ParcelTracker}.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
// TODO some stuff in here depends heavily on order of runs
public class ResultsProcessor {
	private Map<String, String> processedData;	// Measure.name -> file contents
	private List<Measure> measures;

	/**
	 * Creates empty ResultsProcessor. Add own Measures and then call load(). For now, don't let anyone use it
	 */
	private ResultsProcessor() {
		processedData = new LinkedHashMap<String, String>();
		measures = newLinkedList();
	}

	/**
	 * Constructs default ResultsProcessor with standard result measures (objective value and computation time)
	 * @param data
	 */
	public ResultsProcessor(Experiment.ExperimentResults data) {
		this();

		// TODO this isn't very clean
		final ObjectiveFunction objFunction = data.objectiveFunction;

		// What data to extract (eww Java)
		addMeasure(new Measure() {
			@Override
			public double getValue(StatisticsDTO stats) {
				return objFunction.computeCost(stats);
			}

			@Override
			public String getName() {
				return "fitness";
			}
		});
		addMeasure(new Measure() {
			@Override
			public double getValue(StatisticsDTO stats) {
				return stats.computationTime;
			}

			@Override
			public String getName() {
				return "computationtime";
			}
		});

		load(data);
	}

	public void addMeasure(Measure m) {
		measures.add(m);
	}

	public void load(Experiment.ExperimentResults data) {
		checkState(processedData.isEmpty(), "Data already loaded");
		checkState(!measures.isEmpty(), "I need some measures to evaluate");

		// Create some stuff
		StringBuilder sb;
		Map<String, List<StatisticsDTO>> dtoBins = new LinkedHashMap<String, List<StatisticsDTO>>();

		// Bin statistics DTO's
		for (Experiment.SimulationResult res : data.results) {
			if (!dtoBins.containsKey(res.masConfiguration.toString()))
				dtoBins.put(res.masConfiguration.toString(), new LinkedList<StatisticsDTO>());

			dtoBins.get(res.masConfiguration.toString()).add(res.stats);
		}

		// Go over result measures
		for (Measure m : measures) {
			sb = new StringBuilder();

			// Write headers
			for (String binName : dtoBins.keySet()) {
				sb.append(binName);
				sb.append(",");
			}

			sb = sb.deleteCharAt(sb.length() - 1).append('\n');

			// Store data
			for (int i = 0; i < data.repetitions * data.scenarios.size(); i++) {
				for (String binName : dtoBins.keySet()) {
					sb.append(m.getValue(dtoBins.get(binName).get(i)));
					sb.append(",");
				}

				sb = sb.deleteCharAt(sb.length() - 1).append('\n');
			}

			processedData.put(m.getName(), sb.toString());
		}

		// Now process remaining data, stored in random static objects
		// ParcelTracker
		int count = 0;
		String key = "";
		// These orderings should be preserved since backed by LinkedHashMap
		Iterator<String> binIterator = dtoBins.keySet().iterator();
		Iterator<List<ReAuctionableParcel>> runIterator = ParcelTracker.getParcels().values().iterator();

		Map<String, List<List<ReAuctionableParcel>>> parcelBins = new LinkedHashMap<String, List<List<ReAuctionableParcel>>>();

		while (runIterator.hasNext()) {
			if (count % data.repetitions == 0) {
				// Move key
				key = binIterator.next();
				parcelBins.put(key, new LinkedList<List<ReAuctionableParcel>>());
			}

			parcelBins.get(key).add(runIterator.next());

			count++;
		}

		System.out.println();
	}

	/**
	 * Write resulting csv files to given directory
	 * @param directory
	 * @throws Exception
	 */
	public void write(String directory) throws Exception {
		// Create result directory if it doesn't exist
		File dir = new File(directory);
		if (!dir.exists()) dir.mkdir();

		PrintWriter writer;

		for (String file : processedData.keySet()) {
			writer = new PrintWriter(directory + file + ".csv");

			writer.write(processedData.get(file));
			writer.close();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (String file : processedData.keySet()) {
			sb.append('\n').append(file).append(".csv\n");
			sb.append(processedData.get(file));
		}

		return sb.toString();
	}

	/**
	 * Defines a measure to be evaluated on a StatisticsDTO
	 */
	public interface Measure {
		public double getValue(StatisticsDTO stats);
		public String getName();
	}

}

package common.results;

import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.pdptw.common.StatisticsDTO;
import rinde.sim.pdptw.experiment.Experiment;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newLinkedList;

/**
 * Class that writes out ExperimentResults from an experiment run to csv files. On one hand,
 * it interpretes data from ExperimentResults and on the other hand data from static fields in stats objects. (e.g.
 * {@link ParcelTracker}.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
// TODO some stuff in here depends heavily on order of runs
public class ResultsProcessor {
	private List<CSVWriter<String>> processedData;	// CSV writers containing measures
	private List<Measure> measures;

	/**
	 * Creates empty ResultsProcessor. Add own Measures and then call load(). For now, don't let anyone use it
	 */
	private ResultsProcessor() {
		processedData = new LinkedList<CSVWriter<String>>();
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
		Map<String, List<StatisticsDTO>> dtoBins = new LinkedHashMap<String, List<StatisticsDTO>>();
		CSVWriter<String> csv;

		// Bin statistics DTO's
		for (Experiment.SimulationResult res : data.results) {
			if (!dtoBins.containsKey(res.masConfiguration.toString()))
				dtoBins.put(res.masConfiguration.toString(), new LinkedList<StatisticsDTO>());

			dtoBins.get(res.masConfiguration.toString()).add(res.stats);
		}

		// Calculate measures
		for (Measure m : measures) {
			csv = new CSVWriter<String>(m.getName());

			for (String runName : dtoBins.keySet()) {
				for (int i = 0; i < data.repetitions * data.scenarios.size(); i++) {
					csv.addToColumn(runName, Double.toString(m.getValue(dtoBins.get(runName).get(i))));
				}
			}

			processedData.add(csv);
		}


		/*for (Measure m : measures) {
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
			if (count % (data.repetitions * data.scenarios.size()) == 0) {
				// Get next key (scenario)
				key = binIterator.next();
				parcelBins.put(key, new LinkedList<List<ReAuctionableParcel>>());
			}

			parcelBins.get(key).add(runIterator.next());

			count++;
		}

		// Do something with it
		float avg;
		sb = new StringBuilder();
		// Configurations
		for (String conf : parcelBins.keySet()) {
			System.out.println();
			System.out.println(conf);
			// Runs
			for (int i = 0; i < parcelBins.get(key).size(); i++) {
				avg = 0;

				// Parcels
				for (ReAuctionableParcel p : parcelBins.get(conf).get(i))
					avg += p.getOwnerHistory().size();

				avg /= parcelBins.get(key).get(i).size();

				System.out.println(avg);
			}
		}*/
	}

	/**
	 * Write resulting csv files to given directory
	 * @param directory
	 * @throws Exception
	 */
	public void write(String directory) throws Exception {
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

	/**
	 * Defines a measure to be evaluated on a StatisticsDTO
	 */
	public interface Measure {
		public double getValue(StatisticsDTO stats);
		public String getName();
	}

}

package common;

import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.pdptw.common.StatisticsDTO;
import rinde.sim.pdptw.experiment.Experiment;

import java.io.File;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newLinkedList;

/**
 * Class that writes out ExperimentResults from an experiment run to csv files.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ResultsProcessor {
	private Map<String, String> processedData;
	private List<Measure> measures;

	/**
	 * Creates empty ResultsProcessor. Add own Measures and then call load()
	 */
	public ResultsProcessor() {
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

	public void write(String directory) throws Exception {
		// Create result directory if it doesn't exist
		File dir = new File(directory);
		if (!dir.exists()) dir.mkdir();

		PrintWriter writer;

		for (String file : processedData.keySet()) {
			writer = new PrintWriter(directory + file + ".csv");
		}
	}

	public void addMeasure(Measure m) {
		measures.add(m);
	}

	public void load(Experiment.ExperimentResults data) {
		checkState(processedData.isEmpty(), "Data already loaded");
		checkState(!measures.isEmpty(), "I need some measures to evaluate");

		// Create some stuff
		StringBuilder sb;
		Map<String, List<StatisticsDTO>> bins = new LinkedHashMap<String, List<StatisticsDTO>>();

		// Bin statistics DTO's
		for (Experiment.SimulationResult res : data.results) {
			if (!bins.containsKey(res.masConfiguration.toString()))
				bins.put(res.masConfiguration.toString(), new LinkedList<StatisticsDTO>());

			bins.get(res.masConfiguration.toString()).add(res.stats);
		}

		// Go over result measures
		for (Measure m : measures) {
			//writer = new PrintWriter(directory + m.getName() + ".csv");
			sb = new StringBuilder();

			// Write headers
			for (String binName : bins.keySet()) {
				sb.append(binName);
				sb.append(",");
			}

			sb = sb.deleteCharAt(sb.length() - 1).append('\n');

			// Write data
			for (int i = 0; i < data.repetitions * data.scenarios.size(); i++) {
				for (String binName : bins.keySet()) {
					sb.append(m.getValue(bins.get(binName).get(i)));
					sb.append(",");
				}

				sb = sb.deleteCharAt(sb.length() - 1).append('\n');
			}

			processedData.put(m.getName(), sb.toString());
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

	public interface Measure {
		public double getValue(StatisticsDTO stats);
		public String getName();
	}

}

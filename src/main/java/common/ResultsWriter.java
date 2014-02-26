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

import static com.google.common.collect.Lists.newLinkedList;

/**
 * Class that writes out ExperimentResults from an experiment run to csv files.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ResultsWriter {

	public static void write(String directory, Experiment.ExperimentResults data) throws Exception {
		final ObjectiveFunction obj = data.objectiveFunction;

		// What data to extract (eww Java)
		List<Measure> measures = newLinkedList();
		measures.add(new Measure() {
			@Override
			public double getValue(StatisticsDTO stats) {
				return obj.computeCost(stats);
			}

			@Override
			public String getName() {
				return "fitness";
			}
		});
		measures.add(new Measure() {
			@Override
			public double getValue(StatisticsDTO stats) {
				return stats.computationTime;
			}

			@Override
			public String getName() {
				return "computationtime";
			}
		});

		// Create result directory if it doesn't exist
		File dir = new File(directory);
		if (!dir.exists()) dir.mkdir();

		// Create some stuff
		PrintWriter writer;
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
			writer = new PrintWriter(directory + m.getName() + ".csv");
			sb = new StringBuilder();

			// Write headers
			for (String binName : bins.keySet()) {
				sb.append(binName);
				sb.append(",");
			}

			writer.println(sb.deleteCharAt(sb.length() - 1));

			// Write data
			for (int i = 0; i < data.repetitions * data.scenarios.size(); i++) {
				sb = new StringBuilder();

				for (String binName : bins.keySet()) {
					sb.append(m.getValue(bins.get(binName).get(i)));
					sb.append(",");
				}

				writer.println(sb.deleteCharAt(sb.length() - 1));
			}

			writer.close();
		}
	}

	private interface Measure {
		public double getValue(StatisticsDTO stats);
		public String getName();
	}

}

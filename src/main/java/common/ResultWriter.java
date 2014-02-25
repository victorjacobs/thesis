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

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ResultWriter {

	public static void write(String directory, Experiment.ExperimentResults data) throws Exception {
		// Create result directory if it doesn't exist
		File dir = new File(directory);
		if (!dir.exists()) dir.mkdir();

		// Create some stuff
		PrintWriter writer = new PrintWriter(directory + "fitness.csv");
		StringBuilder sb = new StringBuilder();
		ObjectiveFunction obj = data.objectiveFunction;

		Map<String, List<StatisticsDTO>> bins = new LinkedHashMap<String, List<StatisticsDTO>>();

		// Bin statistics DTO's
		for (Experiment.SimulationResult res : data.results) {
			if (!bins.containsKey(res.masConfiguration.toString()))
				bins.put(res.masConfiguration.toString(), new LinkedList<StatisticsDTO>());

			bins.get(res.masConfiguration.toString()).add(res.stats);
		}

		// Write headers
		for (String binName : bins.keySet()) {
			sb.append(binName);
			sb.append(",");
		}

		writer.println(sb);

		// Write data
		for (int i = 0; i < data.repetitions * data.scenarios.size(); i++) {
			sb = new StringBuilder();

			for (String binName : bins.keySet()) {
				sb.append(obj.computeCost(bins.get(binName).get(i)));
				sb.append(",");
			}

			writer.println(sb);
		}

		writer.close();
	}

}

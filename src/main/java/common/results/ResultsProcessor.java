package common.results;

import com.google.common.collect.ImmutableList;
import ra.parcel.ReAuctionableParcel;
import rinde.sim.pdptw.common.ObjectiveFunction;
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
	 *
	 * @param data ExperimentResults to be processed
	 */
    @SuppressWarnings("all")
	public ResultsProcessor(Experiment.ExperimentResults data) {
		this();

		// TODO this isn't very clean
		final ObjectiveFunction objFunction = data.objectiveFunction;

		// What data to extract (eww Java)
        // TODO should move this out of this class
		addMeasure(new Measure<String>("fitness") {
			@Override
			public void calculate(Experiment.SimulationResult result) {
				addToMeasure(Double.toString(objFunction.computeCost(result.stats)));
			}
		});
		addMeasure(new Measure<String>("computationtime") {
			@Override
			public void calculate(Experiment.SimulationResult result) {
                addToMeasure(Long.toString(result.stats.computationTime));
			}
		});
        addMeasure(new Measure<String>("totalReauctions") {
            @Override
            public void calculate(Experiment.SimulationResult result) {
                List<ReAuctionableParcel> pars = (List<ReAuctionableParcel>) result.simulationData;

                int total = 0;
                for (ReAuctionableParcel par : pars) {
                    total += par.getOwnerHistory().size();
                }

                addToMeasure(Integer.toString(total));
            }
        });
        addMeasure(new Measure<String>("nbReauctions") {
            @Override
            public void calculate(Experiment.SimulationResult result) {
                List<ReAuctionableParcel> pars = (List<ReAuctionableParcel>) result.simulationData;

                for (ReAuctionableParcel par : pars)
                    addToMeasure(Integer.toString(par.getOwnerHistory().size()));
            }
        });

		load(data);
	}

	/**
	 * Add measure to be evaluated on experiment results.
	 *
	 * @param m Measure to be evaluated
	 */
	public void addMeasure(Measure m) {
		measures.add(m);
	}

	/**
	 * Load list of experimental results
	 *
	 * @param data List to be loaded
	 */
	public void load(Experiment.ExperimentResults data) {
		checkState(processedData.isEmpty(), "Data already loaded");
		checkState(!measures.isEmpty(), "I need some measures to evaluate");

		// Create some stuff
		Map<String, List<Experiment.SimulationResult>> dtoBins = new LinkedHashMap<String, List<Experiment.SimulationResult>>();
		CSVWriter<String> csv;

		// Bin SimulationResults
		for (Experiment.SimulationResult res : data.results) {
			if (!dtoBins.containsKey(res.masConfiguration.toString()))
				dtoBins.put(res.masConfiguration.toString(), new LinkedList<Experiment.SimulationResult>());

			dtoBins.get(res.masConfiguration.toString()).add(res);
		}

		// Calculate measures
		for (Measure m : measures) {
			csv = new CSVWriter<String>(m.getName());

			for (String runName : dtoBins.keySet()) {
				for (int i = 0; i < data.repetitions * data.scenarios.size(); i++) {
                    csv.addToColumn(runName, m.evaluate(dtoBins.get(runName).get(i)));
				}
			}

			processedData.add(csv);
		}
	}

	/**
	 * Write processed data to a set of CSV files in given directory.
	 *
	 * @param directory Directory where to write processed data
	 * @throws IOException IO broke
	 */
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

    // Brute force TODO make this cleaner
    public void createOwnerGraph(Experiment.SimulationResult result) {
        CSVWriter<String> w = new CSVWriter<String>("ownerGraph", false);
        List<ReAuctionableParcel> pars = (List<ReAuctionableParcel>) result.simulationData;

        // Find parcel with most re-auctions, this will be the most interesting
        int maxReAuctions = Integer.MIN_VALUE;
        ReAuctionableParcel maxPar = null;
        for (ReAuctionableParcel par : pars) {
            if (par.getOwnerHistory().size() > maxReAuctions) {
                maxReAuctions = par.getOwnerHistory().size();
                maxPar = par;
            }
        }

        Map<String, Integer> edgeList = maxPar.getWeighedEdgeListOwnerGraph();

        // To CSV
        List<String> row;
        for (String edge : edgeList.keySet()) {
            row = new LinkedList<String>();
            row.add(edge.split("-")[0]);
            row.add(edge.split("-")[1]);
            row.add(Integer.toString(edgeList.get(edge)));

            w.addRow(row);
        }

        System.out.println(w);
    }

	/**
	 * Defines a measure to be evaluated on a SimulationResult
	 */
	abstract class Measure<E> {
        private String name;
        // TODO Eigenlijk zou dit stateless moeten zijn
        private List<E> values;

        public Measure(String n) {
            this.name = n;
        }

		protected abstract void calculate(Experiment.SimulationResult result);

        protected final void addToMeasure(E val) {
            values.add(val);
        }

        public final ImmutableList<E> evaluate(Experiment.SimulationResult result) {
            this.values = newLinkedList();
            calculate(result);
            return ImmutableList.copyOf(values);
        }

		public final String getName() {
            return name;
        }
	}

}

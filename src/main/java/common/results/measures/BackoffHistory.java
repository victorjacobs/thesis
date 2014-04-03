package common.results.measures;

import common.results.Result;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.pdptw.experiment.Experiment;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class BackoffHistory extends Measure<String> {
    public BackoffHistory() {
        super("backoffHistory", null);
    }

    @Override
    public Result evaluate(Map<String, List<Experiment.SimulationResult>> resultBins) {
        return null;
    }
}

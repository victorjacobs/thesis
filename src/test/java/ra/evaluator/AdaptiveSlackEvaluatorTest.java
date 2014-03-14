package ra.evaluator;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static com.google.common.collect.Lists.newLinkedList;
import static org.junit.Assert.assertEquals;

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class AdaptiveSlackEvaluatorTest {

	private Random rng;
	private AdaptiveSlackEvaluator ev;

	@Before
	public void setup() {
		rng = new Random(42);
		ev = new AdaptiveSlackEvaluator(42);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testStandardDeviation() {
		DescriptiveStatistics stats;

		double[] data1 = new double[10];
		double[] data2 = new double[10];

		// Fill with random data
		for (int i = 0; i < 10; i++) {
			data1[i] = rng.nextDouble();
			data2[i] = rng.nextDouble();
		}

		stats = new DescriptiveStatistics(data1);
		ev.update(fromPrimitiveToList(data1));
		assertEquals(stats.getStandardDeviation(), ev.getStandardDeviation(), 0.1f);

		stats = new DescriptiveStatistics(ArrayUtils.addAll(data1, data2));
		ev.update(fromPrimitiveToList(data2));
		assertEquals(stats.getStandardDeviation(), ev.getStandardDeviation(), 0.1f);
	}

	private List<Double> fromPrimitiveToList(double[] in) {
		LinkedList<Double> ret = newLinkedList();

		for (double anIn : in) {
			ret.add(anIn);
		}

		return ret;
	}

}

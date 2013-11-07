package ra;

import common.truck.StateEvaluator;
import common.truck.Truck;

/**
 * Created with IntelliJ IDEA.
 * User: victor
 * Date: 05/11/13
 * Time: 17:01
 */
public class RandomStateEvaluator extends StateEvaluator {

	public RandomStateEvaluator(Truck truck) {
		super(truck);
	}

	@Override
	public boolean evaluateState(int ticksSinceLastCall, long time) {
		return false;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
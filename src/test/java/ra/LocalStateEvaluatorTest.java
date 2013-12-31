package ra;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import common.truck.Truck;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import rinde.sim.core.graph.Point;
import rinde.sim.core.model.pdp.Parcel;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.util.TimeWindow;

import static junit.framework.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Truck.class, DefaultParcel.class})
public class LocalStateEvaluatorTest {

	private LocalStateEvaluator ev;
	private Truck tr;
	private ImmutableList.Builder<DefaultParcel> pars;

	@Before
	public void setup() {
		tr = mock(Truck.class);
		pars = ImmutableList.builder();

		ev = new LocalStateEvaluator();
		ev.setTruck(tr);

		// Set up truck: location (1, 1), speed 1
		ImmutableSet<Parcel> emptySet = ImmutableSet.of();
		when(tr.getContents()).thenReturn(emptySet);
		when(tr.getSpeed()).thenReturn(1d);
		when(tr.getPosition()).thenReturn(new Point(1, 1));
	}

	@Test
	/**
	 * Simple test:
	 * 	- 1 parcel pickup (1, 3), dropoff (2, 3)
	 * 	- Pickup and delivery duration 1
	 * 	- Delivery timewindow ends at 5
	 * 	- Should have slack of 1
	 * 	-> travel 2 time units + 1 time unit pickup + 1 time unit travel == 4
	 */
	public void testSlackSimple() throws Exception {
		DefaultParcel par = mockedParcel(new Point(1, 3), new Point(2, 3), 1l, 1l, new TimeWindow(0, 5));
		addParcel(par);
		addParcel(par);

		when(tr.getRoute()).thenReturn(pars.build());

		assertEquals(1.0, ev.calculateSlackForState().get(par));
	}

	public void addParcel(DefaultParcel par) {
		pars.add(par);
	}

	public DefaultParcel mockedParcel(Point pickup, Point delivery, long pDur, long dDur, TimeWindow deliveryWindow) {
		DefaultParcel par = mock(DefaultParcel.class);
		when(par.getPickupDuration()).thenReturn(pDur);
		when(par.getDeliveryDuration()).thenReturn(dDur);
		when(par.getDeliveryTimeWindow()).thenReturn(deliveryWindow);
		when(par.getPickupLocation()).thenReturn(pickup);
		when(par.getDestination()).thenReturn(delivery);

		return par;
	}
}

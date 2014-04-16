package ra.evaluator;

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

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Truck.class, DefaultParcel.class})
public class FixedSlackEvaluatorTest {

	private FixedSlackEvaluator ev;
	private Truck tr;
	private ImmutableList.Builder<DefaultParcel> pars;

	@Before
	public void setup() {
		tr = mock(Truck.class);
		pars = ImmutableList.builder();

		ev = new FixedSlackEvaluator(0);
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
		DefaultParcel par = mockedParcel(new Point(1, 3), new Point(2, 3), 1l, 1l, new TimeWindow(0, 10),
				new TimeWindow(0, 5));

		addParcel(par);
		addParcel(par);

		buildRoute();

		assertEquals(9.0, ev.calculateSlackForState(0).get(par), 0.0001);
	}

	@Test
	/**
	 * Same test as above, but with waiting at pickup location (arrival before time window starts).
	 */
	public void testSlackSimpleWithWaiting() throws Exception {
		DefaultParcel par = mockedParcel(new Point(1, 3), new Point(2, 3), 1l, 1l, new TimeWindow(3, 6),
				new TimeWindow(0, 6));

		addParcel(par);
		addParcel(par);

		buildRoute();

		assertEquals(4.0, ev.calculateSlackForState(0).get(par), 0.0001);
	}

	@Test
	/**
	 * Test pick-up and delivery of two parcels, interleaved, with waiting at delivery location
	 */
	public void testSlackTwoParcels() throws Exception {
		DefaultParcel par1 = mockedParcel(new Point(1, 3), new Point(2, 5), 1l, 1l, new TimeWindow(3, 6),
				new TimeWindow(9, 10));
		DefaultParcel par2 = mockedParcel(new Point(2, 3), new Point(3, 5), 1l, 1l, new TimeWindow(0, 10),
				new TimeWindow(0, 12));

		addParcel(par1);
		addParcel(par2);
		addParcel(par1);
		addParcel(par2);

		buildRoute();

		assertEquals(4.0, ev.calculateSlackForState(0).get(par1), 0.0001);
		assertEquals(6.0, ev.calculateSlackForState(0).get(par2), 0.0001);
	}

	/**
	 * Builds route and assigns it to the truck
	 */
	private void buildRoute() {
		when(tr.getRoute()).thenReturn(pars.build());
	}

	/**
	 * Adds parcel to route
	 * @param par Parcel to be added
	 */
	private void addParcel(DefaultParcel par) {
		pars.add(par);
	}

	/**
	 * Mocks a DefaultParcel with given parameters
	 * @param pickup The pick-up location
	 * @param delivery The delivery location
	 * @param pDur Duration of pick-up
	 * @param dDur Duration of delivery
	 * @param pickupWindow Pickup timewindow
	 * @param deliveryWindow Delivery timewindow
	 * @return
	 */
	private DefaultParcel mockedParcel(Point pickup, Point delivery, long pDur, long dDur, TimeWindow pickupWindow,
									  TimeWindow deliveryWindow) {
		DefaultParcel par = mock(DefaultParcel.class);
		when(par.getPickupDuration()).thenReturn(pDur);
		when(par.getDeliveryDuration()).thenReturn(dDur);
		when(par.getDeliveryTimeWindow()).thenReturn(deliveryWindow);
		when(par.getPickupTimeWindow()).thenReturn(pickupWindow);
		when(par.getPickupLocation()).thenReturn(pickup);
		when(par.getDestination()).thenReturn(delivery);

		return par;
	}
}

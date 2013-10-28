package ra;

import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.pdptw.common.ObjectiveFunction;
import rinde.sim.util.SupplierRng;

/**
 * Created with IntelliJ IDEA.
 * User: victor
 * Date: 28/10/13
 * Time: 15:52
 */
public class TestReAuctionBidder extends AbstractReAuctionBidder {

	@Override
	protected int getDelay() {
		return 50 + rng.nextInt(50);
	}

	@Override
	protected void reEvaluateParcels() {
		System.out.println("tick");
		commModel.reAuction(null);
	}

	@Override
	public double getBidFor(DefaultParcel p, long time) {
		return 0;
	}

	public static SupplierRng<TestReAuctionBidder> supplier(final ObjectiveFunction objFunc) {
		return new SupplierRng.DefaultSupplierRng<TestReAuctionBidder>() {
			@Override
			public TestReAuctionBidder get(long seed) {
				return new TestReAuctionBidder();
			}
		};
	}
}

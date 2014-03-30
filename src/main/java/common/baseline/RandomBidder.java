package common.baseline;

import common.truck.Bid;
import common.truck.Bidder;
import rinde.sim.pdptw.common.DefaultParcel;
import rinde.sim.util.SupplierRng;

import java.util.Random;

/**
 * Random bidding strategy
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class RandomBidder extends Bidder {

	private Random rng;

	public RandomBidder(long seed) {
		this.rng = new Random(seed);
	}

	@Override
	public Bid getBidFor(DefaultParcel par, long time) {
		int bid = rng.nextInt(100);

		return new Bid<DefaultParcel>(this, par, bid);
	}

	public static SupplierRng<RandomBidder> supplier() {
		return new SupplierRng.DefaultSupplierRng<RandomBidder>() {
			@Override
			public RandomBidder get(long seed) {
				return new RandomBidder(seed);
			}

			@Override
			public String toString() {
				return super.toString();
			}
		};
	}
}

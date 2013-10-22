package ca.wdp;

import common.Bid;
import rinde.sim.pdptw.common.DefaultParcel;

import java.util.*;

/**
 * Implements a brute force, depth first WDP solver
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class BruteForceParcelAllocator extends ParcelAllocator {

	@Override
	ParcelAllocation solve() {
		Collection<Bid> bestAllocation = null;
		double bestValue = Double.MAX_VALUE;

		// Prune all bids a that are contained by bid b and have a lower value
		List<Bid> prunedBids = new LinkedList<Bid>(bids);	// Copy list since we're removing things
		Bid bi, bj;

		for (int i = 0; i < bids.size(); i++) {
			bi = bids.get(i);

			for (int j = 0; j < bids.size(); j++) {
				if (i == j) continue;
				bj = bids.get(j);

				if (bj.contains(bi) && bi.getBidValue() < bj.getBidValue())
					prunedBids.remove(bi);
			}
		}

		// Loop over goods
		// Select some bid for some good
		// Allocate it -> this spreads it out over the goods it belongs to
		// Recursive step next good
		// If good already allocated continue deeper
		// Backtrack etc

		return solveRecursiveStep(getBinQueue(prunedBids), new ParcelAllocation());
	}

	private ParcelAllocation solveRecursiveStep(Queue<Set<Bid>> queue, ParcelAllocation acc) {
		// Base case
		if (queue.isEmpty())
			return new ParcelAllocation();

		Iterator<Bid> it = queue.poll().iterator();
		Bid curBid;
		double localBest = Double.MAX_VALUE;
		ParcelAllocation localBestAllocation = null;
		ParcelAllocation nextStep;
		ParcelAllocation nextAcc;

		while (it.hasNext()) {
			curBid = it.next();

			nextAcc = acc;

			// Already added, don't do it a second time
			if (!acc.containsBid(curBid) && !acc.conflictingBid(curBid)) {
				// Recursion to next level
				nextAcc = new ParcelAllocation(acc);
				nextAcc.allocateBid(curBid);
			}

			nextStep = solveRecursiveStep(new LinkedList<Set<Bid>>(queue), nextAcc);

			if (nextStep != null && nextStep.getValue() < localBest) {
				localBest = nextStep.getValue();
				localBestAllocation = nextStep;
			}
		}

		return localBestAllocation;
	}

	private Queue<Set<Bid>> getBinQueue(List<Bid> bids) {
		// First bin all bids, then convert to queue
		Map<DefaultParcel, Set<Bid>> bins = new HashMap<DefaultParcel, Set<Bid>>();

		for (Bid b : bids) {
			for (DefaultParcel p : b.getParcels()) {
				if (!bins.containsKey(p)) {
					// Init bin
					bins.put(p, new HashSet<Bid>());
				}

				// Throw in bin
				bins.get(p).add(b);
			}
		}

		Queue<Set<Bid>> ret = new LinkedList<Set<Bid>>();

		for (Set<Bid> bin : bins.values()) {
			ret.offer(bin);
		}

		return ret;
	}

}

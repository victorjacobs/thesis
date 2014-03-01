package common.results;

import common.auctioning.Auctioneer;
import common.auctioning.ReAuctionableParcel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newLinkedHashMap;

/**
 * Tracks all parcels created in simulation runs. This is needed to later derive some interesting statistics from
 * them. Auctioneers are used as key for data storage, since only one of them is created every run.
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ParcelTracker {

	private static Map<String, List<ReAuctionableParcel>> parcels;

	public synchronized static void addParcel(Auctioneer auctioneer, ReAuctionableParcel par) {
		if (parcels == null)
			parcels = newLinkedHashMap();

		if (!parcels.containsKey(auctioneer.toString()))
			parcels.put(auctioneer.toString(), new LinkedList<ReAuctionableParcel>());

		parcels.get(auctioneer.toString()).add(par);
	}

	public static Map<String, List<ReAuctionableParcel>> getParcels() {
		return parcels;	// Be nice and don't tinker with the resulting value
	}

}

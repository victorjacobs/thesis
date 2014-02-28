package common;

import com.google.common.collect.ImmutableList;
import common.auctioning.ReAuctionableParcel;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public class ParcelTracker {

	private static List<ReAuctionableParcel> parcels;

	public synchronized static void addParcel(ReAuctionableParcel par) {
		if (parcels == null)
			parcels = new LinkedList<ReAuctionableParcel>();

		parcels.add(par);
	}

	public static ImmutableList<ReAuctionableParcel> getParcels() {
		return ImmutableList.copyOf(parcels);
	}

}

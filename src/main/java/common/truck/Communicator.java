package common.truck;

import rinde.logistics.pdptw.mas.route.RoutePlanner;

/**
 * Created with IntelliJ IDEA.
 * User: victor
 * Date: 04/11/13
 * Time: 18:23
 */
public abstract class Communicator {
	private final ReAuctionTruck truck;
	private final RoutePlanner routePlanner;

	public Communicator(ReAuctionTruck truck, RoutePlanner routePlanner) {
		this.truck = truck;
		this.routePlanner = routePlanner;
	}
}

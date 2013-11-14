package common.truck;

import rinde.sim.pdptw.common.DefaultParcel;

/**
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public interface ParcelRemoveHandler {

	void handleParcelRemove(DefaultParcel par, long time);
}

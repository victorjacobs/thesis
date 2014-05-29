package common.truck;

/**
 *
 * @author Victor Jacobs <victor.jacobs@me.com>
 */
public interface StateObserver {
    public void setTruck(Truck truck);
	public void notify(long time);
}

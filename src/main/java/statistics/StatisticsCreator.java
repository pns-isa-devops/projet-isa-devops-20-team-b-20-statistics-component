package statistics;

import javax.ejb.Local;

import statistics.exception.NoOccupancyOnThatDroneException;

import java.util.GregorianCalendar;

@Local
public interface StatisticsCreator {
    /**
     * Modify the statistics for the droneId at the date specified
     * This method is supposed to be called everytime something is done on the drone (reparation,charge,delivery)
     * @throws NoOccupancyOnThatDroneException
     *
     */
    void addOccupancy(String droneId, GregorianCalendar date,double duration) throws NoOccupancyOnThatDroneException;
}

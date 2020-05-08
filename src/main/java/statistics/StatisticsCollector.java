package statistics;

import javax.ejb.Local;

import statistics.exception.NoOccupancyOnThatDroneException;

@Local
public interface StatisticsCollector {
    /**
     * Returns the statistics on drone for every day since the beginning .
     * 
     * @throws NoOccupancyOnThatDroneException
     *
     */
    double getOccupancyRate(String droneId) throws NoOccupancyOnThatDroneException;
}

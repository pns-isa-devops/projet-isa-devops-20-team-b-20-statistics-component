package statistics;

import javax.ejb.Local;

@Local
public interface StatisticsCollector {
    /**
     * Returns the statistics on drone for every day since the beginning .
     *
     */
    double getOccupancyRate(String droneId);
}

package statistics;

import javax.ejb.Local;
import fr.polytech.entities.Drone;


@Local
public interface StatisticsCollector {
    /**
     * Returns the statistics on drone for every day since the beginning .
     *
     */
    double getOccupancyRate(String droneId);
}

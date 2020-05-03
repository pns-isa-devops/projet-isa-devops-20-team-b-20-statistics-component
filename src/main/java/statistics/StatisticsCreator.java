package statistics;

import javax.ejb.Local;
import java.util.GregorianCalendar;

@Local
public interface StatisticsCreator {
    /**
     * Modify the statistics for the droneId at the date specified
     * This method is supposed to be called everytime something is done on the drone (reparation,charge,delivery)
     *
     */
    void addOccupancy(String droneId, GregorianCalendar date,double duration);
}

package statistics;

import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fr.polytech.entities.Drone;
import fr.polytech.entities.DroneInformation;
import statistics.exception.NoOccupancyOnThatDroneException;

@Stateless
public class StatisticsBean implements StatisticsCollector, StatisticsCreator {

    private static final Logger log = Logger.getLogger(StatisticsBean.class.getName());

    @EJB
    StatisticsCollector statisticsCollector;

    @EJB
    StatisticsCreator statisticsCreator;

    private final static int HOURS_OF_OCCUPANCY_MAX = 10;

    @PersistenceContext
    private EntityManager entityManager;

    public StatisticsBean() {

    }

    private Optional<Drone> getDroneFromId(String droneId) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Drone> criteria = builder.createQuery(Drone.class);
        Root<Drone> root = criteria.from(Drone.class);
        criteria.select(root).where(builder.equal(root.get("droneId"), droneId));

        TypedQuery<Drone> query = entityManager.createQuery(criteria);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            log.log(Level.FINEST, "No result for [" + droneId + "]", e);
            return Optional.empty();
        }
    }

    @Override
    public double getOccupancyRate(String droneId) throws NoOccupancyOnThatDroneException {
        Drone drone = getDrone(droneId);
        DroneInformation droneInformation = drone.getDroneInformationAtDate(new GregorianCalendar());
        return droneInformation != null ? droneInformation.getOccupationRate() / HOURS_OF_OCCUPANCY_MAX : 0.0;

    }

    @Override
    public void addOccupancy(String droneId, GregorianCalendar date, double duration)
            throws NoOccupancyOnThatDroneException {
        Drone drone = getDrone(droneId);
        DroneInformation droneInformation = drone.getDroneInformationAtDate(date);

        // Recalculate the occupancy by using HOURS_MAX and duration
        if (droneInformation != null) {
            double currentOccupancy = droneInformation.getOccupationRate();

            currentOccupancy = currentOccupancy + duration;
            droneInformation.setOccupationRate(currentOccupancy);

        } else {
            droneInformation = new DroneInformation(date);
            droneInformation.setOccupationRate(duration);
            entityManager.persist(droneInformation);

            Set<DroneInformation> droneInformations = drone.getDroneInformation();
            droneInformations.add(droneInformation);
        }
    }

    private Drone getDrone(String droneId) throws NoOccupancyOnThatDroneException {
        Optional<Drone> drone = getDroneFromId(droneId);
        if (!drone.isPresent()) {
            throw new NoOccupancyOnThatDroneException(droneId);
        }
        return drone.get();
    }
}

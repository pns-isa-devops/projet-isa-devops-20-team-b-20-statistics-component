package statistics;

import fr.polytech.entities.Drone;
import fr.polytech.entities.DroneInformation;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;


@Stateless
public class StatisticsBean implements StatisticsCollector, StatisticsCreator{

    @EJB
    StatisticsCollector statisticsCollector;

    @EJB
    StatisticsCreator statisticsCreator;

    private final static int HOURS_OF_OCCUPANCY_MAX = 10;

    @PersistenceContext
    private EntityManager entityManager;

    public StatisticsBean() {

    }


    private Drone getDroneFromId(String droneId){
        // GET the drone

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Drone> criteria = builder.createQuery(Drone.class);
        Root<Drone> root = criteria.from(Drone.class);
        criteria.select(root).where(builder.equal(root.get("droneId"), droneId));

        TypedQuery<Drone> query = entityManager.createQuery(criteria);
        try {
            Optional<Drone> droneOptional =  Optional.of(query.getSingleResult());
            if (droneOptional.isPresent()) {
                return droneOptional.get();

            } else {
                return null;
                //  throw new DroneNotFoundException("The drone 000 has not been found.");
            }
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public double getOccupancyRate(String droneId) {

        // GET the drone

        Drone drone = getDroneFromId(droneId);
        System.out.println(drone);

        // Get occupancies

        DroneInformation droneInformation = drone.getDroneInformationAtDate(new GregorianCalendar());

        if(droneInformation != null){
            return droneInformation.getOccupationRate()/HOURS_OF_OCCUPANCY_MAX;
        }
        else{
            return 0.0;

        }
    }

    @Override
    public void addOccupancy(String droneId, GregorianCalendar date, double duration) {

        Drone drone = getDroneFromId(droneId);

        DroneInformation droneInformation = drone.getDroneInformationAtDate(date);

        // Recalculate the occupancy by using HOURS_MAX and duration
        if(droneInformation != null){
            double currentOccupancy = droneInformation.getOccupationRate();

            currentOccupancy = currentOccupancy + duration;
            droneInformation.setOccupationRate(currentOccupancy);

        }
        else{
            droneInformation = new DroneInformation(date,drone);
            droneInformation.setOccupationRate(duration);
            entityManager.persist(droneInformation);


            Set<DroneInformation> droneInformations = drone.getDroneInformation();
            droneInformations.add(droneInformation);
        }

        //Persist
        entityManager.persist(drone);


    }
}


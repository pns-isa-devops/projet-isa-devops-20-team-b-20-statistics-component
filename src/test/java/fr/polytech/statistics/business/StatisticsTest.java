package fr.polytech.statistics.business;

import static org.junit.Assert.assertEquals;

import java.util.GregorianCalendar;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import arquillian.AbstractStatisticsTest;
import fr.polytech.entities.Drone;
import statistics.StatisticsCollector;
import statistics.StatisticsCreator;
import statistics.exception.NoOccupancyOnThatDroneException;

/**
 * StatisticsTest
 */
@RunWith(Arquillian.class)
@Transactional(TransactionMode.COMMIT)
public class StatisticsTest extends AbstractStatisticsTest {
    Drone drone1;
    Drone drone2;

    @PersistenceContext
    EntityManager entityManager;

    @EJB
    StatisticsCollector statisticsCollector;

    @EJB
    StatisticsCreator statisticsCreator;

    @Inject
    UserTransaction utx;

    @Before
    public void init() {
        drone1 = new Drone("000");
        drone2 = new Drone("111");

        entityManager.persist(drone1);
        entityManager.persist(drone2);
    }

    @Test
    public void OccupancyTest() throws NoOccupancyOnThatDroneException {

        // Add an occupancy delivery
        statisticsCreator.addOccupancy(drone1.getDroneId(), new GregorianCalendar(), 0.25);

        assertEquals(0.25 / 10, statisticsCollector.getOccupancyRate(drone1.getDroneId()), 0.001);

        // Add two occupancy charge and review
        statisticsCreator.addOccupancy(drone1.getDroneId(), new GregorianCalendar(), 1);
        statisticsCreator.addOccupancy(drone1.getDroneId(), new GregorianCalendar(), 3);

        assertEquals(4.25 / 10, statisticsCollector.getOccupancyRate(drone1.getDroneId()), 0.001);
    }

    @Test
    public void OccupancyTestTwoDays() throws NoOccupancyOnThatDroneException {
        // fixed date
        GregorianCalendar fixedDay = new GregorianCalendar(2018, 5, 30);

        // Add an occupancy delivery
        statisticsCreator.addOccupancy(drone1.getDroneId(), fixedDay, 0.25);

        assertEquals(0, statisticsCollector.getOccupancyRate(drone1.getDroneId()), 0.001);

        // Add an occupancy delivery
        statisticsCreator.addOccupancy(drone1.getDroneId(), new GregorianCalendar(), 1);
        statisticsCreator.addOccupancy(drone1.getDroneId(), new GregorianCalendar(), 3);

        assertEquals(4.0 / 10, statisticsCollector.getOccupancyRate(drone1.getDroneId()), 0.001);
    }

    @After
    public void clean() throws SystemException, NotSupportedException, HeuristicRollbackException,
            HeuristicMixedException, RollbackException {
        utx.begin();

        drone1 = entityManager.merge(drone1);
        drone2 = entityManager.merge(drone2);
        entityManager.remove(drone1);
        entityManager.remove(drone2);

        utx.commit();
    }

}

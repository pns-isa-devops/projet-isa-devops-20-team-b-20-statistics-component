package arquillian;

import fr.polytech.entities.Delivery;
import org.hsqldb.Database;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import statistics.StatisticsBean;
import statistics.StatisticsCollector;
import statistics.StatisticsCreator;

import javax.ejb.EJB;

/**
 * AbstractEntitiesTest
 */
public class AbstractStatisticsTest {


    @Deployment
    public static WebArchive createDeployment() {
        // @formatter:off
        return ShrinkWrap.create(WebArchive.class)
                // Components and Interfaces
                .addPackage(StatisticsBean.class.getPackage())
                // Libraries
                .addAsLibraries(Maven.resolver()
                        .loadPomFromFile("pom.xml")
                        .importRuntimeDependencies()
                        .resolve()
                        .withTransitivity()
                        .asFile());


        // @formatter:on
    }
}

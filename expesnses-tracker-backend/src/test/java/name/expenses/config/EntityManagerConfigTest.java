package name.expenses.config;

import junit.framework.TestCase;

public class EntityManagerConfigTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
    }

    public void testEntityManager() {
        System.out.println(EntityManagerConfig.entityManager());
    }
}
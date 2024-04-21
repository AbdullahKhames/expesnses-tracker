package name.expenses.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.Persistence;

public class EntityManagerConfig {
    private static final EntityManagerFactory entityManagerFactory;
    static {

        entityManagerFactory = Persistence.createEntityManagerFactory("expenses-unit");
    }
    public static EntityManager entityManager(){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.setFlushMode(FlushModeType.AUTO);
        return entityManager;
    }

}
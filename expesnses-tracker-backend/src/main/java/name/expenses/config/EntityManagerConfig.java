package name.expenses.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.jpa.HibernatePersistenceProvider;
import java.util.HashMap;

public class EntityManagerConfig {
    private static final EntityManagerFactory entityManagerFactory;
    static {
//        entityManagerFactory = new HibernatePersistenceProvider()
//                .createContainerEntityManagerFactory(new MyPersistenceUnit(), new HashMap());
        entityManagerFactory = Persistence.createEntityManagerFactory("expenses-unit");
    }
    public static EntityManager entityManager(){
        return entityManagerFactory.createEntityManager();
    }

}

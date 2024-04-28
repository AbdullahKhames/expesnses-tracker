package name.expenses.repostitories;

import jakarta.persistence.EntityManager;
import name.expenses.config.EntityManagerConfig;
import name.expenses.models._2auth;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class _2authRepoImplTest {
    static EntityManager entityManager = EntityManagerConfig.entityManager();

    static _2authRepoImpl authRepo;
    @BeforeAll
    public static void init(){
        authRepo = new _2authRepoImpl();
        authRepo.setEntityManager(entityManager);
    }

    @Test
    void findByEmailAndOtpAndExpiredFalse() {
        _2auth auth = authRepo.findByEmailAndOtpAndExpiredFalse("new", "1234");
        System.out.println(auth);
        assertNotNull(auth);
        assertFalse(auth.isExpired());
    }
}
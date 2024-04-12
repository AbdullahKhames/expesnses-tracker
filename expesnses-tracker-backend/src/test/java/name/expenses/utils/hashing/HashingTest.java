package name.expenses.utils.hashing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashingTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void hash() {
        System.out.println(Hashing.hash("1234"));
    }

    @Test
    void verify() {
    }

    @Test
    void verifyAndUpdateHash() {
    }
}
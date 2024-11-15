package domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TransactionTest {

    Transaction transaction;

    @BeforeEach
    void setup() {
        transaction = new Transaction();
        transaction.setTransactionId(1);
    }

    @Test
    @DisplayName("Test equals same id")
    void testEqualsSameId() {
        Transaction transaction2 = new Transaction();
        transaction2.setTransactionId(1);

        assertTrue(transaction.equals(transaction2));
    }

    @Test
    @DisplayName("Test equals different id")
    void testEqualsDifferentId() {
        Transaction transaction2 = new Transaction();
        transaction2.setTransactionId(2);

        assertFalse(transaction.equals(transaction2));
    }

    @Test
    @DisplayName("Test different class")
    void testEqualsDifferentClass() {
        assertFalse(transaction.equals(new Object()));
    }
}

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
    void testEquals_SameId() {
        Transaction transaction2 = new Transaction();
        transaction2.setTransactionId(1);

        assertTrue(transaction.equals(transaction2));
    }

    @Test
    @DisplayName("Test equals different id")
    void testEquals_DifferentId() {
        Transaction transaction2 = new Transaction();
        transaction2.setTransactionId(2);

        assertFalse(transaction.equals(transaction2));
    }

    @Test
    @DisplayName("Test different class")
    void testEquals_DifferentClass() {
        assertFalse(transaction.equals(new Object()));
    }
}

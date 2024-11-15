package domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TransactionEngineTest {

    TransactionEngine transactionEngine;
    Transaction transaction1, transaction2, transaction3, transaction4;

    @BeforeEach
    void setup() {
        transactionEngine = new TransactionEngine();

        transaction1 = new Transaction();
        transaction1.setTransactionId(1);
        transaction1.setAccountId(1);
        transaction1.setDebit(false);
        transaction1.setAmount(100);

        transaction2 = new Transaction();
        transaction2.setTransactionId(2);
        transaction2.setAccountId(1);
        transaction2.setDebit(false);
        transaction2.setAmount(50);

        transaction3 = new Transaction();
        transaction3.setTransactionId(3);
        transaction3.setAccountId(2);
        transaction3.setDebit(false);
        transaction3.setAmount(200);

        transaction4 = new Transaction();
        transaction4.setTransactionId(4);
        transaction4.setAccountId(1);
        transaction4.setDebit(false);
        transaction4.setAmount(260);
    }

    @Test
    @DisplayName("Test getAverageTransactionAmountByAccount empty")
    void testGetAverageTransactionAmountByAccountEmpty() {
        assertEquals(0, transactionEngine.getAverageTransactionAmountByAccount(1));
    }

    @Test
    @DisplayName("Test getAverageTransactionAmountByAccount not empty")
    void testGetAverageTransactionAmountByAccount() {
        transactionEngine.addTransactionAndDetectFraud(transaction1);
        transactionEngine.addTransactionAndDetectFraud(transaction2);
        transactionEngine.addTransactionAndDetectFraud(transaction3);
        assertEquals(75, transactionEngine.getAverageTransactionAmountByAccount(1));
    }

    @Test
    @DisplayName("Test getTransactionPatternAboveThreshold empty")
    void testGetTransactionPatternAboveThresholdEmpty() {
        assertEquals(0, transactionEngine.getTransactionPatternAboveThreshold(100));
    }

    @Test
    @DisplayName("Test getTransactionPatternAboveThreshold not empty")
    void testGetTransactionPatternAboveThreshold() {
        transactionEngine.addTransactionAndDetectFraud(transaction1);
        transactionEngine.addTransactionAndDetectFraud(transaction2);
        transactionEngine.addTransactionAndDetectFraud(transaction3);
        assertEquals(100, transactionEngine.getTransactionPatternAboveThreshold(50));
    }
}

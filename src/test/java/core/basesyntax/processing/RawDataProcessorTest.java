package core.basesyntax.processing;

import static org.junit.jupiter.api.Assertions.assertThrows;

import core.basesyntax.enums.Operation;
import core.basesyntax.exceptions.NegativeFruitBalanceException;
import core.basesyntax.handler.TransactionHandler;
import core.basesyntax.handler.impl.BalanceTransactionHandler;
import core.basesyntax.handler.impl.PurchaseTransactionHandler;
import core.basesyntax.handler.impl.ReturnTransactionHandler;
import core.basesyntax.handler.impl.SupplyTransactionHandler;
import core.basesyntax.reader.CsvFileReader;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RawDataProcessorTest {
    private static final int ZERO_INDEX_POSITION = 0;
    private static final int FIRST_INDEX_POSITION = 1;
    private static final int SECOND_INDEX_POSITION = 2;
    private static final int THIRD_INDEX_POSITION = 3;
    private static final int FOURTH_INDEX_POSITION = 4;
    private static final int SEVENTH_INDEX_POSITION = 7;
    private static final int QUANTITY_EXAMPLE = 20;
    private static final String FRUIT_BANANA = "banana";
    private static final String FRUIT_APPLE = "apple";
    private static final String FILE_TO_READ =
            "src/test/resources/db/ReadTransactionTest";
    private static final String FILE_WITH_NEGATIVE_BALANCE =
            "src/test/resources/db/fileWithNegativeBalance";
    private static final String FILE_WITH_DIFFERENT_TYPES =
            "src/test/resources/db/ReadTransactionWithDifferentTypes";

    private CsvFileReader fileReader;
    private RawDataProcessor rawDataProcessor;

    @BeforeEach
    void setUp() {
        Map<String, TransactionHandler> map = Map.of(
                Operation.BALANCE.getCode(), new BalanceTransactionHandler(),
                Operation.SUPPLY.getCode(), new SupplyTransactionHandler(),
                Operation.PURCHASE.getCode(), new PurchaseTransactionHandler(),
                Operation.RETURN.getCode(), new ReturnTransactionHandler()
        );
        rawDataProcessor = new RawDataProcessor(map);
    }

    @Test
    void reading_notEmptyFile_Ok() {
        fileReader = new CsvFileReader(FILE_TO_READ);
        List<List<String>> actualLists = fileReader.readTransactions();

        Map<String, Integer> actualProcessMap = rawDataProcessor.process(actualLists);
        Map<String, Integer> expectedMap = Map.of(
                FRUIT_BANANA, QUANTITY_EXAMPLE,
                FRUIT_APPLE, QUANTITY_EXAMPLE
        );

        Assertions.assertEquals(expectedMap, actualProcessMap);
    }

    @Test
    void checkTypesOperation_Ok() {
        fileReader = new CsvFileReader(FILE_WITH_DIFFERENT_TYPES);
        List<List<String>> actualLists = fileReader.readTransactions();

        Assertions.assertEquals(
                Operation.BALANCE.getCode(),
                actualLists.get(ZERO_INDEX_POSITION).get(ZERO_INDEX_POSITION)
        );
        Assertions.assertEquals(
                Operation.BALANCE.getCode(),
                actualLists.get(FIRST_INDEX_POSITION).get(ZERO_INDEX_POSITION)
        );
        Assertions.assertEquals(
                Operation.SUPPLY.getCode(),
                actualLists.get(SECOND_INDEX_POSITION).get(ZERO_INDEX_POSITION)
        );
        Assertions.assertEquals(
                Operation.PURCHASE.getCode(),
                actualLists.get(THIRD_INDEX_POSITION).get(ZERO_INDEX_POSITION)
        );
        Assertions.assertEquals(
                Operation.RETURN.getCode(),
                actualLists.get(FOURTH_INDEX_POSITION).get(ZERO_INDEX_POSITION)
        );
        Assertions.assertEquals(
                Operation.SUPPLY.getCode(),
                actualLists.get(SEVENTH_INDEX_POSITION).get(ZERO_INDEX_POSITION)
        );
    }

    @Test
    void processWithNegativeBalance_NotOk() {
        fileReader = new CsvFileReader(FILE_WITH_NEGATIVE_BALANCE);
        List<List<String>> actualLists = fileReader.readTransactions();

        assertThrows(NegativeFruitBalanceException.class,
                () -> rawDataProcessor.process(actualLists));
    }
}

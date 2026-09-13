package org.labs;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.Executors;

import org.junit.jupiter.api.Test;

class SpoonTest {
    @Test
    void takeAfterPut() throws Exception {
        Spoon spoon = new Spoon(0);

        spoon.take();
        spoon.put();

        try (var executor = Executors.newSingleThreadExecutor()) {
            var result = executor.submit(() -> {
                spoon.take();

                try {
                    return true;
                } finally {
                    spoon.put();
                }
            });

            assertTrue(result.get());
        }
    }
}

package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PaoloUnitTest {

    @Test
    void test() throws ClassNotFoundException {
        Mockito.mock(Object.class);
        Assertions.assertNotNull(PaoloUnitTest.class);
    }
}

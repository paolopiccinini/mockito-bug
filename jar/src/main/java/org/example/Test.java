package org.example;

import org.mockito.Mockito;

public class Test {

    @org.junit.jupiter.api.Test
    void test() throws ClassNotFoundException {
        //Class.forName(" org.mockito.internal.creation.bytebuddy.inject.MockMethodDispatcher");
        Mockito.mock(Object.class);
    }
}

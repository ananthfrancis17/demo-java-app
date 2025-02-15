package com.example;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {
    @Test
    public void testAppGreeting() {
        App app = new App();
        assertNotNull("App should have a greeting", app.getGreeting());
        assertEquals("Hello, World!", app.getGreeting());
    }
}
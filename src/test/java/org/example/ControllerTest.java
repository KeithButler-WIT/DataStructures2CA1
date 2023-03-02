package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class ControllerTest {

    int[] imageArray;

    @BeforeEach
    void setUp() {
        imageArray = new int[]{ 0, 1, 0, 0, 0,
                                0, 1, 8, 8, 0,
                                0, 1, 1, 8, 8,
                                16, 1, 16, 8, 0,
                                0, 16, 0, 0, 0 };
    }

    @AfterEach
    void tearDown() {
        imageArray = new int[]{0};
    }

    @Test
    void find() {
        assertEquals(1, Controller.find(imageArray, 1));
        assertEquals(1, Controller.find(imageArray, 6));
        assertEquals(1, Controller.find(imageArray, 15));
        assertEquals(8, Controller.find(imageArray, 7));
        assertEquals(8, Controller.find(imageArray, 8));
    }

    @Test
    void union() {
        Controller.union(imageArray, 7, 6);
        assertEquals(8, Controller.find(imageArray, 7));
//        Controller.union(imageArray, 7, 6);
//        assertEquals(imageArray, Controller.find(imageArray, 7));
    }

    //TODO: MORE TESTS
}
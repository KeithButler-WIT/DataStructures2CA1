package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
class ControllerTest {

    long[] imageArray;

    @BeforeEach
    void setUp() {
        imageArray = new long[]{ 0, 1, 0, 0, 0,
                                0, 1, 8, 8, 0,
                                0, 1, 1, 8, 8,
                                16, 1, 16, 8, 0,
                                0, 16, 0, 0, 0 };

        for (int i = 0; i< imageArray.length; i++) {
            Controller.setRoot(imageArray,i,imageArray[i]);
            if(imageArray[i] == 1) Controller.setSize(imageArray,i,5);
            else if(imageArray[i] == 8) Controller.setSize(imageArray,i,5);
            else if(imageArray[i] == 16) Controller.setSize(imageArray,i,3);
            else Controller.setSize(imageArray,i,1);
        }
    }

    @AfterEach
    void tearDown() {
        imageArray = new long[]{};
    }

    @Test
    void find() {
        assertEquals(1, Controller.find(imageArray, 1));
        assertEquals(1, Controller.find(imageArray, 6));
        assertEquals(1, Controller.find(imageArray, 15));
        assertEquals(8, Controller.find(imageArray, 7));
        assertEquals(8, Controller.find(imageArray, 8));
//        assertThrows(ArrayIndexOutOfBoundsException, Controller.find(imageArray, 100));
//        assertThrows(ArrayIndexOutOfBoundsException, Controller.find(imageArray, -1));
    }

    @Test
    void getSize() {
        assertEquals(5, Controller.getSize(imageArray[1]));
    }

    @Test
    void getRoot() {
        assertEquals(1, Controller.getRoot(imageArray[1]));
        assertEquals(8, Controller.getRoot(imageArray[7]));
    }

    @Test
    void setSize() {
        assertEquals(5, Controller.getSize(imageArray[1]));
        Controller.setSize(imageArray, 1, 6);
        assertEquals(6, Controller.getSize(imageArray[1]));
        Controller.setSize(imageArray, 1, 100);
        assertEquals(100, Controller.getSize(imageArray[1]));
    }

    @Test
    void setRoot() {
        assertEquals(0, Controller.getRoot(imageArray[0]));
        Controller.setRoot(imageArray, 0, 1);
        assertEquals(1, Controller.getRoot(imageArray[0]));

        assertEquals(0, Controller.getRoot(imageArray[5]));
        Controller.setRoot(imageArray, 5, 1);
        assertEquals(1, Controller.getRoot(imageArray[5]));
    }

    @Test
    void union() {
        assertEquals(8, Controller.find(imageArray, 7));
        Controller.union(imageArray, 7, 6);
        assertEquals(1, Controller.find(imageArray, 7));
    }

    //TODO: MORE TESTS
}
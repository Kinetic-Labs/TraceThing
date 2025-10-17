package com.github.kinetic.tracething;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    @Test
    void main() {
        // When
        try {
            Main.main(new String[]{});
        } catch (InterruptedException e) {
            // Then
            e.printStackTrace();
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackframe.pattymelt;

import com.stackframe.pattymelt.DCPU16.Register;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author mcculley
 */
public class DCPU16Test {

    public DCPU16Test() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of initial state.
     */
    @Test
    public void initialStateTest() {
        System.out.println("Testing initial state.");
        DCPU16 cpu = new DCPU16Emulator();
        short[] memory = cpu.memory();
        for (int i = 0; i < 0x10000; i++) {
            assertEquals("memory at " + i, 0, memory[i]);
        }

        for (DCPU16.Register r : DCPU16.Register.values()) {
            assertEquals(r.name(), 0, cpu.register(r));
        }

        assertEquals("PC", 0, cpu.PC());
        assertEquals("SP", (short) 0xFFFF, (short) cpu.SP()); // FIXME: Not sure if this is what SP should be at initial state. Should it be zero?
        assertEquals("O", 0, cpu.O());
    }
}

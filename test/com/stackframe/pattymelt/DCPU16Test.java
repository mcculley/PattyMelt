/*
 * Copyright 2012, Gene McCulley
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *   Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 *
 *   Redistributions in binary form must reproduce the above copyright 
 *   notice, this list of conditions and the following disclaimer in the 
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR 
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT 
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package com.stackframe.pattymelt;

import java.nio.ShortBuffer;
import org.junit.*;
import static org.junit.Assert.*;

/**
 * Unit tests for DCPU-16 implementation.
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
        Memory memory = cpu.memory();
        for (int i = 0; i < 0x10000; i++) {
            assertEquals("memory at " + i, 0, memory.get(i));
        }

        for (DCPU16.Register r : DCPU16.Register.values()) {
            assertEquals(r.name(), 0, cpu.register(r));
        }

        assertEquals("PC", 0, cpu.PC());
        assertEquals("SP", 0, (short) cpu.SP());
        assertEquals("O", 0, cpu.O());
    }

    /**
     * Test of simple program defined in v1.1 of the specification.
     */
    @Test
    public void simpleProgramTest() throws IllegalOpcodeException {
        System.out.println("Testing simple program.");
        DCPU16 cpu = new DCPU16Emulator();
        int[] program = new int[]{
            0x7c01,
            0x0030,
            0x7de1,
            0x1000,
            0x0020,
            0x7803,
            0x1000,
            0xc00d,
            0x7dc1,
            0x001a,
            0xa861,
            0x7c01,
            0x2000,
            0x2161,
            0x2000,
            0x8463,
            0x806d,
            0x7dc1,
            0x000d,
            0x9031,
            0x7c10,
            0x0018,
            0x7dc1,
            0x001a,
            0x9037,
            0x61c1,
            0x7dc1,
            0x001a,
            0x0000,
            0x0000,
            0x0000,
            0x0000
        };
        Memory memory = cpu.memory();
        for (int i = 0; i < program.length; i++) {
            memory.put(i,(short)program[i]);
        }

        // SET A, 0x30 ; 7c01 0030
        assertEquals("SET A, 0x0030", DCPU16Utilities.disassemble(memory, cpu.PC()));
        cpu.step();
        assertEquals("A", 0x30, cpu.A());
        assertEquals("O", 0x0, cpu.O());
        assertEquals("PC", 0x2, cpu.PC());

        // SET [0x1000], 0x20 ; 7de1 1000 0020
        assertEquals("SET [0x1000], 0x0020", DCPU16Utilities.disassemble(memory, cpu.PC()));
        cpu.step();
        assertEquals("memory", 0x20, memory.get(0x1000));
        assertEquals("O", 0x0, cpu.O());
        assertEquals("PC", 0x5, cpu.PC());

        // SUB A, [0x1000] ; 7803 1000
        assertEquals("SUB A, [0x1000]", DCPU16Utilities.disassemble(memory, cpu.PC()));
        cpu.step();
        assertEquals("A", 0x10, cpu.A());
        assertEquals("O", 0x0, cpu.O());
        assertEquals("PC", 0x7, cpu.PC());

        // IFN A, 0x10      ; c00d 
        //    SET PC, crash ; 7dc1 001a
        assertEquals("IFN A, 0x10", DCPU16Utilities.disassemble(memory, cpu.PC()));
        cpu.step();
        assertEquals("SET PC, 0x001A", DCPU16Utilities.disassemble(memory, cpu.PC()));
        cpu.step();
        assertEquals("A", 0x10, cpu.A());
        assertEquals("O", 0x0, cpu.O());
        assertEquals("PC", 0xA, cpu.PC());

        // SET I, 10 ; a861
        assertEquals("SET I, 0xA", DCPU16Utilities.disassemble(memory, cpu.PC()));
        cpu.step();
        assertEquals("I", 0xA, cpu.I());
        assertEquals("O", 0x0, cpu.O());
        assertEquals("PC", 0xB, cpu.PC());

        // SET A, 0x2000 ; 7c01 2000
        assertEquals("SET A, 0x2000", DCPU16Utilities.disassemble(memory, cpu.PC()));
        cpu.step();
        assertEquals("A", 0x2000, cpu.A());
        assertEquals("O", 0x0, cpu.O());
        assertEquals("PC", 0xD, cpu.PC());

        // SET [0x2000+I], [A] ; 2161 2000
        assertEquals("SET [0x2000+I], [A]", DCPU16Utilities.disassemble(memory, cpu.PC()));
        cpu.step();
        assertEquals("memory", memory.get(cpu.A()), memory.get(0x2000 + 0xA));
        assertEquals("O", 0x0, cpu.O());
        assertEquals("PC", 0xF, cpu.PC());

        // SUB I, 1 ; 8463
        assertEquals("SUB I, 0x1", DCPU16Utilities.disassemble(memory, cpu.PC()));
        cpu.step();
        assertEquals("I", 0x9, cpu.I());
        assertEquals("O", 0x0, cpu.O());
        assertEquals("PC", 0x10, cpu.PC());

        // IFN I, 0        ; 806d
        //    SET PC, loop ; 7dc1 000d [*]
        assertEquals("IFN I, 0x0", DCPU16Utilities.disassemble(memory, cpu.PC()));
        cpu.step();
        assertEquals("SET PC, 0x000D", DCPU16Utilities.disassemble(memory, cpu.PC()));
        cpu.step();
        assertEquals("I", 0x9, cpu.I());
        assertEquals("O", 0x0, cpu.O());
        assertEquals("PC", 0xD, cpu.PC());

        for (int i = 0; i < 9; i++) {
            cpu.step();
            cpu.step();
            cpu.step();
            cpu.step();
        }

        assertEquals("PC", 0x13, cpu.PC());

        // SET X, 0x4 ; 9031
        assertEquals("SET X, 0x4", DCPU16Utilities.disassemble(memory, cpu.PC()));
        cpu.step();
        assertEquals("X", 0x4, cpu.X());

        // JSR testsub ; 7c10 0018
        assertEquals("JSR 0x0018", DCPU16Utilities.disassemble(memory, cpu.PC()));
        cpu.step();
        assertEquals("PC", 0x18, cpu.PC());

        // SHL X, 4 ; 9037
        assertEquals("SHL X, 0x4", DCPU16Utilities.disassemble(memory, cpu.PC()));
        cpu.step();
        assertEquals("X", 0x40, cpu.X());

        // SET PC, POP ; 61c1
        assertEquals("SET PC, POP", DCPU16Utilities.disassemble(memory, cpu.PC()));
        cpu.step();
        assertEquals("PC", 0x16, cpu.PC());

        // SET PC, crash ; 7dc1 001a
        assertEquals("SET PC, 0x001A", DCPU16Utilities.disassemble(memory, cpu.PC()));
        cpu.step();
        assertEquals("PC", 0x1A, cpu.PC());

        // SET PC, crash ; 7dc1 001a
        assertEquals("SET PC, 0x001A", DCPU16Utilities.disassemble(memory, cpu.PC()));
        cpu.step();
        assertEquals("PC", 0x1A, cpu.PC());
    }
}

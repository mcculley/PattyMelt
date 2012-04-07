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

/**
 * An abstraction for the DCPU-16 virtual machine.
 *
 * @author mcculley
 */
public interface DCPU16 extends Runnable {

    public enum Register {

        A, B, C, X, Y, Z, I, J;

        /**
         * Get the name of a register by number.
         *
         * @param n the register number
         * @return the register name, as a char
         */
        public static char name(int n) {
            return values()[n].name().charAt(0);
        }
    }

    public enum Opcode {

        nonbasic,
        SET,
        ADD,
        SUB,
        MUL,
        DIV,
        MOD,
        SHL,
        SHR,
        AND,
        BOR,
        XOR,
        IFE,
        IFN,
        IFG,
        IFB;

        /**
         * Get the name of an opcode by number.
         *
         * @param n the opcode number
         * @return the opcode name
         */
        public static String name(int n) {
            return values()[n].name();
        }
    }

    /**
     * Get the program counter
     *
     * @return the current state of the program counter
     */
    short PC();

    /**
     * Get the stack pointer
     *
     * @return the current state of the stack pointer
     */
    short SP();

    /**
     * Get the overflow bit
     *
     * @return the current state of the overflow bit
     */
    short O();

    /**
     * Get a given register
     *
     * @param the register to return
     * @return the current state of the specified register
     */
    short register(Register r);

    /**
     * Get the A register
     *
     * @return the current state of the A register
     */
    short A();

    /**
     * Get the B register
     *
     * @return the current state of the B register
     */
    short B();

    /**
     * Get the C register
     *
     * @return the current state of the C register
     */
    short C();

    /**
     * Get the X register
     *
     * @return the current state of the X register
     */
    short X();

    /**
     * Get the Y register
     *
     * @return the current state of the Y register
     */
    short Y();

    /**
     * Get the Z register
     *
     * @return the current state of the Z register
     */
    short Z();

    /**
     * Get the I register
     *
     * @return the current state of the I register
     */
    short I();

    /**
     * Get the J register
     *
     * @return the current state of the J register
     */
    short J();

    /**
     * Step a single instruction.
     */
    void step() throws IllegalOpcodeException;

    /**
     * Get a buffer that represents the memory.
     *
     * @return a buffer that represents the memory.
     */
    ShortBuffer memory();
}

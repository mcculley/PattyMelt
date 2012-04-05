/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackframe.pattymelt;

/**
 *
 * @author mcculley
 */
public interface DCPU16 extends Runnable {

    public enum Register {

        A, B, C, X, Y, Z, I, J
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
     * Get the skip bit
     *
     * @return the current state of the skip bit
     */
    boolean SKIP();

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
     * Get an array that represents the memory.
     *
     * @return an array that represents the memory.
     */
    short[] memory();
}

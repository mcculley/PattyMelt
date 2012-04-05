/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stackframe.pattymelt;

/**
 *
 * @author mcculley
 */
public class IllegalOpcodeException extends Exception {

    public final short opcode;

    public IllegalOpcodeException(short opcode) {
        super(Integer.toString(opcode));
        this.opcode = opcode;
    }
}

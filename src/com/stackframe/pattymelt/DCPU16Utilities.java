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

/**
 * Utilities for dealing with the DCPU16 architecture.
 *
 * The disassembler functionality was derived from the disassembler in Brian
 * Swetland's DCPU-16 implementation.
 *
 * @author mcculley
 */
public class DCPU16Utilities {

    private DCPU16Utilities() {
    }

    /**
     * Decode the operand of an instruction.
     *
     * @param memory the memory of the virtual machine
     * @param pc the PC of the instruction
     * @param operand the operand to disassemble
     * @param buf a StringBuilder into which the disassembled operand will be
     * written
     * @return the PC of the next instruction, if the operand implies PC should
     * be modified
     */
    private static int disassembleOperand(short[] memory, int pc, short operand, StringBuilder buf) {
        if (operand < 0x08) {
            buf.append(String.format("%c", DCPU16.Register.name(operand & 7)));
        } else if (operand < 0x10) {
            buf.append(String.format("[%c]", DCPU16.Register.name(operand & 7)));
        } else if (operand < 0x18) {
            buf.append(String.format("[0x%04X+%c]", memory[pc++], DCPU16.Register.name(operand & 7)));
        } else if (operand > 0x1f) {
            buf.append(String.format("0x%X", operand - 0x20));
        } else {
            switch (operand) {
                case 0x18:
                    buf.append("POP");
                    break;
                case 0x19:
                    buf.append("PEEK");
                    break;
                case 0x1A:
                    buf.append("PUSH");
                    break;
                case 0x1B:
                    buf.append("SP");
                    break;
                case 0x1C:
                    buf.append("PC");
                    break;
                case 0x1D:
                    buf.append("O");
                    break;
                case 0x1e:
                    buf.append(String.format("[0x%04X]", memory[pc++]));
                    break;
                case 0x1f:
                    buf.append(String.format("0x%04X", memory[pc++]));
                    break;
            }
        }

        return pc;
    }

    /**
     * Disassemble a single instruction.
     *
     * @param memory the memory of the virtual machine
     * @param pc the PC to disassemble at @buf a StringBuilder into which the
     * disassembled instruction and operands will be written
     * @return the PC incremented to the next instruction
     */
    public static int disassemble(short[] memory, int pc, StringBuilder buf) {
        short n = memory[pc++];
        int op = n & 0xF;
        short a = (short) ((n >> 4) & 0x3F);
        short b = (short) ((n >> 10) & 0x3F);
        if (op > 0) {
            buf.append(String.format("%s ", DCPU16.Opcode.values()[op]));
            pc = disassembleOperand(memory, pc, a, buf);
            buf.append(", ");
            pc = disassembleOperand(memory, pc, b, buf);
            return pc;
        }

        if (a == 1) {
            buf.append("JSR ");
            pc = disassembleOperand(memory, pc, b, buf);
            return pc;
        }

        buf.append(String.format("UNK[%02X] ", a));
        pc = disassembleOperand(memory, pc, b, buf);
        return pc;
    }

    /**
     * Disassemble a single instruction.
     *
     * @param memory the memory of the virtual machine
     * @param pc the PC to disassemble at
     * @return a String with the disassembled instruction and operands
     */
    public static String disassemble(short[] memory, int pc) {
        StringBuilder buf = new StringBuilder();
        disassemble(memory, pc, buf);
        return buf.toString();
    }
}

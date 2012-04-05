package com.stackframe.pattymelt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * A simple command line driver for DCPU-16.
 *
 * @author mcculley
 */
public class PattyMelt {

    /**
     * Load a file into memory. The file is assumed to be lines of hexadecimal
     * 16-bit words.
     *
     * @param memory a ShortBuffer to read the file into
     * @param reader a BufferedReader to read from
     * @throws IOException
     */
    private static void load(short[] memory, BufferedReader reader) throws IOException {
        int i = 0;
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                return;
            }

            short value = (short) Integer.parseInt(line, 16);
            memory[i++] = value;
        }
    }

    private static String dumpState(DCPU16 cpu) {
        return String.format("%04x %04x %04x %04x %04x %04x %04x %04x %04x %04x %04x %04x",
                cpu.PC(), cpu.SP(), cpu.O(), cpu.SKIP() ? 1 : 0, cpu.A(), cpu.B(), cpu.C(), cpu.X(), cpu.Y(), cpu.Z(), cpu.I(), cpu.J());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        DCPU16 cpu = new DCPU16Emulator();
        String filename = args[0];
        System.err.println("Loading " + filename);
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        load(cpu.memory(), reader);
        final String header = "PC   SP   OV   SKIP A    B    C    X    Y    Z    I    J\n"
                + "---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ---- ----";
        System.out.println(header);
        while (true) {
            System.out.println(dumpState(cpu));
            cpu.step();
        }
    }
}
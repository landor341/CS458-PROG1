package Instructions;

import enums.OP;
import enums.REG;
import mips.labelPosTracker;

import java.security.Key;
import java.util.Arrays;

// Represents a word in memory
public class Word {

    public Word() { }

    public Word(boolean debug) {
        if (debug) {
            Arrays.fill(bits, true);
        }
    }

    public Word(String immOrLabel, labelPosTracker pos) {
        this.addImm(immOrLabel, 31, pos, OP.nop);
    }

    public Word(String hexNum) {
        boolean[] res = hexToBinary(hexNum);
        for (int i=0; i<res.length; i++) { bits[i] = res[i]; }
    }

    protected boolean[] bits = new boolean[32];

    public String toHex() { // Similar to toString method except it converts it to the hexadecimal bytecode (This will be the final output for this assignment)
        StringBuilder res = new StringBuilder();
        for (int i=0; i<bits.length; i+=4) {
            boolean[] cur = {bits[i],bits[i+1],bits[i+2],bits[i+3]};
            for (int j=0; j<binary.length; j++) {
                boolean match = true;
                for (int k=0; k<4; k++)  {
                    if (binary[j][k] != cur[k])  match = false;
                }

                if (match) {
                    res.append(hex[j]);
                    break;
                }
            }
        }
        return res.toString();
    }

    public String toBinary() {
        StringBuilder sb = new StringBuilder();
        for (boolean bit: this.bits) sb.append(bit ? '1' : '0');
        return sb.toString();
    }

    public static boolean[] decimalToBinary(int num, int length) {
        boolean needsTwosComplement = num < 0;
        boolean[] res = new boolean[length];
        int og = num;
        int index = res.length-1;
        while (index >= 0 && Math.abs(num) >= 1) {
            if (num % 2 == 1 || num % 2 == -1) res[index] = true;
            num /= 2;
            index--;
        }

        if (needsTwosComplement) { //Find twos complement
            for (int i=0; i<res.length; i++) { res[i] = !res[i]; }

            boolean carry = true; // add 1
            for (int i=res.length-1; i>=0; i--) {
                res[i] = res[i] ^ carry;
                carry = !res[i] && carry; // some weird logic since res[i] was just changed.
            }
        }

        return res;
    }


    protected boolean[] hexToBinary(String num) {
        boolean[] res = new boolean[num.length()*4];

        for (int i=0; i<num.length(); i++) {
            char curChar = Character.isUpperCase(num.charAt(i)) ? Character.toLowerCase(num.charAt(i)) : num.charAt(i);
            for (int j=0; j<hex.length; j++) {
                if (curChar == hex[j]) {
                    for (int k=0; k<binary[j].length; k++) {
                        res[i*4+k] = binary[j][k];
                    }
                }
            }
        }

        return res;
    }


    // LUT is probably an efficient implementation from hex to binary and vice versa
    protected final char[] hex = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    protected final boolean[][] binary = {
            {false, false, false, false}, {false, false, false, true}, {false, false, true, false}, {false, false, true, true},
            {false, true, false, false}, {false, true, false, true}, {false, true, true, false}, {false, true, true, true},
            {true, false, false, false}, {true, false, false, true}, {true, false, true, false}, {true, false, true, true},
            {true, true, false, false}, {true, true, false, true}, {true, true, true, false}, {true, true, true, true}};

    protected void addReg(String r, int baseBit) {
        boolean[] boolReg = null;
        if (Character.isDigit(r.charAt(1))) {
            boolReg = this.decimalToBinary(Integer.parseInt(r.substring(1)), 5);
        } else {
            REG[] REGs = REG.values();
            for (int i = 0; i < REGs.length; i++) {
                if (REGs[i].name().equals(r)) {
                    boolReg = this.decimalToBinary(REGs[i].value, 5);
                    break;
                }
            }
        }

        if (boolReg == null) throw new IllegalArgumentException();

        for (int j = 0; j < boolReg.length; j++) { this.bits[j + baseBit] = boolReg[j]; }
    }

    protected void addImm(String value, int baseBit, labelPosTracker pos, OP o) {
        boolean[] boolAddress;
        boolean isNum = Character.isDigit(value.charAt(0));
        boolean isHex = value.length() > 1 && value.charAt(0) == '0' && value.charAt(1) == 'x';
        if (isNum) {
            if (isHex) {
                boolAddress = hexToBinary(value.substring(2));
            } else {
                boolAddress = decimalToBinary(Integer.parseInt(value), baseBit);
            }
        } else {
            if (o == OP.beq || o == OP.bne) boolAddress = decimalToBinary((pos.getRelativeLabelValue(value, o)/4)-1, 32-baseBit);
            else if (o == OP.PSEUDObne) boolAddress = decimalToBinary(pos.getRelativeLabelValue(value, o)/4-1, 32-baseBit);
            else if (o == OP.j) boolAddress = decimalToBinary(pos.getLabelValue(value)/4, 32-baseBit); // TODO Truncate value?
            else boolAddress = decimalToBinary(pos.getRelativeLabelValue(value, o), 32 - baseBit);
        }

        int offset = (32-baseBit) - boolAddress.length;
        if (isHex && boolAddress[0]) { //SIGN EXTENSION
            for (int i=0; i<=offset; i++) {
                this.bits[baseBit+i] = true;
            }
        }
        for (int i=0; i<boolAddress.length; i++) {
            this.bits[baseBit+offset+i] = boolAddress[i];
        }
    }
}

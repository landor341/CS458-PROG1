package Instructions;

// Represents a word in memory
public class Word {

    Word() { this(0); }

    Word(int data) { storeInt(data); }

    public void storeInt(int val) {

    }

    boolean[] bits = new boolean[32];

    public String toHex() { // Similar to toString method except it converts it to the hexadecimal bytecode (This will be the final output for this assignment)
        return ""; // TODO: Convert bits array to hexidecimal
    }

    public String toBinary() {
        StringBuilder sb = new StringBuilder();
        for (boolean bit: this.bits) sb.append(bit ? '1' : '0');
        return sb.toString();
    }
}

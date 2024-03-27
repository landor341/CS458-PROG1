package mips;

import enums.OP;

import java.util.HashMap;

public class labelPosTracker {

    final int dataStart = 0x10010000, textStart = 0x00400000;

    // Maps string labels to their index in memory
    private HashMap<String, Integer> labelMap = new HashMap<>();

    int curLine = 0, // Corresponds to the key used in the arrayList. This is like the "address", each instruction will get loaded into the curLine address then increment it
        dataLine = 0;


    public enum programStage {none, data,text}
    private programStage state = programStage.none;


    public void addLabel(String label) {
        labelMap.put(label, this.state == programStage.data ? dataLine+dataStart : curLine+textStart);
    }

    public int getLabelValue(String label) {
        return labelMap.get(label);
    }

    public int getRelativeLabelValue(String label, OP o) {
        if (o == OP.PSEUDObne) {
            if (this.state == programStage.text) return this.labelMap.get(label) - (this.curLine+this.textStart+4);
            else return this.labelMap.get(label) - (this.dataLine+this.dataStart+4);
        }
        if (this.state == programStage.text) return this.labelMap.get(label) - (this.curLine+this.textStart);
        else return this.labelMap.get(label) - (this.dataLine+this.dataStart);
    }

    public int getCurrentPos() {
        if (this.state == programStage.text) return this.curLine;
        else return this.dataLine;
    }

    /**
     * In the case that you did a directive .asciiz it's possible that the cursor is current in the middle of the word. If the next instruction is an op you need to go to the beginning of the next word.
     * This will do nothing if you are already at the beginning of a word
     */
    public void goToNextWordBeginning() {
        if (this.state == programStage.text) curLine += 4-(curLine%4)==4 ? 0 : 4-(curLine%4);
        else if (this.state == programStage.data) dataLine += 4-(dataLine%4)==4 ? 0 : 4-(dataLine%4);
    }

    public void start() {
        curLine = 0;
        dataLine = 0;
        state = programStage.none;
    }

    public void advanceNBytes(int n) {
        if (this.state == programStage.text) curLine += n;
        else dataLine += n;
    }

    public void goToNextWord() {
        if (this.state == programStage.text) curLine += 4-(curLine%4);
        else if (this.state == programStage.data) dataLine += 4-(dataLine%4);
    }

    public void setState(programStage s) { this.state = s; }

    public int getCurrentIndex() {
        if (this.state == programStage.data) return dataLine/4;
        else return curLine/4;
    }

    public programStage getState() { return this.state; }

}

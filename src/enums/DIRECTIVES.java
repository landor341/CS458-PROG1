package enums;

// In a program, directives are prefixed with a period
public enum DIRECTIVES {
    asciiz(".asciiz"), // .asciiz "{YOUR_STRING}"    each string gets 1 byte (so 4 letters per word)
    space(".space"), // .space 40        reserves 40 bytes of memory
    word(".word"); // .word 23           stores the integer literal 23 into the next available memory space (Always exactly one word long)

    public String name;

    DIRECTIVES(String name) {
        this.name = name;
    }
}

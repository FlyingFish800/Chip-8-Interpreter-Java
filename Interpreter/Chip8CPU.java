// 8/31/21 | Alexander Symons | Chip 8 interpreter | Chip8CPU.java

package Interpreter;
import java.util.Random;

/** Alexander Symons | 9/4/21 | Chip8CPU.java
 * Chip8CPU
 */
public class Chip8CPU extends Thread{

    final int PROG_OFFSET = 0x200;  // CHIP 8 programs start at addr 0x200
    final int MEM_SIZE = 0xFFF;     // 4kb of RAM
    final int SCREEN_WIDTH = 64;    //Screen Size
    final int SCREEN_HEIGHT = 32;   
    // Default font below
    final int[] SPRITE_DATA = {0xF0, 0x90, 0x90, 0x90, 0xF0, 0x20, 0x60, 0x20, 0x20, 0x70, 0xF0, 0x10, 0xF0, 0x80, 0xF0, 0xF0, 0x10, 0xF0, 0x10, 0xF0, 0x90, 0x90, 0xF0, 0x10, 0x10, 0xF0, 0x80, 0xF0, 0x10, 0xF0, 0xF0, 0x80, 0xF0, 0x90, 0xF0, 0xF0, 0x10, 0x20, 0x40, 0x40, 0xF0, 0x90, 0xF0, 0x90, 0xF0, 0xF0, 0x90, 0xF0, 0x10, 0xF0, 0xF0, 0x90, 0xF0, 0x90, 0x90, 0xE0, 0x90, 0xE0, 0x90, 0xE0, 0xF0, 0x80, 0x80, 0x80, 0xF0, 0xE0, 0x90, 0x90, 0x90, 0xE0, 0xF0, 0x80, 0xF0, 0x80, 0xF0, 0xF0, 0x80, 0xF0, 0x80, 0x80};
    // Assigns square from 1 to v to a hexidecimal keypad
    final int[] KEY_ASSGNMENT = {88 ,49 ,50 ,51 ,81 ,87 ,69 ,65 ,83 ,68 ,90 ,67 , 52, 82, 70, 86};

    
    private boolean paused = false; // Halts processor
    private int[] memory; // Stores memory
    private int[] stack; // Holds stack
    private int[] registers; // Stores registers
    private int I; // Sprite pointer
    private int progCounter; // Program counter, holds addr of current instruction
    private short stackPtr; // Points to current position in stack
    private int instructionRegister; // Holds current instruction
    private boolean[][] screenData; // Contains data to be drawn to screen
    private int delayTimer; // Timer for delay purposes
    private Random rand;

    // Create CPU
    public Chip8CPU (){
        reset();
    }

    // Reset internal registers/memory
    public void reset(){
        memory = new int[MEM_SIZE]; // Create memory
        progCounter = PROG_OFFSET; // Set pc to starting address
        stackPtr = 0; // Reset stack pointer
        stack = new int[0x100]; // Create stack
        registers = new int[0x10]; // Create registers
        I = 0; // Reset sprite register
        screenData = new boolean[SCREEN_WIDTH][SCREEN_HEIGHT]; // Reset screen data
        delayTimer = 0; // Reset timer
        rand = new Random();
        loadSpriteData(); // Load default sprites into memory
        System.out.println("--------RESET-PROCESSOR--------");
    }

    public void loadSpriteData(){ // Load default sprites into memory
        // Sprite data contains necesary sprite info in order it need to be read in.
        for (int i = 0; i < SPRITE_DATA.length; i++) {
            memory[i] = SPRITE_DATA[i];
        }
    }

    public void loadProg(byte[] data){ //Load program into memory
        int index = PROG_OFFSET; // Program starts at offset
        for (byte b : data) {
            memory[index] = b; // Put every byte in memory in order
            index++;          
        }
        System.out.println("--------RELOAD-PROGRAM--------");
    }

    public boolean[][] getScreenData(){ // Getter for screen data
        return screenData;
    }

    private boolean getBitAtPos (int data, int index){ // Helper method for getting bit at index in int
        return ((data >> index) & 1) == 1;
    }

    public int[] getRegisters (){ // Getter for displaying registers for debug purposes
        return registers;
    }

    public int[] getStack (){ // Getter for displaying stack for debug purposes
        return stack;
    }

    public void printKeys(boolean[] keys){ // Prints keys for debug purposes
        for (int i = 0; i < KEY_ASSGNMENT.length; i++) {
            System.out.print(keys[KEY_ASSGNMENT[i]]);
        }
        System.out.println();
    }

    public String getIR (){ // Getter for displaying Instruction Register for debug purposes
        return Integer.toHexString(instructionRegister);
    }

    public int getI (){
        return I;
    }

    public void tick (boolean[] keys){ // Clock CPU, takes key presses as input
        if(paused) return; // Skip if paused

        if(delayTimer > 0) delayTimer--; // Decrease delay timer

        // THE BOARD IS FUCKING WRAPPING AROUND WHYYYYYYYYYY

        // FETCH
        // Load Instruction Register with memory at program counter
        instructionRegister = ((memory[progCounter] << 8) & 0xFF00) + (memory[progCounter + 1] & 0xFF);
        // Get commonly used x and y operands to acess registers from the 2nd and 3rd nibbles respectively
        int x = (instructionRegister & 0xF00) >> 8;
        int y = (instructionRegister & 0xF0) >> 4;
        // Print adress and opcode
        System.out.printf("ADDR %x:  OP %x  -->  ",progCounter,instructionRegister);

        // DECODE
        switch (instructionRegister & 0xF000) {
            case 0x0000: //assorted
                if(instructionRegister == 0x00EE){ // RET
                    // Return from called subroutine
                    System.out.println("RET");
                    progCounter = stack[stackPtr-1];
                    stackPtr--;
                    progCounter += 2;
                }else{
                    // Unimplemented/Invalid
                    System.err.printf("Invalid/Unimplemented opcode %x%n", instructionRegister);
                    paused = true;
                }
                break;

            case 0x1000: // JP nnn
                // Unconditional jump to addr nnn
                System.out.println("JP nnn");
                progCounter = instructionRegister & 0xFFF;
                break;

            case 0x2000: // CALL nnn
                // Call subroutine at nnn
                System.out.println("CALL nnn");
                stack[stackPtr] = progCounter;
                stackPtr++;
                progCounter = instructionRegister & 0xFFF;
                break;

            case 0x3000: // SE Vx, byte
                // Skip next instruction if register x == byte
                System.out.println("SE Vx, byte");
                if(registers[x] == (instructionRegister & 0xFF)) progCounter += 2;
                progCounter += 2;
                break;

            case 0x4000: // SNE Vx, byte
                // Skip next instruction if register x != byte
                System.out.println("SNE Vx, byte");
                if(registers[x] != (instructionRegister & 0xFF)) progCounter += 2;
                progCounter += 2;
                break;

            case 0x6000: // LD Vx, byte
                // Load register x with byte
                System.out.println("LD Vx, byte");
                registers[x] = instructionRegister & 0xFF;
                progCounter += 2;
                break;

            case 0x7000: // ADD Vx, byte
                // add byte to register x
                System.out.println("ADD Vx, byte ");
                registers[x] += instructionRegister & 0xFF;
                progCounter += 2;
                break;

            case 0x8000:
                if((instructionRegister & 0xF) == 0x0){ // LD Vx, Vy
                    // Load register y with value of register y
                    System.out.println("LD Vx, Vy"); 
                    registers[x] = registers[y];
                }else if((instructionRegister & 0xF) == 0x2){ // AND Vx, Vy
                    // And registers x and y, store in register x
                    System.out.println("AND Vx, Vy"); 
                    registers[x] &= registers[y];
                }else if((instructionRegister & 0xF) == 0x4){ // ADD Vx, Vy
                    // Add registers x and y, and with 255, put result in register x
                    System.out.println("ADD Vx, Vy"); 
                    if(registers[x] + registers[y] > 255) {
                        registers[0xF] = 1;
                    } else {
                        registers[0xF] = 0;
                    }
                    registers[x] = (registers[x] + registers[y]) & 0xFF;
                }else if((instructionRegister & 0xF) == 0x5){ // SUB Vx, Vy
                    // Add registers x and y, and with 255, put result in register x
                    System.out.println("SUB Vx, Vy"); 
                    if(registers[x] > registers[y]) {
                        registers[0xF] = 1;
                    } else {
                        registers[0xF] = 0;
                    }
                    registers[x] = registers[x] - registers[y];
                }else{
                    // Unimplemented/Invalid
                    System.err.printf("Invalid/Unimplemented opcode %x%n", instructionRegister);
                    paused = true;
                }
                progCounter += 2;
                break;

            case 0xA000: // LD I, addr
                // Load sprite pointer with adress nnn
                System.out.println("LD I, addr");
                I = instructionRegister & 0xFFF;
                progCounter += 2;
                break;

            case 0xC000: // RND Vx, byte
                // Generate random number, and with 255, store in register x
                System.out.println("RND Vx, byte");
                registers[x] = rand.nextInt(256) & (instructionRegister & 0xFF);
                progCounter += 2;
                break;

            case 0xD000: // DRW Vx, Vy, nibble
                // Draw sprtie starting at sprite index, going down(?) by a number of lines given in a nibble of data,
                // starting at the x and y coords held in registers x and y
                // TODO: Weird offset, IDK why
                // Nibble is y, data seems to be put in center going left of 8XNibble zone
                System.out.println("DRW Vx, Vy, nibble");
                int spriteSize = instructionRegister & 0xF; // X offset by 5??? 
                registers[0xF] = 0;
                for (int j = 0; j < spriteSize; j++) {
                    for (int i = 0; i < 8; i++) {

                        int drawX = i + registers[x];
                        int drawY = j + registers[y];

                        if (drawX > SCREEN_WIDTH - 1) drawX -= SCREEN_WIDTH;
                        if (drawY > SCREEN_HEIGHT - 1) drawY -= SCREEN_HEIGHT;
          
                        if(screenData[drawX][drawY] == true) registers[0xF] = 1;
                        System.out.println(drawX + "," + drawY + ": " + screenData[drawX][drawY]);
                        screenData[drawX][drawY] ^= getBitAtPos(memory[I + j], i);
                    }
                }
                /*for (int j = 0; j < 15; j++) {
                    for (int i = 0; i < 15; i++) { 
                        System.out.println(j + "," + i + ": " + screenData[j][i]);
                    }
                }*/
                //paused = true;
                progCounter += 2;
                break;

            case 0xE000: // Assorted
                if((instructionRegister & 0xFF) == 0x9E){ // SKP Vx
                    // Skip next instruction if key in register x is pressed
                    System.out.println("SKP Vx");
                    if(keys[KEY_ASSGNMENT[registers[x]]]) progCounter += 2;
                } else if((instructionRegister & 0xFF) == 0xA1){ // SKNP Vx
                    // Skip next instruction if key in register x is not pressed
                    System.out.println("SKNP Vx"); 
                    if(!keys[KEY_ASSGNMENT[registers[x]]]) progCounter += 2;
                } else {
                    // Unimplemented/Invalid
                    System.err.printf("Invalid/Unimplemented opcode %x%n", instructionRegister);
                    paused = true;
                }
                progCounter += 2;
                break;

            case 0xF000: // assorted
                if((instructionRegister & 0xFF) == 0x07){ // LD Vx, DT
                    // Load register x with value of delay timer
                    System.out.println("LD Vx, DT");
                    registers[x] = delayTimer;
                    progCounter += 2;
                }else if((instructionRegister & 0xFF) == 0x15){ // LD DT, Vx
                    // Load delay timer with value of register x
                    System.out.println("LD DT, Vx");
                    delayTimer = registers[x];
                    progCounter += 2;
                }else if((instructionRegister & 0xFF) == 0x29){ // LD F, Vx
                    // Set Sprite pointer to adress of a hex character (from register x) from default instruction set
                    System.out.println("LD F, Vx");
                    // Character Sprites are 5 lines starting at mem addr 0x0. Formula = desired_character * 5 (0*5 = addr 0, 1*5 = addr 5)
                    I = registers[x] * 5; 
                    progCounter += 2;
                }else if((instructionRegister & 0xFF) == 0x33){ // LD B, Vx
                    // Load memory addresses from Sprite pointer to Sprite pointer + 2 with 3 places of decimal value of register x
                    System.out.println("LD B, Vx");
                    System.out.println(registers[x]);
                    memory[I] = registers[x] / 100;
                    memory[I+1] = (registers[x] / 10) % 10;
                    memory[I+2] = registers[x] % 10;
                    System.out.println("Mem: " + memory[I] + " " + memory[I + 1] + " " + memory[I + 2]);
                    progCounter += 2;
                }else if((instructionRegister & 0xFF) == 0x65){ // LD Vx, I
                    // Load registers 0 through x to value from sprite pointer
                    System.out.println("LD Vx, I");
                    for (int i = 0; i <= x; i++) {
                        registers[i] = memory[I + i];
                        System.out.print("Register[" + i + "] = " + registers[i] + " ");
                    }
                    System.out.println();
                    progCounter += 2;
                }else{
                    // Unimplemented/Invalid
                    System.err.printf("Invalid/Unimplemented opcode %x%n", instructionRegister);
                    paused = true;
                }
                break;
        
            default:
                // Unimplemented/Invalid
                System.err.printf("Invalid/Unimplemented opcode %x%n", instructionRegister);
                paused = true;
                break;
        }
    }

} // end class
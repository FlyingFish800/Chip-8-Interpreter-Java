// 8/31/21 | Alexander Symons | Chip 8 interpreter | Chip8CPU.java

package Interpreter;

import java.util.Random;
import java.awt.event.KeyEvent;

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
    final int[] KEY_ASSGNMENT = {KeyEvent.VK_X ,KeyEvent.VK_1 ,KeyEvent.VK_2 ,KeyEvent.VK_3 ,KeyEvent.VK_Q ,KeyEvent.VK_W ,KeyEvent.VK_E ,KeyEvent.VK_A ,KeyEvent.VK_S ,KeyEvent.VK_D ,KeyEvent.VK_Z ,KeyEvent.VK_C , KeyEvent.VK_4, KeyEvent.VK_R, KeyEvent.VK_F, KeyEvent.VK_V};

    
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
    private int soundTimer; // Timer for sound purposes
    private Random rand;
    private boolean redraw = false; // Flag to redraw screen only when necessary
    private boolean debug = true; // Flag to enable debug output to terminal

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
        redraw = true;
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
        // - 7 to start at most significant bit, shift and get last bit if its a 1
        return ((data >> 7 - index) & 1) == 1;
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

    public boolean redrawScreen (){ // Getter for redraw variable that resets it if true
        if (redraw){
            redraw = false;
            return true;
        }
        return false;
    }

    public void tick (boolean[] keys){ // Clock CPU, takes key presses as input
        if(paused) return; // Skip if paused

        if(delayTimer > 0) delayTimer--; // Decrease delay timer
        if(soundTimer > 0) {
            soundTimer--; // Decrease sound timer TODO: unimplemented, make noise
        }

        // FETCH
        // Load Instruction Register with memory at program counter
        instructionRegister = ((memory[progCounter] << 8) & 0xFF00) + (memory[progCounter + 1] & 0xFF);
        // Get commonly used x and y operands to acess registers from the 2nd and 3rd nibbles respectively
        int x = (instructionRegister & 0xF00) >> 8;
        int y = (instructionRegister & 0xF0) >> 4;
        // Print adress and opcode
        if(debug)System.out.printf("ADDR %x:  OP %x  -->  ",progCounter,instructionRegister);

        // DECODE
        switch (instructionRegister & 0xF000) {
            case 0x0000: //assorted
                if(instructionRegister == 0x00EE){ // RET
                    // Return from called subroutine
                    if(debug)System.out.println("RET");
                    progCounter = stack[stackPtr-1];
                    stackPtr--;
                    progCounter += 2;
                }else if(instructionRegister == 0x00E0){ // CLS
                    // Clear Screen
                    if(debug)System.out.println("CLS");
                    screenData = new boolean[SCREEN_WIDTH][SCREEN_HEIGHT];
                    progCounter += 2;
                }else{
                    // Unimplemented/Invalid
                    System.err.printf("Invalid/Unimplemented opcode %x%n", instructionRegister);
                    paused = true;
                }
                break;

            case 0x1000: // JP nnn
                // Unconditional jump to addr nnn
                if(debug)System.out.println("JP nnn");
                progCounter = instructionRegister & 0xFFF;
                break;

            case 0x2000: // CALL nnn
                // Call subroutine at nnn
                if(debug)System.out.println("CALL nnn");
                stack[stackPtr] = progCounter;
                stackPtr++;
                progCounter = instructionRegister & 0xFFF;
                break;

            case 0x3000: // SE Vx, byte
                // Skip next instruction if register x == byte
                if(debug)System.out.println("SE Vx, byte");
                if(registers[x] == (instructionRegister & 0xFF)) progCounter += 2;
                progCounter += 2;
                break;

            case 0x4000: // SNE Vx, byte
                // Skip next instruction if register x != byte
                if(debug)System.out.println("SNE Vx, byte");
                if(registers[x] != (instructionRegister & 0xFF)) progCounter += 2;
                progCounter += 2;
                break;

            case 0x5000: // SE Vx, Vy
                // Skip next instruction if register x == byte
                System.out.println("SE Vx, Vy");
                if(registers[x] == registers[y]) progCounter += 2;
                progCounter += 2;
                break;

            case 0x6000: // LD Vx, byte
                // Load register x with byte
                if(debug)System.out.println("LD Vx, byte");
                registers[x] = instructionRegister & 0xFF;
                progCounter += 2;
                break;

            case 0x7000: // ADD Vx, byte
                // add byte to register x
                if(debug)System.out.println("ADD Vx, byte ");
                registers[x] += instructionRegister & 0xFF;
                progCounter += 2;
                break;

            case 0x8000:
                if((instructionRegister & 0xF) == 0x0){ // LD Vx, Vy
                    // Load register y with value of register y
                    if(debug)System.out.println("LD Vx, Vy"); 
                    registers[x] = registers[y];
                }else if((instructionRegister & 0xF) == 0x1){ // OR Vx, Vy
                    // And registers x and y, store in register x
                    if(debug)System.out.println("OR Vx, Vy"); 
                    registers[x] |= registers[y];
                }else if((instructionRegister & 0xF) == 0x2){ // AND Vx, Vy
                    // And registers x and y, store in register x
                    if(debug)System.out.println("AND Vx, Vy"); 
                    registers[x] &= registers[y];
                }else if((instructionRegister & 0xF) == 0x3){ // XOR Vx, Vy
                    // And registers x and y, store in register x
                    if(debug)System.out.println("XOR Vx, Vy"); 
                    registers[x] ^= registers[y];
                }else if((instructionRegister & 0xF) == 0x4){ // ADD Vx, Vy
                    // Add registers x and y, and with 255, put result in register x
                    if(debug)System.out.println("ADD Vx, Vy"); 
                    if(registers[x] + registers[y] > 255) {
                        registers[0xF] = 1;
                    } else {
                        registers[0xF] = 0;
                    }
                    registers[x] = (registers[x] + registers[y]) & 0xFF;
                }else if((instructionRegister & 0xF) == 0x5){ // SUB Vx, Vy
                    // Add registers x and y, and with 255, put result in register x
                    if(debug)System.out.println("SUB Vx, Vy"); 
                    if(registers[x] > registers[y]) {
                        registers[0xF] = 1;
                    } else {
                        registers[0xF] = 0;
                    }
                    registers[x] = registers[x] - registers[y];
                }else if((instructionRegister & 0xF) == 0x6){ // SHR Vx, {, Vy}
                    // If the least-significant bit of Vx is 1, then VF is set to 1, otherwise 0. Then Vx is divided by 2
                    if(debug)System.out.println("SHR Vx, {, Vy}"); 
                    registers[0xF] = registers[x] & 1;
                    registers[x] /= 2;
                }else if((instructionRegister & 0xF) == 0x7){ // SUBN Vx, Vy
                    // Vx -= Vy, VF not borrow
                    System.out.println("SUBN Vx, Vy"); 
                    registers[0xF] = 0;
                    if(registers[y] > registers[x]) registers[0xF] = 1;
                    registers[x] -= registers[y];
                }else if((instructionRegister & 0xF) == 0xE){ // SHL Vx, {, Vy}
                    // If the most-significant bit of Vx is 1, then VF is set to 1, otherwise 0. Then Vx is mulyiplied by 2
                    System.out.println("SHL Vx, {, Vy}"); 
                    registers[0xF] = registers[x] & 128;
                    registers[x] = (registers[x] * 2) & 0xFF;
                }else{
                    // Unimplemented/Invalid
                    System.err.printf("Invalid/Unimplemented opcode %x%n", instructionRegister);
                    paused = true;
                }
                progCounter += 2;
                break;

            case 0x9000: // SNE Vx, Vy
                // Skip next instruction if Vx != Vy
                System.out.println("SNE Vx, Vy");
                if(registers[x] != registers[y]) progCounter += 2;
                progCounter += 2;
                break;

            case 0xA000: // LD I, addr
                // Load sprite pointer with adress nnn
                if(debug)System.out.println("LD I, addr");
                I = instructionRegister & 0xFFF;
                progCounter += 2;
                break;

            case 0xB000: // JP V0, addr
                // Jump to location addr + V0
                System.out.println("JP V0, addr");
                progCounter = (instructionRegister & 0xFFF) + registers[0];
                break;

            case 0xC000: // RND Vx, byte
                // Generate random number, and with 255, store in register x
                if(debug)System.out.println("RND Vx, byte");
                registers[x] = rand.nextInt(256) & (instructionRegister & 0xFF);
                progCounter += 2;
                break;

            case 0xD000: // DRW Vx, Vy, nibble
                // Draw sprtie starting at sprite index, going down by a number of lines given in a nibble of data,
                // starting at the x and y coords held in registers x and y
                if(debug)System.out.println("DRW Vx, Vy, nibble");
                int spriteSize = instructionRegister & 0xF; // X offset by 5??? 
                registers[0xF] = 0;
                for (int j = 0; j < spriteSize; j++) {
                    for (int i = 0; i < 8; i++) {

                        int drawX = i + registers[x];
                        int drawY = j + registers[y];

                        while (drawX > SCREEN_WIDTH - 1) drawX -= SCREEN_WIDTH;
                        while (drawY > SCREEN_HEIGHT - 1) drawY -= SCREEN_HEIGHT;
          
                        if(screenData[drawX][drawY] == true) registers[0xF] = 1;
                        screenData[drawX][drawY] ^= getBitAtPos(memory[I + j], i);
                    }
                }
                progCounter += 2;
                redraw = true;
                break;

            case 0xE000: // Assorted
                if((instructionRegister & 0xFF) == 0x9E){ // SKP Vx
                    // Skip next instruction if key in register x is pressed
                    if(debug)System.out.println("SKP Vx");
                    if(keys[KEY_ASSGNMENT[registers[x]]]) progCounter += 2;
                } else if((instructionRegister & 0xFF) == 0xA1){ // SKNP Vx
                    // Skip next instruction if key in register x is not pressed
                    if(debug)System.out.println("SKNP Vx"); 
                    if(!keys[KEY_ASSGNMENT[registers[x]]]) progCounter += 2;
                } else {
                    // Unimplemented/Invalid
                    if(debug)System.err.printf("Invalid/Unimplemented opcode %x%n", instructionRegister);
                    paused = true;
                }
                progCounter += 2;
                break;

            case 0xF000: // assorted
                if((instructionRegister & 0xFF) == 0x07){ // LD Vx, DT
                    // Load register x with value of delay timer
                    if(debug)System.out.println("LD Vx, DT");
                    registers[x] = delayTimer;
                    progCounter += 2;
                }else if((instructionRegister & 0xFF) == 0x15){ // LD DT, Vx
                    // Load delay timer with value of register x
                    if(debug)System.out.println("LD DT, Vx");
                    delayTimer = registers[x];
                    progCounter += 2;
                }else if((instructionRegister & 0xFF) == 0x18){ // LD ST, Vx
                    // Load sound timer with value of register x
                    if(debug)System.out.println("LD ST, Vx");
                    soundTimer = registers[x];
                    progCounter += 2;
                }else if((instructionRegister & 0xFF) == 0x1E){ // ADD I, Vx
                    // Add value of Vx to I
                    if(debug)System.out.println("ADD I, Vx");
                    I += registers[x];
                    progCounter += 2;
                }else if((instructionRegister & 0xFF) == 0x29){ // LD F, Vx
                    // Set Sprite pointer to adress of a hex character (from register x) from default instruction set
                    if(debug)System.out.println("LD F, Vx");
                    // Character Sprites are 5 lines starting at mem addr 0x0. Formula = desired_character * 5 (0*5 = addr 0, 1*5 = addr 5)
                    I = registers[x] * 5; 
                    progCounter += 2;
                }else if((instructionRegister & 0xFF) == 0x33){ // LD B, Vx
                    // Load memory addresses from Sprite pointer to Sprite pointer + 2 with 3 places of decimal value of register x
                    if(debug)System.out.println("LD B, Vx");
                    memory[I] = registers[x] / 100;
                    memory[I+1] = (registers[x] / 10) % 10;
                    memory[I+2] = registers[x] % 10;
                    progCounter += 2;
                }else if((instructionRegister & 0xFF) == 0x55){ // LD I, Vx
                    // Load memory adresses I through I + x to registers V0 through Vx
                    if(debug)System.out.println("LD I, Vx");
                    for (int i = 0; i <= x; i++) {
                        memory[I+i] = registers[i];
                    }
                    progCounter += 2;
                }else if((instructionRegister & 0xFF) == 0x65){ // LD Vx, I
                    // Load registers 0 through x to value from sprite pointer
                    if(debug)System.out.println("LD Vx, I");
                    for (int i = 0; i <= x; i++) {
                        registers[i] = memory[I + i];
                    }
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
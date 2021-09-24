import java.util.Random;

/** Alexander Symons | 9/4/21 | Chip8CPU.java
 * Chip8CPU
 */
public class Chip8CPU extends Thread{

    final int PROG_OFFSET = 0x200;
    final int MEM_SIZE = 0xFFF;
    final int SCREEN_WIDTH = 64;
    final int SCREEN_HEIGHT = 32;
    final int[] SPRITE_DATA = {0xF0, 0x90, 0x90, 0x90, 0xF0, 0x20, 0x60, 0x20, 0x20, 0x70, 0xF0, 0x10, 0xF0, 0x80, 0xF0, 0xF0, 0x10, 0xF0, 0x10, 0xF0, 0x90, 0x90, 0xF0, 0x10, 0x10, 0xF0, 0x80, 0xF0, 0x10, 0xF0, 0xF0, 0x80, 0xF0, 0x90, 0xF0, 0xF0, 0x10, 0x20, 0x40, 0x40, 0xF0, 0x90, 0xF0, 0x90, 0xF0, 0xF0, 0x90, 0xF0, 0x10, 0xF0, 0xF0, 0x90, 0xF0, 0x90, 0x90, 0xE0, 0x90, 0xE0, 0x90, 0xE0, 0xF0, 0x80, 0x80, 0x80, 0xF0, 0xE0, 0x90, 0x90, 0x90, 0xE0, 0xF0, 0x80, 0xF0, 0x80, 0xF0, 0xF0, 0x80, 0xF0, 0x80, 0x80};
    final int[] KEY_ASSGNMENT = {88 ,49 ,50 ,51 ,81 ,87 ,69 ,65 ,83 ,68 ,90 ,67 , 52, 82, 70, 86};

    boolean paused = false;
    int[] memory;
    int[] stack;
    int[] registers;
    int I;
    int progCounter;
    short stackPtr;
    int instructionRegister;
    boolean[][] screenData;
    int delayTimer;
    Random rand;

    public Chip8CPU (){
        reset();
    }

    public void reset(){
        memory = new int[MEM_SIZE];
        progCounter = PROG_OFFSET;
        stackPtr = 0;
        stack = new int[0xF];
        registers = new int[0x10];
        I = 0;
        screenData = new boolean[SCREEN_WIDTH][SCREEN_HEIGHT];
        delayTimer = 0;
        rand = new Random();
        loadSpriteData();
        System.out.println("--------RESET-PROCESSOR--------");
    }

    public void loadSpriteData(){
        for (int i = 0; i < SPRITE_DATA.length; i++) {
            memory[i] = SPRITE_DATA[i];
        }
    }

    public void loadProg(byte[] data){
        int index = PROG_OFFSET;
        for (byte b : data) {
            memory[index] = b;
            index++;          
        }
        System.out.println("--------RELOAD-PROGRAM--------");
    }

    public boolean[][] getScreenData(){
        return screenData;
    }

    private boolean getBitAtPos (int data, int index){
        return ((data >> index) & 1) == 1;
    }

    public int[] getRegisters (){
        return registers;
    }

    public int[] getStack (){
        return stack;
    }

    public void printKeys(boolean[] keys){
        for (int i = 0; i < KEY_ASSGNMENT.length; i++) {
            System.out.print(keys[KEY_ASSGNMENT[i]]);
        }
        System.out.println();
    }

    public String getIR (){
        return Integer.toHexString(instructionRegister);
    }

    public void tick (boolean[] keys){
        if(paused) return;

        if(delayTimer > 0) delayTimer--;

        // THE BOARD IS FUCKING WRAPPING AROUND WHYYYYYYYYYY

        // FETCH
        instructionRegister = ((memory[progCounter] << 8) & 0xFF00) + (memory[progCounter + 1] & 0xFF);
        int x = (instructionRegister & 0xF00) >> 8;
        int y = (instructionRegister & 0xF0) >> 4;
        System.out.printf("ADDR %x:  OP %x  -->  ",progCounter,instructionRegister);

        // DECODE
        switch (instructionRegister & 0xF000) {
            case 0x0000: //assorted
                if(instructionRegister == 0x00EE){ // RET
                    System.out.println("RET");
                    progCounter = stack[stackPtr-1];
                    stackPtr--;
                    progCounter += 2;
                }else{
                    System.err.printf("Invalid/Unimplemented opcode %x%n", instructionRegister);
                    paused = true;
                }
                break;

            case 0x1000: // JP nnn
                System.out.println("JP nnn");
                progCounter = instructionRegister & 0xFFF;
                break;

            case 0x2000: // CALL nnn
                System.out.println("CALL nnn");
                stack[stackPtr] = progCounter;
                stackPtr++;
                progCounter = instructionRegister & 0xFFF;
                break;

            case 0x3000: // SE Vx, byte
                System.out.println("SE Vx, byte");
                if(registers[x] == (instructionRegister & 0xFF)) progCounter += 2;
                progCounter += 2;
                break;

            case 0x4000: // SNE Vx, byte
                System.out.println("SNE Vx, byte");
                if(registers[x] != (instructionRegister & 0xFF)) progCounter += 2;
                progCounter += 2;
                break;

            case 0x6000: // LD Vx, byte
                System.out.println("LD Vx, byte");
                registers[x] = instructionRegister & 0xFF;
                progCounter += 2;
                break;

            case 0x7000: // ADD Vx, byte
                System.out.println("ADD Vx, byte ");
                registers[x] += instructionRegister & 0xFF;
                progCounter += 2;
                break;

            case 0x8000:
                if((instructionRegister & 0xF) == 0x2){ // AND Vx, Vy
                    System.out.println("AND Vx, Vy"); 
                    registers[x] &= registers[y];
                }else if((instructionRegister & 0xF) == 0x4){ // ADD Vx, Vy
                    System.out.println("ADD Vx, Vy"); 
                    if(registers[x] + registers[y] > 255) {
                        registers[0xF] = 1;
                    } else {
                        registers[0xF] = 0;
                    }
                    registers[x] = (registers[x] + registers[y]) & 0xFF;
                }else{
                    System.err.printf("Invalid/Unimplemented opcode %x%n", instructionRegister);
                    paused = true;
                }
                progCounter += 2;
                break;

            case 0xA000: // LD I, addr
                System.out.println("LD I, addr");
                I = instructionRegister & 0xFFF;
                progCounter += 2;
                break;

            case 0xC000: // RND Vx, byte
                System.out.println("RND Vx, byte");
                registers[x] = rand.nextInt(256) & (instructionRegister & 0xFF);
                progCounter += 2;
                break;

            case 0xD000: // DRW Vx, Vy, nibble
                // Weird offset, IDK why
                System.out.println("DRW Vx, Vy, nibble");
                int spriteSize = instructionRegister & 0xF;
                registers[0xF] = 0;
                for (int j = 0; j < spriteSize; j++) {
                    for (int i = 0; i < 8; i++) {

                        int drawX = i + registers[x];
                        int drawY = j + registers[y];

                        if (drawX > SCREEN_WIDTH - 1) drawX -= SCREEN_WIDTH;
                        if (drawY > SCREEN_HEIGHT - 1) drawY -= SCREEN_HEIGHT;
                        System.out.println(drawX + "," + drawY);
          
                        if(screenData[drawX][drawY] == true) registers[0xF] = 1;
                        screenData[drawX][drawY] ^= getBitAtPos(memory[I + j], i);
                    }
                }
                //paused = true;
                progCounter += 2;
                break;

            case 0xE000:
                if((instructionRegister & 0xFF) == 0xA1){ // SKNP Vx
                    System.out.println("SKNP Vx"); 
                    if(!keys[KEY_ASSGNMENT[registers[x]]]) progCounter += 2;
                }else if((instructionRegister & 0xFF) == 0x9E){ // SKP Vx
                    System.out.println("SKP Vx");
                    if(keys[KEY_ASSGNMENT[registers[x]]]) progCounter += 2;
                }else{
                    System.err.printf("Invalid/Unimplemented opcode %x%n", instructionRegister);
                    paused = true;
                }
                progCounter += 2;
                break;

            case 0xF000: // assorted
                if((instructionRegister & 0xFF) == 0x07){ // LD Vx, DT
                    System.out.println("LD Vx, DT");
                    registers[x] = delayTimer;
                    progCounter += 2;
                }else if((instructionRegister & 0xFF) == 0x15){ // LD DT, Vx
                    System.out.println("LD DT, Vx");
                    delayTimer = registers[x];
                    progCounter += 2;
                }else if((instructionRegister & 0xFF) == 0x29){ // LD F, Vx
                    System.out.println("LD F, Vx");
                    I = registers[x] * 5;
                    System.out.println("Mem: " + I);
                    progCounter += 2;
                }else if((instructionRegister & 0xFF) == 0x33){ // LD B, Vx
                    System.out.println("LD B, Vx");
                    System.out.println(registers[x]);
                    memory[I] = registers[x] / 100;
                    memory[I+1] = (registers[x] / 10) % 10;
                    memory[I+2] = registers[x] % 10;
                    System.out.println("Mem: " + memory[I] + " " + memory[I + 1] + " " + memory[I + 2]);
                    progCounter += 2;
                }else if((instructionRegister & 0xFF) == 0x65){ // LD Vx, I
                    System.out.println("LD Vx, I");
                    for (int i = 0; i <= x; i++) {
                        registers[i] = memory[I + i];
                        System.out.print("Register[" + i + "] = " + registers[i] + " ");
                    }
                    System.out.println();
                    progCounter += 2;
                }else{
                    System.err.printf("Invalid/Unimplemented opcode %x%n", instructionRegister);
                    paused = true;
                }
                break;
        
            default:
                System.err.printf("Invalid/Unimplemented opcode %x%n", instructionRegister);
                paused = true;
                break;
        }
    }

} // end class
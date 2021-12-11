// 9/26/21 | Alexander Symons | Assembler | AssemblerMain.java

package Assembler;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Generator {

    private ArrayList<Token> tokens;    // Keep list of all tokens
    private ArrayList<Token> labels = new ArrayList<Token>();   // Keep list of all labels for crossrefencing
    private ArrayList<Byte> machineCode = new ArrayList<Byte>(); // Keep list of bytes for machine code
    private Token entrypoint;

    public Generator(ArrayList<Token> tokens){  // Constuctor, store tokens
        this.tokens = tokens;
    }

    public void generateCode (){    // Generate code
        String instruction;

        calulateLabelAdresses(); // Track adress for labels, and get entrypoint

        // JP to start point
        instruction = String.format("1%03x",findAdressFromLabel(entrypoint.getOperands()[0]));
        machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
        machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
        System.out.println("0200: " + instruction);

        // Times counter
        int times = 1;

        // Address
        int address = 0x202;

        // TODO: make entrypoint work, check adresses
        // TODO: make error if wrong mem adress mode, don't just ignore instruction

        for (Token token : tokens) {    // For every token
            switch (token.getID()) {    // Get its id
                case "GLOBAL":
                    break;

                case "LABEL":
                    // TODO: check for duplicates
                    break;                

                case "DB":
                    for (int time = 0; time < times; time++) {
                        for (int i = 0; i < token.getOperands().length; i++) {
                            instruction = String.format("%02x",Integer.decode(token.getOperands()[i]));
                            machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                            System.out.println(String.format("%04x: %02x", address, Integer.decode(token.getOperands()[i])));
                            address += 1;
                        } 
                    }   
                    times = 1;
                    break;

                case "TIMES":
                    times = Integer.decode(token.getOperands()[0]);
                    break;

                case "LD":
                    switch (token.getAdressingModes()[0]){
                        case "Register":
                            if(token.getAdressingModes()[1].equals("Immediate")){ // LD Rx, byte
                                // TODO: Bytes not strings 
                                instruction = String.format("6%x%02x",Integer.decode(token.getOperands()[0].replace("R", "")),Integer.decode(token.getOperands()[1]));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                                System.out.println(String.format("%04x: %s", address, instruction));
                                address += 2;
                            } else if (token.getAdressingModes()[1].equals("Register")){ //LD Rx, Ry
                                instruction = String.format("8%x%x0",Integer.decode(token.getOperands()[0].replace("R", "")),Integer.decode(token.getOperands()[1].replace("R", "")));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                                System.out.println(String.format("%04x: %s", address, instruction));
                                address += 2;
                            } else if (token.getAdressingModes()[1].equals("Sprite Pointer")){ //LD Rx, I
                                instruction = String.format("F%x65",Integer.decode(token.getOperands()[0].replace("R", "")));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                                System.out.println(String.format("%04x: %s", address, instruction));
                                address += 2;
                            } 
                            break;

                        case "Sprite Pointer Digit":
                            if (token.getAdressingModes()[1].equals("Register")){ //LD F, Rx
                                instruction = String.format("F%x29",Integer.decode(token.getOperands()[1].replace("R", "")));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                                System.out.println(String.format("%04x: %s", address, instruction));
                                address += 2;
                            } 
                            break;

                        case "Sprite Pointer":
                            if (token.getAdressingModes()[1].equals("Register")){ //LD I, Rx
                                instruction = String.format("F%x55",Integer.decode(token.getOperands()[1].replace("R", "")));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                                System.out.println(String.format("%04x: %s", address, instruction));
                                address += 2;
                            } else if (token.getAdressingModes()[1].equals("Immediate")) {//LD I, addr TODO: Token mem mode
                                instruction = String.format("A%03x",Integer.decode(token.getOperands()[1]));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                                System.out.println(String.format("%04x: %s", address, instruction));
                                address += 2;
                            } else if (token.getAdressingModes()[1].equals("Label")){ //LD I, addr (label ver) TODO: Token mem mode
                                instruction = String.format("A%03x",findAdressFromLabel(token.getOperands()[1]));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                                System.out.println(String.format("%04x: %s", address, instruction));
                                address += 2;
                            }
                            break;
                    }
                    break;

                case "ADD":
                    switch (token.getAdressingModes()[0]){
                        case "Register":
                            if(token.getAdressingModes()[1].equals("Register")){ // ADD Rx, Ry
                                instruction = String.format("8%x%x4",Integer.decode(token.getOperands()[0].replace("R", "")),Integer.decode(token.getOperands()[1].replace("R", "")));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                                System.out.println(String.format("%04x: %s", address, instruction));
                                address += 2;
                            } else if(token.getAdressingModes()[1].equals("Immediate")){ // ADD Rx, byte
                                instruction = String.format("7%x%02x",Integer.decode(token.getOperands()[0].replace("R", "")),Integer.decode(token.getOperands()[1]));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                                System.out.println(String.format("%04x: %s", address, instruction));
                                address += 2;
                            }  
                            break;
                    }
                    break;
                
                case "OR":
                    switch (token.getAdressingModes()[0]){
                        case "Register":
                            if(token.getAdressingModes()[1].equals("Register")){ // OR Rx, Ry
                                instruction = String.format("8%x%x1",Integer.decode(token.getOperands()[0].replace("R", "")),Integer.decode(token.getOperands()[1].replace("R", "")));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                                System.out.println(String.format("%04x: %s", address, instruction));
                                address += 2;
                            }
                            break;
                    }
                    break;
                
                case "AND":
                    switch (token.getAdressingModes()[0]){
                        case "Register":
                            if(token.getAdressingModes()[1].equals("Register")){ // AND Rx, Ry
                                instruction = String.format("8%x%x2",Integer.decode(token.getOperands()[0].replace("R", "")),Integer.decode(token.getOperands()[1].replace("R", "")));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                                System.out.println(String.format("%04x: %s", address, instruction));
                                address += 2;
                            }
                            break;
                    }
                    break;
                
                case "XOR":
                    switch (token.getAdressingModes()[0]){
                        case "Register":
                            if(token.getAdressingModes()[1].equals("Register")){ // XOR Rx, Ry
                                instruction = String.format("8%x%x3",Integer.decode(token.getOperands()[0].replace("R", "")),Integer.decode(token.getOperands()[1].replace("R", "")));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                                System.out.println(String.format("%04x: %s", address, instruction));
                                address += 2;
                            }
                            break;
                    }
                    break;

                case "SUB":
                    switch (token.getAdressingModes()[0]){
                        case "Register":
                            if(token.getAdressingModes()[1].equals("Register")){ // SUB Rx, Ry
                                instruction = String.format("8%x%x5",Integer.decode(token.getOperands()[0].replace("R", "")),Integer.decode(token.getOperands()[1].replace("R", "")));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                                System.out.println(String.format("%04x: %s", address, instruction));
                                address += 2;
                            }
                            break;
                    }
                    break;

                case "SE":
                    switch (token.getAdressingModes()[0]){
                        case "Register":
                            if(token.getAdressingModes()[1].equals("Immediate")){ // SE Rx, byte
                                // TODO: Bytes not strings 
                                instruction = String.format("3%x%02x",Integer.decode(token.getOperands()[0].replace("R", "")),Integer.decode(token.getOperands()[1]));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                                System.out.println(String.format("%04x: %s", address, instruction));
                                address += 2;
                            } else if (token.getAdressingModes()[1].equals("Register")){ //SE Rx, Ry
                                instruction = String.format("5%x%x0",Integer.decode(token.getOperands()[0].replace("R", "")),Integer.decode(token.getOperands()[1].replace("R", "")));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                                System.out.println(String.format("%04x: %s", address, instruction));
                                address += 2;
                            } 
                            break;
                    }
                    break;

                case "SNE":
                    switch (token.getAdressingModes()[0]){
                        case "Register":
                            if(token.getAdressingModes()[1].equals("Immediate")){ // LD Rx, byte
                                // TODO: Bytes not strings 
                                instruction = String.format("4%x%02x",Integer.decode(token.getOperands()[0].replace("R", "")),Integer.decode(token.getOperands()[1]));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                                System.out.println(String.format("%04x: %s", address, instruction));
                                address += 2;
                            } else if (token.getAdressingModes()[1].equals("Register")){ //LD Rx, Ry
                                instruction = String.format("9%x%x0",Integer.decode(token.getOperands()[0].replace("R", "")),Integer.decode(token.getOperands()[1].replace("R", "")));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                                System.out.println(String.format("%04x: %s", address, instruction));
                                address += 2;
                            }
                            break;
                    }
                    break;

                case "RND":
                    instruction = String.format("C%x%02x",Integer.decode(token.getOperands()[0].replace("R", "")),Integer.decode(token.getOperands()[1]));
                    machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                    machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                    System.out.println(String.format("%04x: %s", address, instruction));
                    address += 2;
                    break;

                case "DRW":
                    instruction = String.format("D%x%x%x",Integer.decode(token.getOperands()[0].replace("R", "")),Integer.decode(token.getOperands()[1].replace("R", "")),Integer.decode(token.getOperands()[2]));
                    machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                    machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                    System.out.println(String.format("%04x: %s", address, instruction));
                    address += 2;
                    break;

                case "SKP":
                    instruction = String.format("E%x9E",Integer.decode(token.getOperands()[0].replace("R", "")));
                    machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                    machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                    System.out.println(String.format("%04x: %s", address, instruction));
                    address += 2;
                    break;

                case "SKNP":
                    instruction = String.format("E%xA1",Integer.decode(token.getOperands()[0].replace("R", "")));
                    machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                    machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                    System.out.println(String.format("%04x: %s", address, instruction));
                    address += 2;
                    break;

                case "JP":
                    instruction = String.format("1%03x",findAdressFromLabel(token.getOperands()[0]));
                    machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                    machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                    System.out.println(String.format("%04x: %s", address, instruction));
                    address += 2;
                    break;

                case "CALL":
                    instruction = String.format("2%03x",findAdressFromLabel(token.getOperands()[0]));
                    machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                    machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                    System.out.println(String.format("%04x: %s", address, instruction));
                    address += 2;
                    break;

                case "RET":
                    instruction = String.format("00EE");
                    machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                    machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                    System.out.println(String.format("%04x: %s", address, instruction));
                    address += 2;
                    break;

                case "CLS":
                    instruction = String.format("00E0");
                    machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                    machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                    System.out.println(String.format("%04x: %s", address, instruction));
                    address += 2;
                    break;
            
                default:
                    System.out.println(address+": Unimplemented/Invalid token in generateCode(), " + token.getID());
                    break;
            }
        }
    }

    public void calulateLabelAdresses (){
        int adress = 0x202; // First instruction is global 
        for (Token token : tokens) {
            switch (token.getID()) {
                case "GLOBAL": // GLOBAL implemented as unconditional jump to entrypoint
                    entrypoint = token; // Set entrypoint
                    break;

                case "LABEL":
                    token.setAdress(adress);
                    labels.add(token);
                    break;

                case "LD":
                case "ADD":
                case "SUB":
                case "SE":
                case "SNE":
                case "DRW":
                case "JP":
                case "CALL":
                case "RND":
                case "SKP":
                case "SKNP":
                case "RET":
                case "CLS":
                    adress += 2;
                    break;

                case "DB":
                    adress += token.getOperands().length;
                    break;

                case "TIMES":   // TODO: make dynamic
                    System.out.println("A: "+Integer.parseInt(token.getOperands()[0]));
                    adress += Integer.parseInt(token.getOperands()[0]) - 1; // Because next instruction also triggers adress increase. TODO: make dynamic
                    break;
            
                default:
                    System.out.println("Unimplemented/Invalid token in calulateLabelAdresses(), " + token.getID());
                    break;
            }
        }

    }

    public int findAdressFromLabel (String label){ // get labels
        for (Token l : labels) {
            if(l.getOperands()[0].split(":")[0].equals(label)) {
                return l.getAdress();
            }
        }
        return 0;
    }

    public void outputMachineCode(String path) throws IOException{
        OutputStream opStream = null;
        try {
            // TODO: 0-127 unchanged, 128 = -128
            File myFile = new File("/home/alex/Documents/Chip/Assembler/" + path);
            // check if file exist, otherwise create the file before writing
            if (!myFile.exists()) {
                myFile.createNewFile();
            }
            opStream = new FileOutputStream(myFile);
            for (Byte codeBit : machineCode) {
                opStream.write(codeBit);
            }
            opStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try{
                if(opStream != null) opStream.close();
            } catch(Exception ex){
                 
            }
        }
    }

    Byte[] codeToBytes (String[] code){
        char codeChars[] = new char[code.length * 4];
        Byte ops[] = new Byte[code.length * 2];
        for (int i = 0; i < code.length; i++) {
            codeChars[(i*4)] = code[i].charAt(0);
            codeChars[(i*4)+1] = code[i].charAt(1);
            codeChars[(i*4)+2] = code[i].charAt(2);
            codeChars[(i*4)+3] = code[i].charAt(3);
        }
        
        for (int i = 0; i < codeChars.length; i+=2) {
            // convert ops to bytes with 128-255 being -128--1 for codechars i and i+1
        }
        return ops;
    }

}

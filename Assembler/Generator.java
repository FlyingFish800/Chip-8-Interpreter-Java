// 9/26/21 | Alexander Symons | Assembler | AssemblerMain.java

package Assembler;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Generator {

    private ArrayList<Token> tokens;
    private ArrayList<Token> labels = new ArrayList<Token>();
    private ArrayList<Byte> machineCode = new ArrayList<Byte>();

    private Token entrypoint;

    public Generator(ArrayList<Token> tokens){
        this.tokens = tokens;
    }

    public void generateCode (){

        int adress = 0;
        
        // TODO: make entrypoint work
        for (Token token : tokens) {
            switch (token.getID()) {
                case "GLOBAL":
                    entrypoint = token;
                    break;

                case "LABEL":
                    // TODO: check for duplicates
                    Token temp = token;
                    token.setAdress(adress + 0x202);
                    labels.add(token);
                    break;                

                case "LD":
                    switch (token.getAdressingModes()[0]){
                        case "Register":
                            if(token.getAdressingModes()[1].equals("Immediate")){ // LD Rx, byte
                                // TODO: Bytes not strings 
                                String instruction = String.format("6%x%02x",Integer.decode(token.getOperands()[0].replace("R", "")),Integer.decode(token.getOperands()[1]));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                                System.out.println(instruction);
                            } else if (token.getAdressingModes()[1].equals("Register")){ //LD Rx, Ry
                                String instruction = String.format("8%x%x0",Integer.decode(token.getOperands()[0].replace("R", "")),Integer.decode(token.getOperands()[1].replace("R", "")));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                                System.out.println(instruction);
                            }
                            adress += 1;    
                            break;
                    }
                    break;

                case "ADD":
                    switch (token.getAdressingModes()[0]){
                        case "Register":
                            if(token.getAdressingModes()[1].equals("Register")){ // ADD Rx, Ry
                                String instruction = String.format("8%x%x4",Integer.decode(token.getOperands()[0].replace("R", "")),Integer.decode(token.getOperands()[1].replace("R", "")));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                                machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                                System.out.println(instruction);
                            }
                            break;
                    }
                    adress += 1;    
                    break;

                case "JP":
                    String instruction = String.format("1%03x",findAdressFromLabel(token.getOperands()[0]));
                    machineCode.add((byte) ((Character.digit(instruction.charAt(0), 16) << 4) + Character.digit(instruction.charAt(1), 16)));
                    machineCode.add((byte) ((Character.digit(instruction.charAt(2), 16) << 4) + Character.digit(instruction.charAt(3), 16)));
                    System.out.println(instruction);
                    adress += 1;    
                    break;
            
                default:
                    System.out.println("Unimplemented/Invalid token " + token.getID());
                    break;
            }
        }
    }

    public int findAdressFromLabel (String label){
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

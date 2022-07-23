// 9/25/21 | Alexander Symons | Assembler | AssemblerMain.java
package Assembler;

public class Token {

    // Memory addressing modes. Implied will be mislabeled Immideate, but that instuction
    // type does not require a memory adressing mode anyways, so its fine.

    private String opID;
    private String[] operands;
    private String[] adressingModes;
    private Lexer.segmentType tokenSegmentType;
    private int adress = 0;

    // Constructor for Data section, or instructions with implied operands
    public Token(String opID, String[] operands, Lexer.segmentType currentSegmentType){
        this.opID = opID;           // Store token ID (what instruction)
        this.operands = operands;   // Store token operands (data to be used)
        this.tokenSegmentType = currentSegmentType; // Specify segment (Instruction or constant)

        // Calculate adressing modes (probably all mislabeled Immidate but whatever)
        calculateAdressingTypes();

    } // end constructor

    // Constructor for Text section
    public Token(String opID, String[] operands){
        this.opID = opID;           // Store operation ID
        this.operands = operands;   // Store operands
        calculateAdressingTypes(); // Calculate adressing modes based on operands
        // If memory adresing modes are required, it must be in data segment
        tokenSegmentType = Lexer.segmentType.TEXT;
    } // end constructor

    public String getID (){
        return opID;
    }

    public String[] getOperands (){
        return operands;
    }

    public String[] getAdressingModes (){
        return adressingModes;
    }

    public Lexer.segmentType getSegment (){
        return tokenSegmentType;
    }

    public int getAdress(){
        return adress;
    }

    public void setAdress(int adress){
        this.adress = adress;
    }
    
    public void addOperand(String operand){
        String[] temp = operands;
        operands = new String [temp.length + 1];
        int i = 0;
        for (String string : temp) {
            operands[i] = string;
            i++;
        }
        operands[i] = operand;
    }

    public void calculateAdressingTypes(){

        adressingModes = new String[operands.length];

        for (int i = 0; i < operands.length; i++) {
            // TODO: add handling for mem address, and hex
            if(operands[i].startsWith("R")){ // Registers are Rx
                adressingModes[i] = "Register";
            } else if(operands[i].contains("0x")){  // Hex is 0xBYTE
                operands[i] = Integer.parseInt(operands[i].split("0x")[1],16) + "";
                adressingModes[i] = "Immediate";
            } else if(operands[i].contains("%")){  // Binary is %BYTE
                operands[i] = Integer.parseInt(operands[i].split("%")[1],2) + "";
                adressingModes[i] = "Immediate";
            } else if(operands[i].equals("B")){  // Sprite Register with location of digit in VX
                adressingModes[i] = "Decimal Representation";
            } else if(operands[i].equals("F")){  // Sprite Register with location of digit in VX
                adressingModes[i] = "Sprite Pointer Digit";
            } else if(operands[i].equals("I")){  // Load I-I+x with V0-Vx
                adressingModes[i] = "Sprite Pointer";
            } else if(operands[i].equals("K")){  // Load Key pressed
                adressingModes[i] = "Key";
            } else if(operands[i].charAt(0) == '_'){
                adressingModes[i] = "Label";
            } else {    // Immediate values are unmarked
                adressingModes[i] = "Immediate";
            } // end if
            
        } // end for

    } // end calculateAdressingTypes
    
} // end class
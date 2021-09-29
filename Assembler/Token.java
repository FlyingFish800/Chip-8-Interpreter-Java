// 9/25/21 | Alexander Symons | Assembler | AssemblerMain.java

package Assembler;

public class Token {

    // Memory addressing modes. Implied will be mislabeled Immideate, but that instuction
    // type does not require a memory adressing mode anyways, so its fine.
    public static enum memAdressingMode {Immideate, Register, Memory};

    private String opID;
    private String[] operands;
    private memAdressingMode[] adressingModes;
    private Lexer.segmentType tokenSegmentType;

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

    public memAdressingMode[] getAdressingModes (){
        return adressingModes;
    }

    public Lexer.segmentType getSegment (){
        return tokenSegmentType;
    }

    public void calculateAdressingTypes(){

        adressingModes = new memAdressingMode[operands.length];

        for (int i = 0; i < operands.length; i++) {
            // TODO: add handling for mem address, and hex
            if(operands[i].startsWith("R")){ // Registers are Rx
                adressingModes[i] = memAdressingMode.Register;
            } else {// Immediate values are unmarked
                adressingModes[i] = memAdressingMode.Immideate;
            } // end if
            
        } // end for

    } // end calculateAdressingTypes
    
} // end class
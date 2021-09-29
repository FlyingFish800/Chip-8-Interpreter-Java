// 9/25/21 | Alexander Symons | Assembler | AssemblerMain.java

package Assembler;

public class Token {

    public static enum memAdressingMode {Implied, ImmideateToRegister, RegisterToRegister, DelayTimerToRegister, KeyboardToRegister, RegisterToDelayTimer, RegisterToSoundTimer, SpriteAddrToRegister, DecimalToRegister, RegisterToMemory, MemoryToRegisters};

    private String opID;
    private String[] operands;
    private memAdressingMode adressingMode;

    public Token(String opID, String[] operands){
        this.opID = opID;
        this.operands = operands;
        adressingMode = memAdressingMode.Implied;
    }

    public Token(String opID, String[] operands, memAdressingMode adressingMode){
        this.opID = opID;
        this.operands = operands;
        this.adressingMode = adressingMode;
    }
    
    public String getID (){
        return opID;
    }

    public String[] getOperands (){
        return operands;
    }

    public memAdressingMode getAdressingMode (){
        return adressingMode;
    }
    
}
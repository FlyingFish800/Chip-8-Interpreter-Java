// 9/26/21 | Alexander Symons | Assembler | AssemblerMain.java

package Assembler;

import java.util.ArrayList;

public class Generator {

    private ArrayList<Token> tokens = new ArrayList<Token>();
    
    private String entrypoint;

    public Generator(ArrayList<Token> tokens){
        this.tokens = tokens;
    }

    public void generateCode (){
        int i = 0;
        for (Token token : tokens) {
            switch (token.getID()) {
                case "GLOBAL":
                    entrypoint = token.getOperands()[0];
                    break;
                    
                default:
                    System.out.println("Unimplemented/Invalid token " + token);
                    break;
            }
            i++;
        }
    }
}

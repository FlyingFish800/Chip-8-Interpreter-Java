// 9/26/21 | Alexander Symons | Assembler | AssemblerMain.java

package Assembler;

import java.util.ArrayList;

public class Generator {

    private ArrayList<Token> tokens;
    private ArrayList<Token> labels = new ArrayList<Token>();
    private ArrayList<Token>[] AST;
    
    private String entrypoint;

    public Generator(ArrayList<Token> tokens){
        this.tokens = tokens;
    }

    public void generateCode (){
<<<<<<< HEAD
        int i = 0;
=======
        findLabels();
>>>>>>> 718c7a91321dbc45f9df1ceed195212e0b9026ff
        for (Token token : tokens) {
            switch (token.getID()) {
                case "GLOBAL":
                    entrypoint = token.getOperands()[0];
                    break;
<<<<<<< HEAD
                    
=======

                case "LABEL":
                    break;                
            
>>>>>>> 718c7a91321dbc45f9df1ceed195212e0b9026ff
                default:
                    System.out.println("Unimplemented/Invalid token " + token);
                    break;
            }
            i++;
        }
    }

    void findLabels (){
        int i = 0;
        for (Token token : tokens) {
            if(token.getID().equals("LABEL")) {
                labels.add(token);
            } // TODO: Could put all code under proper label, or translate code to adress. Seocond one better
                
        }
    }

}

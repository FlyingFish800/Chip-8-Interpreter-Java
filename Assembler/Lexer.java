// 9/26/21 | Alexander Symons | Assembler | AssemblerMain.java

package Assembler;

import java.util.ArrayList;

public class Lexer {

    // Types of segments in an asm program
    private enum segmentType {TEXT, DATA};
    // variable for storing unparsed code
    private String code;
    private ArrayList<Token> tokens = new ArrayList<Token>();

    public Lexer(String code) {
        this.code = code;
    }

    public void tokenize (){
        String[] lines = code.split("\n");

        //TODO: Adressing modes

        segmentType currentSegmentType = segmentType.TEXT;

        for (String line : lines) {

            boolean comment = false;
            String[] words = line.split(" ");

            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                if(!comment && word.length() >= 1){
                    switch (word) {
                        case ";":   // Always need space after semicolon
                            comment = true;
                            break;

                        case ".text":
                            System.out.println(word);
                            currentSegmentType = segmentType.TEXT;
                            break;

                        case ".data":
                            System.out.println("DATA");
                            currentSegmentType = segmentType.DATA;
                            break;

                        case ".global":
                            System.out.println("GLOBAl: " + words[i + 1]);
                            tokens.add(new Token("GLOBAL", convertToArray(words, i + 1, 1)));
                            i += 1;
                            break;

                        case "LD":
                            System.out.println("LD: " + words[i + 1] + " " + words[i + 2]);
                            tokens.add(new Token("LD", convertToArray(words, i + 1, 2)));
                            i += 2;
                            break;

                        case "ADD":
                            System.out.println("ADD: " + words[i + 1] + " " + words[i + 2]);
                            tokens.add(new Token("ADD", convertToArray(words, i + 1, 2)));
                            i += 2;
                            break;

                        case "JP":
                            System.out.println("JP: " + words[i + 1]);
                            tokens.add(new Token("JP", convertToArray(words, i + 1, 1)));
                            i += 1;
                            break;
                    
                        default:
                            System.out.println("LABEL: " + words[i]);
                            tokens.add(new Token("LABEL", convertToArray(words, i, 1)));
                            break;
                    }
                }
            }
            
        }
    }

    public ArrayList<Token> getTokens (){
        return tokens;
    }

    public String[] convertToArray(String[] list, int startIndex, int length){
        String[] temp = new String[length];

        for (int i = 0; i < length; i++) {
            temp[i] = list[startIndex + i].split(",")[0];
        }

        return temp;
    }

    
}

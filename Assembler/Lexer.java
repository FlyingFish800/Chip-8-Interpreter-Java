// 9/26/21 | Alexander Symons | Assembler | AssemblerMain.java

package Assembler;

import java.util.ArrayList;

public class Lexer {

    // Types of segments in an asm program
    public enum segmentType {TEXT, DATA};
    // variable for storing unparsed code
    private String code;
    private ArrayList<Token> tokens = new ArrayList<Token>();

    public Lexer(String code) { // Construct lexer, save source code
        this.code = code;
    }

    public void tokenize (){    // Split into tokens
        String[] lines = code.split("\n");  // Split source code by newlines

        segmentType currentSegmentType = segmentType.TEXT;  // May not be necessary

        for (String line : lines) { // For every line

            boolean comment = false;
            String[] words = line.split(" ");   // Split by spaces
            int times = 0; // Times for TIMES keyword

            for (int i = 0; i < words.length; i++) {    // For every 'word'

                String word = words[i]; // Get word

                if(word.length() >= 1 && word.charAt(0) == ';') comment = true; // Set comment if it is one

                if(!comment && word.length() >= 1){ // If it isn't a comment or nonexistant
                    if(currentSegmentType == segmentType.TEXT){ // If in text segment
                        switch (word) { // Try to generate token
                            case ".data":   // Handle segments
                                System.out.println(word);
                                currentSegmentType = segmentType.DATA;
                                break;

                            case ".global": // Define global label
                                System.out.println("GLOBAl: " + words[i + 1]);
                                tokens.add(new Token("GLOBAL", convertToArray(words, i + 1, 1), segmentType.TEXT));
                                i += 1; // Skip label, already processed
                                break;

                            case "LD":  // Load instruction, takes next two tokens as args
                                System.out.println("LD: " + words[i + 1] + " " + words[i + 2]);
                                tokens.add(new Token("LD", convertToArray(words, i + 1, 2)));
                                i += 2; // Skip args, already processed 
                                break;

                            case "ADD":  // Add instruction, takes next two tokens as args
                                System.out.println("ADD: " + words[i + 1] + " " + words[i + 2]);
                                tokens.add(new Token("ADD", convertToArray(words, i + 1, 2)));
                                i += 2; // Skip args, already processed 
                                break;
                                
                            case "OR":  // Or instruction, takes next two tokens as args
                                System.out.println("OR: " + words[i + 1] + " " + words[i + 2]);
                                tokens.add(new Token("OR", convertToArray(words, i + 1, 2)));
                                i += 2; // Skip args, already processed 
                                break;
                                
                            case "AND":  // And instruction, takes next two tokens as args
                                System.out.println("AND: " + words[i + 1] + " " + words[i + 2]);
                                tokens.add(new Token("AND", convertToArray(words, i + 1, 2)));
                                i += 2; // Skip args, already processed 
                                break;
                                
                            case "XOR":  // Xor instruction, takes next two tokens as args
                                System.out.println("XOR: " + words[i + 1] + " " + words[i + 2]);
                                tokens.add(new Token("XOR", convertToArray(words, i + 1, 2)));
                                i += 2; // Skip args, already processed 
                                break;

                            case "SUB":  // Subtract instruction, takes next two tokens as args
                                System.out.println("SUB: " + words[i + 1] + " " + words[i + 2]);
                                tokens.add(new Token("SUB", convertToArray(words, i + 1, 2)));
                                i += 2; // Skip args, already processed 
                                break;

                            case "SE":  // Skip Equal instruction, takes next two tokens as args
                                System.out.println("SE: " + words[i + 1] + " " + words[i + 2]);
                                tokens.add(new Token("SE", convertToArray(words, i + 1, 2)));
                                i += 2; // Skip args, already processed 
                                break;

                            case "SNE":  // Skip not equal instruction, takes next two tokens as args
                                System.out.println("SNE: " + words[i + 1] + " " + words[i + 2]);
                                tokens.add(new Token("SNE", convertToArray(words, i + 1, 2)));
                                i += 2; // Skip args, already processed 
                                break;

                            case "JP":  // unconditional jump instruction, takes next two tokens as args
                                System.out.println("JP: " + words[i + 1]);
                                tokens.add(new Token("JP", convertToArray(words, i + 1, 1)));
                                i += 1; // Skip args, already processed 
                                break;

                            case "CALL":  // call, calls subroutine
                                System.out.println("CALL: " + words[i + 1]);
                                tokens.add(new Token("CALL", convertToArray(words, i + 1, 1)));
                                i += 1; // Skip argslength, already processed 
                                break;

                            case "RET":  // return from subroutine
                                System.out.println("RET");
                                tokens.add(new Token("RET", convertToArray(words, i, 1)));
                                break;

                            case "CLS":  // clear screen
                                System.out.println("CLS");
                                tokens.add(new Token("CLS", convertToArray(words, i, 1)));
                                break;

                            case "DRW":  // unconditional jump instruction, takes next two tokens as args
                                System.out.println("DRW: " + words[i + 1]);
                                tokens.add(new Token("DRW", convertToArray(words, i + 1, 3)));
                                i += 3; // Skip args, already processed 
                                break;
                        
                            default:    // Otherwise, try to process as label
                                if(word.charAt(0) == '_'){ // Label
                                    System.out.println("LABEL: " + words[i]);
                                    tokens.add(new Token("LABEL", convertToArray(words, i, 1)));
                                }else{
                                    System.out.println("Unimplemented/Invalid token in tokenize() (text segment), " + word);
                                }
                                break;
                        }
                    }else if(currentSegmentType == segmentType.DATA){
                        switch (word) { // Try to generate token
                            case ".text":   // Handle segments
                                System.out.println(word);
                                currentSegmentType = segmentType.TEXT;
                                break;

                            case "DB":   // Handle segments
                                System.out.println("DB");
                                int endIndex = 0;
                                for (int j = i + 1; j < words.length; j++) { // look through rest of line for bytes to define
                                    if(words[j].contains(";")) break;   // Stop if comment, otherwise go to end
                                    endIndex++;
                                }
                                tokens.add(new Token("DB", convertToArray(words, i + 1, endIndex), currentSegmentType));
                                i += endIndex;
                                break;

                            case "TIMES":
                                System.out.println("TIMES"); 
                                tokens.add(new Token("TIMES", convertToArray(words, i + 1, 1), currentSegmentType));
                                i += 1;
                                break;
                        
                            default:    // Otherwise, try to process as label
                                if(word.charAt(0) == '_'){ // Label
                                    System.out.println("LABEL: " + words[i]);
                                    tokens.add(new Token("LABEL", convertToArray(words, i, 1), segmentType.DATA));
                                }else{ 
                                    System.out.println("Unimplemented/Invalid token in tokenize() (data segment), " + word);
                                }
                                break;
                        }
                    }
                }
            }
            
        }
    }

    public ArrayList<Token> getTokens (){   // Getter for tokens
        return tokens;
    }

    public String[] convertToArray(String[] list, int startIndex, int length){  // Get slice of array to use as args in token
        String[] temp = new String[length]; // Temp variable

        // TODO: Handle decimal, hex, mem, comma

        for (int i = 0; i < length; i++) {  // Add individual entries to temp
            // Split comma off of operands
            temp[i] = list[startIndex + i].split(",")[0];
        }

        return temp;    // Return temp
    }
    
}

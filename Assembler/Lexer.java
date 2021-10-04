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

            for (int i = 0; i < words.length; i++) {    // For every 'word'
                String word = words[i]; // Get word
                if(!comment && word.length() >= 1){ // If it isn't a comment or nonexistant
                    switch (word) { // Try to generate token
                        case ";":   // Always need space after semicolon
                            comment = true; // Set comment if it is one
                            break;

                        case ".text":   // Handle segments
                            System.out.println(word);
                            currentSegmentType = segmentType.TEXT;
                            break;

                        case ".data":
                            System.out.println("DATA");
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
                    
                        default:    // Otherwise, try to process as label
                            System.out.println("LABEL: " + words[i]);
                            // TODO: UNHARDCODE; NOT ALWAYS TEXT!!
                            tokens.add(new Token("LABEL", convertToArray(words, i, 1)));
                            break;
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

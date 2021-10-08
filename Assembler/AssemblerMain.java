// 9/25/21 | Alexander Symons | Assembler | AssemblerMain.java

package Assembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class AssemblerMain {
    public static void main(String[] args) {

        String fileContents;    // Collect contents of sourcecode file
        Lexer lexer;            // Define Lexer
        Generator generator;    // Define Gnenerator

        fileContents = readFile("examples/test_DRW.asm");  // Load source code
        
        lexer = new Lexer(fileContents);    // Create Lexer
        lexer.tokenize();   // Tokenize source code

        generator = new Generator(lexer.getTokens());   // Create Generator
        generator.generateCode();   // Generate code from tokens
        try {
            generator.outputMachineCode("out.c8");  // Try to write code to file
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String readFile (String path){    // Load file

        File file = new File("/home/alex/Documents/Chip/Assembler/" + path);    // Create refernce to file
        Scanner sc; // Define scanner
        String fileContents = "";   // Define string to hold file contents

        try {
            sc = new Scanner(file); // Try to create scanner
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }

        while (sc.hasNextLine()){
            fileContents += sc.nextLine() + "\n"; // Append text to temp variable to hold all source code
        }

        sc.close(); // Close scanner

        return fileContents;    // Return file contents

    }
}

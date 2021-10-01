// 9/25/21 | Alexander Symons | Assembler | AssemblerMain.java

package Assembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class AssemblerMain {
    public static void main(String[] args) {

        String fileContents;
        Lexer lexer;
        Generator generator;

        fileContents = readFile("test.asm");
        
        lexer = new Lexer(fileContents);
        lexer.tokenize();

        generator = new Generator(lexer.getTokens());
        generator.generateCode();
        try {
            generator.outputMachineCode("out.c8");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String readFile (String path){

        File file = new File("/home/alex/Documents/Chip/Assembler/" + path);
        Scanner sc;
        String fileContents = "";

        try {
            sc = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }

        while (sc.hasNextLine()){
            fileContents += sc.nextLine() + "\n";
        }

        sc.close();

        return fileContents;

    }
}

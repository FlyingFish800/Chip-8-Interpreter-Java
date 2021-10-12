// 8/31/21 | Alexander Symons | Chip 8 interpreter | Chip8Main.java

package Interpreter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class Chip8Main extends Thread{

    static final long SLEEP_TIME = 1000/60;

    static Chip8CPU cpu;
    static Chip8Display display;

    @Override
    public void run() {
        while (true) {
            //System.out.println(SLEEP_TIME);
            if(!display.pauseClock) cpu.tick(display.getKeys());
            display.Refresh(cpu.getScreenData(), cpu);
            //cpu.printKeys(display.getKeys());
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] getProgram(String path) throws IOException {
        FileInputStream fileStream = new FileInputStream("/home/alex/Documents/Chip/Interpreter/" + path);
        DataInputStream dataStream = new DataInputStream(fileStream);
        byte[] data = new byte[dataStream.available()];
        short index = 0;

        while (dataStream.available() > 0){
            data[index] = dataStream.readByte();
            index++;
        } // end while
        dataStream.close();
        fileStream.close();
        return data;
    }

    public static void main(String[] args) {
        Chip8Main chipInterpreter = new Chip8Main();
        cpu = new Chip8CPU();
        display = new Chip8Display(12);
        try {
            cpu.loadProg(getProgram("out.c8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        chipInterpreter.start();
    } // end main

    public static void pulse() {
        cpu.tick(display.getKeys());
    }

    public static void reset() {
        cpu.reset();
        try {
            cpu.loadProg(getProgram("out.c8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

} //end class

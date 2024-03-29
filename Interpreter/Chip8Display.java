// 8/31/21 | Alexander Symons | Chip 8 interpreter | Chip8Display.java

package Interpreter;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Chip8Display implements KeyListener{

    final int WIDTH = 64;
    final int HEIGHT = 32;
    final int PANEL_WIDTH = 130;
	final int MAX_KEYS = 111; // Up to numpad /
	final boolean FANCY_COLORS = true;

    JFrame jf;
    Canvas canv;
    Graphics g;
	private BufferStrategy bs;
    int scale;
	boolean keys[];
	boolean pauseClock;

    public Chip8Display(int scale) {
        this.scale = scale; 
		keys = new boolean[MAX_KEYS];
		pauseClock = false;
        jf = new JFrame("Chip 8 interpreter"); 
		canv = new Canvas();
		Dimension dim = new Dimension(WIDTH*scale+PANEL_WIDTH,HEIGHT*scale);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setResizable(true);
		jf.setLocationRelativeTo(null);
		jf.setVisible(true);
		canv.setPreferredSize(dim);
		canv.setMaximumSize(dim);
		canv.setMinimumSize(dim);
		canv.setSize(dim);
		canv.setFocusable(false);
		canv.addKeyListener(this);
		jf.addKeyListener(this);
		jf.add(canv);
		jf.pack();
    }

    public void Refresh(boolean[][] screenData, Chip8CPU cpu){
        bs = canv.getBufferStrategy();
		if(bs==null) {
			canv.createBufferStrategy(3);
			return;
		}
		g = bs.getDrawGraphics();

		//draw

		if(cpu.redrawScreen()){ // Draw screen, but only when cpu updates it
			if(FANCY_COLORS) g.setColor(Color.BLACK);
			else g.setColor(Color.WHITE);
			g.fillRect(0, 0, WIDTH*scale+PANEL_WIDTH, HEIGHT*scale);
			if(FANCY_COLORS) g.setColor(Color.GREEN);
			else g.setColor(Color.BLACK);
			for (int x = 0; x < WIDTH; x++) {
				for (int y = 0; y < HEIGHT; y++) {
					if(screenData[x][y]) g.fillRect(x*scale, y*scale, scale, scale);
				}
			}
		}
		g.setColor(Color.RED);
		g.fillRect(WIDTH*scale, 0, PANEL_WIDTH, HEIGHT*scale);
		g.setColor(Color.WHITE);
		int currentYpos = 16;
		g.drawString("Paused: " + pauseClock, WIDTH*scale+4, currentYpos);
		currentYpos += 16;
		g.drawString("Instruction: 0x" + cpu.getIR(), WIDTH*scale+4, currentYpos);
		int[] registers = cpu.getRegisters();
		for (int i = 0; i < registers.length; i++) {
			currentYpos += 16;
			g.drawString("Register["+i+"]: " + registers[i], WIDTH*scale+4, currentYpos);
		}
		currentYpos += 16;
		g.drawString("I", WIDTH*scale+4, currentYpos);
		currentYpos += 16;
		g.drawString("[I]: " + cpu.getI(), WIDTH*scale+4, currentYpos);
		currentYpos += 16;
		g.drawString("STACK", WIDTH*scale+4, currentYpos);
		int[] stack = cpu.getStack();
		for (int i = 0; i < 2; i++) {
			currentYpos += 16;
			g.drawString("["+i+"]: " + stack[i], WIDTH*scale+4, currentYpos);
		}
		//end draw
		bs.show();
		g.dispose();
    }

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() < MAX_KEYS) keys[e.getKeyCode()] = true;
		Chip8Main.keyPressed(e.getKeyCode());
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			pauseClock = !pauseClock;
		} else if(e.getKeyCode() == KeyEvent.VK_SHIFT){
			Chip8Main.pulse();
		}else if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
			Chip8Main.reset();
		}else if(e.getKeyCode() == KeyEvent.VK_CONTROL){
			System.exit(0);
		}
		
		//System.out.println(e.getKeyCode());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() < MAX_KEYS) keys[e.getKeyCode()] = false;	
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	
	public boolean[] getKeys(){
		return keys;
	}
}

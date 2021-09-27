// 9/25/21 | Alexander Symons | Assembler | test.java
//Java implementation of test.asm to get and idea of how to implment in asm

package Assembler;

public class test {
    public static void main(String[] args) {
        int a = 0;
        int b = 1;
        int c = 0;
        while (a < 10000){
            c = a + b;
            a = b;
            b = c;
            System.out.println(a);
        }
    }
}

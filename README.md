# Chip-8-Interpreter-Java
A Chip 8 Interpreter written in java

## Games
Pong programs are not mine, they came from Chip-8 games pack, aquired here:
https://www.zophar.net/pdroms/chip8/chip-8-games-pack.html

## Todo
Add all opcodes to interpreter
Add all instructions to assembler

Possibly later:
Write Disassembler
Write (very simplified) higher-level language compiler (lisp?)

## Chip 8 Assembly manual
### naming conventions
Vx -> Replace Vx with 'Rx' where x is a number corrosponding to register 0-15 (15 is flag register). 
ex: R0 
ex2: R10

_label -> Label section of code or data, can be anything preceded by underscore. Follow with newline, and then code to be labeled
ex:
_start:
    LD R0, 0

byte -> Replace with 8-bit number, 0-255 or 0x0-0xFF (4-bit for nibble, 12-bit for addr)

### instructions
.data -> data segment
DB Byte, Byte -> Define byte(s), put byte value(s) into memory. Can have as many bytes as you want, seperated by commas
TIMES x -> Repeat next data segment instruction x times
.text -> code segment
.global _label -> defines what _label to start from
_label -> label segment of code, store adress as immideate, replace _label with anything preceded by underscore
CLS -> Clear Screen
RET -> Return from subroutine
JP _label -> Unconditional jump to _label
CALL _label -> jump to subroutine at _label
SE Vx, byte -> Skip next instuction if register x is equal to byte
SNE Vx, byte -> Skip next instuction if register x is not equal to byte
SE Vx, Vy -> Skip next instuction if register x is equal to register y
LD Vx, byte -> Load register x with byte
ADD Vx, byte -> Add byte to register x
LD Vx, Vy -> Load register x with register y
OR Vx, Vy -> Or register x with y, store in x
AND Vx, Vy -> And register x with y, store in x
XOR Vx, Vy -> Xor register x with y, store in x
ADD Vx, Vy -> Add register x with y, store in x
SUB Vx, Vy -> Subtract register y from x, store in x

LD I, addr -> Load sprite pointer with 12-bit adress (0-4095 or 0x0-0xFFF)
LD I, _label -> Load sprite pointer with adress of label (point to data stored in .data segment)

DRW Vx, Vy, nibble -> Draw Sprite from memory stored at adress I, draw (nibble) lines, starting at coordinate point (Vx, Vy) (using values stored in each register)

LD F, Vx -> Load I to location of sprite for hexadecimal DIGIT stored in Vx (default sprite set: 0-F)

LD I, Vx -> Load memory adresses I-I+x with V0-Vx
Ld Vx, I -> Load registers V0-Vx with memory adresses I-I+x

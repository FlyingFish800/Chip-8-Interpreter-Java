.text   ; Instructions here. Dont htink its required for Chip 8 lol

.global _start  ; _start globally available

_start: ; label 
    LD R0, 0 ; Load V0 with x coord 0
    LD R1, 0 ; Load V1 with y coord 0
    LD R2, 0xA   ; load V2 with char code A
    LD R3, 5   ; load V3 with char width
    LD R4, 1   ; load V4 with 1 to increment cahr code by

_draw_chars:  ; draws chars going across screen from a to f  
    SNE R2, 0x10    ; skip if not greater that f
    JP _end
    CALL _drw_next_char
    JP _draw_chars

_drw_next_char: ; char in R2 at R0,R1, increments R2 and R0 to next position
    LD F, R2 ; load sprite pointer to char in V2
    DRW R0, R1, 5   ; draw sprite at sprite pointer going down 5 adresses at (R0, R1)
    ADD R0, R3
    ADD R2, R4
    RET

_end: ; end
    JP _end
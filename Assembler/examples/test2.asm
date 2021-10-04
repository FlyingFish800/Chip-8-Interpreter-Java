.text   ; Instructions here. Dont htink its required for Chip 8 lol

.global _start  ; _start globally available

_start: ; label 
    LD R0, 0
    LD R1, 1
_loop:
    LD R2, R1 ; might be uneccesary after LD R1, R2
    ADD R2, R0
    LD R0, R1
    LD R1, R2
    SE R2, 233
    JP _loop    ; JP might be screwy
_exit:
    JP _exit
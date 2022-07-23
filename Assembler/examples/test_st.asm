.text   ; Instructions here. Dont htink its required for Chip 8 lol

.global _start  ; _start globally available

_start: ; label
    ; Set timer to 1/3 sec
    LD R8, 20
    LD R2, 0xA ; Key to check
_loop:
    SKNP R2
    LD ST, R8
    JP _loop


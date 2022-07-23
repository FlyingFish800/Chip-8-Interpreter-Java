.text   ; Instructions here. Dont htink its required for Chip 8 lol

.global _start  ; _start globally available

_start: ; label
    ; Where to draw number
    LD R0, 0
    LD R1, 1
_loop:
    LD R2, K ; Wait for key press, load R2
    LD F, R2 ; Load sprite pointer to that char
    CLS
    DRW R0, R1, 5 ; Draw char at (R0,R1) for 5 rows
    JP _loop


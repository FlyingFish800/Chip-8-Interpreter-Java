.text   ; Instructions here. Dont htink its required for Chip 8 lol

.global _start  ; _start globally available

_start: ; label 
    LD I, 80
    LD R0, 1
    LD R1, 2
    LD R2, 3
    LD I, R2
    LD R0, 0
    LD R1, 0
    LD R2, 0
    LD R2, I
    LD F, R2
    DRW R0, R1, 5
_end
    JP _end
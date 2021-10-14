.text   ; Instructions here. Dont htink its required for Chip 8 lol

.global _start  ; _start globally available

_start: ; label 
    LD R0, 0 ; Load V0 with x coord 0
    LD R1, 0 ; Load V1 with y coord 0
    LD R2, 0xA   ; load V2 with char code A
    LD F, R2 ; load sprite pointer to char in V2
    DRW R0, R1, 5   ; draw sprite at sprite pointer going down 5 adresses at (R0, R1)
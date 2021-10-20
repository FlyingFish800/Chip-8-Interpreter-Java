.data   ; Data here
_sprite_data_H: ; new sprite to be drawn
    DB 0x9, 0x9, 0xF, 0x9, 0x9

_sprite_data_I: ; new sprite to be drawn
    TIMES 5 DB 0x6

.text   ; Instructions here.

.global _start  ; _start globally available

_start: ; label 
    LD I, _sprite_data_H
    LD R0, 0
    LD R1, 0
    DRW R0, R1, 5
    ADD R0, 5
    LD I, _sprite_data_I
    DRW R0, R1, 5
_end:
    JP _end
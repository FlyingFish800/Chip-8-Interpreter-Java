.data ; Sprite data

_bike:  ; Sprite for bike, one pixel
    DB %1

.text   ; Code

.global _start  ; Define entry point

_start: ; Beginning of code
    JP _level_init

_level_init:    ; initialize level
    CLS
    LD R0, 3    ; R0 is player X
    LD R1, 15   ; R1 is player Y
    ; Key:Value W:5 A:7 S:8 D:9
    LD R2, 5   ; R2 is the W key code
    LD R3, 7   ; R3 is the A key code
    LD R4, 8   ; R4 is the S key code
    LD R5, 9   ; R5 is the D key code
    CALL _draw_player
    JP _game_loop


_game_loop: ; Main loop containing game logic
    SKNP R2
    LD R6, 42
    JP _game_loop

_end:   ; Jump to self, 'halt' cpu
    JP _end

_draw_player:   ; Subroutine to draw player
    LD I, _bike ; 'Load' bike sprite
    DRW R0, R1, 1   ; Draw at player X and Y
    RET
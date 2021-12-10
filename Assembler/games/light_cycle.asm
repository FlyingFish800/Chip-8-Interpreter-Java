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
    LD R2, 5    ; R2 is the W key code
    LD R3, 7    ; R3 is the A key code
    LD R4, 8    ; R4 is the S key code
    LD R5, 9    ; R5 is the D key code
    LD R6, 0    ; Direction of bike, (0-3 -> NESW) 
    LD R7, 0    ; Two's complement -1 
    CALL _draw_player
    JP _game_loop


_game_loop: ; Main loop containing game logic
    SKNP R2     ; If W pressed
    LD R6, 0    ; Set Direction North
    SKNP R3     ; If A pressed
    LD R6, 3    ; Set Direction West
    SKNP R4     ; If S pressed
    LD R6, 2    ; Set Direction South
    SKNP R5     ; If D pressed
    LD R6, 1    ; Set Direction East
    SNE R6, 0   ; If direction is North
    ADD R0, 1   ; Go North 
    SNE R6, 1   ; If direction is East
    ADD R1, 1   ; Go East 
    SNE R6, 2   ; If direction is South
    SNE R6, 3   ; If direction is West
    JP _game_loop

_end:   ; Jump to self, 'halt' cpu
    JP _end

_draw_player:   ; Subroutine to draw player
    LD I, _bike ; 'Load' bike sprite
    DRW R0, R1, 1   ; Draw at player X and Y
    RET
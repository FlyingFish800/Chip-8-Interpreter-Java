.data ; Sprite data

_bike:  ; Sprite for bike, one pixel
    DB %10000000

_wall:  ; Sprite for Top/Bottom border
    DB %11111111

_wall_vertical:  ; Sprite for Left/Right border
    TIMES 5 DB %10000000

.text   ; Code

.global _start  ; Define entry point

_start: ; Beginning of code
    JP _level_init

_level_init:    ; initialize level
    CLS
    LD R0, 1    ; R0 is player X
    LD R1, 15   ; R1 is player Y
    ; Key:Value W:5 A:7 S:8 D:9
    LD R2, 5    ; R2 is the W key code
    LD R3, 7    ; R3 is the A key code
    LD R4, 8    ; R4 is the S key code
    LD R5, 9    ; R5 is the D key code
    LD R6, 4    ; Direction of bike, (0-3 -> NESW) 
    LD R7, 1    ; 1
    LD R8, 32   ; R8 is prllet X
    LD R9, 15   ; R9 is pellet Y
    LD R10, 0   ; General Purpose counter 1
    LD R11, 0   ; General Purpose counter 2
    ; CALL _draw_pellet
    CALL _draw_border
_debug:
    JP _debug


_game_loop: ; Main loop containing game logic
    ; Change Current Direction
    SKNP R2     ; If W pressed
    LD R6, 0    ; Set Direction North
    SKNP R3     ; If A pressed
    LD R6, 3    ; Set Direction West
    SKNP R4     ; If S pressed
    LD R6, 2    ; Set Direction South
    SKNP R5     ; If D pressed
    LD R6, 1    ; Set Direction East

    ; Handle movement
    SNE R6, 0   ; If direction is North
    SUB R1, R7  ; Go North 
    SNE R6, 1   ; If direction is East
    ADD R0, R7  ; Go East 
    SNE R6, 2   ; If direction is South
    ADD R1, R7  ; Go left
    SNE R6, 3   ; If direction is West
    SUB R0, R7  ; Go right

    ; Draw player
    LD I, _bike ; 'Load' bike sprite
    DRW R0, R1, 1   ; Draw at player X and Y

    ; Check collisions

    JP _game_loop

_end:   ; Jump to self, 'halt' cpu
    JP _end

_draw_pellet:   ; Subroutine to draw pellet
    LD I, _bike ; 'Load' bike sprite
    DRW R8, R9, 1   ; Draw at player X and Y
    RET

_draw_border:   ; Subroutine to draw playing field border
    ; --TOP AND BOTTOM--
    SNE R10, 64 ; While loop
    JP _draw_border_escape_loop1    ; End while
    LD I, _wall ; 'Load' top border wall sprite
    DRW R10, R11, 1
    ADD R10, 8
    JP _draw_border
_draw_border_escape_loop1:
    SNE R11, 31 ; Escape loop if round 2 completed
    JP _draw_border_end_loop1
    LD R10, 0   ; Reset for round two
    LD R11, 31  ; Set Y to bottom
    JP _draw_border
_draw_border_end_loop1:

    ; --LEFT AND RIGHT--
    LD R10, 0   ; Set X to 0
_draw_border_sides:
    LD R11, 1   ; Set Y to 1
_draw_border_loop2:
    SNE R11, 31
    JP _draw_border_escape_loop2
    LD I, _wall_vertical ; 'Load' side border wall sprite
    DRW R10, R11, 5 ; Draw it
    ADD R11, 5  ; Increment Y by 5
    JP _draw_border_loop2
_draw_border_escape_loop2:
    SNE R10, 63
    JP _draw_border_end_loop2
    LD R10, 63  ; Set X to right side
    JP _draw_border_sides
_draw_border_end_loop2
    RET
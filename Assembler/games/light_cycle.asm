.data ; Sprite data

_bike:  ; Sprite for bike, one pixel
    DB %10000000

_wall:  ; Sprite for Top/Bottom border
    DB %11111111

_wall_vertical:  ; Sprite for Left/Right border
    TIMES 5 DB %10000000

_s:
    DB %11110000
    DB %10000000
    DB %11110000
    DB %00010000
    DB %11110000

_o:
    DB %11110000
    DB %10010000
    DB %10010000
    DB %10010000
    DB %11110000

_r:
    DB %11110000
    DB %10010000
    DB %11110000
    DB %10100000
    DB %10010000

_w:
    DB %10001000
    DB %10101000
    DB %10101000
    DB %10101000
    DB %11111000
    
_n:
    DB %10001000
    DB %11001000
    DB %10101000
    DB %10011000
    DB %10001000
    
_bang:
    DB %01000000
    DB %01000000
    DB %01000000
    DB %00000000
    DB %01000000
    
_l:
    DB %01000000
    DB %01000000
    DB %01000000
    DB %01000000
    DB %01111000
    
_t:
    DB %11111000
    DB %00100000
    DB %00100000
    DB %00100000
    DB %00100000

_colon:
    DB %00000000
    DB %01100000
    DB %00000000
    DB %01100000
    DB %00000000

_temp_decimal:  ; Reserve memory to store decimal score value
    TIMES 3 DB 0x0



.text   ; Code

.global _start  ; Define entry point

_start: ; Beginning of code
    JP _level_init


; BUG: When restarting, prior _game_loop return adress still on stack from _do_collision_check call
_level_init:    ; initialize level
    CLS
    LD R0, 1    ; R0 is player X
    LD R1, 15   ; R1 is player Y
    ; Key:Value W:5 A:7 S:8 D:9
    LD R2, 5    ; R2 is the W key code
    LD R3, 7    ; R3 is the A key code
    LD R4, 8    ; R4 is the S key code
    LD R5, 9    ; R5 is the D key code
    LD R6, 1    ; Direction of bike, (0-3 -> NESW) 
    LD R7, 1    ; 1
    LD R8, 8   ; R8 is pellet X
    LD R9, 15   ; R9 is pellet Y
    LD R10, 0   ; General Purpose counter 1
    LD R11, 0   ; General Purpose counter 2
    LD R12, 0   ; Pellets collected
    CALL _draw_border_entrypt
    CALL _draw_pellet

    ; Draw player
    LD I, _bike ; 'Load' bike sprite
    DRW R0, R1, 1   ; Draw at player X and Y
    JP _game_loop


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
    SNE R15, 1  ; If collision
    CALL _do_collision_check

    JP _game_loop


_end:   ; Jump to self, 'halt' cpu
    SKP R7
    JP _end
    JP _level_init


_draw_pellet:   ; Subroutine to draw pellet
    LD I, _bike ; 'Load' bike sprite
    DRW R8, R9, 1   ; Draw at player X and Y
    RET


_draw_border_entrypt:
    CLS
    LD R10, 0   ; Reset counter
    LD R11, 0 
_draw_border:   ; Subroutine to draw playing field border
    ; --TOP AND BOTTOM--
    SNE R10, 64 ; While loop
    JP _draw_border_escape_loop1    ; End while
    LD I, _wall ; 'Load' top border wall sprite
    DRW R10, R11, 1 ; Draw it
    ADD R10, 8  ; Increment X by 8
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
    SNE R11, 31 ; While loop
    JP _draw_border_escape_loop2    ; End while
    LD I, _wall_vertical ; 'Load' side border wall sprite
    DRW R10, R11, 5 ; Draw it
    ADD R11, 5  ; Increment Y by 5
    JP _draw_border_loop2
_draw_border_escape_loop2:
    SNE R10, 63 ; Skip if round 2 completed
    JP _draw_border_end_loop2
    LD R10, 63  ; Set X to right side
    JP _draw_border_sides
_draw_border_end_loop2
    RET


_do_collision_check:    ; Check collisions with various objects
    ; Check if collision with pellet
    SE R0, R8   ; Skip if not same x pos
    JP _do_collision_check_not_pellet
    SE R1, R9   ; Skip if not same y pos
    JP _do_collision_check_not_pellet

    ; Bike is on pellet
    ADD R12, 1  ; Add 1 to pellets score
    SNE R12, 5  ; Load level 2 if score is 5
    JP _do_collision_check_ld_lvl2
    SNE R12, 10 ; Load level 3 if score is 10
    JP _do_collision_check_ld_lvl3
    SNE R12, 15 ; Load win screen if score is 15
    JP _do_collision_check_ld_win_screen
    JP _do_collision_check_end  ; End check    

_do_collision_check_ld_lvl2:
    CALL _draw_border_entrypt
    JP _do_collision_check_end  ; End check    
_do_collision_check_ld_lvl3:
    CALL _draw_border_entrypt
    JP _do_collision_check_end  ; End check    
_do_collision_check_ld_win_screen:
    JP _display_game_over_screen
    JP _do_collision_check_end  ; End check    

    ; Bike is not on pellet
_do_collision_check_not_pellet:
    JP _display_game_over_screen

_do_collision_check_end:
    CALL _randomize_pellet_location
    RET


_randomize_pellet_location: ; Randomize pellet location
    RND R8, 63
    RND R9, 31
    LD I, _bike
    DRW R8, R9, 1
    SNE R15, 0
    JP _randomize_pellet_location_end
    LD I, _bike
    DRW R8, R9, 1
    JP _randomize_pellet_location
_randomize_pellet_location_end:
    RET

_display_game_over_screen:
    CALL _draw_border_entrypt

    ; Write SCORE: to screen
    LD R0, 4
    LD R1, 4
    LD I, _s
    DRW R0, R1, 5
    LD R7, 0xC
    LD R0, 9
    LD F, R7
    DRW R0, R1, 5
    LD R0, 14
    LD I, _o
    DRW R0, R1, 5
    LD R0, 19
    LD I, _r
    DRW R0, R1, 5
    LD R7, 0xE
    LD R0, 24
    LD F, R7
    DRW R0, R1, 5
    LD R0, 28
    LD I, _colon
    DRW R0, R1, 5

    ; Get demical value of score
    LD I, _temp_decimal ; Set memory pointer to decimal var
    LD B, R12   ; Load decimal digits of score to [I:I+2]
    LD R2, I    ; Load Registers [R0:R2] from [I:I+2]

    ; Draw the digits
    LD R9, 4    ; Load Y
    LD R8, 32   ; Load X
    LD F, R0
    DRW R8, R9, 5
    LD R8, 37   ; Load X
    LD F, R1
    DRW R8, R9, 5
    LD R8, 42   ; Load X
    LD F, R2
    DRW R8, R9, 5
    
    LD R7, 0xd  ; Load R7 with code for D
    SNE R12, 15
    JP _won
    JP _lose

_won:
    ; Write WON! to screen
    LD R0, 4
    LD R1, 10
    LD I, _w
    DRW R0, R1, 5
    LD R0, 11
    LD I, _o
    DRW R0, R1, 5
    LD R0, 18
    LD I, _n
    DRW R0, R1, 5
    LD R0, 25
    LD I, _bang
    DRW R0, R1, 5
    JP _end

_lose:
    ; Write lost to screen
    LD R0, 3
    LD R1, 10
    LD I, _l
    DRW R0, R1, 5
    LD R0, 9
    LD I, _o
    DRW R0, R1, 5
    LD R0, 14
    LD I, _s
    DRW R0, R1, 5
    LD R0, 19
    LD I, _t
    DRW R0, R1, 5
    JP _end
# Tetris
A Tetris clone made for my ICS4U culminating project in Java.
It's quite simple, using only standard java libraries for everything (awt, javax.sound, etc.).
The main focus of this project was to emphasize object orientated coding, by making everything it's own object.
The main game-loop is contained within the Tetromino pieces themselves, as they handle falling and collision.
An interesting point to make is that the "physics" calculations are varaible, instead of fixed or tied to the graphics.
This means it's possible to make otherwise impossible movements, as the Tetromino would have to skip lines in higher levels.
Otherwise, it's a fairly straightforward project.

Some things I learned during this project include:
- Object orientated programming
- The observer model
- Using callbacks as function parameters in Java
- (Limited) Future handling & scheduling
- Keyboard handling in Java
- Javax.sound
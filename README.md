# Snake
A fully playable version of Snake that I made as a passion project while learning Java at University. 
To be honest, I'm quite happy with how this one turned out, even though the program is full of repeated code across the various files, which 
made for some painful times when I had to change some parts of it. That's because I kept adding new features that went beyond the original 
scope of the project, like 2-player support and a CPU that can play the game, so I wrote most of this program completely "blind". 
And speaking of the CPU, I had a blast trying to figure out an algorithm to teach it how to play the game. 
My goal was to ensure it could score an average of 30 points per game, and to help myself I implemented a "benchmark" mode in the program that 
could simulate thousands of games in a few minutes, so I could instantly see if I was improving or worsening the AI with each change to the code. 
Finally, thanks to a trial&error process where I tried countless tactics, I found one that could ensure an average of 35 points per game, while 
some games reached a peak of 55/60 points!
This was also my first program where I implemented a matrix made of empty labels to simulate the grid where the game itself is played: I was so 
satisfied with this solution that I reused it for later games, such as Tetris and Battleship.

The code is commented in Italian whenever possible.

LIST OF FILES:
Main.java
Menu.java
Coordinate.java
Snake.java
Snakeframe.java
IASnake.java
Vs.java

INSTALL INSTRUCTIONS:
Compile Main.java with "javac Main.java" and then execute it with "JAVA Main" on your command prompt. 
The program is compatible with Windows systems only.

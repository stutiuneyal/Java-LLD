A. Clarifications

Candidate: Should the game support variable board sizes, such as 4x4 or 5x5?

Interviewer: For the purpose of this interview, let’s stick with the standard 3x3 board.

Candidate: Should the game support both player-vs-player and player-vs-computer modes?

Interviewer: Let’s keep it simple and focus only on the player-vs-player mode for now.

Candidate: What should happen if a player tries to make an invalid move, like selecting an already filled cell?

Interviewer: The game should reject the move and inform the player to make another selection.

Candidate: Should the system maintain a scoreboard across multiple games to track player wins?

Interviewer: Yes, tracking the scoreboard across games would be a good addition.

Candidate: How should the user input be handled? Should we take input from the console, or just hardcode a sample game sequence?

Interviewer: To keep things focused on the design, take user input main method.

Candidate: Should we track the history of moves to allow features like undo or move replay?

Interviewer: That's an interesting feature to consider, yes.


B. Functional Requirements:


- The game is played on a 3x3 grid.

- Two players take alternate turns, identified by markers ‘X’ and ‘O’.

- The game should detect and announce the winner.

- The game should declare a draw if all cells are filled and no player has won.

- The game should reject invalid moves and inform the player.

- The system should maintain a scoreboard across multiple games.

- Moves can be hardcoded in a driver/demo class to simulate gameplay.

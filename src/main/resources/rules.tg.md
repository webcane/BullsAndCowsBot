*Bulls @ Cows*

*Task*
Predict the secret number in the fewest attempts possible

*Preparation*
A new game begins with guessing a digit secret number
The digits must be all different
The secret can be from 1 to 6 digits long. The default is 4
The game's difficulty can be changed in the settings `/settings`

*Gameplay*
The player try to guess the secret number by trial and error
The guess response gives the number of matches
A _bull_ means a digit matches in both value and position
A _cow_ means a digit is in the secret number but in a different position

*Goal*
Guess all the _bulls_ correctly

*End of the Game*
The game continues until the player reveal the secret number

*Example*
||Secret number: 235. Difficulty: 3||
>Player's try: 1
>Invalid move: The guess must contain exactly 3 digits
>Player's try: 111
>Invalid move: The secret number consists of unique digits
>Player's try: 123
>0 bulls, 2 cows
>Player's try: 234
>2 bulls, 0 cows
>Player's try: 235
>3 bulls, 0 cows
>Victory!!! The secret number is 235. Correctly guessed in 3 turns.

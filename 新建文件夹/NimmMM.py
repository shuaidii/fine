# NIMM MM version 
# 
# The game works as follows:
# The human player picks a pile size. The program then calculates a 
# sensible maximum number of tokens that may be taken by any player 
# in any single turn. The players take turns in grabbing tokens 
# from the pile. Any player can take any quantity less thatn  
# or equal to the maximum. For instance, if the maximum bound 
# turns out to be 4, a player can take 4,3,2, or 1 token(s). However, 
# each distinct quantity may be taken only once throughout the 
# game. One cannot repeatedly take the same quantity in 
# several turns of the same game. Whoever is 
# able to take the last token, or leave the opponent with no legal 
# moves left, wins. 
#
# This version employs the Minimax algorithm.
# Set the boolean verbose True to get trace printing.


from random import randrange 

verbose = False            # flag to control amount of printing
pile = 0                   # number of tokens in the pile
count = 0                  # current number of tokens in pile
bound = 0                  # max any player can take
humanTakes = []            # takes of the human player
computerTakes = []         # takes of the computer player
successorEvaluations = []  # evaluations of successors
seCount, deCount, pCount = 0,0,0
name = ("me", "I", "the meatbag")  # for verbose output

def getComputerTake():
   global successorEvaluations, computerTakes, humanTakes, count 
   global count, seCount, deCount, pCount, verbose
   successorEvaluations = []
   seCount, deCount, pCount = 0,0,0
   minimax(0,1)
   print("\nI'm thinking...SE:" + str(seCount) + " DE:" + str(deCount))
   #if verbose:
   print()
   for tas in successorEvaluations:
      print("If I take " + str(tas[1]) + ", the score is " + str(tas[0]))
   return returnBestMove()
  

def getAvailableTakes(player, depth, curr_pile):
   global verbose
   if player == 1:
      history = computerTakes
   elif player == 2:
      history = humanTakes
   takes = []
   if verbose:
      print()
      for d in range(0, depth):
         print("- ", end="")
      print("Available takes for " + name[0 if player == 1 else 2] + " :", end="")
   for i in range(1, len(history)):
      if curr_pile-i >= 0 and 1 == abs(history[i]-1):
         takes.append(i)
         if verbose:
            print(" " + str(i), end="")
   return takes


def minimax(depth, player):
   global seCount, deCount, pCount, count, successorEvaluations, verbose
   bestScore = 0
   if player == 1:               # initialise bestScore for the player
      bestScore = -10            # Computer plays to maximise - low initial bestScore
   elif player == 2:
      bestScore = 10             # Human plays to minimise - high initial bestScore
   # terminal test
   if player == 1 and count == 0:
      seCount += 1
      #print("Win for Human at depth " + str(depth) + "Pile: "+str(count))
      return 0
   elif player == 1 and (playerStuck(computerTakes, count) or count < 0):
      seCount += 1
      #print("Win for Human at depth " + str(depth) + " - computer stuck at pile: "+str(count))
      return 0
   elif player == 2 and count == 0:
      seCount += 1
      #print("Win for computer at depth " + str(depth) + "Pile: "+str(count))
      return 1
   elif player == 2 and (playerStuck(humanTakes, count) or count < 0):
      seCount += 1
      #print("Win for computer at depth " + str(depth) + " - Human stuck at pile: "+str(count))
      return 1
   # A list to store pairs of takes and scores in
   availableTakes = getAvailableTakes(player, depth, count)
   for i in range(0, len(availableTakes)):
      take = availableTakes[i]
      currentScore = 0
      deCount += 1
      if count-take >= 0 and verbose:
         for d in range(0, depth):
            print("- ", end="")
         print(name[player] + " could take " + str(take) + ". Remaining: " + str(count-take), end="")
         if count-take == 0:
            print(" - " + name[player] + " would win.")
      if count-take < 0:
         if verbose:
            for d in range(0,depth):
               print("- ", end="")
            print("Can't take " + str(take) + " only " + str(count) + " left - " + name[player] + " would lose.")
      if player == 1:
         computerTakes[take] = 1
         count -= take
         #print("P1 takes " + str(take) + " pile: " + str(count) +" Depth:" + str(depth))
         currentScore = minimax(depth+1, 2)
         bestScore = max(bestScore, currentScore)
         #print("P1 CS at depth" + str(depth) +" is "+ str(currentScore) + " BS:" + str(bestScore))
         if depth == 0:
            successorEvaluations.append((currentScore, take))
      elif player == 2:
         humanTakes[take] = 1
         count -= take
         #print("P2 takes " + str(take) + " pile: " + str(count) +" Depth:" + str(depth))
         currentScore = minimax(depth+1, 1)
         bestScore = min(bestScore, currentScore)
         #print("P2 CS at depth" + str(depth) +" is "+ str(currentScore) + " BS:" + str(bestScore))
      # reset changes
      count += take
      if player == 1:
         computerTakes[take] = 0
      else:
         humanTakes[take] = 0 
   return bestScore


def returnBestMove():
   max = -10
   best = -1
   for i in range(0, len(successorEvaluations)):
      if max < successorEvaluations[i][0]:
         max = successorEvaluations[i][0]
         best = i
   return successorEvaluations[best][1]


def calcUpperBound():
   global bound
   temp = 0
   while temp+bound+1 < pile:
      bound += 1
      temp += bound


def processUserCommand(c):
   global bound, count, pile, humanTakes, computerTakes
   bound = 0
   count = int(c)
   pile = count
   calcUpperBound() 
   humanTakes = [0 for n in range(0,bound+1)]
   computerTakes = [0 for n in range(0,bound+1)]
   print()
   print("The pile has " + str(count) + " tokens")
   print("The maximum you may take in any turn is " + str(bound))


def playerStuck(history, pile):
   for i in range(1, len(history)):
      if pile-i >= 0 and 1 == abs(history[i]-1): 
          return False
   return True


def play(humanName):
   global count, pile, bound, humanTakes, computerTakes
   while count > 0:
      if playerStuck(humanTakes,count):
         print("\n* " + str(humanName) + " can't move anymore. \n\n**The computer wins! **")
         break
      humanMove = int(input("\nHow many do you want? "))
      if humanMove < 1:
         print("You have to take at least one token per turn.")
         continue
      if humanMove > bound:
         print("You can't take that many tokens! (Maximum " + str(bound) + ")")
         continue         
      if (count-humanMove < 0):
         print("There aren't that many tokens left in the pile!")
         continue
      if humanTakes[humanMove] == 1:
         print("You cannot take the same amount twice!")
         continue
      # move appears to be valid; make move
      count -= humanMove
      print("* " + humanName + " takes " + str(humanMove))
      # record the move in history
      humanTakes[humanMove] = 1
      # terminal test for human player
      if count == 0:
         print("\n* The pile is empty. \n\n** " + humanName + " wins! **")
         break
      # computer MOVE
      if playerStuck(computerTakes,count):
         print("\n* The computer player can't move anymore \n\n** " + humanName + " wins! **")
         break
      computerTake = getComputerTake()
      print("* The computer takes " + str(computerTake))
      count -= computerTake
      computerTakes[computerTake] = 1
      # terminal test for computer player
      if count == 0:
         print("\n* The pile is empty. \n\n** The computer wins! **")
         break
      print("* The pile now has " + str(count) + " tokens")

def run():
   print("Play Nimm (Minimax)!")
   humanName = input("What is your name? ")
   while True:                      
      c = input("Please enter the number of tokens in the pile, or 'q' to quit: ")
      if c == "q":
         return
      processUserCommand(c)
      play(humanName)
      c = input("Would you like to play again? ")
      if c != "y":
         print("Goodbye")
         break 

run()

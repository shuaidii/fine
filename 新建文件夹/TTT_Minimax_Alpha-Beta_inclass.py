# Tic Tac Toe, using Minimax with Alpha-Beta pruning
# The computer is player 1, the human is player 2

from random import randrange

board = []


####################
# TO DO: add a mechanism for limiting the difficulty

def startGame(player):
    global board
    board = [[0, 0, 0], [0, 0, 0], [0, 0, 0]]
    ####################
    # TO DO: print out selected difficulty level
    # if the starting player is the AI, their first move is always random
    if player == 1:
        i, j = (randrange(0, 3), randrange(0, 3))
        placePiece(i, j, 1)
    displayBoard()
    while not isGameOver():
        ## Human move
        # obtain user input (two integers separated by a space character)
        userMove = input("Your move (row col): ").split()
        # adjust entered numbers to account for list indices
        placePiece(int(userMove[0]) - 1, int(userMove[1]) - 1, 2)
        displayBoard()
        # stop the AI from making a move after you've won
        if isGameOver():
            break
        ## AI move
        startEvaluation()
        print("\nI'm thinking... SE:" + str(seCount) + " DE: " + str(deCount) + " P:" + str(pCount))
        for eval in successorEvaluations:
            i, j = eval[0][0], eval[0][1]
            print("Position [" + str(i + 1) + "," + str(j + 1) + "] scores " + str(eval[1]))
        bm = getBestMove()
        i, j = bm[0], bm[1]
        print("\nI think " + str(i + 1) + " " + str(j + 1) + " will be a good move.")
        placePiece(i, j, 1)
        displayBoard()
    if hasWon(1):
        print("\nYOU LOSE!")
    elif hasWon(2):
        print("\nYOU WIN!")
    ####################
    # TO DO: add cheat detection
    else:
        print("\nDRAW")


def isGameOver():
    return hasWon(1) or hasWon(2) or len(getAvailableStates()) == 0


def hasWon(player):
    global board
    # diagonal win
    p = board[1][1]
    if p == player and (p == board[0][0] and p == board[2][2]
                        or p == board[0][2] and p == board[2][0]):
        return True
    # row or column win
    for i in range(0, 3):
        pc, pr = board[i][0], board[0][i]
        if (pc == player and pc == board[i][1] and pc == board[i][2]
                or pr == player and pr == board[1][i] and pr == board[2][i]):
            return True
    return False


def getAvailableStates():
    # available positions are those that have a '0' value
    global board
    availablePositions = []
    for i in range(0, 3):
        for j in range(0, 3):
            if board[i][j] == 0:
                availablePositions.append((i, j))
    return availablePositions


def placePiece(i, j, player):
    board[i][j] = player


def pr(s):
    print(s, end="")  # print without line break


def startEvaluation():
    global successorEvaluations, seCount, deCount, pCount
    seCount, deCount, pCount = 0, 0, 0
    successorEvaluations = []
    minimax(0, 1, -1, 1)


def minimax(depth, player, alpha, beta):
    global seCount, deCount, pCount
    # 1. perform static evaluation (has anyone won?)
    if hasWon(1):
        seCount += 1
        return 1
    if hasWon(2):
        seCount += 1
        return -1
    positionsAvailable = getAvailableStates()
    if len(positionsAvailable) == 0:
        seCount += 1
        return 0
    # 2. loop through available positions, recursively simulating the game for each player in turn
    bestScore = -1 if player == 1 else 1
    for i in range(0, len(positionsAvailable)):
        pos = positionsAvailable[i]
        deCount += 1
        if player == 1:
            placePiece(pos[0], pos[1], 1)
            currentScore = minimax(depth + 1, 2, alpha, beta)
            if currentScore > bestScore:
                bestScore = currentScore
            alpha = max(currentScore, alpha)
            if depth == 0:
                successorEvaluations.append((pos, currentScore))
        elif player == 2:
            placePiece(pos[0], pos[1], 2)
            currentScore = minimax(depth + 1, 1, alpha, beta)
            if currentScore < bestScore:
                bestScore = currentScore
                beta = min(currentScore, beta)
        #     reset board positions
        placePiece(pos[0], pos[1], 0)
        # pruning
        if alpha >= beta:
            pCount += 1
            break
    # return best score
    return bestScore


def getBestMove():
    max = -1
    best = -1
    for i in range(0, len(successorEvaluations)):
        if (max < successorEvaluations[i][1]):
            max = successorEvaluations[i][1]
            best = i
    return successorEvaluations[best][0]


def displayBoard():
    # print current board state to console
    print()
    print("     1 2 3")
    print("    -------")
    for i in range(0, 3):
        for j in range(0, 3):
            if j == 0:
                pr(" " + str(i + 1) + " |")
            if board[i][j] == 1:
                pr(" X")
            elif board[i][j] == 2:
                pr(" 0")
            else:
                pr(" .")
        print(" |")
    print("    -------")


def run():
    print("*******************************")
    print("Hello, welcome to Tic Tac Toe AB!")
    print("*******************************")
    while True:
        c = input("Enter '1' for the AI to begin, or '2' to make the first move, or 'q' to quit: ")
        if (c == "q"):
            print("Goodbye")
            return
        ####################
        # TO DO: diff level input
        startGame(int(c))


run()

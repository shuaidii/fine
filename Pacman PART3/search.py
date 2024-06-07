# search.py
# ---------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


"""
In search.py, you will implement generic search algorithms which are called by
Pacman agents (in searchAgents.py).
"""

from cmath import inf
from itertools import accumulate
from queue import PriorityQueue
import util


class SearchProblem:
    """
    This class outlines the structure of a search problem, but doesn't implement
    any of the methods (in object-oriented terminology: an abstract class).

    You do not need to change anything in this class, ever.
    """

    def getStartState(self):
        """
        Returns the start state for the search problem.
        """
        util.raiseNotDefined()

    def isGoalState(self, state):
        """
          state: Search state

        Returns True if and only if the state is a valid goal state.
        """
        util.raiseNotDefined()

    def getSuccessors(self, state):
        """
          state: Search state

        For a given state, this should return a list of triples, (successor,
        action, stepCost), where 'successor' is a successor to the current
        state, 'action' is the action required to get there, and 'stepCost' is
        the incremental cost of expanding to that successor.
        """
        util.raiseNotDefined()

    def getCostOfActions(self, actions):
        """
         actions: A list of actions to take

        This method returns the total cost of a particular sequence of actions.
        The sequence must be composed of legal moves.
        """
        util.raiseNotDefined()


def tinyMazeSearch(problem):
    """
    Returns a sequence of moves that solves tinyMaze.  For any other maze, the
    sequence of moves will be incorrect, so only use this for tinyMaze.
    """
    from game import Directions
    s = Directions.SOUTH
    w = Directions.WEST
    return  [s, s, w, s, w, w, s, w]

def depthFirstSearch(problem):
    """
    Search the deepest nodes in the search tree first.

    Your search algorithm needs to return a list of actions that reaches the
    goal. Make sure to implement a graph search algorithm.

    To get started, you might want to try some of these simple commands to
    understand the search problem that is being passed in:

    print("Start:", problem.getStartState())
    print("Is the start a goal?", problem.isGoalState(problem.getStartState()))
    print("Start's successors:", problem.getSuccessors(problem.getStartState()))
    """
    "*** YOUR CODE HERE ***"

    util.raiseNotDefined()

def breadthFirstSearch(problem):
    """Search the shallowest nodes in the search tree first."""
    "*** YOUR CODE HERE ***"

    util.raiseNotDefined()

def uniformCostSearch(problem):
    """Search the node of least total cost first."""
    "*** YOUR CODE HERE ***"
    util.raiseNotDefined()

def nullHeuristic(state, problem=None):
    """
    A heuristic function estimates the cost from the current state to the nearest
    goal in the provided SearchProblem.  This heuristic is trivial.
    """
    return 0

# Please DO NOT change the following code, we will use it later
def aStarSearch(problem, heuristic=nullHeuristic):
    """Search the node that has the lowest combined cost and heuristic first."""
    "*** YOUR CODE HERE ***"
    myPQ = util.PriorityQueue()
    startState = problem.getStartState()
    startNode = (startState, '',0, [])
    myPQ.push(startNode,heuristic(startState,problem))
    visited = set()
    best_g = dict()
    while not myPQ.isEmpty():
        node = myPQ.pop()
        state, action, cost, path = node
        if (not state in visited) or cost < best_g.get(state):
            visited.add(state)
            best_g[state]=cost
            if problem.isGoalState(state):
                path = path + [(state, action)]
                actions = [action[1] for action in path]
                del actions[0]
                return actions
            for succ in problem.getSuccessors(state):
                succState, succAction, succCost = succ
                newNode = (succState, succAction, cost + succCost, path + [(state, action)])
                myPQ.push(newNode,heuristic(succState,problem)+cost+succCost)
    util.raiseNotDefined()


def enforcedHillClimbing(problem, heuristic=nullHeuristic):
    """
    Local search with heuristic function.
    You DO NOT need to implement any heuristic, but you DO have to call it.
    The heuristic function is "manhattanHeuristic" from searchAgent.py.
    It will be pass to this function as second argument (heuristic).
    """
    "*** YOUR CODE HERE FOR TASK 1 ***"
    
    startState = problem.getStartState()    # initialize the start node
    node = (startState, '', 0, [])
    while not problem.isGoalState(node[0]):     # call the improve process for searching 
        node = improve_EHC(node, heuristic, problem)
    state, action, cost, path = node
    path = path + [(state, action)]
    actionSequence = [action[1] for action in path]     # formulate the action sequence through the path
    del actionSequence[0]

    return actionSequence

# a helpful funtion for improve process of enforced hill climbing
def improve_EHC(currentNode, heuristic, problem):
    open_list = util.Queue()    # initialize a Queue as the open list
    open_list.push(currentNode)   # push the currently considered node into the open list
    closed_list = set()     # initialize a Set as the closed list
    state, action, cost, path = currentNode     # the state model  
    h_currentNode = heuristic(state, problem)   # compute the h-value of the currently considered node
    while not open_list.isEmpty():      # search for the node with the better heuristic than the currently considered node
        node = open_list.pop()
        state, action, cost, path = node
        if not state in closed_list:     # add the visited node into the closed list
            closed_list.add(state)
            if heuristic(state, problem) < h_currentNode:
                return node
            for succ in problem.getSuccessors(state):   # expand the nodes
                succState, succAction, succCost = succ
                nextNode = (succState, succAction, cost + succCost, path + [(state, action)])
                open_list.push(nextNode)

    #put the below line at the end of your code or remove it
    util.raiseNotDefined()
        

from math import inf as INF   
def bidirectionalAStarEnhanced(problem, heuristic=nullHeuristic, backwardsHeuristic=nullHeuristic):
    
    """
    Bidirectional global search with heuristic function.
    You DO NOT need to implement any heuristic, but you DO have to call them.
    The heuristic functions are "manhattanHeuristic" and "backwardsManhattanHeuristic" from searchAgent.py.
    It will be pass to this function as second and third arguments.
    You can call it by using: heuristic(state,problem) or backwardsHeuristic(state,problem)
    """
    "*** YOUR CODE HERE FOR TASK 2 ***"
    # The problem passed in going to be BidirectionalPositionSearchProblem    
    
    open_forward = util.PriorityQueue()     # initialize the Priority Queue as the open list for BAE
    open_backward = util.PriorityQueue()
    
    # open_forwardSet = dict()      # initialize the set
    # open_backwardSet = dict()

    open_forwardSet = dict()      # try dict
    open_backwardSet = dict()

    closed_forward = set()      # initialize the Set for effeiciently check the membership
    closed_backward = set()

    g_forward = dict()   # initialize the Distionary for recording best g value (cost)
    g_backward = dict()

    lower = 0    # initialize the lower bound, upper bound, direction for controling search space
    upper = INF
    plan = []
    direction = 'f'  # direciton initially defined as forward
 
    startState = problem.getStartState()    # initialize the start state and goal states
    goalStates = problem.getGoalStates()
    
    startNode = (startState, [], 0)  # initialize the start node; node = (state, path, cost)    
    open_forward.push(startNode, heuristic(startState, problem))    # push the start node into the queue
    # open_forwardSet.add(str(startState))      # add the start state into the open list set
    open_forwardSet[str(startState)] = 1    # record the pushed node in the dictionary
    g_forward[startState] = (0, [])       # record the g-value (cost) and path of the start state

    # initialize the goal node
    for goalState in goalStates:    # initialize the goal node in a iterative manner
        goalNode = (goalState, [], 0)   # intialize the goal node; node = (state, path, cost)    
        open_backward.push(goalNode, backwardsHeuristic(goalState, problem))   # push the goal node into the queue
        # open_backwardSet.add(str(goalState))  # add the goal state into the open list set
        open_backwardSet[str(goalState)] = 1
        g_backward[goalState] = (0, [])   # record the g-value (cost) and path of the goal state

    while not open_forward.isEmpty() and not open_backward.isEmpty():
        bMin_forward = open_forward.getMinimumPriority()
        bMin_backward = open_backward.getMinimumPriority()
        lower = (bMin_forward + bMin_backward) / 2
        
        # forward search 
        if direction == 'f':
            node = open_forward.pop()   # pop the most prior node from the forward open list
            state, path, cost = node   # define the info of the node 
            open_forwardSet[str(state)] -= 1    
            closed_forward.add(str(state))   # add the visited note in to the set for closed set
            
            # check if the state has been visited in the backward search and update the upper bound and the final plan
            if (str(state) in open_backwardSet): 
                if open_backwardSet[str(state)] > 0 and (g_forward[str(state)][0] + g_backward[str(state)][0] < upper): 
                    upper = g_forward[str(state)][0] + g_backward[str(state)][0]

            # both directions meet
            if lower >= upper:
                plan = g_forward[str(state)][1] + list(reversed(g_backward[str(state)][1]))
                return plan

            # expansion 
            for succ in problem.getSuccessors(state):
                succState, succAction, succCost = succ
                newNode = (succState, path + [succAction], cost + succCost)           
                if str(succState) not in closed_forward:
                    # define the f_value and d_value given the paper
                    fValue_forward = newNode[2] + heuristic(succState, problem)
                    dValue_forward = newNode[2] - backwardsHeuristic(succState, problem)
                    priority_forward = fValue_forward + dValue_forward
                    open_forward.push(newNode, priority_forward)
                    if str(succState) not in open_forwardSet: 
                        open_forwardSet[str(succState)] = 1
                    else:
                        open_forwardSet[str(succState)] += 1
                    if str(succState) not in g_forward or (newNode[2] < g_forward[str(succState)][0]):
                        g_forward[str(succState)] = (newNode[2], newNode[1]) 
            
            # direction = 'b'

        # backward search 
        if direction == 'b':
            node = open_backward.pop()   # pop the most prior node from the forward open list
            state, path, cost = node   # define the info of the node 
            open_backwardSet[str(state)] -= 1   # correspondingly adjust the backwardDict
            closed_backward.add(str(state))   # add the visited note in to the set for closed set
            
            # check if the state has been visited in the backward search and update the upper bound and the final plan
            if (str(state) in open_forwardSet):
                if open_forwardSet[str(state)] > 0 and (g_forward[str(state)][0] + g_backward[str(state)][0] < upper): 
                    upper = g_forward[str(state)][0] + g_backward[str(state)][0]
                
            # both directions meet
            if lower >= upper:
                plan = g_forward[str(state)][1] + list(reversed(g_backward[str(state)][1]))
                return plan

            # expansion 
            for succ in problem.getBackwardsSuccessors(state):
                succState, succAction, succCost = succ
                newNode = (succState, path + [succAction], cost + succCost)

                if str(succState) not in closed_backward:
                    # define the f_value and d_value given the paper
                    fValue_backward = newNode[2] + backwardsHeuristic(succState, problem)
                    dValue_backward = newNode[2] - heuristic(succState, problem)
                    priority_backward = fValue_backward + dValue_backward
                    open_backward.push(newNode, priority_backward)

                    if str(succState) not in open_backwardSet: 
                        open_backwardSet[str(succState)] = 1
                    else:
                        open_backwardSet[str(succState)] += 1    

                    if str(succState) not in g_backward or (newNode[2] < g_backward[str(succState)][0]):
                        g_backward[str(succState)] = (newNode[2], newNode[1]) 
                    
        # choose direction: always follow the direction with smaller priority
        if bMin_forward < bMin_backward:
            direction = 'f'
        else:
            direction = 'b'
 
      
        


# Abbreviations
bfs = breadthFirstSearch
dfs = depthFirstSearch
astar = aStarSearch
ucs = uniformCostSearch


ehc = enforcedHillClimbing
bae = bidirectionalAStarEnhanced



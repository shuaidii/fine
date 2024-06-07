class Robot:
    def __init__(self, location, carried_items, strength):
        self.location = location
        self.carried_items = carried_items
        self.strength = strength

    def weight_carried(self):
        return sum([ITEM_WEIGHT[i] for i in self.carried_items])

    ## Define unique string representation for the state of the robot object
    def __repr__(self):
        return str((self.location,
                    self.carried_items,
                    self.strength))


class Door:
    def __init__(self, roomA, roomB, doorkey=None, locked=False):
        self.goes_between = {roomA, roomB}
        self.doorkey = doorkey
        self.locked = locked
        # Define handy dictionary to get room on other side of a door
        self.other_loc = {roomA: roomB, roomB: roomA}

    ## Define a unique string representation for a door object
    def __repr__(self):
        return str(("door", self.goes_between, self.doorkey, self.locked))


class State:
    def __init__(self, robot, doors, room_contents):
        self.robot = robot
        self.doors = doors
        self.room_contents = room_contents

    ## Define a string representation that will be uniquely identify the state.
    ## An easy way is to form a tuple of representations of the components of
    ## the state, then form a string from that:
    def __repr__(self):
        return str((self.robot.__repr__(),
                    [d.__repr__() for d in self.doors],
                    self.room_contents))


ROOM_CONTENTS = {
    'workshop': {'rusty key'},
    'store room': {'bucket', 'suitcase'},
    'tool cupboard': {'sledge hammer', 'anvil', 'saw', 'screwdriver'},
}

ITEM_WEIGHT = {
    'rusty key': 0,
    'bucket': 2,
    'suitcase': 4,
    'screwdriver': 1,
    'sledge hammer': 5,
    'anvil': 12,
    'saw': 2,
}

DOORS = [
    Door('workshop', 'store room'),
    Door('store room', 'tool cupboard', doorkey='rusty key', locked=False)
]

from copy import deepcopy

from bbmodcache.bbSearch import SearchProblem, search


class RobotWorker(SearchProblem):

    def __init__(self, state, goal_item_locations):
        self.initial_state = state
        self.goal_item_locations = goal_item_locations

    def possible_actions(self, state):

        robot_location = state.robot.location
        strength = state.robot.strength
        weight_carried = state.robot.weight_carried()

        actions = []
        # Can put down any carried item
        for i in state.robot.carried_items:
            actions.append(("put down", i))

        # Can pick up any item in room if strong enough
        for i in state.room_contents[robot_location]:
            if strength >= weight_carried + ITEM_WEIGHT[i]:
                actions.append(("pick up", i))

        # If there is an unlocked door between robot location and
        # another location can move to that location
        for door in state.doors:
            if door.locked == False and robot_location in door.goes_between:
                actions.append(("move to", door.other_loc[robot_location]))

        # Now the actions list should contain all possible actions
        return actions

    def successor(self, state, action):
        next_state = deepcopy(state)
        act, target = action
        if act == "put down":
            next_state.robot.carried_items.remove(target)
            next_state.room_contents[state.robot.location].add(target)

        if act == "pick up":
            next_state.robot.carried_items.append(target)
            next_state.room_contents[state.robot.location].remove(target)

        if act == "move to":
            next_state.robot.location = target

        return next_state

    def goal_test(self, state):
        # print(state.room_contents)
        for room, contents in self.goal_item_locations.items():
            for i in contents:
                if not i in state.room_contents[room]:
                    return False
        return True

    def display_state(self, state):
        print("Robot location:", state.robot.location)
        print("Robot carrying:", state.robot.carried_items)
        print("Room contents:", state.room_contents)


def cost(path, state):
    in_score = 0
    out_score = 0
    for room, contents in goal_item_locations.items():
        for i in contents:
            if i not in state.room_contents[room]:
                in_score += 1
            else:
                out_score += 1

    return (in_score - out_score) * len(path)


def cost1(path, state):
    return -len(path)


def heuristic(state):
    score = 0
    for room, contents in goal_item_locations.items():
        for i in contents:
            if i not in state.room_contents[room]:
                score += 1
    return 2 ** score


rob = Robot('store room', [], 15)

state = State(rob, DOORS, ROOM_CONTENTS)

goal_item_locations = {"store room": {"sledge hammer", "screwdriver", "anvil"}}

RW_PROBLEM_1 = RobotWorker(state, goal_item_locations)

poss_acts = RW_PROBLEM_1.possible_actions(RW_PROBLEM_1.initial_state)

for act in poss_acts:
    print("Action", act, "leads to the following state:")
    next_state = RW_PROBLEM_1.successor(RW_PROBLEM_1.initial_state, act)
    RW_PROBLEM_1.display_state(next_state)
    print()

# search(RW_PROBLEM_1, 'BF/FIFO', 100000, loop_check=True)

T0 = search(RW_PROBLEM_1, 'BF/FIFO', 100000, cost=cost, loop_check=True, return_info=True)
T1 = search(RW_PROBLEM_1, 'DF/LIFO', 100000, cost=cost, loop_check=True, return_info=True)
T2 = search(RW_PROBLEM_1, 'BF/FIFO', 100000, heuristic=heuristic, loop_check=True, return_info=True)
T3 = search(RW_PROBLEM_1, 'DF/LIFO', 100000, heuristic=heuristic, loop_check=True, return_info=True)
T4 = search(RW_PROBLEM_1, 'DF/LIFO', 100000, heuristic=heuristic, cost=cost, loop_check=True, return_info=True)
T5 = search(RW_PROBLEM_1, 'DF/LIFO', 100000, heuristic=heuristic, cost=cost1, loop_check=True, return_info=True)

# search(RW_PROBLEM_1, 'DF/LIFO', 100000, cost=cost, heuristic=heuristic, loop_check=True)
# search(RW_PROBLEM_1, 'DF/LIFO', 100000, heuristic=heuristic, loop_check=True)
# search(RW_PROBLEM_1, 'BF/FIFO', 100000, heuristic=heuristic, loop_check=True)


## Comparing results by generating a table
TEST_RESULTS = [T0, T1, T2, T3, T4, T5]
short_tc = {"GOAL_STATE_FOUND": "Y",
            "NODE_LIMIT_EXCEEDED": "!",
            "SEARH-SPACE_EXHAUSTED": "x"}

print("\n                **TESTS SUMMARY**\n")

print("Test    #max   Result   #gen     #inQ    Time s")
for i, test in enumerate(TEST_RESULTS):
    max = test['args']['max_nodes']
    tc = test['result']['termination_condition']
    stc = short_tc[tc]

    ng = test['search_stats']['nodes_generated']
    nq = test['search_stats']['nodes_left_in_queue']
    time = round(test['search_stats']['time_taken'], 2)
    print(f"{i:>3}: {max:>8}    {stc}  {ng:>8} {nq:>8}     {time} ")

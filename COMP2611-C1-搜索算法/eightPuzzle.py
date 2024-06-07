## A couple of possible goal states:
from IPython.core.display import display

from crazy8heuristics import bb_manhattan, bb_misplaced_tiles

NORMAL_GOAL = [[1, 2, 3],
               [4, 5, 6],
               [7, 8, 0]]

SPIRAL_GOAL = [[1, 2, 3],
               [8, 0, 4],
               [7, 6, 5]]

### Declarations of some example states
LAYOUT_0 = [[1, 5, 2],
            [0, 4, 3],
            [7, 8, 6]]

LAYOUT_1 = [[5, 1, 7],
            [2, 4, 8],
            [6, 3, 0]]

LAYOUT_2 = [[2, 6, 3],
            [4, 0, 5],
            [1, 8, 7]]

LAYOUT_3 = [[7, 2, 5],
            [4, 8, 1],
            [3, 0, 6]]

LAYOUT_4 = [[8, 6, 7],
            [2, 5, 4],
            [3, 0, 1]]


def number_position_in_layout(n, layout):
    for i, row in enumerate(layout):
        for j, val in enumerate(row):
            if val == n:
                return (i, j)


from copy import deepcopy

from bbmodcache.bbSearch import SearchProblem, search


class EightPuzzle(SearchProblem):
    action_dict = {
        (0, 0): [(1, 0, 'up'), (0, 1, 'left')],
        (0, 1): [(0, 0, 'right'), (1, 1, 'up'), (0, 2, 'left')],
        (0, 2): [(0, 1, 'right'), (1, 2, 'up')],
        (1, 0): [(0, 0, 'down'), (1, 1, 'left'), (2, 0, 'up')],
        (1, 1): [(1, 0, 'right'), (0, 1, 'down'), (1, 2, 'left'), (2, 1, 'up')],
        (1, 2): [(0, 2, 'down'), (1, 1, 'right'), (2, 2, 'up')],
        (2, 0): [(1, 0, 'down'), (2, 1, 'left')],
        (2, 1): [(2, 0, 'right'), (1, 1, 'down'), (2, 2, 'left')],
        (2, 2): [(2, 1, 'right'), (1, 2, 'down')]
    }

    def __init__(self, initial_layout, goal_layout):
        pos0 = number_position_in_layout(0, initial_layout)
        # Initial state is pair giving initial position of space
        # and the initial tile layout.
        self.initial_state = (pos0, initial_layout)
        self.goal_layout = goal_layout

    ### I just use the position on the board (state[0]) to look up the
    ### appropriate sequence of possible actions.
    def possible_actions(self, state):
        actions = EightPuzzle.action_dict[state[0]]
        actions_with_tile_num = []
        for r, c, d in actions:
            tile_num = state[1][r][c]  ## find number of moving tile
            # construct the action representation including the tile number
            actions_with_tile_num.append((r, c, (tile_num, d)))
        return actions_with_tile_num

    def successor(self, state, action):
        old0row, old0col = state[0]  # get start position
        new0row, new0col, move = action  # unpack the action representation
        moving_number, _ = move
        ## Make a copy of the old layout
        newlayout = deepcopy(state[1])
        # Swap the positions of the new number and the space (rep by 0)
        newlayout[old0row][old0col] = moving_number
        newlayout[new0row][new0col] = 0
        return ((new0row, new0col), newlayout)

    def goal_test(self, state):
        return state[1] == self.goal_layout

    def display_action(self, action):
        _, _, move = action
        tile_num, direction = move
        print("Move tile", tile_num, direction)

    def display_state(self, state):
        for row in state[1]:
            nums = [(n if n > 0 else '.') for n in row]
            print("   ", nums[0], nums[1], nums[2])


def cost(path, state):
    return len(path)


EP = EightPuzzle(LAYOUT_1, NORMAL_GOAL)

# search(EP, 'BF/FIFO', 1000000, loop_check=False, show_state_path=True)

## 使用启发函数，都能快速找到路径
T0 = search(EP, 'BF/FIFO', 1000000, heuristic=bb_misplaced_tiles, loop_check=True, show_state_path=True,
            return_info=True)
display(T0)

T1 = search(EP, 'BF/FIFO', 1000000, heuristic=bb_manhattan, loop_check=True, show_state_path=True, return_info=True)

## 使用成本函数，性能较差，但也能找到路径，平均创建节点47万次
T2 = search(EP, 'BF/FIFO', 1000000, cost=cost, loop_check=True, show_state_path=True, return_info=True)

## 使用 A*，能找到路径，性能居中
T3 = search(EP, 'BF/FIFO', 1000000, heuristic=bb_misplaced_tiles, cost=cost, loop_check=True, show_state_path=True,
            return_info=True)
T4 = search(EP, 'BF/FIFO', 1000000, heuristic=bb_manhattan, cost=cost, loop_check=True, show_state_path=True,
            return_info=True)

# 第三个参数max_nodes改成10000，找不到路径
T5 = search(EP, 'BF/FIFO', 10000, heuristic=bb_misplaced_tiles, cost=cost, loop_check=True, show_state_path=True,
            return_info=True)
T6 = search(EP, 'BF/FIFO', 10000, heuristic=bb_manhattan, cost=cost, loop_check=True, show_state_path=True,
            return_info=True)

T7 = search(EP, 'BF/FIFO', 10000, heuristic=bb_misplaced_tiles, cost=cost, loop_check=True, show_state_path=True,
            dots=False, return_info=True)
T8 = search(EP, 'BF/FIFO', 10000, heuristic=bb_manhattan, cost=cost, loop_check=True, show_state_path=True, dots=False,
            return_info=True)

## Comparing results by generating a table
TEST_RESULTS = [T0, T1, T2, T3, T4, T5, T6, T7, T8]
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

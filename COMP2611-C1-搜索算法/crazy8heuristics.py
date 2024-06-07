#!/usr/bin/env python
# coding: utf-8

# In[6]:


## Coding can be harmful: a lesson in how NOT to program in Python

## I'm providing this unusual file that defines two heuristic functions
## for use with bbSearch when solving the 8-puzzle problem.

## The heuristics functions are:
##  * bb_misplaced_tiles(state)
##  * bb_manhattan(state)
## These both give very rough lower bounds on the minimum number of moves it
## will take to get from a given tile state to the solution state.
## They assume that the goal state has the tiles in ascenting order going from
## top row to bottom row and left to right along each row, with the space at
## the bottom left.

## I have witten the code in the most obscure way I could come up with.
## It still provides quite efficient implementations. However, you should
## definitely consider writing your own heuristics instead of using this file.


## Let's start with something sensible that might be useful
## for calculating heuristics for the 8-puzzle problem.

def number_position_in_layout(n, layout):
    for i, row in enumerate(layout):
        for j, val in enumerate(row):
            if val == n:
                return (i, j)


## That was too easy. Let's see if we can write the rest of the
## code by using a strange seemingly random sequence of numbers and symbols
## and performing some bizzare operations on it:

X = """78@79@82@77@6  5@76@95@  71@79@65@76@95@8  0@79@83@73@8  4@73@79@7      8@83@32@6  1@32@  123*32@32@32@32@49@58    @40@48@44@48@41@44  @32@50@58@40@48@44@4                  9@41@44@32@51@  58@40@48@4 4@50@41  @44*32@      32@32@32@52     @58@40@49@44 @48@41@44@32@53@58@40@49@44@49@41   @44@3                        2@54@58@40@49@4  4@50@41@44*32@32@32@32@55@58@40@50               @44@48@41@44  @32@56@58
@40@50@44@            49@
41@32@125"""
V = "".join([x for x in list(X.replace('*', '@10@')) if not x in ' \\n\\m\\t\\r\\v'])
U = ''.join([chr(int(x)) for x in V.split(chr(64))])
exec(U, globals(), locals())

## So far so good.

## Do I see a pattern emerging from the chaos?

fx1 = (exec(
    'c = """    10@100@         101@10     2@32@120@120@120@49@40@115@116@97@116@101@41@58*32    @32  @32@3      2@109@10     5@115@112@108@97@99@101@100@32@61@32@48*32@32@32@    32@    102@     11  1@114@    32@110@32@105@110@32@114@97@110@103@101@40@49@44@57    @41    @58*     32  @32@32     @32@32@32@32@32@120@44@121@32@61@32@110@117@109@98@1    01@114@95@1     12@111@11       5@105@116@105@111@110@95@105@110@95@108@97@121@1    11@   117@1     16@40@110@      44@115@116@97@116@101@91@49@93@41*32@32@32@32@32@3    2@3    2@32@    105    @102     @32@40@120@44@121@41@32@33@61@32@78@79@82@77@65@76    @95     @71@7   9@6     5@76    @95@80@79@83@73@84@73@79@78@83@91@110@93@58*32@    32@   32@32@    32@     32@3   2@32@32@32@32@32@109@105@115@112@108@97@99@101@    100@32@43       @61@32@49*3   2@32@32@32@114@101@116@117@114@110@32@109@105    @115@112        @108@97@9    9@101@100@10    """ ')
       or exec("""S = "".join([x for x in list(c.replace('*','@10@')) if not x in ' \\n\\m\\t\\r\\v' ]) """,
               globals(), locals())
       or exec("""T = ''.join([chr(int(x)) for x in S.split(chr(64))])""", globals(), locals())
       or exec(T, globals(), locals()) or xxx1)

## Don't try this on your own kids!

fx2 = (exec(
    'c = """    10@100@101@102@32@120@120@120@50@40@115@116@97@116@101@41@58*32@32@32@32@109@97    @110@95@100@105@115         @116@32@61@32@48*32@32@32@32@102@111@114@32    @110@32@105@110@               32@114@97@110@103@101@40@49@44@57@41@58*32@32@    32@32@32@32@32@                 32@120@44@121@32@61@32@110@117@109@98@101@114@95    @112@111@115@10    5@1   16@     105@111@110@95@105@110@95@108@97@121@111@117@    116@40@110@44@1    15@   116     @97@116@101@91@49@93@41*32@32@32@32@32@32    @32@32@103@120@  4            4  @32@103@121@32@61@32@78@79@82@77@65@76@95@71@79    @65@76@95@80@79@  83        @7  3@84@73@79@78@83@91@110@93*32@32@32@32@32@32@    32@32@109@97@110@   95@100@1    05@115@116@32@43@61@32@40@32@97@98@115@4    0@120@45@103@120@4            1@32@43@32@97@98@115@40@121@45@103@121@41@41*32@32@3    2@32@114@101@116@117@114@110@32@109@97@110@95@100@105@115@116@10""" ')
       or exec("""S = "".join([x for x in list(c.replace('*','@10@')) if not x in ' \\n\\m\\t\\r\\v' ]) """,
               globals(), locals())
       or exec("""T = ''.join([chr(int(x)) for x in S.split(chr(64))])""", globals(), locals())
       or exec(T, globals(), locals()) or xxx2)


## Now if my calculations are correct we can define the required 8-puzzle heristics as follows:

def bb_misplaced_tiles(state):
    return fx1(state)


def bb_manhattan(state):
    return fx2(state)

# In[ ]:

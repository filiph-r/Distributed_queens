# Distributed queens
This is a functional distributed system that will solve the problem of [n queens](https://www.geeksforgeeks.org/n-queen-problem-backtracking-3). <br>The system can do the following:<br><br>
● Starting calculations for an arbitrary size problem.<br>
● Distributed calculation for a given problem.<br>
● View the solution on all nodes.<br>
  &nbsp;&nbsp;&nbsp;&nbsp;○ Find all the solutions for the given dimension of the board.<br>
● Pausing the calculation.<br>
● Start multiple calculations in parallel.<br>
  &nbsp;&nbsp;&nbsp;&nbsp;○ Only one calculation is active at a time, the rest are paused.<br>
● View progress of running calculations.<br>
● Arbitrary switching of nodes on and off without failure.<br>
● All nodes are constantly loaded with the work stealing approach.<br>

# Functionality
The job the nodes do is test whether the N queen can be deployed on the chessboard
dimension N x N so that all do not attack each other. It is necessary to find all of this
schedules for the given N.
This problem is addressed by the brute-force approach, ie. by examining all possible options.

# Node configuration
When starting a node, the configuration file that states the following is automatically read
attributes:<br>
● The port on which the node will listen.<br>
● IP address and bootstrap node of the node. <br>
● Limit - the percentage of work below which theft of work is not allowed. <br>

# Commands and reports
The user can assign the following commands to the system (from nodes):<br><br>
● status - Displays the status of all started calculations, and indicates the current status
actively, if any. Possible states are: active, paused, done, fuzzy. Just one
calculation can be active at one time, while all others have to be
paused or done. For calculations that are not completed it prints how many possible positions there are
examined so far, expressed as a percentage. This command asks all nodes for
state of there jobs. If there is a disagreement on at least two nodes for a job, then it
will be marked as fuzzy.<br>
● start X - The calculation for the given parameter X starts (X represents the dimension of the table,
as well as the number of queens to be deployed). This command is only allowed if calculation for parameter X is not already
active. If the calculation for parameter X is previous
started, an error will be reported. If the calculation for parameter X is previous
paused, then this command continues the calculation for that parameter. If it is
the current active calculation for a parameter Y other than X should be paused
and start calculating for X. When starting a job, the problem is evenly distributed to
all the nodes in the system.<br>
● pause - Pauses the current active calculation. If there are no active calculations,
a mistake will be reported. After executing this command, no calculation is active.<br>
● result
X - Displays the results for the completed calculation for parameter X. If
the calculation for X did not start, or did not complete, an error will be reported. Result displayed in
a matrix form where '_' indicates a position where there is no queen and 'Q' indicates a position
on which there are queens. No messages are sent when executing this command.<br>
● stop - Stops the node. Another node will continue where this node left off.
There is one node to which the remaining work gets allocated.<br>

# Node message documentation
The [documentation](https://github.com/filiph-r/Distributed_queens/blob/master/Documentation%20(Serbian)/KiDS%20Dokumentacija.pdf) that states the behaviour and messages of the nodes written in serbian. 

# Note
This Project was written as a part of the course "Concurrent and Distributed Systems" at The Faculty Of Computer Science in Belgrade.

# Contributed
Filip Hadzi-Ristic<br>
filip.h-r@protonmail.com<br>

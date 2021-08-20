***Warning - under heavy development, this is in no way complete. Clone only if you are curious**

The aim of DialogueTreeMaker is to create a simple to use, but at the same time versatile dialogue tree graph. 
The main target is dialogues in games, but can also be used anywhere else dialogue trees are required (chat bots etc.)
You can currently export to JSON and XML which can then be read in to the user's program which ever way they like.
In addition to that, if your project uses java then you can clone the state machine class and then export to an object file. 
Which the state machine can read in automatically and provide a ready-made layer of abstraction within your program.

The core is a very simple drag and connect mechanic between nodes which represent dialogue parts and replies.
A single project can have multiple dialogue graphs for easier management.
In addition to graphs there can also be characters(people) and variables can be added, which are both project-wide.
Each Person is intended to be a character in the conversation and can have a custom image and properties.
The variables are intended to be the attributes of a player which can then be used for conditional comparisons.
The state machine can in fact be configured to read in the variables automatically from any class if the 
variable names there are the same as they were set in the graph.

There are currently 5 types of node: 
 - The start node which is unique and indicates the start of a conversation.
 - The end node which indicates the end of a conversation.
 - The Dialogue node which provides a single piece of dialogue.
 - The Choice node which in of itself is similar to the dialogue node, however it can have various answers
	intended to be dialogue options for the player.
 - The answer node which is always held inside a choice node and represents a single answer option.
 
In addition to this, the dialogue and answer nodes can also have conditional statements attached. 
Each conditional is a comparison between static values/person propertis/variables. And can determine where the conversation goes.
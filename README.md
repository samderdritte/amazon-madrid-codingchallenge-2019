# Amazon Madrid CodingChallenge 2019

This project is my solution for the 2019 Amazon Madrid Coding challenge. The landing page for the challenge can be found [here](https://amazonbusinessmadrid-codechallenge2019.com/).

## Overview

The Challenge had two excercises. Both of which were solved with the code in this repository. 
The description of the exercises can be found at the bottom of this readme.

## How to use the code

The two main classes for the exercises are:
- DijkstraAlgorithm.java (for exercise 1)
- RobotDelivery.java (for exercise 2)

Running these classes will output the solutions for the exercises in the console.


## Approach

Exercise 1 was solved by using a variant of the DijkstraAlgorithm which finds the shortest path between two nodes in a weighted graph.
Dijkstra's algorithm ouputs any of the shortest paths between two node. In order to meet the restriction of the exercise 
(travellers take the route in alphabetical order in case there are several possible routes), the algorithm was adjusted to meet this criteria.
See the method *DijkstraAlgorithm.getAlphabeticalPath()*.

For exercise 2, I have tested several strategies in order to find the shortest possible time to deliver all the packages.
Uncomment the strategies in the main method of RobotDelivery.java if you want to see how the other strategies work.

## Exercise Description
### Exercise 1 - Most visited Stations

You have to find out what are the most visited stations from the subway network.
As input you are given a file with a list of trip records and a file with the map of the subway.

| Input	Format  | Description | Downloads  |
| :------------- |:-------------|:------------|
| Subway map    | JSON file with an array containing a list of subway line routes. Each line has a number as name (line) and a list of stations (stations), with the station name (name) and the time to reach that station in minutes (time). The total time required to go from one station to another in the same line is the difference between the time fields. Note that line "6" is circular, as first and last station are the same. | [Subway Map](https://github.com/samderdritte/amazon-madrid-codingchallenge-2019/blob/master/metro_lines.json) |
| Trips recorded| JSON Lines file. Each line represents a single trip defined by 2 attributes: trip origin (origin) and trip destination (destination) | [List of trips recorded](https://github.com/samderdritte/amazon-madrid-codingchallenge-2019/blob/master/trip_records.jsonl) |

To calculate the most visited stations, consider that travelers take the shortest time route. In case of many possible routes taking the same time, they will take the first one following alphabetical order.


Once you calculate the top four most visited stations you can try if your solution is correct.

### Exercise 2 - Most visited Stations

You have to implement a scheduler that gives packages to a group of robots to move around the subway lines and transport the packages as efficiently as possible.
The input of that service is a map of the subway, a list of available robots and a list of packages to transport.

| Input	Format  | Description | Downloads  |
| :------------- |:-------------|:------------|
| Subway map    | JSON file with an array containing a list of subway line routes. Each line has a number as name (line) and a list of stations (stations), with the station name (name) and the time to reach that station in minutes (time). The total time required to go from one station to another in the same line is the difference between the time fields. Note that line "6" is circular, as first and last station are the same. | [Subway Map](https://github.com/samderdritte/amazon-madrid-codingchallenge-2019/blob/master/metro_lines.json) |
| Robots        | JSON Lines file. Each line defines a robot. Each robot has 3 attributes: a numeric identifier (id), the name of origin station (origin) and the number of packages that can transport (capacity) | [Robot list](https://github.com/samderdritte/amazon-madrid-codingchallenge-2019/blob/master/robots.jsonl) |
| Packages      | JSON Lines file. Each line defines a customer package with 4 attributes: a numeric identifier (id), the time when the package was available is available expressed as a number of minutes (time), the name of the origin station (origin) and destination station (destination). | [Package list](https://github.com/samderdritte/amazon-madrid-codingchallenge-2019/blob/master/orders.jsonl) |

All robot actions take one minute to execute. Pick and drop require one minute always, regardless the number of packages they have capacity for.

The delivery time is the time elapsed since the package is available (as indicated in the input file) until the package is dropped in the destination.
Robots can go back to any other station that is not the original one.
Robots can only drop a packages at their destination.


You must generate a list of actions for our fleet of robots. Each action has to be a JSON object with 3 attributes, all of them mandatory:

Time when the action should be executed expressed as a number of minutes (time)
The identifier of the robot which must execute the action (robot)
The action to execute (verb), which is a string with try possible values: pick, go, drop
If the action verb is pick or drop, there is an additional attribute:
The list of packages identifiers to pick or drop (orders)
If the action verb is go, there are two additional attributes:
The line to use (line)
The station where the robot must go (station)

An example of possible output could be:

{"time":20,"robot":0,"verb":"pick","orders":[2,205,988]}

{"time":21,"robot":0,"verb":"go","line":"4","station":"Goya"}

{"time":31,"robot":0,"verb":"drop","orders":[205,988]}

{"time":32,"robot":0,"verb":"go","line":"2","station":"Príncipe de Vergara"}

{"time":34,"robot":0,"verb":"drop","orders":[2]}

{"time":35,"robot":0,"verb":"go","line":"2","station":"Goya"}

It is important that your output meets the following criteria

Uses the field names described above.
Have all actions for the same robot sorted by time.

The goal of your algorithm must be minimizing the total time taken to deliver all the packages. 

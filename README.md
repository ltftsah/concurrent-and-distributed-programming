# Concurrent and Distributed Programming
University assignments for the Concurrent and Distributed Programming module

## Grades
Assignment 1: 84/100
Assignment 2: 69/100

## Assignment 1
You have been hired by SpaceCorpto build arobotspace explorationmission controller for at least10 simultaneousmissions. Each mission consists of boost stage, an interplanetarytransit stage,anentry/landingstageand an exploration(rover)stage. Depending on the missiontarget, each mission must be allocated variablesupplies of fuel, thrusters, instruments, control systemsand powerplants. The mission destination can be approximated as a functionof the fuel loadfor the mission(ie more fuelimplies a mission to further locations in the solar system).The transit and explorationmission stagestakesvariable amounts of timeto execute (in months), boost and landing are effectively instantevents. Each stagehas at least a 10% chance of failing. 25% of failures can be recovered from by sending a software upgrade which takes a variable number of days to develop and is a variable size in MB. There are three types of deep space communicationsnetworks(2MB/secwith 80% availability, 2KB/secwith 90% availabilityand 20bits/secwith 99.9% availability).When on a mission it is necessary for all mission components totransmit reports (telemetry ) on progressand instruments send dataon a regular basis, but this is limited by bandwidth and subject to increasing delays as the mission travels further away fromEarth. 30% of reportsrequire a command response and the mission is paused until that commandis received. Software upgrades must be transmitted from the mission controller. A variable burst of reports and commands are sentatthe transitionbetween mission stages. There are a variable number of types of commands and reports for each mission.Reports can be telemetry (100-10k bytes, frequent) or data(100k-100MB, periodic)Each missioncan be represented using threads.You must implement the methods called by the missioncontroller to constructthe missionvehicles from components, move missionsalong their stages, check for failures, sendreports, send instructionsand software updates. It is important to represent the communications networks (queues) too. A vanilla option would be to assume the following:
- The communicationnetworks for a mission are a shared resourceused by all mission components,but each mission has its own network.
- Network availabilitycan be checked before a message is to be sent and if a network is available then it can be used to transmit thefull message.
- Themission controller is ashared resourceusedfor all missions.
- Time can simulated by allowinga fixed ratioof wall clock time to mission time eg 1sec:1 month.
- When waiting a mission'sleeps'.

These specifications are for the vanilla option of the implementation.  Making use of java large-scale thread management support isessentialto securea high grade.Any form of creativity that you feel like putting in that will add interest to my marking of the project (multiple missioncontrollers, just in time supplies, Nice GUIs, reporting on the number of threads executing/number of jobs being performed, random events such as solar flaresetc) is encouraged and marks awarded for the project will reflect this.

In order to compile your java code, I should just be able to do so with java compiler with “javac <filename>“ and after it gets compiled execute it with “java <filename(executable)>”.  Please do not rely on my using an IDE for the execution of your code.Output file:The format for an output file should be something like following:“Mission Component# with (Thread ID) # makes request to network# at time # for message#.”There should be as many lines printed as are the number of requests.  You should also generate an output file output.dat to store your output.Note: You must assume that you the starting time for the first request is 0.

## Assignment 2

With web services or distributed objects (NB see notes below on allowed programming languages) you should develop a client-server application implementing an online orderingsystem with the following specification:

1. The client and server must be capable of running on 2 distinct machines connected by a network.

2. The server should manage a list of productsand orders. The products are specified in a file (along with each product’s quantity availableand restocking information). For example:


    | Product | Quantity | Restock Date |
    | -------- | -------- | -------- |
    | Gameboy     | 500     | 1     |
    | Apples     | 2500     | 11     |
    | Oranges     | 400     | 23     |

3. The server should provide the following functionality to a client:
- Login as a specific customerID
- Provide a list of projected product availability over the next 6 months (givenrestocks and current orders).
- Check the availablequantityof a productgiven a specified day and time.
- Place an order a quantity of a productfor a specified date.
- Provide a list of the customer’s current orders.
- Cancel an order.

4. The server should be able to process requests from many clients concurrently.The server must be able to be configured to drop/ignore individual messages from/to the client to illustrate failure modes.
**NB The server cannot use a database, data should be kept in filesand your code mustmanage access/locking.**

5. The client should be able to:
- Display the projected product availability over the next 6 months.
- Check the availability of a quantity of productgiven a specified date.
- Allow a user to place an orderfor a specified product, quantity and date.
- Be able to run in a test mode where many client requests are automatically generated with random delays between them.
- Deal with an unreliable server or network.

Hint: you could use RMI to implement a basic B2B orderingservice.

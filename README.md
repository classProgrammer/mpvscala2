# mpvscala2

## Exercise 2.3 e)
Q:  Trace all operations (i. e. generation and storing of measurements) of your system.
Show when and where (in which threads) operations are performed. 
A:
```text
=========== WeatherStationApp ===========
   ### WeatherStation started (thread id=24)
   ### WeatherStation started (thread id=21)
   ### WeatherStation started (thread id=23)
   ### WeatherStation started (thread id=25)
fs1 => Message Received (thread id=25)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=25)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=25)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=25)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=25)
fs2 => Message Received (thread id=21)
fs1 => THRESHOLD ELEMENTS WRITTEN (thread id=25)
fs1 => Message Received (thread id=25)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=25)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=25)
fs2 => THRESHOLD ELEMENTS WRITTEN (thread id=21)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=25)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=25)
fs2 => Message Received (thread id=21)
fs1 => THRESHOLD ELEMENTS WRITTEN (thread id=25)
fs1 => Message Received (thread id=24)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=24)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=24)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=24)
fs2 => Message Received (thread id=21)
fs1 => BUFFER FLUSHED (thread id=24)
fs1 => Message Received (thread id=24)
fs2 => THRESHOLD ELEMENTS WRITTEN (thread id=21)
fs2 => BUFFER FLUSHED (thread id=21)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=24)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=24)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=24)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=24)
fs2 => Message Received (thread id=21)
fs1 => THRESHOLD ELEMENTS WRITTEN (thread id=24)
fs1 => Message Received (thread id=24)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=24)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=24)
fs2 => THRESHOLD ELEMENTS WRITTEN (thread id=21)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=24)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=24)
fs2 => Message Received (thread id=21)
   ### WeatherStation stopped, 18 number of messages generated (thread id=18)
   ### WeatherStation stopped, 14 number of messages generated (thread id=20)
   ### WeatherStation stopped, 25 number of messages generated (thread id=23)
   ### WeatherStation stopped, 15 number of messages generated (thread id=25)
fs1 => THRESHOLD ELEMENTS WRITTEN (thread id=24)
fs1 => Message Received (thread id=19)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=19)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=19)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=19)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=19)
fs2 => THRESHOLD ELEMENTS WRITTEN (thread id=21)
fs2 => Message Received (thread id=21)
fs1 => THRESHOLD ELEMENTS WRITTEN (thread id=19)
fs1 => BUFFER FLUSHED (thread id=19)
fs1 => Message Received (thread id=19)
fs2 => BUFFER FLUSHED (thread id=21)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=19)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=19)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=19)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=19)
fs2 => Message Received (thread id=21)
fs1 => THRESHOLD ELEMENTS WRITTEN (thread id=19)
fs1 => Message Received (thread id=19)
fs2 => Message Received (thread id=21)
fs1 => Message Received (thread id=19)
fs2 => Message Received (thread id=21)
fs1 => BUFFER FLUSHED (thread id=19)
fs1 => +++ 36 messages processed (thread id=19)
fs2 => THRESHOLD ELEMENTS WRITTEN (thread id=21)
fs2 => BUFFER FLUSHED (thread id=21)
fs2 => +++ 36 messages processed (thread id=21)

Process finished with exit code 0
```

Q:
Analyze what happens if the rate at which measurements are generated is much higher than measurements can be stored

A:
Messages are delayed and possibly lost at some point. Memory capacity could be exceeded because the storage is slower than the generation of messages but it depends on what the actor system does in such a case. Old messages are read.

Q:
What can be done to mitigate this problem? Propose two possible improvements of your program. The implementation of these improvements is optional.

- Put more storage actors in round robin queue
- Assign e.g. one storage always to N weather stations
- If it is a real big delay then each weatherstation can get several file storage actors
- Adjust params for storage interval and threshold to optimize storage operation times
- In a real system => Use faster storage than to file if possible (SQLite blob storage claims to be 35% faster) when file storage is the bottleneck
- In a real system => Optimize the storage actor performance if possible


## Exercise 2.4 d)
Q:
Analyze what happens if the rate at which measurements are generated is much higher than measurements can be stored.

A:
The delay between messages leads to reading old messages. Old messages are read.
It seems that messages are lost when the source stops producing.

Q:
What is the fundamental difference between the two approaches (actor-based and
stream-based implementation)?

A:
Completely different concept. Source, Flow, Sink pattern is like writing functions. Creating Actors is like writing classes.
Actors communicate via messages, Source-Flow-Sink feels like function chaining.
Actors can be customized more easy. Actors feel like microservices and Streams more like cloud functions.
Actors need more code, are more structural because of the classes and messages. 
As far as I see it, i cannot use a stream cyclic and non cyclic at the same time like I did with the actor (e.g. store all 4 seconds cyclic and when the threashold of 7 is reached).
It seems that streams loose messages whereas with the actors all messages can be processed. But this is completely irrelevant when the system never stops => IOT shouldn't stop. 


Q:Trace all operations (i. e. generation and storing of measurements) of your system.
Show when and where (in which threads) operations are performed. 

A:
```text
========== WeatherStationStream App ========== (thread id=1)
Las Vegas:1 => (02:34:50:587,44.86714,Fahrenheit) (thread id=28)
Vienna:1 => (02:34:50:587,23.666248,Celsius) (thread id=24)
Linz:1 => (02:34:50:587,21.596184,Celsius) (thread id=22)
Dornbirn:1 => (02:34:50:587,18.64306,Celsius) (thread id=19)
--- Write 1 items to file (thread id=26)
--- Write 1 items to file (thread id=36)
--- Write 1 items to file (thread id=35)
--- Write 1 items to file (thread id=18)
Linz:2 => (02:34:50:614,21.24592,Celsius) (thread id=28)
Las Vegas:2 => (02:34:50:614,46.844467,Fahrenheit) (thread id=26)
Dornbirn:2 => (02:34:50:614,19.038,Celsius) (thread id=19)
Vienna:2 => (02:34:50:614,23.91502,Celsius) (thread id=18)
Vienna:3 => (02:34:50:645,23.333237,Celsius) (thread id=35)
Dornbirn:3 => (02:34:50:645,19.421144,Celsius) (thread id=22)
Linz:3 => (02:34:50:645,21.987877,Celsius) (thread id=19)
Las Vegas:3 => (02:34:50:645,48.935493,Fahrenheit) (thread id=18)
Vienna:4 => (02:34:50:677,22.730211,Celsius) (thread id=24)
Linz:4 => (02:34:50:677,20.257393,Celsius) (thread id=19)
Las Vegas:4 => (02:34:50:677,47.151016,Fahrenheit) (thread id=26)
Dornbirn:4 => (02:34:50:677,19.576824,Celsius) (thread id=22)
Vienna:5 => (02:34:50:692,23.36379,Celsius) (thread id=26)
Linz:5 => (02:34:50:692,20.50819,Celsius) (thread id=18)
Las Vegas:5 => (02:34:50:692,52.753216,Fahrenheit) (thread id=36)
Dornbirn:5 => (02:34:50:692,18.53541,Celsius) (thread id=22)
Las Vegas:6 => (02:34:50:723,52.145016,Fahrenheit) (thread id=26)
Linz:6 => (02:34:50:723,21.415192,Celsius) (thread id=35)
Vienna:6 => (02:34:50:723,23.693121,Celsius) (thread id=18)
Dornbirn:6 => (02:34:50:723,18.61109,Celsius) (thread id=36)
Vienna:7 => (02:34:50:756,23.265125,Celsius) (thread id=24)
Las Vegas:7 => (02:34:50:756,51.61303,Fahrenheit) (thread id=22)
Dornbirn:7 => (02:34:50:756,19.082813,Celsius) (thread id=18)
Linz:7 => (02:34:50:756,21.0726,Celsius) (thread id=19)
--- Write 1 items to file (thread id=35)
--- Write 1 items to file (thread id=22)
--- Write 1 items to file (thread id=18)
--- Write 1 items to file (thread id=36)
Vienna:8 => (02:34:50:781,22.851011,Celsius) (thread id=19)
Dornbirn:8 => (02:34:50:781,19.295656,Celsius) (thread id=18)
Linz:8 => (02:34:50:781,21.046072,Celsius) (thread id=28)
Las Vegas:8 => (02:34:50:781,51.520374,Fahrenheit) (thread id=35)
Linz:9 => (02:34:50:813,20.993162,Celsius) (thread id=24)
Dornbirn:9 => (02:34:50:813,18.88403,Celsius) (thread id=18)
Vienna:9 => (02:34:50:813,22.603252,Celsius) (thread id=28)
Las Vegas:9 => (02:34:50:813,45.075863,Fahrenheit) (thread id=19)
Dornbirn:10 => (02:34:50:845,19.576265,Celsius) (thread id=22)
Vienna:10 => (02:34:50:845,22.728996,Celsius) (thread id=35)
Las Vegas:10 => (02:34:50:845,48.774715,Fahrenheit) (thread id=19)
Linz:10 => (02:34:50:845,21.35215,Celsius) (thread id=26)
Vienna:11 => (02:34:50:877,23.854322,Celsius) (thread id=18)
Las Vegas:11 => (02:34:50:877,52.57646,Fahrenheit) (thread id=19)
Linz:11 => (02:34:50:877,20.28944,Celsius) (thread id=24)
Dornbirn:11 => (02:34:50:877,18.566605,Celsius) (thread id=36)
Las Vegas:12 => (02:34:50:893,52.059055,Fahrenheit) (thread id=22)
Linz:12 => (02:34:50:893,21.61325,Celsius) (thread id=26)
Dornbirn:12 => (02:34:50:893,18.603964,Celsius) (thread id=28)
Vienna:12 => (02:34:50:893,23.006245,Celsius) (thread id=19)
Dornbirn:13 => (02:34:50:925,19.511442,Celsius) (thread id=19)
Linz:13 => (02:34:50:925,20.477549,Celsius) (thread id=35)
Vienna:13 => (02:34:50:925,23.95157,Celsius) (thread id=28)
Las Vegas:13 => (02:34:50:925,51.398205,Fahrenheit) (thread id=18)
Vienna:14 => (02:34:50:955,22.945862,Celsius) (thread id=36)
Linz:14 => (02:34:50:955,21.882383,Celsius) (thread id=24)
Dornbirn:14 => (02:34:50:955,19.195137,Celsius) (thread id=22)
Las Vegas:14 => (02:34:50:955,47.07194,Fahrenheit) (thread id=26)
--- Write 5 items to file (thread id=24)
--- Write 5 items to file (thread id=22)
--- Write 5 items to file (thread id=26)
Linz:15 => (02:34:50:987,21.835762,Celsius) (thread id=28)
Dornbirn:15 => (02:34:50:987,18.53545,Celsius) (thread id=26)
Vienna:15 => (02:34:50:987,23.591,Celsius) (thread id=22)
Las Vegas:15 => (02:34:50:987,45.00039,Fahrenheit) (thread id=24)
--- Write 5 items to file (thread id=22)
Vienna:16 => (02:34:51:002,23.351452,Celsius) (thread id=28)
Linz:16 => (02:34:51:002,21.046427,Celsius) (thread id=35)
Dornbirn:16 => (02:34:51:002,19.176575,Celsius) (thread id=18)
Las Vegas:16 => (02:34:51:002,42.759136,Fahrenheit) (thread id=26)
Dornbirn:17 => (02:34:51:034,18.629778,Celsius) (thread id=19)
Linz:17 => (02:34:51:034,20.591167,Celsius) (thread id=36)
Las Vegas:17 => (02:34:51:034,45.945194,Fahrenheit) (thread id=18)
Vienna:17 => (02:34:51:034,22.797274,Celsius) (thread id=22)
Linz:18 => (02:34:51:065,20.692467,Celsius) (thread id=22)
Las Vegas:18 => (02:34:51:065,47.974937,Fahrenheit) (thread id=28)
Vienna:18 => (02:34:51:065,23.672337,Celsius) (thread id=35)
Dornbirn:18 => (02:34:51:065,18.867548,Celsius) (thread id=24)
Dornbirn:19 => (02:34:51:097,19.186232,Celsius) (thread id=26)
Linz:19 => (02:34:51:097,21.437588,Celsius) (thread id=35)
Las Vegas:19 => (02:34:51:097,49.800323,Fahrenheit) (thread id=36)
Vienna:19 => (02:34:51:097,23.767607,Celsius) (thread id=18)
Dornbirn:20 => (02:34:51:112,18.503801,Celsius) (thread id=24)
Vienna:20 => (02:34:51:112,22.728283,Celsius) (thread id=35)
Linz:20 => (02:34:51:112,21.832727,Celsius) (thread id=19)
Las Vegas:20 => (02:34:51:112,45.95355,Fahrenheit) (thread id=28)
Dornbirn:21 => (02:34:51:143,18.515278,Celsius) (thread id=18)
Linz:21 => (02:34:51:143,21.831484,Celsius) (thread id=28)
Vienna:21 => (02:34:51:143,22.736135,Celsius) (thread id=26)
Las Vegas:21 => (02:34:51:143,45.131413,Fahrenheit) (thread id=24)
--- Write 7 items to file (thread id=26)
--- Write 7 items to file (thread id=22)
--- Write 7 items to file (thread id=35)
Linz:22 => (02:34:51:176,22.069937,Celsius) (thread id=28)
--- Write 7 items to file (thread id=36)
Vienna:22 => (02:34:51:176,22.950226,Celsius) (thread id=19)
Dornbirn:22 => (02:34:51:175,18.816416,Celsius) (thread id=18)
Las Vegas:22 => (02:34:51:175,41.418648,Fahrenheit) (thread id=24)
Las Vegas:23 => (02:34:51:197,43.127716,Fahrenheit) (thread id=22)
Dornbirn:23 => (02:34:51:197,19.200102,Celsius) (thread id=35)
Vienna:23 => (02:34:51:202,23.594175,Celsius) (thread id=22)
Linz:23 => (02:34:51:202,21.797598,Celsius) (thread id=18)
Las Vegas:24 => (02:34:51:212,45.69645,Fahrenheit) (thread id=22)
Dornbirn:24 => (02:34:51:212,19.500452,Celsius) (thread id=35)
Vienna:24 => (02:34:51:233,23.500834,Celsius) (thread id=22)
Linz:24 => (02:34:51:233,22.03622,Celsius) (thread id=35)
Dornbirn:25 => (02:34:51:243,18.903797,Celsius) (thread id=24)
Las Vegas:25 => (02:34:51:243,43.085953,Fahrenheit) (thread id=18)
Vienna:25 => (02:34:51:264,24.073534,Celsius) (thread id=18)
Linz:25 => (02:34:51:264,20.329655,Celsius) (thread id=22)
Dornbirn:26 => (02:34:51:274,19.589245,Celsius) (thread id=22)
Las Vegas:26 => (02:34:51:274,41.615833,Fahrenheit) (thread id=35)
Linz:26 => (02:34:51:295,20.578455,Celsius) (thread id=18)
Vienna:26 => (02:34:51:295,22.98664,Celsius) (thread id=35)
Las Vegas:27 => (02:34:51:306,45.38376,Fahrenheit) (thread id=22)
Dornbirn:27 => (02:34:51:306,19.160294,Celsius) (thread id=24)
Linz:27 => (02:34:51:326,20.354883,Celsius) (thread id=35)
Vienna:27 => (02:34:51:326,23.93869,Celsius) (thread id=18)
Las Vegas:28 => (02:34:51:336,50.842133,Fahrenheit) (thread id=18)
Dornbirn:28 => (02:34:51:336,18.822584,Celsius) (thread id=24)
Linz:28 => (02:34:51:358,21.052238,Celsius) (thread id=35)
Vienna:28 => (02:34:51:358,23.033676,Celsius) (thread id=36)
Las Vegas:29 => (02:34:51:361,47.458984,Fahrenheit) (thread id=35)
Dornbirn:29 => (02:34:51:361,18.78146,Celsius) (thread id=24)
--- Write 7 items to file (thread id=24)
--- Write 7 items to file (thread id=18)
--- Write 7 items to file (thread id=26)
Linz:29 => (02:34:51:372,21.097284,Celsius) (thread id=22)
Vienna:29 => (02:34:51:372,23.390936,Celsius) (thread id=35)
Dornbirn:30 => (02:34:51:382,19.174023,Celsius) (thread id=35)
--- Write 7 items to file (thread id=18)
Las Vegas:30 => (02:34:51:382,41.32292,Fahrenheit) (thread id=24)
Vienna:30 => (02:34:51:402,23.647917,Celsius) (thread id=18)
Linz:30 => (02:34:51:402,20.544601,Celsius) (thread id=35)
Las Vegas:31 => (02:34:51:413,52.7246,Fahrenheit) (thread id=26)
Dornbirn:31 => (02:34:51:413,19.413116,Celsius) (thread id=18)
Linz:31 => (02:34:51:434,21.949505,Celsius) (thread id=18)
Vienna:31 => (02:34:51:434,23.776386,Celsius) (thread id=24)
Dornbirn:32 => (02:34:51:444,19.615843,Celsius) (thread id=35)
Las Vegas:32 => (02:34:51:444,51.467438,Fahrenheit) (thread id=26)
Vienna:32 => (02:34:51:464,23.272552,Celsius) (thread id=22)
Linz:32 => (02:34:51:464,20.211502,Celsius) (thread id=18)
Las Vegas:33 => (02:34:51:475,46.300644,Fahrenheit) (thread id=35)
Dornbirn:33 => (02:34:51:475,19.073942,Celsius) (thread id=26)
Linz:33 => (02:34:51:496,20.43982,Celsius) (thread id=22)
Vienna:33 => (02:34:51:496,22.913418,Celsius) (thread id=35)
Las Vegas:34 => (02:34:51:507,41.462887,Fahrenheit) (thread id=18)
Dornbirn:34 => (02:34:51:507,18.564701,Celsius) (thread id=26)
Linz:34 => (02:34:51:522,21.496342,Celsius) (thread id=22)
Vienna:34 => (02:34:51:522,23.153704,Celsius) (thread id=26)
Las Vegas:35 => (02:34:51:532,46.041553,Fahrenheit) (thread id=35)
Dornbirn:35 => (02:34:51:532,19.501183,Celsius) (thread id=18)
Vienna:35 => (02:34:51:542,23.773256,Celsius) (thread id=22)
Linz:35 => (02:34:51:542,22.01336,Celsius) (thread id=26)
Dornbirn:36 => (02:34:51:563,18.967611,Celsius) (thread id=26)
Las Vegas:36 => (02:34:51:563,51.57367,Fahrenheit) (thread id=22)
--- Write 7 items to file (thread id=24)
--- Write 7 items to file (thread id=35)
Vienna:36 => (02:34:51:574,23.194138,Celsius) (thread id=26)
Linz:36 => (02:34:51:574,22.131397,Celsius) (thread id=18)
--- Write 7 items to file (thread id=18)
--- Write 7 items to file (thread id=24)
Las Vegas:37 => (02:34:51:594,44.827023,Fahrenheit) (thread id=35)
Dornbirn:37 => (02:34:51:594,19.476046,Celsius) (thread id=24)
Vienna:37 => (02:34:51:605,23.325233,Celsius) (thread id=24)
Linz:37 => (02:34:51:605,21.469555,Celsius) (thread id=26)
Dornbirn:38 => (02:34:51:627,19.38155,Celsius) (thread id=24)
Las Vegas:38 => (02:34:51:627,49.278446,Fahrenheit) (thread id=18)
Linz:38 => (02:34:51:637,22.047216,Celsius) (thread id=19)
Vienna:38 => (02:34:51:637,22.62087,Celsius) (thread id=35)
Dornbirn:39 => (02:34:51:652,18.550423,Celsius) (thread id=26)
Linz:39 => (02:34:51:652,21.13334,Celsius) (thread id=22)
Las Vegas:39 => (02:34:51:652,52.796143,Fahrenheit) (thread id=28)
Vienna:39 => (02:34:51:652,24.045284,Celsius) (thread id=24)
Las Vegas:40 => (02:34:51:684,52.630302,Fahrenheit) (thread id=26)
Dornbirn:40 => (02:34:51:684,18.487312,Celsius) (thread id=19)
Linz:40 => (02:34:51:684,21.743996,Celsius) (thread id=36)
Vienna:40 => (02:34:51:684,23.552868,Celsius) (thread id=18)
Vienna:41 => (02:34:51:716,22.740866,Celsius) (thread id=35)
Las Vegas:41 => (02:34:51:716,49.32256,Fahrenheit) (thread id=36)
Dornbirn:41 => (02:34:51:716,19.047852,Celsius) (thread id=24)
Linz:41 => (02:34:51:716,21.735638,Celsius) (thread id=26)
Vienna:42 => (02:34:51:742,23.90292,Celsius) (thread id=28)
Las Vegas:42 => (02:34:51:742,47.041405,Fahrenheit) (thread id=22)
Dornbirn:42 => (02:34:51:742,19.415195,Celsius) (thread id=19)
Linz:42 => (02:34:51:742,21.666119,Celsius) (thread id=18)
--- Write 7 items to file (thread id=22)
Dornbirn:43 => (02:34:51:773,19.619873,Celsius) (thread id=26)
Las Vegas:43 => (02:34:51:773,45.057053,Fahrenheit) (thread id=35)
--- Write 7 items to file (thread id=24)
Linz:43 => (02:34:51:773,22.043139,Celsius) (thread id=18)
Vienna:43 => (02:34:51:773,23.773535,Celsius) (thread id=19)
--- Write 7 items to file (thread id=22)
--- Write 7 items to file (thread id=18)
Las Vegas:44 => (02:34:51:805,50.021694,Fahrenheit) (thread id=35)
Linz:44 => (02:34:51:805,22.103687,Celsius) (thread id=24)
Vienna:44 => (02:34:51:805,23.96082,Celsius) (thread id=26)
Dornbirn:44 => (02:34:51:805,19.649021,Celsius) (thread id=36)
Vienna:45 => (02:34:51:836,22.899937,Celsius) (thread id=18)
Dornbirn:45 => (02:34:51:836,18.843601,Celsius) (thread id=36)
Las Vegas:45 => (02:34:51:836,46.97784,Fahrenheit) (thread id=28)
Linz:45 => (02:34:51:836,21.01516,Celsius) (thread id=19)
Las Vegas:46 => (02:34:51:867,45.978317,Fahrenheit) (thread id=22)
Dornbirn:46 => (02:34:51:867,18.50698,Celsius) (thread id=36)
Linz:46 => (02:34:51:867,21.295,Celsius) (thread id=26)
Vienna:46 => (02:34:51:867,23.327177,Celsius) (thread id=28)
Las Vegas:47 => (02:34:51:882,44.96839,Fahrenheit) (thread id=18)
Dornbirn:47 => (02:34:51:882,19.625406,Celsius) (thread id=26)
Linz:47 => (02:34:51:882,21.652138,Celsius) (thread id=24)
Vienna:47 => (02:34:51:882,23.267721,Celsius) (thread id=35)
Linz:48 => (02:34:51:902,21.378742,Celsius) (thread id=35)
Dornbirn:48 => (02:34:51:902,18.682804,Celsius) (thread id=24)
Las Vegas:48 => (02:34:51:902,44.807453,Fahrenheit) (thread id=18)
Vienna:48 => (02:34:51:902,23.976933,Celsius) (thread id=36)
Linz:49 => (02:34:51:933,21.886492,Celsius) (thread id=19)
Vienna:49 => (02:34:51:933,23.67412,Celsius) (thread id=18)
Las Vegas:49 => (02:34:51:933,40.797226,Fahrenheit) (thread id=36)
Dornbirn:49 => (02:34:51:933,19.371674,Celsius) (thread id=26)
Linz:50 => (02:34:51:965,21.65435,Celsius) (thread id=19)
Las Vegas:50 => (02:34:51:965,43.712723,Fahrenheit) (thread id=22)
Dornbirn:50 => (02:34:51:965,19.000319,Celsius) (thread id=18)
Vienna:50 => (02:34:51:965,23.351572,Celsius) (thread id=28)
--- Write 7 items to file (thread id=22)
--- Write 7 items to file (thread id=19)
--- Write 7 items to file (thread id=19)
--- Write 7 items to file (thread id=18)
Linz:51 => (02:34:51:996,22.024015,Celsius) (thread id=35)
Dornbirn:51 => (02:34:51:996,18.984312,Celsius) (thread id=24)
Vienna:51 => (02:34:51:996,22.791231,Celsius) (thread id=26)
Las Vegas:51 => (02:34:51:996,48.768044,Fahrenheit) (thread id=36)
Las Vegas:52 => (02:34:52:022,43.479053,Fahrenheit) (thread id=35)
Vienna:52 => (02:34:52:022,22.663193,Celsius) (thread id=22)
Linz:52 => (02:34:52:022,22.093283,Celsius) (thread id=26)
Dornbirn:52 => (02:34:52:022,19.170334,Celsius) (thread id=28)
Vienna:53 => (02:34:52:042,23.020483,Celsius) (thread id=22)
Dornbirn:53 => (02:34:52:042,18.722534,Celsius) (thread id=19)
Las Vegas:53 => (02:34:52:042,45.822945,Fahrenheit) (thread id=24)
Linz:53 => (02:34:52:042,21.081804,Celsius) (thread id=35)
Linz:54 => (02:34:52:073,20.46046,Celsius) (thread id=18)
Dornbirn:54 => (02:34:52:073,18.700241,Celsius) (thread id=22)
Las Vegas:54 => (02:34:52:073,47.936234,Fahrenheit) (thread id=19)
Vienna:54 => (02:34:52:073,23.425169,Celsius) (thread id=36)
Dornbirn:55 => (02:34:52:106,19.217545,Celsius) (thread id=24)
Vienna:55 => (02:34:52:106,23.416668,Celsius) (thread id=35)
Las Vegas:55 => (02:34:52:106,49.82869,Fahrenheit) (thread id=22)
Linz:55 => (02:34:52:106,20.962456,Celsius) (thread id=18)
Dornbirn:56 => (02:34:52:138,18.52538,Celsius) (thread id=35)
Vienna:56 => (02:34:52:138,24.095259,Celsius) (thread id=24)
Las Vegas:56 => (02:34:52:138,47.78123,Fahrenheit) (thread id=36)
Linz:56 => (02:34:52:138,21.393131,Celsius) (thread id=26)
Linz:57 => (02:34:52:152,21.569603,Celsius) (thread id=35)
Las Vegas:57 => (02:34:52:152,47.995197,Fahrenheit) (thread id=22)
Dornbirn:57 => (02:34:52:152,18.638908,Celsius) (thread id=36)
Vienna:57 => (02:34:52:152,23.228956,Celsius) (thread id=28)
--- Write 7 items to file (thread id=18)
--- Write 7 items to file (thread id=19)
--- Write 7 items to file (thread id=28)
--- Write 7 items to file (thread id=28)
Vienna:58 => (02:34:52:183,23.163534,Celsius) (thread id=19)
Linz:58 => (02:34:52:183,20.512955,Celsius) (thread id=36)
Las Vegas:58 => (02:34:52:183,52.32391,Fahrenheit) (thread id=18)
Dornbirn:58 => (02:34:52:183,19.493742,Celsius) (thread id=24)
Linz:59 => (02:34:52:214,20.44425,Celsius) (thread id=28)
Las Vegas:59 => (02:34:52:214,45.425083,Fahrenheit) (thread id=35)
Vienna:59 => (02:34:52:214,23.552591,Celsius) (thread id=36)
Dornbirn:59 => (02:34:52:214,18.772266,Celsius) (thread id=26)
Las Vegas:60 => (02:34:52:244,44.237007,Fahrenheit) (thread id=18)
Linz:60 => (02:34:52:244,20.711323,Celsius) (thread id=22)
Vienna:60 => (02:34:52:244,23.070044,Celsius) (thread id=28)
Dornbirn:60 => (02:34:52:244,18.918772,Celsius) (thread id=19)
Linz:61 => (02:34:52:275,20.339005,Celsius) (thread id=22)
Dornbirn:61 => (02:34:52:275,19.183945,Celsius) (thread id=28)
Las Vegas:61 => (02:34:52:275,45.24279,Fahrenheit) (thread id=19)
Vienna:61 => (02:34:52:275,22.905167,Celsius) (thread id=18)
Vienna:62 => (02:34:52:306,23.88635,Celsius) (thread id=26)
Dornbirn:62 => (02:34:52:306,18.495586,Celsius) (thread id=36)
Las Vegas:62 => (02:34:52:306,48.644962,Fahrenheit) (thread id=18)
Linz:62 => (02:34:52:306,20.561392,Celsius) (thread id=28)
Linz:63 => (02:34:52:338,20.732582,Celsius) (thread id=24)
Las Vegas:63 => (02:34:52:338,52.489697,Fahrenheit) (thread id=19)
Vienna:63 => (02:34:52:338,23.910423,Celsius) (thread id=35)
Dornbirn:63 => (02:34:52:338,19.004717,Celsius) (thread id=28)
Vienna:64 => (02:34:52:351,23.773571,Celsius) (thread id=36)
Linz:64 => (02:34:52:351,20.267326,Celsius) (thread id=24)
Dornbirn:64 => (02:34:52:351,18.837694,Celsius) (thread id=35)
Las Vegas:64 => (02:34:52:351,40.907536,Fahrenheit) (thread id=26)
--- Write 7 items to file (thread id=19)
--- Write 7 items to file (thread id=22)
Vienna:65 => (02:34:52:372,23.768806,Celsius) (thread id=36)
Linz:65 => (02:34:52:372,20.6826,Celsius) (thread id=35)
--- Write 7 items to file (thread id=19)
--- Write 7 items to file (thread id=24)
Linz:66 => (02:34:52:403,22.147345,Celsius) (thread id=26)
Vienna:66 => (02:34:52:403,23.090412,Celsius) (thread id=35)
Dornbirn:65 => (02:34:52:403,18.969099,Celsius) (thread id=22)
Las Vegas:65 => (02:34:52:403,45.109066,Fahrenheit) (thread id=18)
Dornbirn:66 => (02:34:52:434,19.331465,Celsius) (thread id=19)
Las Vegas:66 => (02:34:52:434,44.972782,Fahrenheit) (thread id=24)
Vienna:67 => (02:34:52:434,23.241743,Celsius) (thread id=26)
Linz:67 => (02:34:52:434,20.72403,Celsius) (thread id=35)
Las Vegas:67 => (02:34:52:465,48.274624,Fahrenheit) (thread id=28)
Dornbirn:67 => (02:34:52:465,18.967014,Celsius) (thread id=35)
Vienna:68 => (02:34:52:465,23.904974,Celsius) (thread id=22)
Linz:68 => (02:34:52:465,21.19706,Celsius) (thread id=19)
Vienna:69 => (02:34:52:497,23.004261,Celsius) (thread id=28)
Las Vegas:68 => (02:34:52:497,43.2415,Fahrenheit) (thread id=19)
Linz:69 => (02:34:52:497,20.899452,Celsius) (thread id=24)
Dornbirn:68 => (02:34:52:497,19.253862,Celsius) (thread id=18)
Dornbirn:69 => (02:34:52:512,19.046656,Celsius) (thread id=36)
Linz:70 => (02:34:52:512,21.558065,Celsius) (thread id=22)
Vienna:70 => (02:34:52:512,23.515089,Celsius) (thread id=35)
Las Vegas:69 => (02:34:52:512,46.86442,Fahrenheit) (thread id=26)
Linz:71 => (02:34:52:543,21.789957,Celsius) (thread id=35)
Las Vegas:70 => (02:34:52:543,48.934795,Fahrenheit) (thread id=24)
Vienna:71 => (02:34:52:543,23.052156,Celsius) (thread id=26)
Dornbirn:70 => (02:34:52:543,18.58958,Celsius) (thread id=28)
Dornbirn:71 => (02:34:52:575,18.511988,Celsius) (thread id=22)
--- Write 7 items to file (thread id=18)
--- Write 7 items to file (thread id=24)
Linz:72 => (02:34:52:575,21.694569,Celsius) (thread id=28)
Las Vegas:71 => (02:34:52:575,41.544746,Fahrenheit) (thread id=26)
Vienna:72 => (02:34:52:575,24.024456,Celsius) (thread id=35)
--- Write 7 items to file (thread id=24)
--- Write 7 items to file (thread id=26)
Linz:73 => (02:34:52:606,20.745266,Celsius) (thread id=35)
Dornbirn:72 => (02:34:52:606,18.756813,Celsius) (thread id=26)
Las Vegas:72 => (02:34:52:606,43.138317,Fahrenheit) (thread id=28)
Vienna:73 => (02:34:52:606,24.054064,Celsius) (thread id=18)
Vienna:74 => (02:34:52:636,23.940865,Celsius) (thread id=36)
Linz:74 => (02:34:52:636,21.176353,Celsius) (thread id=26)
Dornbirn:73 => (02:34:52:636,18.532408,Celsius) (thread id=24)
Las Vegas:73 => (02:34:52:636,41.903652,Fahrenheit) (thread id=28)
Vienna:75 => (02:34:52:662,23.256384,Celsius) (thread id=19)
Las Vegas:74 => (02:34:52:662,50.870415,Fahrenheit) (thread id=28)
Linz:75 => (02:34:52:662,21.67271,Celsius) (thread id=22)
Dornbirn:74 => (02:34:52:662,18.941961,Celsius) (thread id=24)
Vienna:76 => (02:34:52:693,22.890375,Celsius) (thread id=18)
Las Vegas:75 => (02:34:52:693,44.736786,Fahrenheit) (thread id=24)
Linz:76 => (02:34:52:693,20.650957,Celsius) (thread id=22)
Dornbirn:75 => (02:34:52:693,18.613815,Celsius) (thread id=26)
Dornbirn:76 => (02:34:52:724,19.137035,Celsius) (thread id=24)
Vienna:77 => (02:34:52:724,22.754429,Celsius) (thread id=28)
Linz:77 => (02:34:52:731,21.728394,Celsius) (thread id=22)
Las Vegas:76 => (02:34:52:731,43.06058,Fahrenheit) (thread id=35)
Las Vegas:77 => (02:34:52:756,52.920715,Fahrenheit) (thread id=26)
Dornbirn:77 => (02:34:52:756,19.282148,Celsius) (thread id=35)
Linz:78 => (02:34:52:756,21.51991,Celsius) (thread id=18)
Vienna:78 => (02:34:52:756,22.590408,Celsius) (thread id=22)
--- Write 7 items to file (thread id=19)
--- Write 7 items to file (thread id=18)
--- Write 7 items to file (thread id=36)
--- Write 7 items to file (thread id=24)
Las Vegas:78 => (02:34:52:782,51.4335,Fahrenheit) (thread id=36)
Linz:79 => (02:34:52:782,21.972109,Celsius) (thread id=18)
Dornbirn:78 => (02:34:52:782,19.509161,Celsius) (thread id=26)
Vienna:79 => (02:34:52:782,23.02611,Celsius) (thread id=19)
Dornbirn:79 => (02:34:52:813,19.678621,Celsius) (thread id=19)
Linz:80 => (02:34:52:813,20.956396,Celsius) (thread id=36)
Vienna:80 => (02:34:52:813,22.833363,Celsius) (thread id=22)
Las Vegas:79 => (02:34:52:813,48.49173,Fahrenheit) (thread id=28)
Vienna:81 => (02:34:52:835,23.106758,Celsius) (thread id=19)
Dornbirn:80 => (02:34:52:835,19.09314,Celsius) (thread id=28)
Linz:81 => (02:34:52:835,20.982147,Celsius) (thread id=18)
Las Vegas:80 => (02:34:52:835,50.997326,Fahrenheit) (thread id=22)
Dornbirn:81 => (02:34:52:865,19.585981,Celsius) (thread id=26)
Vienna:82 => (02:34:52:865,23.11031,Celsius) (thread id=35)
Las Vegas:81 => (02:34:52:865,46.24218,Fahrenheit) (thread id=19)
Linz:82 => (02:34:52:865,20.442015,Celsius) (thread id=18)
Linz:83 => (02:34:52:896,21.435389,Celsius) (thread id=36)
Las Vegas:82 => (02:34:52:896,41.51487,Fahrenheit) (thread id=26)
Dornbirn:82 => (02:34:52:896,18.79893,Celsius) (thread id=24)
Vienna:83 => (02:34:52:896,23.81006,Celsius) (thread id=19)
Dornbirn:83 => (02:34:52:922,18.661903,Celsius) (thread id=28)
Vienna:84 => (02:34:52:922,23.94751,Celsius) (thread id=36)
Linz:84 => (02:34:52:922,21.615255,Celsius) (thread id=18)
Las Vegas:83 => (02:34:52:922,48.17257,Fahrenheit) (thread id=24)
Dornbirn:84 => (02:34:52:953,18.717459,Celsius) (thread id=24)
Vienna:85 => (02:34:52:953,23.730251,Celsius) (thread id=28)
Linz:85 => (02:34:52:953,21.709538,Celsius) (thread id=35)
Las Vegas:84 => (02:34:52:953,51.295883,Fahrenheit) (thread id=18)
--- Write 7 items to file (thread id=22)
--- Write 7 items to file (thread id=19)
--- Write 7 items to file (thread id=26)
Dornbirn:85 => (02:34:52:985,18.969845,Celsius) (thread id=26)
--- Write 7 items to file (thread id=24)
Las Vegas:85 => (02:34:52:985,52.41886,Fahrenheit) (thread id=22)
Vienna:86 => (02:34:52:985,23.426949,Celsius) (thread id=18)
Linz:86 => (02:34:52:985,20.6051,Celsius) (thread id=19)
Dornbirn:86 => (02:34:53:016,19.363192,Celsius) (thread id=18)
Las Vegas:86 => (02:34:53:016,49.657715,Fahrenheit) (thread id=19)
Vienna:87 => (02:34:53:016,22.702452,Celsius) (thread id=28)
Linz:87 => (02:34:53:016,22.060638,Celsius) (thread id=24)
Dornbirn:87 => (02:34:53:047,18.956549,Celsius) (thread id=24)
Vienna:88 => (02:34:53:047,23.057564,Celsius) (thread id=35)
Linz:88 => (02:34:53:047,22.139837,Celsius) (thread id=28)
Las Vegas:87 => (02:34:53:047,41.159958,Fahrenheit) (thread id=19)
Las Vegas:88 => (02:34:53:071,50.688435,Fahrenheit) (thread id=28)
Linz:89 => (02:34:53:071,21.261444,Celsius) (thread id=24)
Vienna:89 => (02:34:53:071,22.56188,Celsius) (thread id=18)
Dornbirn:88 => (02:34:53:071,19.67884,Celsius) (thread id=19)
Las Vegas:89 => (02:34:53:091,51.029743,Fahrenheit) (thread id=19)
Dornbirn:89 => (02:34:53:091,18.673134,Celsius) (thread id=24)
Linz:90 => (02:34:53:091,20.3893,Celsius) (thread id=36)
Vienna:90 => (02:34:53:091,23.738365,Celsius) (thread id=22)
Las Vegas:90 => (02:34:53:123,45.060123,Fahrenheit) (thread id=18)
Dornbirn:90 => (02:34:53:123,19.448637,Celsius) (thread id=22)
Vienna:91 => (02:34:53:123,23.949545,Celsius) (thread id=36)
Linz:91 => (02:34:53:123,20.776533,Celsius) (thread id=35)
Linz:92 => (02:34:53:155,20.270063,Celsius) (thread id=35)
Dornbirn:91 => (02:34:53:155,18.632729,Celsius) (thread id=19)
Vienna:92 => (02:34:53:155,22.781403,Celsius) (thread id=26)
Las Vegas:91 => (02:34:53:155,47.298733,Fahrenheit) (thread id=18)
--- Write 7 items to file (thread id=35)
--- Write 7 items to file (thread id=19)
Linz:93 => (02:34:53:188,22.13327,Celsius) (thread id=19)
Vienna:93 => (02:34:53:188,23.248552,Celsius) (thread id=28)
--- Write 7 items to file (thread id=36)
--- Write 7 items to file (thread id=24)
Dornbirn:92 => (02:34:53:188,19.097263,Celsius) (thread id=26)
Las Vegas:92 => (02:34:53:188,50.166504,Fahrenheit) (thread id=18)
Dornbirn:93 => (02:34:53:202,19.19954,Celsius) (thread id=36)
Las Vegas:93 => (02:34:53:202,50.940372,Fahrenheit) (thread id=24)
Vienna:94 => (02:34:53:202,22.818792,Celsius) (thread id=35)
Linz:94 => (02:34:53:202,21.053883,Celsius) (thread id=18)
Dornbirn:94 => (02:34:53:234,19.55905,Celsius) (thread id=28)
Linz:95 => (02:34:53:234,21.019701,Celsius) (thread id=24)
Las Vegas:94 => (02:34:53:234,51.87008,Fahrenheit) (thread id=35)
Vienna:95 => (02:34:53:234,23.058733,Celsius) (thread id=26)
Linz:96 => (02:34:53:267,21.11878,Celsius) (thread id=35)
Vienna:96 => (02:34:53:267,23.407442,Celsius) (thread id=28)
Dornbirn:95 => (02:34:53:267,19.398314,Celsius) (thread id=18)
Las Vegas:95 => (02:34:53:267,42.38601,Fahrenheit) (thread id=22)
Linz:97 => (02:34:53:281,20.764332,Celsius) (thread id=22)
Dornbirn:96 => (02:34:53:281,19.416859,Celsius) (thread id=35)
Vienna:97 => (02:34:53:281,22.641642,Celsius) (thread id=24)
Las Vegas:96 => (02:34:53:281,41.955795,Fahrenheit) (thread id=26)
Dornbirn:97 => (02:34:53:313,18.918312,Celsius) (thread id=24)
Vienna:98 => (02:34:53:313,23.195055,Celsius) (thread id=19)
Linz:98 => (02:34:53:313,21.535032,Celsius) (thread id=26)
Las Vegas:97 => (02:34:53:313,51.60098,Fahrenheit) (thread id=22)
Vienna:99 => (02:34:53:345,22.631828,Celsius) (thread id=19)
Dornbirn:98 => (02:34:53:345,19.101843,Celsius) (thread id=22)
Linz:99 => (02:34:53:345,21.283186,Celsius) (thread id=24)
Las Vegas:98 => (02:34:53:345,50.24749,Fahrenheit) (thread id=26)
--- Write 7 items to file (thread id=24)
--- Write 7 items to file (thread id=19)
Las Vegas:99 => (02:34:53:376,47.909912,Fahrenheit) (thread id=35)
Linz:100 => (02:34:53:376,21.658035,Celsius) (thread id=22)
--- Write 7 items to file (thread id=28)
--- Write 7 items to file (thread id=18)
Dornbirn:99 => (02:34:53:376,19.420782,Celsius) (thread id=24)
Vienna:100 => (02:34:53:377,23.91234,Celsius) (thread id=26)
========== END WeatherStationStream App ========== (thread id=1)
Dornbirn:100 => (02:34:53:407,18.824406,Celsius) (thread id=28)
Las Vegas:100 => (02:34:53:407,51.048134,Fahrenheit) (thread id=22)

Process finished with exit code 0

```

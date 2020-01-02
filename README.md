# mpvscala2

## Exercise 2.1 a)
```text
=========== SimpleMessageExchange App ===========
   === MessageConsumer.received message: hi at 04:01:45
   === MessageConsumer.received message: sup at 04:01:45
   === MessageConsumer.received message: bye at 04:01:45
   === MessageConsumer.received message: hi delayed at 04:01:46
   === MessageConsumer.received message: sup delayed at 04:01:47
   === MessageConsumer.received message: bye delayed at 04:01:48

Process finished with exit code 0
```

## Exercise 2.1 b)
```text
=========== AtLeastOnceApp ===========
   +++ send message: 'h1'
   +++ send message: 'h0w w45 y0ur d4y'
   +++ send message: 'm1n3 w45 0k'
   +++ send message: 'n1c3 2 h34r'
   +++ send message: 'by3'
   === UnreliableConsumer PROCESSED: 'h1' @ 04:01:21
   === UnreliableConsumer PROCESSED: 'h0w w45 y0ur d4y' @ 04:01:21
   === UnreliableConsumer PROCESSED: 'm1n3 w45 0k' @ 04:01:21
   === UnreliableConsumer PROCESSED: 'n1c3 2 h34r' @ 04:01:21
   === UnreliableConsumer PROCESSED: 'by3' @ 04:01:21
   *** message: 'h1' : CONFIRMED
   *** message: 'h0w w45 y0ur d4y' : CONFIRMED
   *** message: 'm1n3 w45 0k' : CONFIRMED
   *** message: 'by3' : CONFIRMED
   +++ send message: 'n1c3 2 h34r'
   === UnreliableConsumer PROCESSED: 'n1c3 2 h34r' @ 04:01:22
   +++ send message: 'n1c3 2 h34r'
   === UnreliableConsumer PROCESSED: 'n1c3 2 h34r' @ 04:01:22
   *** message: 'n1c3 2 h34r' : CONFIRMED
TERMINATING: All messages confirmed

Process finished with exit code 0
```

## Exercise 2.1 c)
```text
=========== ExactlyOnceApp ===========
   +++ send message: 'h1'
   +++ send message: 'h0w w45 y0ur d4y'
   +++ send message: 'n1c3 2 h34r'
   +++ send message: 'by3'
   +++ send message: 'm1n3 w45 0k'
   === UnreliableConsumer PROCESSED: 'h1' @ 03:58:10
   === UnreliableConsumer PROCESSED: 'h0w w45 y0ur d4y' @ 03:58:10
   === UnreliableConsumer PROCESSED: 'n1c3 2 h34r' @ 03:58:10
   === UnreliableConsumer PROCESSED: 'by3' @ 03:58:10
   *** message: 'h1' : CONFIRMED
   === UnreliableConsumer PROCESSED: 'm1n3 w45 0k' @ 03:58:10
   *** message: 'n1c3 2 h34r' : CONFIRMED
   *** message: 'by3' : CONFIRMED
   *** message: 'm1n3 w45 0k' : CONFIRMED
   +++ send message: 'h0w w45 y0ur d4y'
   *** message: 'h0w w45 y0ur d4y' : CONFIRMED
TERMINATING: All messages confirmed

Process finished with exit code 0
```

## Exercise 2.1 d)
```text
=========== ExactlyOnceAppRetryLimit ===========
   +++ send message: 'h1'
   +++ send message: 'h0w w45 y0ur d4y'
   +++ send message: 'n1c3 2 h34r'
   +++ send message: 'm1n3 w45 0k'
   +++ send message: 'by3'
   === UnreliableConsumer PROCESSED: 'h1' @ 03:55:52
   === UnreliableConsumer PROCESSED: 'by3' @ 03:55:52
   === UnreliableConsumer PROCESSED: 'm1n3 w45 0k' @ 03:55:52
   === UnreliableConsumer PROCESSED: 'n1c3 2 h34r' @ 03:55:52
   === UnreliableConsumer PROCESSED: 'h0w w45 y0ur d4y' @ 03:55:52
   *** message: 'h1' : CONFIRMED
   +++ send message: 'by3'
   +++ send message: 'n1c3 2 h34r'
   +++ send message: 'h0w w45 y0ur d4y'
   +++ send message: 'm1n3 w45 0k'
   +++ send message: 'by3'
   +++ send message: 'n1c3 2 h34r'
   +++ send message: 'h0w w45 y0ur d4y'
   +++ send message: 'm1n3 w45 0k'
   --- RETRY LIMIT REACHED: 'n1c3 2 h34r' : DISCARDED
   --- RETRY LIMIT REACHED: 'm1n3 w45 0k' : DISCARDED
   --- RETRY LIMIT REACHED: 'h0w w45 y0ur d4y' : DISCARDED
   --- RETRY LIMIT REACHED: 'by3' : DISCARDED
TERMINATING: All messages processed or discarded

Process finished with exit code 0
```

## Exercise 2.2
```text
========== AskApp ==========
=== Test Success Case ===
MyAskActor => created
MyAskActor => sent message to target
SuccessActor => start work
SuccessActor => finished work
MyAskActor => received 'Message()'
SUCCESS received: 'Message()'
=== END Test Success Case ===
=== Test Failure Case ===
MyAskActor => created
MyAskActor => sent message to target
FailActor => start work
MyAskActor => Timeout Received
!!!FAILED: java.util.concurrent.TimeoutException: MyAskActor => Timeout received !!!
=== END Test Failure Case ===
FailActor => finished work
========== END AskApp ==========

Process finished with exit code 0
```

## Exercise 2.3 e)
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

Q:  Trace all operations (i. e. generation and storing of measurements) of your system.
Show when and where (in which threads) operations are performed. 
A:
```text
=========== WeatherStationApp ===========
   ### WeatherStation 'Las_Vegas' started (thread id=25)
   ### WeatherStation 'Linz' started (thread id=22)
   ### WeatherStation 'Dornbirn' started (thread id=24)
   ### WeatherStation 'Vienna' started (thread id=23)
Dornbirn no.1 => produced '(11:37:33:756,17.635202,Celsius)' (thread id=22)
fs1 => Message 'Dornbirn:(11:37:33:756,17.635202,Celsius)' Received (thread id=24)
Linz no.1 => produced '(11:37:33:821,19.048119,Celsius)' (thread id=22)
fs2 => Message 'Linz:(11:37:33:821,19.048119,Celsius)' Received (thread id=22)
Vienna no.1 => produced '(11:37:33:872,21.295269,Celsius)' (thread id=25)
Las_Vegas no.1 => produced '(11:37:33:904,69.96119,Fahrenheit)' (thread id=25)
Dornbirn no.2 => produced '(11:37:33:956,16.405703,Celsius)' (thread id=23)
fs1 => Message 'Vienna:(11:37:33:872,21.295269,Celsius)' Received (thread id=24)
fs2 => Message 'Las_Vegas:(11:37:33:904,69.96119,Fahrenheit)' Received (thread id=22)
Linz no.2 => produced '(11:37:34:085,17.988941,Celsius)' (thread id=25)
Dornbirn no.3 => produced '(11:37:34:151,15.868815,Celsius)' (thread id=25)
fs1 => Message 'Dornbirn:(11:37:33:956,16.405703,Celsius)' Received (thread id=24)
Vienna no.2 => produced '(11:37:34:192,21.177776,Celsius)' (thread id=25)
fs2 => Message 'Linz:(11:37:34:085,17.988941,Celsius)' Received (thread id=22)
Las_Vegas no.2 => produced '(11:37:34:255,58.189728,Fahrenheit)' (thread id=25)
Dornbirn no.4 => produced '(11:37:34:344,17.513927,Celsius)' (thread id=23)
Linz no.3 => produced '(11:37:34:354,17.054033,Celsius)' (thread id=25)
fs1 => Message 'Dornbirn:(11:37:34:151,15.868815,Celsius)' Received (thread id=24)
fs2 => Message 'Vienna:(11:37:34:192,21.177776,Celsius)' Received (thread id=22)
Vienna no.3 => produced '(11:37:34:517,22.8151,Celsius)' (thread id=23)
Dornbirn no.5 => produced '(11:37:34:541,16.831879,Celsius)' (thread id=23)
fs1 => Message 'Las_Vegas:(11:37:34:255,58.189728,Fahrenheit)' Received (thread id=24)
Las_Vegas no.3 => produced '(11:37:34:603,71.175766,Fahrenheit)' (thread id=23)
fs2 => Message 'Dornbirn:(11:37:34:344,17.513927,Celsius)' Received (thread id=22)
Linz no.4 => produced '(11:37:34:624,17.659027,Celsius)' (thread id=25)
Dornbirn no.6 => produced '(11:37:34:743,16.952858,Celsius)' (thread id=23)
Vienna no.4 => produced '(11:37:34:823,20.673002,Celsius)' (thread id=25)
fs2 => Message 'Vienna:(11:37:34:517,22.8151,Celsius)' Received (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
fs1 => Message 'Linz:(11:37:34:354,17.054033,Celsius)' Received (thread id=24)
Linz no.5 => produced '(11:37:34:896,19.685017,Celsius)' (thread id=23)
Dornbirn no.7 => produced '(11:37:34:941,16.323374,Celsius)' (thread id=25)
Las_Vegas no.4 => produced '(11:37:34:951,68.23192,Fahrenheit)' (thread id=25)
fs2 => Message 'Las_Vegas:(11:37:34:603,71.175766,Fahrenheit)' Received (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
fs1 => Message 'Dornbirn:(11:37:34:541,16.831879,Celsius)' Received (thread id=24)
Dornbirn no.8 => produced '(11:37:35:145,15.807308,Celsius)' (thread id=23)
Vienna no.5 => produced '(11:37:35:145,22.674911,Celsius)' (thread id=25)
Linz no.6 => produced '(11:37:35:161,20.328138,Celsius)' (thread id=21)
fs2 => THRESHOLD reached: 7 ELEMENTS WRITTEN (thread id=22)
fs2 => Message 'Dornbirn:(11:37:34:743,16.952858,Celsius)' Received (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
fs1 => Message 'Linz:(11:37:34:624,17.659027,Celsius)' Received (thread id=24)
Las_Vegas no.5 => produced '(11:37:35:303,55.810226,Fahrenheit)' (thread id=21)
Dornbirn no.9 => produced '(11:37:35:345,18.562338,Celsius)' (thread id=25)
fs2 => THRESHOLD reached: 7 ELEMENTS WRITTEN (thread id=22)
fs2 => Message 'Linz:(11:37:34:896,19.685017,Celsius)' Received (thread id=22)
Linz no.7 => produced '(11:37:35:432,18.10897,Celsius)' (thread id=25)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
fs1 => Message 'Vienna:(11:37:34:823,20.673002,Celsius)' Received (thread id=24)
Vienna no.6 => produced '(11:37:35:464,23.44774,Celsius)' (thread id=25)
Dornbirn no.10 => produced '(11:37:35:546,18.032896,Celsius)' (thread id=21)
fs2 => THRESHOLD reached: 7 ELEMENTS WRITTEN (thread id=22)
fs2 => Message 'Las_Vegas:(11:37:34:951,68.23192,Fahrenheit)' Received (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
fs1 => Message 'Dornbirn:(11:37:34:941,16.323374,Celsius)' Received (thread id=24)
Las_Vegas no.6 => produced '(11:37:35:656,67.834076,Fahrenheit)' (thread id=21)
Linz no.8 => produced '(11:37:35:702,17.460665,Celsius)' (thread id=25)
Dornbirn no.11 => produced '(11:37:35:746,16.186716,Celsius)' (thread id=25)
Vienna no.7 => produced '(11:37:35:781,23.486536,Celsius)' (thread id=21)
fs2 => THRESHOLD reached: 7 ELEMENTS WRITTEN (thread id=22)
fs2 => Message 'Vienna:(11:37:35:145,22.674911,Celsius)' Received (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
fs1 => Message 'Dornbirn:(11:37:35:145,15.807308,Celsius)' Received (thread id=24)
Dornbirn no.12 => produced '(11:37:35:944,15.251041,Celsius)' (thread id=25)
Linz no.9 => produced '(11:37:35:975,21.348524,Celsius)' (thread id=25)
Las_Vegas no.7 => produced '(11:37:36:005,78.24274,Fahrenheit)' (thread id=25)
fs2 => THRESHOLD reached: 7 ELEMENTS WRITTEN (thread id=22)
fs2 => Message 'Las_Vegas:(11:37:35:303,55.810226,Fahrenheit)' Received (thread id=22)
fs1 => Message 'Linz:(11:37:35:161,20.328138,Celsius)' Received (thread id=24)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
Vienna no.8 => produced '(11:37:36:102,19.841736,Celsius)' (thread id=23)
Dornbirn no.13 => produced '(11:37:36:144,15.774963,Celsius)' (thread id=23)
Linz no.10 => produced '(11:37:36:244,19.147532,Celsius)' (thread id=23)
fs2 => THRESHOLD reached: 7 ELEMENTS WRITTEN (thread id=22)
fs2 => Message 'Linz:(11:37:35:432,18.10897,Celsius)' Received (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
fs1 => Message 'Dornbirn:(11:37:35:345,18.562338,Celsius)' Received (thread id=24)
Dornbirn no.14 => produced '(11:37:36:343,17.146637,Celsius)' (thread id=21)
Las_Vegas no.8 => produced '(11:37:36:353,68.49117,Fahrenheit)' (thread id=21)
Vienna no.9 => produced '(11:37:36:427,22.117582,Celsius)' (thread id=21)
fs2 => THRESHOLD reached: 7 ELEMENTS WRITTEN (thread id=22)
fs2 => Message 'Dornbirn:(11:37:35:546,18.032896,Celsius)' Received (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
fs1 => Message 'Vienna:(11:37:35:464,23.44774,Celsius)' Received (thread id=24)
Linz no.11 => produced '(11:37:36:514,20.450987,Celsius)' (thread id=19)
Dornbirn no.15 => produced '(11:37:36:545,15.438468,Celsius)' (thread id=21)
fs2 => THRESHOLD reached: 7 ELEMENTS WRITTEN (thread id=22)
fs2 => BUFFER FLUSHED (thread id=22)
fs2 => Message 'Linz:(11:37:35:702,17.460665,Celsius)' Received (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
fs1 => BUFFER FLUSHED (thread id=24)
fs1 => Message 'Las_Vegas:(11:37:35:656,67.834076,Fahrenheit)' Received (thread id=24)
Las_Vegas no.9 => produced '(11:37:36:707,79.46485,Fahrenheit)' (thread id=19)
Vienna no.10 => produced '(11:37:36:733,20.60987,Celsius)' (thread id=21)
Dornbirn no.16 => produced '(11:37:36:744,15.944887,Celsius)' (thread id=21)
Linz no.12 => produced '(11:37:36:786,17.12629,Celsius)' (thread id=21)
fs2 => Message 'Vienna:(11:37:35:781,23.486536,Celsius)' Received (thread id=22)
fs1 => Message 'Dornbirn:(11:37:35:746,16.186716,Celsius)' Received (thread id=24)
Dornbirn no.17 => produced '(11:37:36:947,16.234093,Celsius)' (thread id=21)
Las_Vegas no.10 => produced '(11:37:37:045,63.47509,Fahrenheit)' (thread id=19)
Vienna no.11 => produced '(11:37:37:056,20.36427,Celsius)' (thread id=21)
Linz no.13 => produced '(11:37:37:056,19.358955,Celsius)' (thread id=19)
fs2 => Message 'Linz:(11:37:35:975,21.348524,Celsius)' Received (thread id=22)
fs1 => Message 'Dornbirn:(11:37:35:944,15.251041,Celsius)' Received (thread id=24)
Dornbirn no.18 => produced '(11:37:37:133,18.000832,Celsius)' (thread id=25)
fs2 => Message 'Vienna:(11:37:36:102,19.841736,Celsius)' Received (thread id=22)
fs1 => Message 'Las_Vegas:(11:37:36:005,78.24274,Fahrenheit)' Received (thread id=24)
Linz no.14 => produced '(11:37:37:321,18.01141,Celsius)' (thread id=25)
Dornbirn no.19 => produced '(11:37:37:331,18.104084,Celsius)' (thread id=19)
Vienna no.12 => produced '(11:37:37:373,23.999294,Celsius)' (thread id=25)
Las_Vegas no.11 => produced '(11:37:37:394,70.316864,Fahrenheit)' (thread id=19)
fs2 => Message 'Linz:(11:37:36:244,19.147532,Celsius)' Received (thread id=22)
fs1 => Message 'Dornbirn:(11:37:36:144,15.774963,Celsius)' Received (thread id=24)
Dornbirn no.20 => produced '(11:37:37:536,16.3804,Celsius)' (thread id=25)
Linz no.15 => produced '(11:37:37:593,19.150837,Celsius)' (thread id=25)
fs2 => Message 'Las_Vegas:(11:37:36:353,68.49117,Fahrenheit)' Received (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
fs1 => Message 'Dornbirn:(11:37:36:343,17.146637,Celsius)' Received (thread id=24)
Vienna no.13 => produced '(11:37:37:692,20.050322,Celsius)' (thread id=25)
Dornbirn no.21 => produced '(11:37:37:735,16.061071,Celsius)' (thread id=25)
Las_Vegas no.12 => produced '(11:37:37:746,72.33406,Fahrenheit)' (thread id=19)
fs2 => Message 'Linz:(11:37:36:514,20.450987,Celsius)' Received (thread id=22)
Linz no.16 => produced '(11:37:37:864,21.170153,Celsius)' (thread id=25)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
fs1 => Message 'Vienna:(11:37:36:427,22.117582,Celsius)' Received (thread id=24)
Dornbirn no.22 => produced '(11:37:37:931,17.71967,Celsius)' (thread id=19)
Vienna no.14 => produced '(11:37:38:015,23.337523,Celsius)' (thread id=25)
fs2 => THRESHOLD reached: 7 ELEMENTS WRITTEN (thread id=22)
fs2 => Message 'Las_Vegas:(11:37:36:707,79.46485,Fahrenheit)' Received (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
fs1 => Message 'Dornbirn:(11:37:36:545,15.438468,Celsius)' Received (thread id=24)
Las_Vegas no.13 => produced '(11:37:38:094,57.87105,Fahrenheit)' (thread id=25)
Linz no.17 => produced '(11:37:38:136,19.062126,Celsius)' (thread id=25)
Dornbirn no.23 => produced '(11:37:38:136,15.293193,Celsius)' (thread id=19)
fs2 => THRESHOLD reached: 7 ELEMENTS WRITTEN (thread id=22)
fs2 => Message 'Dornbirn:(11:37:36:744,15.944887,Celsius)' Received (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
fs1 => Message 'Vienna:(11:37:36:733,20.60987,Celsius)' Received (thread id=24)
Vienna no.15 => produced '(11:37:38:331,23.129131,Celsius)' (thread id=25)
Dornbirn no.24 => produced '(11:37:38:337,17.839634,Celsius)' (thread id=21)
Linz no.18 => produced '(11:37:38:404,17.245512,Celsius)' (thread id=25)
Las_Vegas no.14 => produced '(11:37:38:441,67.204834,Fahrenheit)' (thread id=25)
fs2 => THRESHOLD reached: 7 ELEMENTS WRITTEN (thread id=22)
fs2 => Message 'Dornbirn:(11:37:36:947,16.234093,Celsius)' Received (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
fs1 => Message 'Linz:(11:37:36:786,17.12629,Celsius)' Received (thread id=24)
Dornbirn no.25 => produced '(11:37:38:536,16.689661,Celsius)' (thread id=21)
   ### WeatherStation 'Las_Vegas' stopped, 14 messages generated (thread id=23)
   ### WeatherStation 'Dornbirn' stopped, 25 messages generated (thread id=19)
   ### WeatherStation 'Linz' stopped, 18 messages generated (thread id=21)
   ### WeatherStation 'Vienna' stopped, 15 messages generated (thread id=25)
fs2 => THRESHOLD reached: 7 ELEMENTS WRITTEN (thread id=22)
fs2 => Message 'Linz:(11:37:37:056,19.358955,Celsius)' Received (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
fs1 => Message 'Las_Vegas:(11:37:37:045,63.47509,Fahrenheit)' Received (thread id=24)
fs2 => THRESHOLD reached: 7 ELEMENTS WRITTEN (thread id=22)
fs2 => Message 'Dornbirn:(11:37:37:133,18.000832,Celsius)' Received (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
fs1 => Message 'Vienna:(11:37:37:056,20.36427,Celsius)' Received (thread id=24)
fs2 => THRESHOLD reached: 7 ELEMENTS WRITTEN (thread id=22)
fs2 => Message 'Dornbirn:(11:37:37:331,18.104084,Celsius)' Received (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
fs1 => Message 'Linz:(11:37:37:321,18.01141,Celsius)' Received (thread id=24)
fs2 => THRESHOLD reached: 7 ELEMENTS WRITTEN (thread id=22)
fs2 => Message 'Las_Vegas:(11:37:37:394,70.316864,Fahrenheit)' Received (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
fs1 => Message 'Vienna:(11:37:37:373,23.999294,Celsius)' Received (thread id=24)
fs2 => THRESHOLD reached: 7 ELEMENTS WRITTEN (thread id=22)
fs2 => BUFFER FLUSHED (thread id=22)
fs2 => Message 'Linz:(11:37:37:593,19.150837,Celsius)' Received (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
fs1 => Message 'Dornbirn:(11:37:37:536,16.3804,Celsius)' Received (thread id=24)
fs2 => Message 'Dornbirn:(11:37:37:735,16.061071,Celsius)' Received (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=24)
fs1 => BUFFER FLUSHED (thread id=18)
fs1 => Message 'Vienna:(11:37:37:692,20.050322,Celsius)' Received (thread id=18)
fs2 => Message 'Linz:(11:37:37:864,21.170153,Celsius)' Received (thread id=22)
fs1 => Message 'Las_Vegas:(11:37:37:746,72.33406,Fahrenheit)' Received (thread id=18)
fs2 => Message 'Vienna:(11:37:38:015,23.337523,Celsius)' Received (thread id=22)
fs1 => Message 'Dornbirn:(11:37:37:931,17.71967,Celsius)' Received (thread id=18)
fs2 => Message 'Linz:(11:37:38:136,19.062126,Celsius)' Received (thread id=22)
fs1 => Message 'Las_Vegas:(11:37:38:094,57.87105,Fahrenheit)' Received (thread id=18)
fs2 => Message 'Vienna:(11:37:38:331,23.129131,Celsius)' Received (thread id=22)
fs1 => Message 'Dornbirn:(11:37:38:136,15.293193,Celsius)' Received (thread id=18)
fs2 => Message 'Linz:(11:37:38:404,17.245512,Celsius)' Received (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=18)
fs1 => Message 'Dornbirn:(11:37:38:337,17.839634,Celsius)' Received (thread id=18)
fs2 => THRESHOLD reached: 7 ELEMENTS WRITTEN (thread id=22)
fs2 => Message 'Dornbirn:(11:37:38:536,16.689661,Celsius)' Received (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=18)
fs1 => Message 'Las_Vegas:(11:37:38:441,67.204834,Fahrenheit)' Received (thread id=18)
fs2 => THRESHOLD reached: 7 ELEMENTS WRITTEN (thread id=22)
fs2 => BUFFER FLUSHED (thread id=22)
fs2 => +++ 36 messages processed (thread id=22)
fs1 => THRESHOLD reached: 5 ELEMENTS WRITTEN (thread id=18)
fs1 => BUFFER FLUSHED (thread id=18)
fs1 => +++ 36 messages processed (thread id=18)

Process finished with exit code 0
```

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
Vienna:1 => (05:29:16:830,20.710417,Celsius) (thread id=21)
New York:1 => (05:29:16:830,22.840124,Celsius) (thread id=20)
Dornbirn:1 => (05:29:16:830,21.886139,Celsius) (thread id=18)
Linz:1 => (05:29:16:830,21.490545,Celsius) (thread id=19)
New York:2 => (05:29:16:858,22.665272,Celsius) (thread id=19)
Dornbirn:2 => (05:29:16:858,20.459269,Celsius) (thread id=23)
Vienna:2 => (05:29:16:858,22.39447,Celsius) (thread id=20)
Linz:2 => (05:29:16:858,21.41685,Celsius) (thread id=18)
Vienna:3 => (05:29:16:889,23.343428,Celsius) (thread id=20)
Linz:3 => (05:29:16:889,22.83141,Celsius) (thread id=18)
Dornbirn:3 => (05:29:16:889,21.160965,Celsius) (thread id=17)
New York:3 => (05:29:16:889,21.966778,Celsius) (thread id=22)
New York:4 => (05:29:16:921,21.094952,Celsius) (thread id=17)
Vienna:4 => (05:29:16:921,23.032822,Celsius) (thread id=23)
Linz:4 => (05:29:16:921,22.415482,Celsius) (thread id=18)
Dornbirn:4 => (05:29:16:921,21.631502,Celsius) (thread id=22)
Linz:5 => (05:29:16:936,20.6535,Celsius) (thread id=17)
New York:5 => (05:29:16:936,22.108406,Celsius) (thread id=22)
Vienna:5 => (05:29:16:936,22.099808,Celsius) (thread id=18)
Dornbirn:5 => (05:29:16:936,22.317535,Celsius) (thread id=21)
Linz:6 => (05:29:16:969,22.26787,Celsius) (thread id=20)
Dornbirn:6 => (05:29:16:969,23.242407,Celsius) (thread id=18)
Vienna:6 => (05:29:16:969,23.723822,Celsius) (thread id=22)
New York:6 => (05:29:16:969,20.409723,Celsius) (thread id=17)
Dornbirn:7 => (05:29:16:996,20.248276,Celsius) (thread id=17)
Vienna:7 => (05:29:16:996,22.779686,Celsius) (thread id=23)
Linz:7 => (05:29:16:996,20.880762,Celsius) (thread id=20)
New York:7 => (05:29:16:996,22.035631,Celsius) (thread id=21)
--- write 7 elements (thread id=21)
--- write 7 elements (thread id=20)
--- write 7 elements (thread id=17)
--- write 7 elements (thread id=23)
New York:8 => (05:29:17:028,21.33055,Celsius) (thread id=19)
Vienna:8 => (05:29:17:028,21.220608,Celsius) (thread id=17)
Dornbirn:8 => (05:29:17:028,21.114735,Celsius) (thread id=23)
Linz:8 => (05:29:17:028,20.913609,Celsius) (thread id=24)
Dornbirn:9 => (05:29:17:060,23.438955,Celsius) (thread id=17)
Vienna:9 => (05:29:17:060,21.915714,Celsius) (thread id=21)
Linz:9 => (05:29:17:060,20.790058,Celsius) (thread id=23)
New York:9 => (05:29:17:060,21.07939,Celsius) (thread id=19)
New York:10 => (05:29:17:090,20.704304,Celsius) (thread id=24)
Dornbirn:10 => (05:29:17:090,23.444073,Celsius) (thread id=20)
Vienna:10 => (05:29:17:090,23.027136,Celsius) (thread id=18)
Linz:10 => (05:29:17:090,23.527836,Celsius) (thread id=17)
Dornbirn:11 => (05:29:17:120,22.024567,Celsius) (thread id=24)
Vienna:11 => (05:29:17:120,23.526192,Celsius) (thread id=18)
Linz:11 => (05:29:17:120,23.28172,Celsius) (thread id=19)
New York:11 => (05:29:17:120,24.093319,Celsius) (thread id=21)
Linz:12 => (05:29:17:151,24.008223,Celsius) (thread id=17)
Dornbirn:12 => (05:29:17:151,20.328516,Celsius) (thread id=19)
Vienna:12 => (05:29:17:151,22.503391,Celsius) (thread id=20)
New York:12 => (05:29:17:151,20.682434,Celsius) (thread id=23)
New York:13 => (05:29:17:166,20.936268,Celsius) (thread id=18)
Dornbirn:13 => (05:29:17:166,22.914482,Celsius) (thread id=17)
Linz:13 => (05:29:17:166,23.551622,Celsius) (thread id=22)
Vienna:13 => (05:29:17:166,22.702744,Celsius) (thread id=19)
New York:14 => (05:29:17:197,23.844055,Celsius) (thread id=18)
Dornbirn:14 => (05:29:17:197,21.896597,Celsius) (thread id=22)
Vienna:14 => (05:29:17:197,22.671286,Celsius) (thread id=17)
Linz:14 => (05:29:17:197,23.304176,Celsius) (thread id=23)
--- write 7 elements (thread id=23)
--- write 7 elements (thread id=22)
--- write 7 elements (thread id=17)
--- write 7 elements (thread id=18)
New York:15 => (05:29:17:229,22.446007,Celsius) (thread id=20)
Linz:15 => (05:29:17:229,22.358612,Celsius) (thread id=17)
Dornbirn:15 => (05:29:17:229,24.069115,Celsius) (thread id=23)
Vienna:15 => (05:29:17:229,23.157116,Celsius) (thread id=21)
New York:16 => (05:29:17:261,21.381744,Celsius) (thread id=18)
Linz:16 => (05:29:17:261,21.173393,Celsius) (thread id=24)
Vienna:16 => (05:29:17:261,23.33593,Celsius) (thread id=22)
Dornbirn:16 => (05:29:17:261,22.45007,Celsius) (thread id=20)
New York:17 => (05:29:17:277,22.999737,Celsius) (thread id=18)
Linz:17 => (05:29:17:277,20.418787,Celsius) (thread id=17)
Vienna:17 => (05:29:17:277,21.688354,Celsius) (thread id=19)
Dornbirn:17 => (05:29:17:277,23.570074,Celsius) (thread id=24)
Linz:18 => (05:29:17:308,22.569405,Celsius) (thread id=23)
Dornbirn:18 => (05:29:17:308,22.774395,Celsius) (thread id=19)
Vienna:18 => (05:29:17:308,22.833702,Celsius) (thread id=17)
New York:18 => (05:29:17:308,20.216034,Celsius) (thread id=20)
Linz:19 => (05:29:17:340,23.3283,Celsius) (thread id=19)
New York:19 => (05:29:17:340,21.3553,Celsius) (thread id=22)
Dornbirn:19 => (05:29:17:340,20.420588,Celsius) (thread id=21)
Vienna:19 => (05:29:17:340,23.158459,Celsius) (thread id=23)
Dornbirn:20 => (05:29:17:370,23.59746,Celsius) (thread id=20)
Vienna:20 => (05:29:17:370,22.91942,Celsius) (thread id=17)
New York:20 => (05:29:17:370,21.360264,Celsius) (thread id=21)
Linz:20 => (05:29:17:370,22.91037,Celsius) (thread id=24)
Dornbirn:21 => (05:29:17:396,22.6645,Celsius) (thread id=24)
Vienna:21 => (05:29:17:396,22.16171,Celsius) (thread id=18)
--- write 7 elements (thread id=24)
--- write 7 elements (thread id=19)
--- write 7 elements (thread id=20)
New York:21 => (05:29:17:396,21.097872,Celsius) (thread id=19)
--- write 7 elements (thread id=18)
Linz:21 => (05:29:17:396,23.90815,Celsius) (thread id=20)
Dornbirn:22 => (05:29:17:428,21.67286,Celsius) (thread id=23)
New York:22 => (05:29:17:428,21.060812,Celsius) (thread id=18)
Vienna:22 => (05:29:17:428,24.128557,Celsius) (thread id=21)
Linz:22 => (05:29:17:428,22.168396,Celsius) (thread id=17)
New York:23 => (05:29:17:461,22.461346,Celsius) (thread id=21)
Dornbirn:23 => (05:29:17:461,22.380392,Celsius) (thread id=18)
Linz:23 => (05:29:17:461,20.682045,Celsius) (thread id=23)
Vienna:23 => (05:29:17:461,21.595098,Celsius) (thread id=19)
Vienna:24 => (05:29:17:476,23.020247,Celsius) (thread id=17)
New York:24 => (05:29:17:476,20.264894,Celsius) (thread id=21)
Linz:24 => (05:29:17:476,22.706047,Celsius) (thread id=20)
Dornbirn:24 => (05:29:17:476,23.372873,Celsius) (thread id=18)
Dornbirn:25 => (05:29:17:507,21.976532,Celsius) (thread id=20)
New York:25 => (05:29:17:507,20.720219,Celsius) (thread id=21)
Linz:25 => (05:29:17:507,22.930073,Celsius) (thread id=23)
Vienna:25 => (05:29:17:507,20.843624,Celsius) (thread id=17)
New York:26 => (05:29:17:540,21.453436,Celsius) (thread id=20)
Linz:26 => (05:29:17:540,22.869831,Celsius) (thread id=21)
Dornbirn:26 => (05:29:17:540,20.490816,Celsius) (thread id=17)
Vienna:26 => (05:29:17:540,20.398327,Celsius) (thread id=19)
Vienna:27 => (05:29:17:572,23.25418,Celsius) (thread id=23)
Linz:27 => (05:29:17:572,21.021566,Celsius) (thread id=24)
Dornbirn:27 => (05:29:17:572,21.551819,Celsius) (thread id=21)
New York:27 => (05:29:17:572,22.752865,Celsius) (thread id=22)
New York:28 => (05:29:17:586,21.995132,Celsius) (thread id=18)
--- write 7 elements (thread id=18)
--- write 7 elements (thread id=19)
Vienna:28 => (05:29:17:586,22.720495,Celsius) (thread id=20)
--- write 7 elements (thread id=20)
Dornbirn:28 => (05:29:17:586,23.377361,Celsius) (thread id=19)
--- write 7 elements (thread id=22)
Linz:28 => (05:29:17:586,21.999695,Celsius) (thread id=22)
Vienna:29 => (05:29:17:618,22.942417,Celsius) (thread id=20)
Linz:29 => (05:29:17:618,22.945875,Celsius) (thread id=24)
New York:29 => (05:29:17:618,20.500053,Celsius) (thread id=23)
Dornbirn:29 => (05:29:17:618,24.075096,Celsius) (thread id=21)
Linz:30 => (05:29:17:649,20.935612,Celsius) (thread id=20)
Dornbirn:30 => (05:29:17:649,21.239346,Celsius) (thread id=24)
Vienna:30 => (05:29:17:649,23.422932,Celsius) (thread id=17)
New York:30 => (05:29:17:649,21.421455,Celsius) (thread id=18)
Vienna:31 => (05:29:17:681,21.815172,Celsius) (thread id=22)
Linz:31 => (05:29:17:681,20.910246,Celsius) (thread id=24)
New York:31 => (05:29:17:681,22.054672,Celsius) (thread id=18)
Dornbirn:31 => (05:29:17:681,21.862028,Celsius) (thread id=23)
Vienna:32 => (05:29:17:706,22.685764,Celsius) (thread id=24)
New York:32 => (05:29:17:706,20.982868,Celsius) (thread id=18)
Dornbirn:32 => (05:29:17:706,23.443768,Celsius) (thread id=17)
Linz:32 => (05:29:17:706,23.347885,Celsius) (thread id=23)
Vienna:33 => (05:29:17:737,20.827274,Celsius) (thread id=24)
Dornbirn:33 => (05:29:17:737,21.795164,Celsius) (thread id=23)
New York:33 => (05:29:17:737,22.964636,Celsius) (thread id=20)
Linz:33 => (05:29:17:737,21.484133,Celsius) (thread id=19)
New York:34 => (05:29:17:770,20.46891,Celsius) (thread id=20)
Dornbirn:34 => (05:29:17:770,24.000807,Celsius) (thread id=18)
Linz:34 => (05:29:17:770,21.687424,Celsius) (thread id=24)
Vienna:34 => (05:29:17:770,23.940958,Celsius) (thread id=21)
Dornbirn:35 => (05:29:17:801,24.145523,Celsius) (thread id=23)
--- write 7 elements (thread id=23)
--- write 7 elements (thread id=19)
--- write 7 elements (thread id=17)
--- write 7 elements (thread id=22)
Linz:35 => (05:29:17:801,24.037556,Celsius) (thread id=19)
New York:35 => (05:29:17:801,22.340712,Celsius) (thread id=22)
Vienna:35 => (05:29:17:801,20.34074,Celsius) (thread id=17)
Vienna:36 => (05:29:17:816,22.605162,Celsius) (thread id=23)
New York:36 => (05:29:17:816,22.038029,Celsius) (thread id=18)
Linz:36 => (05:29:17:816,23.660412,Celsius) (thread id=20)
Dornbirn:36 => (05:29:17:816,21.027931,Celsius) (thread id=21)
Linz:37 => (05:29:17:848,22.304031,Celsius) (thread id=24)
Vienna:37 => (05:29:17:848,23.15805,Celsius) (thread id=22)
New York:37 => (05:29:17:848,21.415237,Celsius) (thread id=19)
Dornbirn:37 => (05:29:17:848,20.384632,Celsius) (thread id=17)
Dornbirn:38 => (05:29:17:879,22.265066,Celsius) (thread id=21)
Vienna:38 => (05:29:17:879,20.779318,Celsius) (thread id=23)
New York:38 => (05:29:17:879,21.77513,Celsius) (thread id=20)
Linz:38 => (05:29:17:879,22.044666,Celsius) (thread id=17)
New York:39 => (05:29:17:911,22.873753,Celsius) (thread id=19)
Vienna:39 => (05:29:17:911,22.939213,Celsius) (thread id=22)
Linz:39 => (05:29:17:911,23.999466,Celsius) (thread id=23)
Dornbirn:39 => (05:29:17:911,21.813381,Celsius) (thread id=18)
New York:40 => (05:29:17:936,20.670921,Celsius) (thread id=18)
Vienna:40 => (05:29:17:936,22.173347,Celsius) (thread id=17)
Linz:40 => (05:29:17:936,23.114569,Celsius) (thread id=23)
Dornbirn:40 => (05:29:17:936,21.061523,Celsius) (thread id=22)
New York:41 => (05:29:17:967,20.845575,Celsius) (thread id=22)
Dornbirn:41 => (05:29:17:967,21.267288,Celsius) (thread id=24)
Vienna:41 => (05:29:17:967,21.657215,Celsius) (thread id=17)
Linz:41 => (05:29:17:967,23.270264,Celsius) (thread id=18)
Dornbirn:42 => (05:29:17:999,21.198084,Celsius) (thread id=24)
New York:42 => (05:29:17:999,23.466309,Celsius) (thread id=23)
--- write 7 elements (thread id=19)
Vienna:42 => (05:29:17:999,22.53487,Celsius) (thread id=21)
Linz:42 => (05:29:17:999,20.753456,Celsius) (thread id=19)
--- write 7 elements (thread id=21)
--- write 7 elements (thread id=24)
--- write 7 elements (thread id=23)
Linz:43 => (05:29:18:031,21.881838,Celsius) (thread id=21)
Dornbirn:43 => (05:29:18:031,22.76857,Celsius) (thread id=17)
Vienna:43 => (05:29:18:031,20.723564,Celsius) (thread id=24)
New York:43 => (05:29:18:031,22.878347,Celsius) (thread id=22)
Dornbirn:44 => (05:29:18:056,20.35882,Celsius) (thread id=21)
Linz:44 => (05:29:18:056,24.006203,Celsius) (thread id=18)
Vienna:44 => (05:29:18:056,20.203627,Celsius) (thread id=24)
New York:44 => (05:29:18:056,22.493044,Celsius) (thread id=19)
Dornbirn:45 => (05:29:18:087,21.035334,Celsius) (thread id=23)
Vienna:45 => (05:29:18:087,21.32308,Celsius) (thread id=24)
New York:45 => (05:29:18:087,23.861809,Celsius) (thread id=18)
Linz:45 => (05:29:18:087,20.317574,Celsius) (thread id=22)
New York:46 => (05:29:18:120,22.701525,Celsius) (thread id=18)
Dornbirn:46 => (05:29:18:120,23.171745,Celsius) (thread id=21)
Vienna:46 => (05:29:18:120,21.929138,Celsius) (thread id=23)
Linz:46 => (05:29:18:120,20.631496,Celsius) (thread id=24)
Dornbirn:47 => (05:29:18:146,22.873022,Celsius) (thread id=23)
New York:47 => (05:29:18:146,21.025673,Celsius) (thread id=24)
Linz:47 => (05:29:18:146,21.187063,Celsius) (thread id=20)
Vienna:47 => (05:29:18:146,20.821283,Celsius) (thread id=21)
New York:48 => (05:29:18:166,23.770767,Celsius) (thread id=23)
Dornbirn:48 => (05:29:18:166,21.72561,Celsius) (thread id=19)
Linz:48 => (05:29:18:166,22.550703,Celsius) (thread id=20)
Vienna:48 => (05:29:18:166,23.427599,Celsius) (thread id=22)
New York:49 => (05:29:18:198,22.058699,Celsius) (thread id=22)
Linz:49 => (05:29:18:198,22.624216,Celsius) (thread id=23)
Dornbirn:49 => (05:29:18:198,23.873306,Celsius) (thread id=18)
Vienna:49 => (05:29:18:198,23.962553,Celsius) (thread id=17)
--- write 7 elements (thread id=22)
--- write 7 elements (thread id=23)
--- write 7 elements (thread id=17)
--- write 7 elements (thread id=18)
Vienna:50 => (05:29:18:230,23.033964,Celsius) (thread id=19)
Linz:50 => (05:29:18:230,20.256645,Celsius) (thread id=23)
New York:50 => (05:29:18:230,22.424992,Celsius) (thread id=18)
Dornbirn:50 => (05:29:18:230,21.393803,Celsius) (thread id=22)
Dornbirn:51 => (05:29:18:261,22.947124,Celsius) (thread id=22)
New York:51 => (05:29:18:261,21.010725,Celsius) (thread id=19)
Linz:51 => (05:29:18:261,23.338932,Celsius) (thread id=20)
Vienna:51 => (05:29:18:265,22.26795,Celsius) (thread id=24)
Linz:52 => (05:29:18:276,22.752445,Celsius) (thread id=23)
Dornbirn:52 => (05:29:18:276,23.452412,Celsius) (thread id=17)
New York:52 => (05:29:18:276,22.208185,Celsius) (thread id=21)
Vienna:52 => (05:29:18:276,20.795874,Celsius) (thread id=18)
Linz:53 => (05:29:18:307,22.147596,Celsius) (thread id=23)
New York:53 => (05:29:18:307,23.278164,Celsius) (thread id=18)
Vienna:53 => (05:29:18:307,20.585669,Celsius) (thread id=21)
Dornbirn:53 => (05:29:18:307,21.195517,Celsius) (thread id=24)
New York:54 => (05:29:18:338,21.946081,Celsius) (thread id=21)
Dornbirn:54 => (05:29:18:338,20.681883,Celsius) (thread id=22)
Linz:54 => (05:29:18:338,21.060844,Celsius) (thread id=18)
Vienna:54 => (05:29:18:338,21.817436,Celsius) (thread id=20)
New York:55 => (05:29:18:371,22.15622,Celsius) (thread id=18)
Dornbirn:55 => (05:29:18:371,22.875008,Celsius) (thread id=20)
Vienna:55 => (05:29:18:371,23.626175,Celsius) (thread id=24)
Linz:55 => (05:29:18:371,23.145191,Celsius) (thread id=19)
New York:56 => (05:29:18:385,22.846758,Celsius) (thread id=22)
Vienna:56 => (05:29:18:385,23.653524,Celsius) (thread id=23)
Dornbirn:56 => (05:29:18:385,23.425734,Celsius) (thread id=18)
--- write 7 elements (thread id=23)
--- write 7 elements (thread id=20)
Linz:56 => (05:29:18:385,22.839025,Celsius) (thread id=20)
--- write 7 elements (thread id=22)
--- write 7 elements (thread id=18)
New York:57 => (05:29:18:406,22.791515,Celsius) (thread id=21)
Vienna:57 => (05:29:18:406,22.212448,Celsius) (thread id=22)
Linz:57 => (05:29:18:406,23.776361,Celsius) (thread id=23)
Dornbirn:57 => (05:29:18:406,23.704826,Celsius) (thread id=17)
New York:58 => (05:29:18:438,20.70495,Celsius) (thread id=22)
Dornbirn:58 => (05:29:18:438,22.415823,Celsius) (thread id=24)
Vienna:58 => (05:29:18:438,21.927725,Celsius) (thread id=18)
Linz:58 => (05:29:18:438,22.935986,Celsius) (thread id=19)
Dornbirn:59 => (05:29:18:469,20.993925,Celsius) (thread id=23)
Vienna:59 => (05:29:18:469,21.416767,Celsius) (thread id=24)
New York:59 => (05:29:18:469,24.118828,Celsius) (thread id=21)
Linz:59 => (05:29:18:469,24.105978,Celsius) (thread id=22)
Vienna:60 => (05:29:18:500,20.539244,Celsius) (thread id=19)
New York:60 => (05:29:18:500,21.568981,Celsius) (thread id=21)
Linz:60 => (05:29:18:500,20.204031,Celsius) (thread id=20)
Dornbirn:60 => (05:29:18:500,22.118883,Celsius) (thread id=22)
New York:61 => (05:29:18:526,22.465147,Celsius) (thread id=21)
Linz:61 => (05:29:18:526,23.582653,Celsius) (thread id=19)
Vienna:61 => (05:29:18:526,21.76871,Celsius) (thread id=22)
Dornbirn:61 => (05:29:18:526,23.356562,Celsius) (thread id=23)
Linz:62 => (05:29:18:547,22.87133,Celsius) (thread id=18)
Dornbirn:62 => (05:29:18:547,20.894539,Celsius) (thread id=17)
New York:62 => (05:29:18:547,22.475056,Celsius) (thread id=22)
Vienna:62 => (05:29:18:547,22.584229,Celsius) (thread id=24)
Linz:63 => (05:29:18:578,23.940073,Celsius) (thread id=22)
New York:63 => (05:29:18:578,20.291616,Celsius) (thread id=21)
Vienna:63 => (05:29:18:578,20.844774,Celsius) (thread id=17)
--- write 7 elements (thread id=24)
Dornbirn:63 => (05:29:18:578,21.648092,Celsius) (thread id=24)
--- write 7 elements (thread id=22)
--- write 7 elements (thread id=21)
--- write 7 elements (thread id=17)
Vienna:64 => (05:29:18:610,23.16769,Celsius) (thread id=22)
Linz:64 => (05:29:18:610,20.283224,Celsius) (thread id=23)
New York:64 => (05:29:18:610,21.79536,Celsius) (thread id=18)
Dornbirn:64 => (05:29:18:610,23.830017,Celsius) (thread id=20)
Vienna:65 => (05:29:18:641,20.58551,Celsius) (thread id=24)
Dornbirn:65 => (05:29:18:641,21.016367,Celsius) (thread id=17)
New York:65 => (05:29:18:641,21.478502,Celsius) (thread id=18)
Linz:65 => (05:29:18:641,21.456093,Celsius) (thread id=19)
New York:66 => (05:29:18:656,23.677608,Celsius) (thread id=21)
Vienna:66 => (05:29:18:656,22.71277,Celsius) (thread id=17)
Linz:66 => (05:29:18:656,23.90728,Celsius) (thread id=22)
Dornbirn:66 => (05:29:18:656,21.117159,Celsius) (thread id=24)
Vienna:67 => (05:29:18:688,21.77415,Celsius) (thread id=23)
Dornbirn:67 => (05:29:18:688,20.880636,Celsius) (thread id=21)
New York:67 => (05:29:18:688,21.278263,Celsius) (thread id=24)
Linz:67 => (05:29:18:688,20.40499,Celsius) (thread id=18)
Linz:68 => (05:29:18:718,21.922796,Celsius) (thread id=17)
New York:68 => (05:29:18:718,20.666622,Celsius) (thread id=18)
Dornbirn:68 => (05:29:18:718,23.710424,Celsius) (thread id=21)
Vienna:68 => (05:29:18:718,22.492924,Celsius) (thread id=23)
Linz:69 => (05:29:18:749,22.730417,Celsius) (thread id=23)
Dornbirn:69 => (05:29:18:749,22.948748,Celsius) (thread id=24)
Vienna:69 => (05:29:18:749,22.101484,Celsius) (thread id=22)
New York:69 => (05:29:18:749,23.401337,Celsius) (thread id=18)
Dornbirn:70 => (05:29:18:780,21.6201,Celsius) (thread id=19)
Linz:70 => (05:29:18:780,21.486547,Celsius) (thread id=20)
Vienna:70 => (05:29:18:780,22.849037,Celsius) (thread id=22)
New York:70 => (05:29:18:780,24.137526,Celsius) (thread id=23)
--- write 7 elements (thread id=22)
--- write 7 elements (thread id=23)
--- write 7 elements (thread id=20)
--- write 7 elements (thread id=19)
Dornbirn:71 => (05:29:18:812,22.457804,Celsius) (thread id=20)
Linz:71 => (05:29:18:812,21.371885,Celsius) (thread id=18)
Vienna:71 => (05:29:18:812,21.422073,Celsius) (thread id=24)
New York:71 => (05:29:18:812,23.591732,Celsius) (thread id=17)
Dornbirn:72 => (05:29:18:826,23.004059,Celsius) (thread id=17)
Linz:72 => (05:29:18:826,22.725132,Celsius) (thread id=20)
New York:72 => (05:29:18:826,21.691513,Celsius) (thread id=23)
Vienna:72 => (05:29:18:826,23.707466,Celsius) (thread id=18)
Vienna:73 => (05:29:18:857,21.433056,Celsius) (thread id=21)
Dornbirn:73 => (05:29:18:857,20.931602,Celsius) (thread id=23)
Linz:73 => (05:29:18:857,21.917736,Celsius) (thread id=18)
New York:73 => (05:29:18:857,21.758913,Celsius) (thread id=19)
Linz:74 => (05:29:18:889,22.849571,Celsius) (thread id=23)
Dornbirn:74 => (05:29:18:889,21.876139,Celsius) (thread id=22)
New York:74 => (05:29:18:889,21.978086,Celsius) (thread id=24)
Vienna:74 => (05:29:18:889,20.726494,Celsius) (thread id=17)
Vienna:75 => (05:29:18:922,22.434887,Celsius) (thread id=20)
New York:75 => (05:29:18:922,22.562681,Celsius) (thread id=21)
Linz:75 => (05:29:18:922,21.95903,Celsius) (thread id=18)
Dornbirn:75 => (05:29:18:922,22.603386,Celsius) (thread id=19)
Vienna:76 => (05:29:18:937,21.261662,Celsius) (thread id=24)
Dornbirn:76 => (05:29:18:937,21.519583,Celsius) (thread id=23)
New York:76 => (05:29:18:937,20.536228,Celsius) (thread id=19)
Linz:76 => (05:29:18:937,22.631577,Celsius) (thread id=22)
Linz:77 => (05:29:18:968,23.326519,Celsius) (thread id=18)
Dornbirn:77 => (05:29:18:968,21.747969,Celsius) (thread id=24)
Vienna:77 => (05:29:18:968,20.360592,Celsius) (thread id=20)
New York:77 => (05:29:18:968,22.063618,Celsius) (thread id=21)
--- write 7 elements (thread id=21)
--- write 7 elements (thread id=18)
--- write 7 elements (thread id=24)
--- write 7 elements (thread id=20)
Linz:78 => (05:29:18:999,21.985878,Celsius) (thread id=18)
Vienna:78 => (05:29:18:999,21.198427,Celsius) (thread id=19)
New York:78 => (05:29:18:999,23.75827,Celsius) (thread id=17)
Dornbirn:78 => (05:29:18:999,22.40662,Celsius) (thread id=21)
Linz:79 => (05:29:19:030,20.290659,Celsius) (thread id=17)
New York:79 => (05:29:19:030,21.396132,Celsius) (thread id=21)
Vienna:79 => (05:29:19:030,21.795294,Celsius) (thread id=18)
Dornbirn:79 => (05:29:19:030,20.467264,Celsius) (thread id=23)
New York:80 => (05:29:19:056,21.903055,Celsius) (thread id=21)
Dornbirn:80 => (05:29:19:056,20.502512,Celsius) (thread id=23)
Vienna:80 => (05:29:19:056,21.158012,Celsius) (thread id=20)
Linz:80 => (05:29:19:056,20.613691,Celsius) (thread id=18)
Linz:81 => (05:29:19:087,22.819372,Celsius) (thread id=20)
Vienna:81 => (05:29:19:087,20.556417,Celsius) (thread id=23)
New York:81 => (05:29:19:087,24.1243,Celsius) (thread id=17)
Dornbirn:81 => (05:29:19:087,23.665642,Celsius) (thread id=21)
Dornbirn:82 => (05:29:19:118,23.355284,Celsius) (thread id=19)
New York:82 => (05:29:19:118,23.814837,Celsius) (thread id=23)
Vienna:82 => (05:29:19:118,21.569252,Celsius) (thread id=17)
Linz:82 => (05:29:19:118,22.872074,Celsius) (thread id=24)
New York:83 => (05:29:19:151,20.272219,Celsius) (thread id=23)
Dornbirn:83 => (05:29:19:151,24.039162,Celsius) (thread id=17)
Vienna:83 => (05:29:19:151,22.127071,Celsius) (thread id=24)
Linz:83 => (05:29:19:151,22.086945,Celsius) (thread id=21)
New York:84 => (05:29:19:176,22.280268,Celsius) (thread id=20)
Linz:84 => (05:29:19:176,20.396776,Celsius) (thread id=24)
--- write 7 elements (thread id=24)
Vienna:84 => (05:29:19:176,21.602112,Celsius) (thread id=21)
Dornbirn:84 => (05:29:19:176,23.7115,Celsius) (thread id=18)
--- write 7 elements (thread id=20)
--- write 7 elements (thread id=21)
--- write 7 elements (thread id=18)
Linz:85 => (05:29:19:207,22.767166,Celsius) (thread id=19)
Dornbirn:85 => (05:29:19:207,22.502216,Celsius) (thread id=24)
Vienna:85 => (05:29:19:207,21.160444,Celsius) (thread id=20)
New York:85 => (05:29:19:207,23.527348,Celsius) (thread id=23)
Linz:86 => (05:29:19:238,21.805216,Celsius) (thread id=19)
Vienna:86 => (05:29:19:238,23.35515,Celsius) (thread id=22)
Dornbirn:86 => (05:29:19:238,20.28968,Celsius) (thread id=23)
New York:86 => (05:29:19:238,21.530779,Celsius) (thread id=17)
Linz:87 => (05:29:19:269,21.095577,Celsius) (thread id=23)
Vienna:87 => (05:29:19:269,23.03987,Celsius) (thread id=20)
New York:87 => (05:29:19:269,23.667809,Celsius) (thread id=17)
Dornbirn:87 => (05:29:19:269,21.956692,Celsius) (thread id=22)
Vienna:88 => (05:29:19:299,23.445528,Celsius) (thread id=20)
Dornbirn:88 => (05:29:19:299,21.6661,Celsius) (thread id=21)
Linz:88 => (05:29:19:299,23.235432,Celsius) (thread id=17)
New York:88 => (05:29:19:299,22.58215,Celsius) (thread id=23)
Linz:89 => (05:29:19:329,22.659012,Celsius) (thread id=20)
New York:89 => (05:29:19:329,22.970701,Celsius) (thread id=18)
Vienna:89 => (05:29:19:329,21.733978,Celsius) (thread id=24)
Dornbirn:89 => (05:29:19:329,20.958202,Celsius) (thread id=17)
Dornbirn:90 => (05:29:19:355,24.128332,Celsius) (thread id=20)
Vienna:90 => (05:29:19:355,24.109211,Celsius) (thread id=21)
New York:90 => (05:29:19:355,21.271574,Celsius) (thread id=22)
Linz:90 => (05:29:19:355,21.273203,Celsius) (thread id=24)
Dornbirn:91 => (05:29:19:387,20.78221,Celsius) (thread id=23)
--- write 7 elements (thread id=23)
--- write 7 elements (thread id=20)
New York:91 => (05:29:19:387,24.062113,Celsius) (thread id=20)
--- write 7 elements (thread id=17)
Vienna:91 => (05:29:19:387,22.62315,Celsius) (thread id=24)
Linz:91 => (05:29:19:387,22.247135,Celsius) (thread id=17)
--- write 7 elements (thread id=24)
Vienna:92 => (05:29:19:418,23.28117,Celsius) (thread id=19)
Dornbirn:92 => (05:29:19:418,20.549356,Celsius) (thread id=23)
New York:92 => (05:29:19:418,20.339142,Celsius) (thread id=17)
Linz:92 => (05:29:19:418,23.678734,Celsius) (thread id=20)
New York:93 => (05:29:19:449,22.253897,Celsius) (thread id=22)
Vienna:93 => (05:29:19:449,22.832092,Celsius) (thread id=19)
Linz:93 => (05:29:19:449,22.14476,Celsius) (thread id=20)
Dornbirn:93 => (05:29:19:449,22.351976,Celsius) (thread id=24)
Vienna:94 => (05:29:19:480,23.018167,Celsius) (thread id=20)
Dornbirn:94 => (05:29:19:480,23.744164,Celsius) (thread id=17)
Linz:94 => (05:29:19:480,23.987356,Celsius) (thread id=23)
New York:94 => (05:29:19:480,20.972921,Celsius) (thread id=22)
Linz:95 => (05:29:19:506,23.66905,Celsius) (thread id=19)
Dornbirn:95 => (05:29:19:506,23.368298,Celsius) (thread id=20)
New York:95 => (05:29:19:506,21.973265,Celsius) (thread id=22)
Vienna:95 => (05:29:19:506,24.016584,Celsius) (thread id=18)
Dornbirn:96 => (05:29:19:538,23.23685,Celsius) (thread id=18)
Linz:96 => (05:29:19:538,22.718435,Celsius) (thread id=17)
Vienna:96 => (05:29:19:538,23.976597,Celsius) (thread id=19)
New York:96 => (05:29:19:538,20.529015,Celsius) (thread id=24)
New York:97 => (05:29:19:570,21.092941,Celsius) (thread id=20)
Vienna:97 => (05:29:19:570,23.802963,Celsius) (thread id=23)
Linz:97 => (05:29:19:570,23.596703,Celsius) (thread id=21)
Dornbirn:97 => (05:29:19:570,22.146603,Celsius) (thread id=22)
Linz:98 => (05:29:19:601,22.384806,Celsius) (thread id=17)
Vienna:98 => (05:29:19:601,22.452473,Celsius) (thread id=23)
--- write 7 elements (thread id=23)
New York:98 => (05:29:19:601,21.639334,Celsius) (thread id=24)
--- write 7 elements (thread id=24)
--- write 7 elements (thread id=17)
--- write 7 elements (thread id=19)
Dornbirn:98 => (05:29:19:601,21.16175,Celsius) (thread id=19)
Vienna:99 => (05:29:19:616,21.235937,Celsius) (thread id=21)
Dornbirn:99 => (05:29:19:616,21.522863,Celsius) (thread id=22)
Linz:99 => (05:29:19:616,20.869837,Celsius) (thread id=23)
New York:99 => (05:29:19:616,23.653107,Celsius) (thread id=17)
Linz:100 => (05:29:19:647,23.168348,Celsius) (thread id=22)
Dornbirn:100 => (05:29:19:647,22.910856,Celsius) (thread id=21)
New York:100 => (05:29:19:647,21.76762,Celsius) (thread id=17)
Vienna:100 => (05:29:19:647,21.061281,Celsius) (thread id=24)
========== END WeatherStationStream App ========== (thread id=1)

Process finished with exit code 0
```

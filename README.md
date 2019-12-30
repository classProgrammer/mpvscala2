# mpvscala2

## Exercise 2.3 e)
Q:
Analyze what happens if the rate at which measurements are generated is much higher than measurements can be stored

A:
Messages are delayed and possibly lost at some point. Memory capacity could be exceeded because the storage is slower than the generation of messages.

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
Analyze what happens if the rate at which measurements are generated is much higher
than measurements can be stored.

A:
messages are lost

Q:
What is the fundamental difference between the two approaches (actor-based and
stream-based implementation)?

A:
Completely different concept. Source, Flow, Sink pattern is like writing functions. Creating Actors is like writing classes.
Actors communicate via messages, Source-Flow-Sink feels like function chaining.
Actors can be customized more easy. Actors feel like microservices and Streams more like cloud functions.
Actors need more code, are more structural because of the classes and messages. 

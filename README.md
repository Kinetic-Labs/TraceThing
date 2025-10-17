# TraceThing

## Demo
First, build the jar:
```shell
./gradlew build shadowJar
```

then run the demo with the agent attached:
```shell
java -javaagent:build/libs/TraceThing-1.0-SNAPSHOT.jar -jar build/libs/TraceThing-1.0-SNAPSHOT.jar
```

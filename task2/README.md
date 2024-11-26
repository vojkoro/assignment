
#As we need to simulate "streaming" there is some extra code to simulate streaming.

To run the application you can use run.sh script. It will build application and run tests. 
And filter the output to show only the relevant information.

```shell
./run.sh
```

Another option is to run application from command line using maven and dev profile

```shell
mvn quarkus:dev
```

And another option to run application with tests

```shell
mvn clean test
```
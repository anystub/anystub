# anystub   

[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=org.anystub:anystub&metric=alert_status)](https://sonarcloud.io/dashboard?id=org.anystub:anystub) 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.anystub/anystub/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.anystub/anystub)

The library helps you automatically record and maintain http request/responses made with RestTemplate or 
apache HttpClient 4.x

Install from Maven Central 
===

``` xml
    <dependency>
      <groupId>org.anystub</groupId>
      <artifactId>anystub</artifactId>
      <version>0.9.0</version>
      <scope>test</scope>
    </dependency>
```


Example
===
you can find the example in test folder of the repository. below the first scenario.

you have `class SourceSystem` which has access to external resources (http access).

``` java
public class SourceSystem {

    public String get() throws IOException {
        URL myURL = new URL("http://localhost:8080/");
        URLConnection myURLConnection = myURL.openConnection();
        myURLConnection.connect();


        InputStream inputStream = myURLConnection.getInputStream();
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining());
    }


}
```

You have class Worker which uses SourceSystem to get data from external data source.

``` java 
public class Worker {

    private SourceSystem sourceSystem;

    public Worker(SourceSystem sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public String get() throws IOException {
        return sourceSystem.get();
    }
}
```

You want to test the Worker. In your tests you can create a mock for SourceSystem and put some data in the mock. The things become complex when you have several layers between your 'worker' class and your 'source system' and methods for the source system have multiple parameters. 
After you created the mock of SourceSystem the main task is to collect relavant data and set up right behaviour for it.

Creating mocks is based on extending existing classes/interfaces. You have to create mocks for every important methods of the class to track parameters and results.

``` java
public class WorkerEasyTest {

    SourceSystem sourceSystem;
    Base base;

    @Before
    public void createStub()
    {
        base = BaseManagerImpl.instance().getBase();
        sourceSystem = new SourceSystem("http://localhost:8080") {
            @Override
            public String get() throws IOException {
                return base.request(() -> super.get(), "root");
            }
        };
    }

    @Test
    public void testEasy() throws IOException {

        Worker worker = new Worker(sourceSystem);
        assertEquals("fixed", worker.get());
        assertEquals(1, base.times());
    }

}
```

The class Base keeps key parameters and return values in stub-files. If input parameters exists in the stub it recovers response from the stub-file and omits calling the real external system.
By default your data is kept in src/test/resources/anystub/stub.yml. 
`base.request(Supplier<>, String keys..)` looks for stored data in the file. In case the search is successful the found result is returned to the client. In other case Base invokes `super.get()`. After the call Base keeps parameters and returned value in stub-file then returns recovered value.
Base could keep returned values or exceptions if the call ended up with exception.
Stub-files allow you to define the behaviour you need manually. The files are yml-files with simple syntax. So you can create it if you do not have access to the external system.

Base has methods match*() and times*() to analyze data-flow in your tests.

Above we created a wrapper for the method `String get()` which has no parameters and returns String. Anystub provides a convenient way to wrap methods which operate trivial or Serializable types. When methods have parameters of non-trivial types you still need to provide serializers.


**Further reading**

Look at [wiki|https://github.com/anystub/anystub/wiki/anystub-and-SpringBoot] to see how it works with spring-boot.


You can [record and play](https://anystub-guides.github.io/) calls which go through RestTemplate/ApacheClient or JdbcTemplate/DataSource. 


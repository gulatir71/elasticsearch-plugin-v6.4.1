<h2> Creating a simple elasticsearch plugin in version 6.4.1 </h2>

A plugin provides a way to extend or enhance the basic functionality of Elasticsearch without having to fork it from GitHub.

Elasticsearch supports a plugin framework which provides many custom plugin classes that we can extend to create our own custom plugin.

A plugin is just a Zip file containing one or more jar files with compiled code and resources. Once a plugin is packaged, it can be easily added to an Elasticsearch installation using a single command.

<h3> Steps to create an Elasticsearch plugin </h3>

<strong> 1. Setting up the plugin structure: </strong>

1.1) Create a maven project using Eclipse IDE (you can use any IDE

1.2) Skip the archetype selection.

1.3) Add the Group Id, Artifact Id and Name, then click finish.

1.4) Create a source folder src/main/assemblies.

1.5) Click finish.

After this the plugin project structure should look like:
```
│

├── pom.xml

├── src

│   └── main

│       ├── assemblies

│       ├── java

│       └── resources

│
```

<strong> 2. Configuring the plugin project: </strong>

2.1) Open the pom.xml and add elasticsearch dependency.

```
<properties>
  <elasticsearch.version>7.7.0</elasticsearch.version>
</properties>
<dependencies>
  <dependency>
    <groupId>org.elasticsearch</groupId>
    <artifactId>elasticsearch</artifactId>
    <version>${elasticsearch.version}</version>
    <scope>provided</scope>
  </dependency>
</dependencies>
```

2.2) Add the plugin descriptor file.

Elasticsearch recommends:

All plugins must contain a file called plugin-descriptor.properties.

This means you must provide a plugin-descriptor.properties which should be assembled with your plugin.

Create plugin-descriptor.properties file in scr/main/resources. 

```
description=${project.description}
version=${project.version}
name=${project.artifactId}
classname=com.technocratsid.elasticsearch.plugin.HelloWorldPlugin
java.version=1.8
elasticsearch.version=${elasticsearch.version}
```

2.3) Add the plugin security policy file (Optional).
Create plugin-security.policy file in scr/main/resources. 

and add the following content:
```
grant {
permission java.security.AllPermission;
};
```

After the creation of plugin-security.policy file, you have to write proper security code around the operations requiring elevated privileges.
```
AccessController.doPrivileged(
  // sensitive operation
);
```

2.4) Create the plugin.xml file.

Create the plugin.xml file in src/main/assemblies which will be used to configure the packaging of the plugin.

and add the following content:
```
<?xml version="1.0"?>
<assembly>
  <id>plugin</id>
  <formats>
    <format>zip</format>
  </formats>
  <includeBaseDirectory>false</includeBaseDirectory>
  <fileSets>
    <fileSet>
      <directory>target</directory>
      <outputDirectory>/</outputDirectory>
      <includes>
        <include>*.jar</include>
      </includes>
    </fileSet>
  </fileSets>
  <files>
    <file>
      <source>${project.basedir}/src/main/resources/plugin-descriptor.properties</source>
      <outputDirectory>/</outputDirectory>
      <filtered>true</filtered>
    </file>
    <file>
      <source>${project.basedir}/src/main/resources/plugin-security.policy</source>
      <outputDirectory>/</outputDirectory>
      <filtered>false</filtered>
    </file>
  </files>
  <dependencySets>
    <dependencySet>
      <outputDirectory>/</outputDirectory>
      <unpack>false</unpack>
    </dependencySet>
  </dependencySets>
</assembly>
```

2.5) pom.xml file should look like this:
```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
<modelVersion>4.0.0</modelVersion>
<groupId>com.technocratsid.elasticsearch.plugin</groupId>
<artifactId>hello-world-plugin</artifactId>
<version>0.0.1-SNAPSHOT</version>
<name>Hello World Elasticsearch Plugin</name>
<properties>
  <maven.compiler.source>1.8</maven.compiler.source>
  <maven.compiler.target>1.8</maven.compiler.target>
  <elasticsearch.version>7.7.0</elasticsearch.version>
  <maven.compiler.plugin.version>3.5.1</maven.compiler.plugin.version>
  <elasticsearch.assembly.descriptor>${basedir}/src/main/assemblies/plugin.xml</elasticsearch.assembly.descriptor>
</properties>
<dependencies>
  <dependency>
    <groupId>org.elasticsearch</groupId>
    <artifactId>elasticsearch</artifactId>
    <version>${elasticsearch.version}</version>
    <scope>provided</scope>
  </dependency>
</dependencies>
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-compiler-plugin</artifactId>
      <version>${maven.compiler.plugin.version}</version>
      <configuration>
        <source>${maven.compiler.target}</source>
        <target>${maven.compiler.target}</target>
      </configuration>
    </plugin>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-assembly-plugin</artifactId>
      <configuration>
        <appendAssemblyId>false</appendAssemblyId>
        <outputDirectory>${project.build.directory}/releases/</outputDirectory>
        <descriptors>
          <descriptor>${elasticsearch.assembly.descriptor}</descriptor>
        </descriptors>
      </configuration>
      <executions>
        <execution>
          <phase>package</phase>
          <goals>
            <goal>attached</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
</project>
```

<strong> 3. Create the plugin classes: </strong>

3.1) Creating a new REST endpoint _hello. 

To create a new endpoint we should extend org.elasticsearch.rest.BaseRestHandler. But before doing that, initialize it in the plugin.

Create a class HelloWorldPlugin which extends org.elasticsearch.plugins.Plugin and implements the interface org.elasticsearch.plugins.ActionPlugin.

```
public class HelloWorldPlugin extends Plugin implements ActionPlugin {
}
```

Implement the getRestHandlers method:

```
public class HelloWorldPlugin extends Plugin implements ActionPlugin {
@Override
public List<RestHandler> getRestHandlers(final Settings settings,
                                         final RestController restController,
                                         final ClusterSettings clusterSettings,
                                         final IndexScopedSettings indexScopedSettings,
                                         final SettingsFilter settingsFilter,
                                         final IndexNameExpressionResolver indexNameExpressionResolver,
                                         final Supplier<DiscoveryNodes> nodesInCluster) {
        return Collections.singletonList(new HelloWorldRestAction(settings, restController));
    }
}
```

Now implement the HelloWorldRestAction class:

Create a class HelloWorldRestAction which extends org.elasticsearch.rest.BaseRestHandler.

```
public class HelloWorldRestAction extends BaseRestHandler {

private static String NAME = "_hello";

@Inject
public HelloWorldRestAction(Settings settings, RestController restController) {
   super(settings);
   restController.registerHandler(RestRequest.Method.GET, "/" + NAME, this);
}

@Override
public String getName() {
   return NAME;
}

@Override
protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
   return channel -> {
      XContentBuilder builder = channel.newBuilder();
      builder.startObject().field("message", "HelloWorld").endObject();
      channel.sendResponse(new BytesRestResponse(RestStatus.OK, builder));
   };
  }
}
```

<strong>4. Build the plugin:</strong>
```
mvn clean install
```
After this step you’ll find the packaged plugin Zip in target/releases folder of your plugin project.

<strong> 5. Install the plugin: </strong>

You can install this plugin using the command:
```
bin\elasticsearch-plugin install file:///path/to/target/releases/hello-world-plugin-0.0.1-SNAPSHOT.zip
```

<strong> 6. Test the plugin: </strong>
After installing the plugin start Elasticsearch.
```
bin\elasticsearch
```

<strong>Use curl / Kibana </strong>
```
curl -XGET "http://localhost:9200/_hello
```

<strong> Output </strong>
```
{
  "message": "HelloWorld"
}
```

<h2> Finished. </h2>

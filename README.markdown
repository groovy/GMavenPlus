#GMavenPlus#
Welcome to the home of GMavenPlus.  The basic information is below.  For more information, check out the [wiki](http://github.com/keeganwitt/GMavenPlus/wiki).

##Available Goals##
* gmavenplus:compile
* gmavenplus:testCompile
* gmavenplus:generateStubs
* gmavenplus:testGenerateStubs
* gmavenplus:groovydoc (work in progress)
* gmavenplus:execute (work in progress)

##Usage Example##
    <plugin>
      <groupId>GMavenPlus</groupId>
      <artifactId>GMavenPlus-Plugin</artifactId>
      <version>1.0-SNAPSHOT</version>
      <executions>
        <execution>
          <goals>
            <goal>compile</goal>
            <goal>testCompile</goal>
            <goal>groovydoc</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
    ....
    <dependencies>
      <dependency>
        <groupId>org.codehaus.groovy</groupId>
        <artifactId>groovy-all</artifactId>
        <version>1.8.1</version>
      </dependency>
    </dependencies>

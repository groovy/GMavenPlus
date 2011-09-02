#GMavenPlus#

##Introduction##
Welcome to the home of GMavenPlus.  Do note that this project is currently in the alpha stage, but you can expect a more rigorous version soon.
The basic information about this project is below.  For more information, check out the [wiki](http://github.com/keeganwitt/GMavenPlus/wiki).

##License##
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)

##Available Goals##
* gmavenplus:compile
* gmavenplus:testCompile
* gmavenplus:generateStubs
* gmavenplus:testGenerateStubs
* gmavenplus:groovydoc (only supported for Groovy >= 1.5.0)
* gmavenplus:testGroovydoc (only supported for Groovy >= 1.5.0)
* gmavenplus:execute (work in progress)

##Basic Usage##
    <project>
      ...
      <build>
        <plugins>
          <plugin>
            <groupId>GMavenPlus</groupId>
            <artifactId>GMavenPlus-Plugin</artifactId>
            <version>1.0-alpha1-SNAPSHOT</version>
            <executions>
              <execution>
                <goals>
                  <goal>compile</goal>
                  <goal>testCompile</goal>
                </goals>
              </execution>
            </executions>
          </plugin>
          ...
        </plugins>
      </build>
      <dependencies>
        <dependency>
          <groupId>org.codehaus.groovy</groupId>
          <artifactId>groovy-all</artifactId>
          <!-- any version of Groovy should work here -->
          <version>1.8.1</version>
        </dependency>
        ....
      </dependencies>
    </project>

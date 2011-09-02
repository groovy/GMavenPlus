#GMavenPlus#
Welcome to the home of GMavenPlus.  The basic information is below.  For more information, check out the [wiki](http://github.com/keeganwitt/GMavenPlus/wiki).

##Available Goals##
* gmavenplus:compile
* gmavenplus:testCompile
* gmavenplus:generateStubs
* gmavenplus:testGenerateStubs
* gmavenplus:groovydoc (note only supported on Groovy >= 1.5.0) &lt;work in progress&gt;
* gmavenplus:testGroovydoc (note only supported on Groovy >= 1.5.0) &lt;work in progress&gt;
* gmavenplus:execute <work in progress>

##Basic Usage##
    <project>
      ...
      <build>
        <plugins>
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

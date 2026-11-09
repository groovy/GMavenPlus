# GMavenPlus Plugin #

## Introduction ##
GMavenPlus is a rewrite of [GMaven](https://github.com/groovy/gmaven), a [Maven](https://maven.apache.org/) plugin that allows you to integrate [Groovy](https://groovy-lang.org/) into your Maven projects.

You should find everything you need to know about its use in the [wiki](https://github.com/groovy/GMavenPlus/wiki). For more information, check out the [Maven site](https://groovy.github.io/GMavenPlus/index.html).

## Groovydoc in Maven Site ##

Configure the Groovydoc reports under `reporting` to generate API documentation and add links to Maven Site's Project Reports page:

```xml
<reporting>
  <plugins>
    <plugin>
      <groupId>org.codehaus.gmavenplus</groupId>
      <artifactId>gmavenplus-plugin</artifactId>
      <version>5.1.1-SNAPSHOT</version>
      <reportSets>
        <reportSet>
          <reports>
            <report>groovydoc-report</report>
            <report>groovydocTests-report</report>
          </reports>
        </reportSet>
      </reportSets>
    </plugin>
  </plugins>
</reporting>
```

Use Maven Site Plugin 3.21.0 and run `mvn site`. Include `groovy` and `groovy-groovydoc` dependencies for your Groovy version in the consuming project; use `org.codehaus.groovy` for Groovy 2.5/3 or `org.apache.groovy` for Groovy 4/5.

The main report generates `gapidocs/index.html` and the test report generates `testgapidocs/index.html` beneath the site output directory (normally `target/site`). Select only the reports you need. Reports with no matching sources are omitted, and `-DskipGroovydoc=true` skips both reports.

The reports support the same source selection, Java source inclusion, templates, classpath, and documentation options as the corresponding standalone goals. Custom top-level templates must generate `index.html` for the report link. Put these options in the reporting plugin's `configuration` for site generation. Maven Site supplies the shared `reportOutputDirectory`, defaulting to `${project.reporting.outputDirectory}`; the standalone `groovyDocOutputDirectory` and `testGroovyDocOutputDirectory` parameters do not control report paths.

The existing `groovydoc`, `groovydocTests`, `groovydoc-jar`, and `groovydocTests-jar` goals retain their standalone behavior. The report goals also run directly, using the site output directory:

```text
mvn org.codehaus.gmavenplus:gmavenplus-plugin:5.1.1-SNAPSHOT:groovydoc-report
mvn org.codehaus.gmavenplus:gmavenplus-plugin:5.1.1-SNAPSHOT:groovydocTests-report
```

For direct invocation, put plugin options under `build/plugins` or `build/pluginManagement`; Maven does not apply the `reporting` configuration to direct goals.

---
![ci](https://github.com/groovy/GMavenPlus/actions/workflows/ci.yaml/badge.svg?branch=master)

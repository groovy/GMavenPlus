# Integration Test Coverage

Each directory is one user-visible Maven workflow. Keep fixtures flat so they can be selected with `-Dinvoker.test`, and add one only when it covers a distinct lifecycle, classpath, reactor, or compatibility boundary.

| Fixture | Workflow | Coverage boundary | Primary assertion |
|---|---|---|---|
| `compile-default` | Compile Groovy sources | Default lifecycle | Compiled classes run in tests |
| `compile-config-script` | Compile with a configuration script | Compiler configuration | Configured source and test compilation |
| `compile-mixed-java-groovy` | Compile Java and Groovy together | Main source ordering | Cross-language runtime behavior |
| `compile-reactor` | Compile a mixed reactor | Inter-module dependency ordering | Reactor consumer test |
| `stubs-main-and-test` | Generate main and test stubs | Source-root registration | Maven source-root model |
| `stubs-cleanup` | Remove test stubs | Source-root removal | Removed test source root |
| `compile-classpath-isolation` | Reject a polluted classpath | Classloader isolation | Expected Maven failure |
| `execute-inline-and-file` | Execute inline and file scripts | Script and Maven-context injection | Script assertions and runtime output |
| `execute-reactor-classpath` | Execute scripts in a reactor | Reactor classpath resolution | Cross-module script execution |
| `groovydoc-main-and-jar` | Generate main GroovyDoc and JAR | Attached documentation artifact | Documentation test and package goal |
| `groovydoc-test-and-test-jar` | Generate test GroovyDoc and JAR | Test documentation lifecycle | Generated test documentation |
| `forked-jvm-compile-and-stubs` | Fork compilation and stubs | JDK toolchain selection | Toolchain build-log check |
| `forked-jvm-groovydoc` | Fork GroovyDoc | JDK toolchain selection | Toolchain build-log check |
| `classpath-project-only` | Resolve Groovy from the project | `PROJECT_ONLY` | Project dependency compilation |
| `classpath-plugin-only` | Resolve Groovy from plugin dependencies | `PLUGIN_ONLY` | Plugin-only compilation and docs |
| `classpath-project-and-plugin` | Resolve both classpaths | `PROJECT_AND_PLUGIN` | Combined dependency compilation and docs |
| `shaded-groovy` | Use Groovy from an uber JAR | Shaded runtime discovery | Consumer runtime test |
| `maven-plugin-consumer` | Build a Groovy Maven plugin | Plugin descriptor and consumer execution | Invoked custom Mojo |

Do not add a fixture for a parameter-only change, reflection failure, or interactive console/shell behavior; cover those with focused unit tests. New fixtures require a regression issue or a documented distinct user workflow.

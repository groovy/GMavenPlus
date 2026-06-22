# Developer Notes

Use `./mvnw` on Unix or `./mvnw.cmd` on Windows for routine development. The commands below use the Windows wrapper; replace it with `./mvnw` on Unix.

## Everyday Development
| Task | Command |
|---|---|
| Run unit tests | `./mvnw.cmd test` |
| Run one unit-test class | `./mvnw.cmd -Dtest=ClassNameTest test` |
| Run the full test suite | `./mvnw.cmd -Dmaven.javadoc.skip=true -Dmaven.source.skip=true -Dgpg.skip=true clean install invoker:install invoker:run` |
| Run integration tests only | `./mvnw.cmd -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -Dmaven.source.skip=true -Dgpg.skip=true clean install invoker:install invoker:run` |
| Run selected integration projects | `./mvnw.cmd -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -Dmaven.source.skip=true -Dgpg.skip=true -Dinvoker.test=basicCompile,advancedCompile clean install invoker:install invoker:run` |
| Install without tests or Invoker projects | `./mvnw.cmd -Dmaven.test.skip=true -Dinvoker.skip=true -Dmaven.javadoc.skip=true -Dmaven.source.skip=true -Dgpg.skip=true clean install` |

## Site and Dependency Maintenance
| Task | Command |
|---|---|
| Generate and stage the site | `./mvnw.cmd clean site site:stage` |
| Generate and stage the site without tests | `./mvnw.cmd -Dmaven.test.skip=true -DskipITs clean site site:stage` |
| Deploy the site | `./mvnw.cmd clean site-deploy` |
| Update the Maven Wrapper | `mvn wrapper:wrapper -Dmaven=[Desired Maven version]` |
| Show available dependency, property, and plugin updates | `./mvnw.cmd versions:display-dependency-updates versions:display-property-updates versions:display-plugin-updates` |
| Show runtime dependencies | `./mvnw.cmd dependency:tree -Dscope=runtime` |
| Show test dependencies | `./mvnw.cmd dependency:tree -Dscope=test` |
| Show compile dependencies | `./mvnw.cmd dependency:tree -Dscope=compile` |

## Release

1. Ensure `gpg-agent` is running.
2. Verify signing: `echo "test" | gpg --clearsign` on Linux/Cmd, or `Write-Host "test" | gpg --clearsign` in PowerShell.
3. Run `mvn clean release:prepare`, then `mvn release:perform`.
4. Verify the plugin with a test project and run `publish.sh` or `publish.ps1`.
5. Fetch from origin, create the next GitHub version, and update the [Usage](https://github.com/groovy/GMavenPlus/wiki/Usage) and [Examples](https://github.com/groovy/GMavenPlus/wiki/Examples) wiki pages.
6. Update the [Android example](https://github.com/keeganwitt/android-gmavenplus-example).
7. Announce the release on `gmavenplus@googlegroups.com` and `users@groovy.apache.org`.
8. Post on Twitter/X with `#GMavenPlus` and `#GroovyLang`.
9. Announce in the [Groovy Community Slack](https://groovy-community.slack.com/messages/C2SLAV9FY/).

### Release Dry Run
- Add `-DdryRun=true` to the release command.

### Redeploy After a Signing Failure

1. Start `gpg-agent` and verify signing as above.
2. Check out the `prepare release` commit and copy `pom.xml` to `target/gmavenplus-plugin-<version>.pom`.
3. Deploy each artifact with the copied POM and `https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2`:

```text
mvn gpg:sign-and-deploy-file -DpomFile=target/gmavenplus-plugin-<version>.pom -Dfile=target/gmavenplus-plugin-<version>.pom -Durl=https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2 -DrepositoryId=ossrh
mvn gpg:sign-and-deploy-file -DpomFile=target/gmavenplus-plugin-<version>.pom -Dfile=target/gmavenplus-plugin-<version>.jar -Durl=https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2 -DrepositoryId=ossrh
mvn gpg:sign-and-deploy-file -DpomFile=target/gmavenplus-plugin-<version>.pom -Dfile=target/gmavenplus-plugin-<version>-sources.jar -Dclassifier=sources -Durl=https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2 -DrepositoryId=ossrh
mvn gpg:sign-and-deploy-file -DpomFile=target/gmavenplus-plugin-<version>.pom -Dfile=target/gmavenplus-plugin-<version>-javadoc.jar -Dclassifier=javadoc -Durl=https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2 -DrepositoryId=ossrh
```

## Publishing
| Task | Command |
|---|---|
| Package and sign | `mvn -Dmaven.test.skip=true clean verify` |
| Deploy a tested snapshot | `mvn -Dmaven.test.skip=true -Dinvoker.skip=true clean deploy` |
| Deploy a new snapshot | `mvn clean deploy` |

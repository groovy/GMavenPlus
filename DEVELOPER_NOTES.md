# Developer Notes

Use `./mvnw` on Unix (including macOS) or `./mvnw.cmd` on Windows for routine development. In the commands below, `<mvnw>` means the wrapper for your platform.

## Everyday Development
| Task | Command |
|---|---|
| Run unit tests | `<mvnw> test` |
| Run one unit-test class | `<mvnw> -Dtest=ClassNameTest test` |
| Run the full test suite | `<mvnw> -Dmaven.javadoc.skip=true -Dmaven.source.skip=true -Dgpg.skip=true clean install invoker:install invoker:run` |
| Run integration tests only | `<mvnw> -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -Dmaven.source.skip=true -Dgpg.skip=true clean install invoker:install invoker:run` |
| Run selected integration projects | `<mvnw> -Dmaven.test.skip=true -Dmaven.javadoc.skip=true -Dmaven.source.skip=true -Dgpg.skip=true -Dinvoker.test=basicCompile,advancedCompile clean install invoker:install invoker:run` |
| Install without tests or Invoker projects | `<mvnw> -Dmaven.test.skip=true -Dinvoker.skip=true -Dmaven.javadoc.skip=true -Dmaven.source.skip=true -Dgpg.skip=true clean install` |

## Forked Integration Tests

Generate the JDK toolchains file before running integration tests that fork a JVM:

```text
<mvnw> org.apache.maven.plugins:maven-toolchains-plugin:3.2.0:generate-jdk-toolchains-xml -Dtoolchain.file=src/it/toolchains.xml
```

## Site and Dependency Maintenance
| Task | Command |
|---|---|
| Generate and stage the site | `<mvnw> clean site site:stage` |
| Generate and stage the site without tests | `<mvnw> -Dmaven.test.skip=true -DskipITs clean site site:stage` |
| Deploy the site | `<mvnw> clean site-deploy` |
| Update the Maven Wrapper | `<mvnw> wrapper:wrapper -Dmaven=[Desired Maven version]` |
| Show available dependency, property, and plugin updates | `<mvnw> versions:display-dependency-updates versions:display-property-updates versions:display-plugin-updates` |
| Update all dependency and plugin versions | `<mvnw> versions:use-latest-versions -DprocessDependencies=true -DprocessPlugins=true -DprocessPluginDependencies=true -DprocessParent=true` |
| Update dependency and plugin versions held in properties | `<mvnw> versions:update-properties -DprocessDependencies=true -DprocessPlugins=true` |
| Show runtime dependencies | `<mvnw> dependency:tree -Dscope=runtime` |
| Show test dependencies | `<mvnw> dependency:tree -Dscope=test` |
| Show compile dependencies | `<mvnw> dependency:tree -Dscope=compile` |

The update goals modify `pom.xml` in place. Run both update commands, review the diff, and retain only versions compatible with the project's Java 8, Maven Plugin API, and Groovy support requirements.

## Release

1. Ensure `gpg-agent` is running.
2. Verify signing: `echo "test" | gpg --clearsign` on Linux/Cmd, or `Write-Host "test" | gpg --clearsign` in PowerShell.
3. Run `<mvnw> clean release:prepare`, then `<mvnw> release:perform`.
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
<mvnw> gpg:sign-and-deploy-file -DpomFile=target/gmavenplus-plugin-<version>.pom -Dfile=target/gmavenplus-plugin-<version>.pom -Durl=https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2 -DrepositoryId=ossrh
<mvnw> gpg:sign-and-deploy-file -DpomFile=target/gmavenplus-plugin-<version>.pom -Dfile=target/gmavenplus-plugin-<version>.jar -Durl=https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2 -DrepositoryId=ossrh
<mvnw> gpg:sign-and-deploy-file -DpomFile=target/gmavenplus-plugin-<version>.pom -Dfile=target/gmavenplus-plugin-<version>-sources.jar -Dclassifier=sources -Durl=https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2 -DrepositoryId=ossrh
<mvnw> gpg:sign-and-deploy-file -DpomFile=target/gmavenplus-plugin-<version>.pom -Dfile=target/gmavenplus-plugin-<version>-javadoc.jar -Dclassifier=javadoc -Durl=https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2 -DrepositoryId=ossrh
```

## Publishing
| Task | Command |
|---|---|
| Package and sign | `<mvnw> -Dmaven.test.skip=true clean verify` |
| Deploy a tested snapshot | `<mvnw> -Dmaven.test.skip=true -Dinvoker.skip=true clean deploy` |
| Deploy a new snapshot | `<mvnw> clean deploy` |

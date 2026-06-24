---
name: update-all-versions
description: Use when refreshing GMavenPlus Groovy support, Maven dependency and plugin versions, and integration-test POM versions.
---

# Update All Versions

## Overview

Treat a version refresh as one compatibility change: update the supported Groovy lines, Maven-managed versions, and integration tests that exercise them. Preserve Java 8 production compatibility and Groovy's runtime-only boundary.

## Workflow

1. Read `AGENTS.md`, `DEVELOPER_NOTES.md`, `pom.xml`, `.github/workflows/ci.yaml`, and affected integration-test POMs. Check the worktree first and preserve unrelated changes.
2. Determine the current stable release for every Groovy major line in the CI matrix from an authoritative Groovy release source. If a new stable major is supported by GMavenPlus's Java and Maven baselines, add it to the matrix and update group-id handling when required. Do not retain obsolete snapshot or prerelease versions merely to fill the matrix.
3. Update `.github/workflows/ci.yaml` and the root `pom.xml` consistently. Set the main POM's `<groovyVersion>` to the newest version in the CI matrix, and update `<groovyGroupId>` when the current major requires it. Keep Groovy 2 and 3 on `org.codehaus.groovy`; use `org.apache.groovy` for Groovy 4 and later.
4. Use the Maven Wrapper to update the root model's dependencies, plugins, plugin dependencies, and version properties:

   ```powershell
   .\mvnw.cmd versions:use-latest-versions -DprocessDependencies=true -DprocessPlugins=true -DprocessPluginDependencies=true -DprocessParent=true
   .\mvnw.cmd versions:update-properties -DprocessDependencies=true -DprocessPlugins=true
   ```

   Review every proposed change before keeping it. Retain compatibility floors, intentional security overrides, Java 8-compatible tooling, and versions constrained by Maven Plugin API or Groovy compatibility. Do not update generated `target/` files.
5. Update versions in `src/it/pom.xml` and every direct `src/it/forked*/pom.xml` to current compatible releases. Do not mechanically edit unrelated nested module POMs; inspect inheritance and change only independently declared versions.
6. Search the root and affected integration-test POMs for stale values. Check that the CI matrix's newest value equals the root `pom.xml` `<groovyVersion>`, and every supported major line has exactly one matrix entry.
7. Run `.\mvnw.cmd -DskipTests verify` for model/build validation, then relevant Invoker tests when a changed POM affects an integration project. For a full refresh, run the CI-equivalent test command for each Groovy line or rely on the GitHub CI matrix after local smoke coverage.

## Checks Before Handoff

Report Groovy lines added, updated, or removed; accepted and rejected Maven version changes with compatibility reasons; affected integration-test POMs; and validation run or left for CI.

## Common Mistakes

- Using `mvn` rather than `mvnw.cmd` on Windows.
- Updating Groovy artifacts without preserving the `org.codehaus`/`org.apache` split.
- Accepting every automatic major update without checking Java 8, Maven, or plugin API compatibility.
- Updating every `src/it` POM instead of the root IT POM and direct `forked*` POMs requested.

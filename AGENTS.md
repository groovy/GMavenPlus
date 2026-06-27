# Agent Instructions

## Toolchain
- Use the Maven Wrapper: `./mvnw` on Unix and `./mvnw.cmd` on Windows.
- Build with Maven 3.9.12+ and Java 11+; production bytecode and APIs must remain Java 8 compatible.

## Project Model
- This is a Maven plugin (`maven-plugin` packaging); Mojo metadata comes from `@Mojo` and `@Parameter` annotations and generates the plugin descriptor.
- Preserve each Mojo's goal, default lifecycle phase, dependency-resolution scope, and parameter compatibility unless the change explicitly alters plugin behavior.
- Do not edit generated files in `target/`.

## Groovy and Compatibility
- Do not add production compile-time dependencies on Groovy APIs; Groovy is supplied by the consuming project at runtime.
- Use `ClassWrangler`, the existing classloader boundaries, version checks, and reflection helpers to call version-dependent Groovy APIs.
- Preserve clear exception translation around reflective calls; never replace a compatibility fallback with a hard dependency on one Groovy version.
- Changes to reflection or compatibility code need focused tests and must pass the Groovy 2.5, 3, 4, and 5 CI matrix in `.github/workflows/ci.yaml`.
- Treat `src/main/java/org/codehaus/gmavenplus/groovyworkarounds` as a compatibility boundary: preserve version guards and their rationale; update focused unit tests and an Invoker test when compiler behavior changes.
- Treat `src/main/java/org/codehaus/gmavenplus/javaparser` as upstream-derived compatibility code: retain provenance and update related language-level constants and feature mappings together.

## Tests
- Put isolated Mojo behavior, parameters, version gates, reflection failures, and utility behavior in `src/test/java` unit tests.
- `src/it` intentionally contains one flat Maven Invoker project per distinct consuming-build workflow; it covers lifecycle wiring, classpaths, Groovy compilation, generated stubs, reactor builds, and packaging boundaries that unit tests cannot establish.
- Add an Invoker project only for a new user-visible workflow or a regression whose boundary is absent from `src/it/README.md`; otherwise extend the existing fixture that owns that boundary.
- Keep parameter-only changes, version gates, reflection failures, and interactive `console`/`shell` behavior in focused unit tests; do not add configuration cross-products mechanically.

## References
| Need | File |
|---|---|
| Build, plugin metadata, and Invoker configuration | `pom.xml` |
| CI compatibility matrix | `.github/workflows/ci.yaml` |
| Common local, release, and maintenance commands | `DEVELOPER_NOTES.md` |
| Invoker workflow coverage and add-test criteria | `src/it/README.md` |
| Project and user documentation | `README.md` |

In no particular order, things that should be done:

  - Make cloverage a higher order lein task, thus letting it support any test framework. This requires:
    - Persistent coverage store
    - Plugin should hook into reader to wrap every top level form with the macro.
    - A small runtime library to inject to project classpath (with insturmentation / storing logic, pref. no dependencies)
    - A richer lein plugin that handles the option parsing, sets up instrumentation hook (or prints out instrumented sources and changes sourcepath), delegates to a lein task then gathers results
    - (opt.) Logic to compose coverage from multiple runs

  - Instrument deftype, reify.
  - Better html output (form-level output rather than colouring lines)
  - TESTS! Oh my god, tests.

## Babashka compatibility (branch: cloverage-bb)

### Resolved

- **Threading macros**: Fixed by using riddley 0.2.2 (which includes bb support) instead of clojure.walk.
- **`case` instrumentation**: Fixed by aligning SCI's `case*` format with JVM Clojure.
- **`cli/cli` deprecated API**: Ported to `cli/parse-opts`.
- **`report-test` loading**: Fixed in SCI — `read` with `nil` eof-value was throwing instead of returning `nil`.
- **`letfn` with duplicate names**: Fixed in SCI — `letfn` crashed with ClassCastException when duplicate function names were used.
- **`ClassName.` constructor syntax**: Fixed in SCI — `macroexpand-1` now expands `(ClassName. args)` to `(new ClassName args)`, matching JVM Clojure.
- **`ns-map` shadowing**: Fixed in SCI — `ns-map` now reflects vars that shadow referred vars (e.g. `(defn inc ...)`).
- **Inlining/interop tests**: Wrapped `test-form-type` (inlined fn detection), `instrument-inlined-primitives-test`, and `instrument-java-interop-forms-test` with `if-bb` — SCI doesn't support inlined fns, and `clojure.lang.RT` interop requires reflection not available in native bb.
- **`clojure.test.junit`**: Added as built-in source namespace in bb. Restored junit runner in cloverage's `coverage.clj`.
- **Reporter snapshots**: Added bb-specific expected report files in `test/resources/bb/` (coverage numbers differ due to no inlining).
- **Inlined expansions in test-instrument-gets-lines**: Skipped inlined form assertions on bb with `if-bb`.
- **test-dependency-sort**: Uses `with-redefs` on bb (built-in nses have no source on classpath).
- **`:tag` metadata on vars**: Fixed in SCI — `copy-var` now preserves `:tag` metadata from original JVM vars.
- **`fn-sym` in args.clj**: Use `(class f)` instead of `.getClass`, filter out SCI internal fn class names.

- **`resolve` NPE during code walking**: Fixed in SCI — `resolve` inside a macro body no longer NPEs when bindings lack analyzer idx state.
- **`test-wrap-deftype-methods`**: In bb, `deftype` expands directly to `deftype*` (no wrapping `let`). Normalized test by wrapping in `let` on bb.

### Status

74 tests, 225 assertions, 0 failures, 0 errors.

Run tests with: `cd cloverage && bb test:bb` (requires babashka with SCI > 0.12.51)

Dev build: `cd cloverage && clojure -M:babashka/dev --config bb.edn test:bb`
(the babashka/dev alias in `~/.clojure/deps.edn` includes `-Duser.language=en -Duser.country=US` to match native bb locale behavior)

#### cloverage.coverage (source)
- `.deref ^IDeref *covered*` changed to `@*covered*`. Original form doesn't work in bb.

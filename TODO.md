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

### Remaining issues

74 tests, 229 assertions, 13 failures, 2 errors.

Run tests with: `cd cloverage && bb test:bb` (requires babashka with SCI > 0.12.51)

Dev build: `cd cloverage && clojure -M:babashka/dev --config bb.edn test:bb`
(the babashka/dev alias in `~/.clojure/deps.edn` includes `-Duser.language=en -Duser.country=US` to match native bb locale behavior)

#### cloverage.args-test
- `validate!` - 2 failures. Validation fn name prints as `sci.impl.fns/fun/arity-1` instead of `cloverage.args/regexes-or-strings?`.

#### cloverage.coverage-test
- `propagates-fn-call-type-hint` - 1 failure. `:tag` metadata not preserved on bb.
- `test-instrument-gets-lines` - 4 failures. Instrumented line counts differ from JVM.
- `test-all-reporters` - 4 failures. Coverage numbers differ (no inlining in bb).

#### cloverage.dependency-test
- `test-dependency-sort` - 1 error. Cannot read ns declaration for `clojure.stacktrace` — bb built-in nses don't have source files on classpath.

#### cloverage.instrument-test
- `test-instrumenting-fn-call-forms-propogates-metadata` - 1 failure (0 on JVM)
- `test-wrap-deftype-methods` - 1 error + 1 failure (0 on JVM). deftype handling differs.

#### cloverage.coverage (source)
- `.deref ^IDeref *covered*` changed to `@*covered*`. Original form doesn't work in bb.

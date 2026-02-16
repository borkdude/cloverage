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

### Remaining issues

74 tests, 247 assertions, 26 failures, 7 errors.

Run tests with: `cd cloverage && bb test:bb` (requires babashka with SCI > 0.12.51)

Dev build: `cd cloverage && clojure -M:babashka/dev --config bb.edn test:bb`
(the babashka/dev alias in `~/.clojure/deps.edn` includes `-Duser.language=en -Duser.country=US` to match native bb locale behavior)

#### cloverage.args-test
- `validate!` - 2 failures. Validation fn name prints as `sci.impl.fns/fun/arity-1` instead of `cloverage.args/regexes-or-strings?`.

#### cloverage.coverage-test
- `test-eval-try` - 1 error + 1 failure. `Exception.` constructor syntax not supported in SCI.
- `test-wrap-new` - 3 errors. `String.` constructor syntax not supported in SCI.
- `propagates-fn-call-type-hint` - 1 failure. `:tag` metadata not preserved on bb.
- `test-instrument-gets-lines` - 4 failures. Instrumented line counts differ from JVM.
- `test-all-reporters` - 5 failures + 1 error (was: 1 error). Now runs further after letfn fix.
- `test-main` - 1 failure (was: 1 error). Now runs further after letfn fix.

#### cloverage.dependency-test
- `test-dependency-sort` - 1 error. Cannot read ns declaration for `clojure.stacktrace` — bb built-in nses don't have source files on classpath.

#### cloverage.instrument-test
- `test-form-type` - 4 failures (0 on JVM). Inlined fn detection differs in bb.
- `instrument-java-interop-forms-test` - 1 failure (1 failure on JVM too)
- `instrument-inlined-primitives-test` - 5 failures + 1 error (6 failures on JVM too)
- `test-instrumenting-fn-call-forms-propogates-metadata` - 1 failure (0 on JVM)
- `test-wrap-deftype-methods` - 1 error + 1 failure (0 on JVM). deftype handling differs.

#### cloverage.coverage (source)
- `.deref ^IDeref *covered*` changed to `@*covered*`. Original form doesn't work in bb.

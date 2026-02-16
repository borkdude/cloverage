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

### Remaining issues

74 tests, 236 assertions, 16 failures, 12 errors.

Run tests with: `cd cloverage && bb test:bb` (requires babashka with SCI > 0.12.51)

Dev build: `cd cloverage && clojure -M:babashka/dev --config bb.edn test:bb`
(the babashka/dev alias in `~/.clojure/deps.edn` includes `-Duser.language=en -Duser.country=US` to match native bb locale behavior)

#### cloverage.args-test
- `validate!` - 2 failures. Validation fn name prints as `sci.impl.fns/fun/arity-1` instead of `cloverage.args/regexes-or-strings?`.
- `parse-custom-report` - 2 errors. Cannot locate `cloverage/custom_reporter` on classpath (sample source-paths not on bb classpath).

#### cloverage.coverage-test
- `test-eval-try` - 1 error + 1 failure
- `test-wrap-new` - 3 errors (constructor interop: `java.io.StringReader.` wrapped in `(do ...)`)
- `propagates-fn-call-type-hint` - 1 failure. `:tag` metadata not preserved on bb.
- `test-instrument-gets-lines` - 1 error
- `test-all-reporters` - 1 error
- `test-main` - 1 error

#### cloverage.dependency-test
- `test-dependency-sort` - 1 error

#### cloverage.instrument-test
- `test-form-type` - 4 failures (0 on JVM). Inlined fn detection differs in bb.
- `instrument-java-interop-forms-test` - 1 failure (1 failure on JVM too)
- `instrument-inlined-primitives-test` - 5 failures + 1 error (6 failures on JVM too)
- `test-instrumenting-fn-call-forms-propogates-metadata` - 1 failure (0 on JVM)
- `test-wrap-deftype-methods` - 1 error + 1 failure (0 on JVM). deftype handling differs.

#### cloverage.coverage (source)
- `.deref ^IDeref *covered*` changed to `@*covered*`. Original form doesn't work in bb.

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

### Remaining issues

#### cloverage.args-test
- `validate!` - 2 extra failures vs JVM. Needs investigation.

#### cloverage.coverage-test
- `test-eval-try` - error + failure on bb
- `test-wrap-new` - 3 errors on bb (constructor interop: `java.io.StringReader.` wrapped in `(do ...)`)
- `propagates-fn-call-type-hint` - `:tag` metadata not preserved on bb
- `test-instrument-gets-lines` - error on bb
- `test-all-reporters` - error on bb
- `test-main` - error on bb

#### cloverage.instrument-test
- `test-form-type` - 4 failures on bb (0 on JVM). Form type detection differs.
- `instrument-java-interop-forms-test` - 2 failures + 4 errors on bb (1 failure on JVM)
- `instrument-inlined-primitives-test` - 6 failures + 1 error on bb (6 failures on JVM too)
- `test-instrumenting-fn-call-forms-propogates-metadata` - 1 failure on bb (0 on JVM)
- `test-wrap-deftype-methods` - 2 failures on bb (0 on JVM). deftype handling differs.

#### cloverage.report-test
- Fails to load on bb. `get-resource-as-stream` returns nil for sample data files. May be a classloader issue with `dev-resources/`.

#### cloverage.coverage (source)
- `.deref ^IDeref *covered*` changed to `@*covered*`. Original form doesn't work in bb.

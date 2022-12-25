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

### 1. Threading macros produce wrong results (silent!)

`(-> x inc (* 2) (- 3))` with `x=2` should return `7` but returns `3`.
`cond->` also affected.

Root cause: replacing `riddley.walk` with `clojure.walk`. Riddley's
`macroexpand` is locals-aware and understands threading. When cloverage wraps
sub-expressions before macro expansion, the threading gets disrupted because
`clojure.walk` doesn't preserve the correct expansion order.

### 2. `case` instrumentation broken

`do-wrap :case*` calls `(keys case-map)` but SCI's `case*` has a different
internal representation than JVM Clojure's, causing
`Don't know how to create ISeq from: java.lang.Long`.

### 3. `with-open` / constructor instrumentation broken

Instrumentation wraps `java.io.StringReader.` in `(do (cover N) java.io.StringReader.)`
which makes SCI try to resolve it as a symbol rather than a constructor call.

### 4. `cli/cli` deprecated API not available in bb

`cloverage.args` uses the deprecated `clojure.tools.cli/cli` function which was
removed from the version of tools.cli bundled in bb. Currently worked around
with a stub. Should be ported to `cli/parse-opts`.

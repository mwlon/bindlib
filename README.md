# bindlib

This is a proof of concept for a new library or programming language.
The idea is to imbue all operations (i.e. `*`, `if`, `for` loops, ...) with
the ability to optimize on data binding.
For instance, if the value of `a` is bound, `a * b` could be optimized into
* `0` if `a=0`
* `b` if `a=1`
* `3 * b` if `a=3` (note that in some programming languages
accessing a literal can be faster than a numerical variable)

This has use cases in numerical computing and distributed computing.

## Example 1: Simple Cases
In this incipient scala library, we can write

```
val a = IntVar("a")
val b = BoolVar("b")
val c = BoolVar("c")
val d = IntVar("d")
val myFunc: Func[Int] = Plus(a, If(Or(b, c), Constant(2), d))

val boundFunc0: Func[Int] = myFunc.bind(Map(
  b -> true,
  a -> 1
))
println(boundFunc0.toString())
// 3

val boundFunc1: Func[Int] = myFunc.bind(Map(
  b -> false,
  a -> 0
))
println(boundFunc1.toString())
// If($c, 2, $d)
println(boundFunc1.toCode())
// this generates scala code:
// if (c) 2 else d
```

## Example 2: Tensors

In `Main.scala` we dynamically generate a simple `Tensor` class implementation and
compile it.
A simple function any tensor implementation must support is going from
multi-dimensional index (e.g. `i, j` coordinates for a matrix) to the index
into the tensor's 1-dimensional data array.
Here's an example implementation it generates:
```
class TensorImpl0(data: Array[Double]) extends Tensor(data, Array(7,5,1)) {
  override def getFlatInd(inds: Array[Int]): Int = {
    ((5 * inds(0)) + inds(1) + inds(2))
  }
}
```
Running the dynamically-compiled class against a statically-compiled
counterpart (written as best possible) shows at least a 10x speedup.
10,000,000 `get` and `set`s later
* the static code took 714ms
* the dynamic code bindlib generated took 42ms.

## (theoretical) Example 3: CSV parsing
A manual way to parse a CSV and sum the 'x' column:

```
var maybeXIndex: Option[Int] = None
var sum = 0
for (line <- file) {
  if (!maybeXIndex.isEmpty) {
    //supposing computeHeader returns Option[Header]
    computeHeader(line).forEach(header => {
      maybeXIndex = Some(header.indexOf("x"))
    })
  } else {
    sum += parseIntFrom(line, maybeXIndex.get)
  }
}
```

This is clearly slower than necessary: on every line after the header it does an unnecessary `if`, and it keeps doing `maybeXIndex.get`, since maybeXIndex is an Option.
In this simple example, there's also an ordinary way to boost efficiency.
But in a more complicated case, we might not have that leisure.
Also, it would be nice if we can make the programmer's job of boosting efficiency easier.
Here's how this would be theoretically doable with bindlang (an as-yet non-existent programming language resembling this library):

```

def processLine(maybeXIndex: Option[Int], line: String): Unit = {
  if (maybeHeader.isEmpty) {
    //supposing computeHeader returns Option[Header]
    computeHeader(line).forEach(header => {
      val xIndex = header.indexOf("x")
      processFn = processLine.bind(maybeXIndex=Some(xIndex))
    })
  } else {
    sum += parseIntFrom(line, maybeXIndex.get)
  }
}
var processFn = processLine.bind(maybeXIndex=None)

for (line <- file) {
  processFn()
}
```

## (theoretical) Example 4: Cluster Computing

This one is a bit esoteric and complicated, but it's something I've been thinking about.
Say we have a Spark Dataset and want to sum a column, grouped by id:
```
case class MyRow(
  id: Int,
  x: Double,
)

val ds: Dataset[MyRow] = ... //supports both Dataset and DataFrame ops
```
We could execute this query two different ways, using either the Dataset
or DataFrame API:

### Approach A: Dataset
```
val result = ds
  .groupByKey(_.id)
  .reduce((a, b) => a.copy(x = a.x + b.x))
```


### Approach B: DataFrame

```
val result = ds
  .groupBy(ds("id"))
  .agg(sum(ds("x")))
```

### Comparing the approaches

(So that you see where this is going - once the grouping function, etc. are
known, we can bind them to the plan and generate more efficient code for all
executors to run.)

Where Datasets win:
* Both `_.id` and `(a, b) => a.copy(x = a.x + b.x)` are Scala functions, so we
can easily modify this code to run arbitrary grouping and aggregating
functions.
* `.groupByKey(_.id)` returns a `KeyValueGroupedDataset[Int, MyRow]`, offering
static type safety and IDE hints.

Where DataFrames win:
* By specifying a column to group by rather than a function, Scala can inspect
the group by operation and compute it more efficiently.
* By specifying a built-in aggregation function, Spark can (in its code
generation step) produce a more efficient code. Overall, DataFrames can be
about 30% faster than Datasets using these snippets.

### How the dream of `bindlib` could help

We can make the Datasets API as efficient as DataFrames.

Let's focus on the grouping step.
In either approach, we want to group by a pre-existing column.
In the Dataset approach, Spark currently appends a new column
computed from applying `_.id` to each deserialized row.
In the DataFrames approach, Spark skips this step.
If scala could support bindlib features, we could have Spark define
`.groupByKey` more smartly - something like this:
```
def groupByKey[K: Encoder](func: T => K): KeyValueGroupedDataset[K, T] = {
  val columns, plan = func match {
    case GetAttr(x) => //a function that matches our _.id pattern
      logicalPlan.output.filter(/*matches this attribute*/), logicalPlan //
    case _ => //any other function
      val withGroupingKey = AppendColumns(func, logicalPlan)
      withGroupingKey.newColumns, withGroupingKey
  }
  ...
}
```

If the whole of Spark's Dataset API were written in this way, its
code generation process could be partially replaced with a call to
`stage.toCode()`, the Datasets API could be made equally CPU- and
memory-efficient as DataFrames, and in fact the DataFrames API could
be rewritten as Dataset operations.

## Q&A

Q: Looking at the current library, your distributed computing/Spark example
seems like it's just going to be just like the DataFrames API.

A: In its current state, it would be similar (but with some type safety),
limiting the user to the Funcs I've provided and any they implement. It would
be clunky. But the dream is to support `.bind` and `.toCode` on scala
expressions, which would solve the problem.

Q: mwlon, your dream of supporting `.bind` and `.toCode` on all scala
expressions is rather far-fetched. How would you make this happen?

A: Yeah, it's probably not happening, and certainly not happening soon.
But I have an idea for how I can almost get it working without contributing to
scala, by leveraging the Scalameta library.


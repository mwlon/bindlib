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

## Example 1
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

## Example 2

In `Main.scala` we dynamically generate a `Tensor` class implementation and
compile it.
An example implementation it generates:
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

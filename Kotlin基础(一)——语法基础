##### 基本类型

| 数字    | 类型   | 比特数（bit） |
| ------- | ------ | ------------- |
| Byte    | 整数   | 8             |
| Short   | 整数   | 16            |
| Int     | 整数   | 32            |
| Long    | 整数   | 64            |
| Float   | 浮点数 | 32            |
| Double  | 浮点数 | 64            |
| Char    | 字符   | 16            |
| Array   | 数组   | ——            |
| Boolean | 布尔   | 16            |
| String  | 字符串 | ——            |

##### 字符串模板

字符串字⾯值可以包含模板表达式 ，即⼀些⼩段代码，会求值并把结果合并到字符串中。 模板表达式以美元符（ $ ）开头，由⼀个简单的名字构成:

```kotlin
val i = 10
println("i = $i") // 输出“i = 10”
```

或者⽤花括号括起来的任意表达式:

```kotlin
val s = "abc"
println("$s.length is ${s.length}") // 输出“abc.length is 3”
```

原始字符串与转义字符串内部都⽀持模板。 如果你需要在原始字符串中表⽰字⾯值 $ 字符（它不⽀持
反斜杠转义），你可以⽤下列语法：

```kotlin
val price = """
${'$'}9.99
"""
```

##### if表达式和When表达式

在`kotlin`中，if是一个表达式，即会返回一个值，因此不需要三元运算符。

```kotlin
val max= if(a>b) a else b
```

`if`的分支可以是代码块，最后的表达式作为该块的值：

```kotlin
val max= if(a>b){
    println("Choose a")
    a
}else{
    println("Choose b")
    b
}
```

`When`表达式取代了`switch`操作符，如：

```kotlin
when (x) {
	0, 1 -> print("x == 0 or x == 1")
	else -> print("otherwise")
}
```

`when`可以用任意表达式作为分支：

```kotlin
when (x) {
	parseInt(s) -> print("s encodes x")
	else -> print("s does not encode x")
}
```

`when`作为一个表达式必须要有`else`分支，除非编译器能检测出所有可能出现的问题，如枚举类或密封类

```kotlin
num class Color {
    RED, ORANGE, YELLOW, GREEN, BLUE, INDIGO, VIOLET
}

fun getWarmth(color: Color) = when(color) {
    Color.RED, Color.ORANGE, Color.YELLOW -> "warm"
    Color.GREEN -> "neutral"
    Color.BLUE, Color.INDIGO, Color.VIOLET -> "cold"
}
```

`when`表达式也可以用来检测值是否在（`in`）或（`!in`)不在一个区间，及值是（`is`）或不是(`!is`)特定类型的值

```kotlin
when (x) {
	in 1..10 -> print("x is in the range")
	in validNumbers -> print("x is valid")
	!in 10..20 -> print("x is outside the range")
	else -> print("none of the above")
}
fun hasPrefix(x: Any) = when(x) {
	is String -> x.startsWith("prefix")
	else -> false
}
```

`when`表达式还可以用来取代`if-else-if`链，如果不提供参数，所有分支都是简单的布尔表达式：

```kotlin
fun Request.getBody() =
	when (val response = executeRequest()) {
		is Success -> response.body
		is HttpError -> throw HttpException(response.status)
	}
```

##### for 循环

`for` 循环可以对任何提供迭代器（`iterator`）的对象进⾏遍历

```kotlin
for (item in collection) print(item)
```

for 可以循环遍历任何提供了迭代器的对象。即：

- 有⼀个成员函数或者扩展函数` iterator()` ，它的返回类型
- 有⼀个成员函数或者扩展函数 `next()` ，并且
- 有⼀个成员函数或者扩展函数 `hasNext()` 返回 `Boolean `。
- 这三个函数都需要标记为` operator` 。

区间遍历

```kotlin
 for (i in 1..3) {
     println(i)
 }

 for(i in 1 until 10) {
     println(i)
 }

 for (i in 6 downTo 0 step 2) {
      println(i)
 }
```

通过索引遍历数组或者一个List

```kotlin
for (i in array.indices) {
	println(array[i])
}
```

或者使用库函数`withIndex`

```kotlin
for ((index, value) in array.withIndex()) {
	println("the element at $index is $value")
}
```

##### Break与Continue标签

在 `Kotlin` 中任何表达式都可以⽤标签（label）来标记。 标签的格式为标识符后跟 @ 符号

```kotlin
loop@ for (i in 1..100) {
	for (j in 1..100) {
		if (……) break@loop
	}
}
```

`Kotlin` 有函数字⾯量、局部函数和对象表达式。因此 `Kotlin` 的函数可以被嵌套。 标签限制的 return 允许我们从外层函数返回。 最重要的⼀个⽤途就是从 lambda 表达式中返回。

```kotlin
fun foo() {
	listOf(1, 2, 3, 4, 5).forEach {
		if (it == 3) return // ⾮局部直接返回到 foo() 的调⽤者
		print(it)
	}
	println("this point is unreachable")
}
```

这个 `return` 表达式从最直接包围它的函数即 `foo `中返回。（ 注意，这种⾮局部的返回只⽀持传给内
联函数的 `lambda` 表达式。） 如果我们需要从` lambda` 表达式中返回，我们必须给它加标签并⽤以限制`return`。

```kotlin
fun foo() {
	listOf(1, 2, 3, 4, 5).forEach lit@{
		if (it == 3) return@lit // 局部返回到该 lambda 表达式的调⽤者，即 forEach 循环
		print(it)
	}
	print(" done with explicit label")
}
```

通常情况下使⽤隐式标签更⽅便。 该标签与接受该 lambda 的函数同名。

```kotlin
fun foo() {
	listOf(1, 2, 3, 4, 5).forEach {
		if (it == 3) return@forEach // 局部返回到该 lambda 表达式的调⽤者，即 forEach 循环
		print(it)
	}
	print(" done with implicit label")
}
```


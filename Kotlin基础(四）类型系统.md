### `Kotlin`基础(四）类型系统

#### 可空性

可空性是`Kotlin`类型系统中帮助避免`NullPointerException`错误的特性。

可空使用`?`表示：

`Type? = Type or null`

##### 安全调用运算符：`?.`

```kotlin
s?.toUpperCase()
//等价于
if(s!=null) s.toUpperCase() else null
```

即：调用一个非空值的方法，如果该值不为空，正常执行; 为空则值为null，调用不执行

```kotlin
foo?.bar()
//等价于
if (foo != null) foo.bar() else null
```

##### Elvis运算符：`?:`

```kotlin
fun foo(s: String?) {
    val t: String = s ?: ""
}
```

`Elvis`运算符接受两个参数，第一个不为`null`则值为第一个运算数，若为`null`，则值为第二个运算数。

```kotlin
foo ?: bar
//等价于
if (foo != null) foo else bar
```

##### 安全转换符：`as?`

```kotlin
foo as? Type
//等价于
if (foo is Type) {
  	foo as Type
} else {
  	null
}
```

安全转换和`Elvis`运算符结合使用：

```kotlin
class Person(val name: String) {
    override fun equals(other: Any?): Boolean {
        val op = other as? Person ?: return false
        return op.name.equals(name)
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
```

##### 非空断言

非空断言是`Kotlin`提供最简单直接的可空类型处理工具，如果值为null，直接抛出`NullPoniterException`. 异常抛出点在断言处而非值调用处。

```kotlin
foo!!
//等价于
if (foo != null) {
	foo
} else {
	throw NullPointerException()
}
```

##### `let`函数

`let`函数就是把一个调用它的对象变成`lambda`表达式的参数，如果结合安全调用语法，能有效的把调用`let`函数的可空对象转变成非空类型。

```kotlin
foo?.let{
    /*...*/
}
//等价于
if (foo != null) {
    //调用lambda表达式
} else {
    //不执行
}
```

##### 延迟初始化的属性

`Kotlin`通常要求在构造函数中初始化所有的属性，如果某个属性不为非空类型就必须提供非空初始化值，否则，需要使用可空类型。但如果使用可空类型，每次调用都要做null检测或者非空断言。

```kotlin
lateinit var hobby :String
```

在初始化前访问一个 `lateinit `属性会抛出一个特定异常，该异常明确标识该属性被访问及它没有初始化的事实。

要检测一个 `lateinit var` 是否已经初始化过，请在该属性的引用上使用 `.isInitialized `：

```kotlin
if (foo::bar.isInitialized) {
	println(foo.bar)
}
```

##### 类型参数的可空性

`kotlin`中所有泛型类和泛型函数的类型参数默认都是可空的。

```kotlin
fun <T> printHashCode(t:T){
    println(t?.hashCode())
}
```

#### 可空类型

`Java`把基本数据类型和引用类型做区分，基本数据类型直接存储值，而引用类型变量存储对象的内存地址；`Kotlin`并不区分数据基本类型和包装类型，永远使用同一个类型。但为了保证运行效率，在大多数情况下，对于变量，属性，参数和返回值类型，`Koltin`的`Int`等数据类型会被编译成`Java`基本数据类型`int`，而泛型类，如集合，用作泛型类型参数的基本数据类型会被编译成对应的`Java`包装类型。

`Kotlin`中的可空类型不能使用`java`的基本数据类型表示，因为`null`只能被存储在`java`的引用类型的变量中，意味着任何时候只要使用了可空类型的基本数据类型都会被编译成对应的包装类型。

##### `Any`,`Any?` 根类型

`Any`类型是`Kotlin`所有非空类型的超类型（非空类型的根），包含`Int`这样的基本数据类型。

```kotlin
//Any是引用类型，值42会被装箱
val answer: Any= 42
```

##### `Unit`类型：`Kotlin`的`void`

`Kotlin`中的`Unit `类型和`Java`中的`void`功能一样。当函数没有任何返回结果，用作函数的返回类型：

```kotlin
fun f() :Unit{
    /*...*/
}
```

`Unit`是一个完备的类型，可以作为类型参数，而`void`则不行，只存在一个值是`Unit`类型，这个值也可作`Unit`,并在函数中被隐式返回：

```kotlin
interface Processor<T> {
    fun process(): T
}

class NoResultProcessor : Processor<Unit> {
    override fun process() {
        //...
    }
}
```

##### `Nothing`类型：函数不返回

知道函数永远不会正常终止，没有返回，用`Nothing`类型：

```kotlin
fun fail(message: String): Nothing {
    throw IllegalArgumentException(message)
}
```

`Nothing`类型没有任何值，只有被当作函数的返回值使用，或者被当作泛型函数的返回值的类型参数使用才有意义。

同样，`Nothing`可以结合`Elvis`运算符使用：

```kotlin
val address= company.address ?: fail("No address")
```




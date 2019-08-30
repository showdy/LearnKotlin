### 函数及Lambda表达式

###### 函数声明

```kotlin
//普通完整方式
fun double(x:Int):Int{
    return 2*x
}
//函数表达式
fun double(x:Int):Int= 2*x
```

##### 参数

函数参数用`Pascal`表示法定义，即`name:Type`定义，参数用逗号隔开，每个参数都必须有显示类型

```kotlin
fun <T> joinToString(collection: Collection<T>, separator: String, prefix: String, postfix: String): String {
    /*....*/
}
```

###### 默认参数

函数参数可以有默认值，当省略相应的参数时使用默认值，可减少重载数量：

```kotlin
fun <T> joinToString(
    collection: Collection<T>,
    separator: String=" ", 
    prefix: String="[", 
    postfix: String="]"): String {
    /*....*/
}
```

覆盖方法总是使用与基类类型方法相同的默认参数，当覆盖一个带有默认参数值的方法时，必须从签名中省略默认参数值

```kotlin
open class A {
	open fun foo(i: Int = 10) { /*……*/ }
}
class B : A() {
	 override fun foo(i: Int) { /*……*/ } // 不能有默认值
}
```

###### 命名参数

可在函数调用时使用命名的函数参数，当函数有大量的参数或默认参数时使用会方便：

```kotlin
fun reformat(str: String,
    normalizeCase: Boolean = true,
    upperCaseFirstLetter: Boolean = true,
    divideByCamelHumps: Boolean = false,
    wordSeparator: Char = ' ') {
	/*……*/
}
//默认参数调用
reformat(str)
//非默认参数调用
reformat(str, true, true, false, '_')
//命名参数调用
reformat(str,
    normalizeCase = true,
    upperCaseFirstLetter = true,
    divideByCamelHumps = false,
    wordSeparator = '_'
)
//如果不需要所有参数时
reformat(str, wordSeparator = '_')
```

###### 可变参数Varrags

函数的参数（通常最后一个）可以用`varag`修饰符标记，与`java`中`...`一致，但是使用时，如果需要传递的参数已经包装成数组，`java`可原样传递数组，而`kotlin`需要显示解包数组，使用展开运算符`*`:

```kotlin
fun <T> asList(vararg ts: T): List<T> {
    val result = ArrayList<T>()
    for (t in ts) // ts is an Array
    result.add(t)
    return result
}
val a = arrayOf(1,2,3)
//需要显示展开
val list= asList(-1,0,*a,4)
```

###### 中缀表达式

标有 infix 关键字的函数也可以使⽤中缀表⽰法（忽略该调⽤的点与圆括号）调⽤。

中缀函数必须满⾜以下要求：

* 它们必须是成员函数或扩展函数；
* 它们必须只有⼀个参数；
* 其参数不得接受可变数量的参数且不能有默认值。

```kotlin
infix fun Int.shl(x:Int):Int={/*...*/}
//中缀调用
1 shl 2
//等同：
1.shl(2)
```

中缀调用优先级低于算术运算符、类型转换、及`rangTo` 操作符

```kotlin
1 shl 2+3 ==> 1 shl (2+3)
0 until n*2 ==> 0 util (n*2)
xs union ys as Set ==> xs union (ys as Set<*>)
```

中缀调用优先级高于布尔操作符 && 、||、is、!is检测

```kotlin
a && b xor c ===> a && (b xor c)
a xoc b in c ===> (a xor b) in c
```

##### 函数作用域

在 `Kotlin `中函数可以在⽂件顶层声明顶层函数，`Kotlin `中函数也可以声明在局部作⽤域、作为成员函数
以及扩展函数。

###### 局部函数

`Koltin`支持局部函数，即在一个函数内声明一个另一个函数

```kotlin
fun dfs(graph: Graph) {
    fun dfs(current: Vertex, visited: MutableSet<Vertex>) {
        if (!visited.add(current)) return
        for (v in current.neighbors)
        	dfs(v, visited)
    }
	dfs(graph.vertices[0], HashSet())
}
```

局部函数可以访问外部函数（即闭包）的局部变量，所以在上例中，visited 可以是局部变量：

```kotlin
fun dfs(graph: Graph) {
    val visited = HashSet<Vertex>()
    fun dfs(current: Vertex) {
    	if (!visited.add(current)) return
    	for (v in current.neighbors)
    		dfs(v)
	}
	dfs(graph.vertices[0])
}
```

#### Lambda表达式

##### Lambda表达式语法

```kotlin
var sum = { x: Int, y: Int -> x + y }
```

* lambda表达式始终用花括号包围，实参并没有用括号括起来，箭头把参数列表和lambda函数体隔开。

* lambda表达式是函数调用的最后一个实参，可以放到括号的外边。

  ```koltin
  people.maxby{ it.age }
  ```

* 如果想传递两个或更多的lambda，不能把超过一个的lambda放到外边。

##### Lambda表达式变量捕获

如果在函数内使用`lambda`表达式，可以访问这个函数的参数及定义在`lambda`表达式之前的局部变量。

```kotlin
fun printMessagesWithPrefix(messages: Collection<String>, prefix: String) {
    var count = 0
    messages.forEach {
        count++
        println("$prefix $it $count")
    }
}
```

`kotlin`和`Java`一个显著的区别就是，**`Kotlin`中不会仅限于final变量的访问，还可以在`lamda`内部修改这些变量**。

##### 成员引用

```kotlin

fun salute() = println("Salute!")
data class Person(val name: String, val age: Int)
fun Person.isAdult(age: Int):Boolean {
    return this.age >= 18
}
fun main(args: Array<String>) {
    //引用顶成函数
    run(::salute)
    //构造方法引用
    val createPerson = ::Person
    val p = createPerson("Alice", 29)
    println(p)

    listOf<Person>().forEach {
        //成员引用
        println(Person::age)
        //扩展函数引用
        println(Person::isAdult)
    }  
}

```

#### 高阶函数

高阶函数就是另一个函数作为参数和返回值的函数。如

```kotlin
public inline fun <T> Iterable<T>.forEach(action: (T) -> Unit): Unit {
    for (element in this) action(element)
}
```

##### 函数类型

声明函数类型，需要将函数参数类型放在括号中，紧接着是一个箭头和函数的返回类型：

```kotlin
(Int,String)->Unit
//声明一个函数变量
val sum: (Int, Int) -> Int = { x, y -> x + y }
```

函数的返回可以为null，或者定义一个函数类型的可空变量

```kotlin
var canReturnNull: (Int, Int) -> Int? = { _, _ -> null }

var funOrNull: ((Int, Int) -> Int)? = null
```



##### 函数的调用

函数类型的值可以通过其 `invoke` 操作符调⽤：`f.invoke(x)` 或者直接` f(x) `。
如果该值具有接收者类型，那么应该将接收者对象作为第⼀个参数传递。 调⽤带有接收者的函数类型值
的另⼀个⽅式是在其前⾯加上接收者对象， 就好⽐该值是⼀个扩展函数：`1.foo(2)` ，

```kotlin
fun <T> Collection<T>.joinToString(
        separator: String = ", ",
        prefix: String = "",
        postfix: String = "",
        transform: ((T) -> String)? = null
): String {
    val result = StringBuilder(prefix)
    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        val str = transform?.invoke(element)
            ?: element.toString()
        result.append(str)
    }
    result.append(postfix)
    return result.toString()
}
```



##### Java中使用函数类

函数类型被声明为普通的接口：一个函数类型的变量是`FunctionN`接口的实现。`Kotlin`定义了一系列的接口，这些接口对应不同参数的函数:

* `Function0<R>`: 无参函数
* `Fucntion1<P1,R>`:一个参数的函数

每个接口都定义了一个`invoke`方法，调用这个方法就会执行函数

```kotlin
fun processTheAnswer(f: (Int) -> Int) {
    println(f(42))
}
```

```java
public static void main(String[] args) {
        processTheAnswer(
            new Function1<Integer, Integer>() {
                @Override
                public Integer invoke(Integer number) {
                    System.out.println(number);
                    return number + 1;
                }
        });
}
```

##### 返回函数的函数

```kotlin
class ContactListFilters {
    var prefix: String = ""
    var onlyWithPhoneNumber: Boolean = false
    fun getPredicate(): (Person) -> Boolean {
        val startsWithPrefix = { p: Person ->
            p.firstName.startsWith(prefix) || p.lastName.startsWith(prefix)
        }
        if (!onlyWithPhoneNumber) {
            return startsWithPrefix
        }
        return { startsWithPrefix(it) && it.phoneNumber != null }
    }
}
```

#### 内联函数

使用高阶函数（lambda表达式）会被正常编译成匿名类，每次调用一次lambda表达式会创建一个额外的类，如果lambda表达式捕获了变量，每次调用就会创建一个新的对象。内存分配（对于函数对象和类）和虚拟调⽤会引⼊运⾏时间开销。

当一个函数被声明为`inline`时，他的函数体是内联的，函数体会被直接替换到函数调用的地方，而非正常调用。

```kotlin

inline fun <T> synchronized(lock: Lock, action: () -> T): T {
    lock.lock()
    try {
        return action()
    } finally {
        lock.unlock()
    }
}
//调用内联函数
fun foo(lock: Lock){
    println("Before Sync")
    synchronized(1){
        println("Action")
    }
    println("After sync")
}
//相同的代码，被编译后：

fun _foo_(lock: Lock){
    println("Before Sync")
    lock.lock()
    try {
        println("Action")
    } finally {
        lock.unlock()
    }
    println("After sync")
}
```

##### 禁用内联

如果希望只内联⼀部分传给内联函数的 lambda 表达式参数，那么可以⽤ `noinline `修饰符标记不希
望内联的函数参数：

```kotlin
inline fun foo(inlined: () -> Unit, noinline notInlined: () -> Unit) { …… }
```


可以内联的 lambda 表达式只能在内联函数内部调⽤或者作为可内联的参数传递， 但是 `noinline`的可以以任何我们喜欢的⽅式操作：存储在字段中、传送它等等。

##### 非局部返回

在 `Kotlin` 中，我们可以只使⽤⼀个正常的、⾮限定的 return 来退出⼀个命名或匿名函数。 这意味着
要退出⼀个 lambda 表达式，我们必须使⽤⼀个标签，并且在 lambda 表达式内部禁⽌使⽤裸
return ，因为 lambda 表达式不能使包含它的函数返回：

```kotlin
fun foo() {
    //非内联函数
    ordinaryFunction {
    	return // 错误：不能使 `foo` 在此处返回
	}
}
```

但是如果 lambda 表达式传给的函数是内联的，该 return 也可以内联，所以它是允许的：

```kotlin
inline fun inlined(block: () -> Unit) { println("hi!") }
fun foo() {
    inlined {
    	return // OK：该 lambda 表达式是内联的
    }
}	
//局部返回可以使用匿名函数或标签
fun foo2() {
    inlined(fun() {
        /*...*/
        return //lamda表达式返回
    })
}  
```

这种返回（位于 lambda 表达式中，但退出包含它的函数）称为⾮局部返回。

**⼀些内联函数可能调⽤传给它们的不是直接来⾃函数体、⽽是来⾃另⼀个执⾏上下⽂的`lambda` 表达式参数，例如来⾃局部对象或嵌套函数**。在这种情况下，该 lambda 表达式中也不允许⾮局部控制流。为了标识这种情况，该 lambda 表达式参数需要⽤ `crossinline` 修饰符标记.

##### 带接收者的高阶函数

* `with`函数：将第一个参数转换成作为第二个参数传给他的lambda表达式的接受者。返回值为lambda表达式最后的一行结果。

  ```kotlin
  fun alphabet(): String {
      val stringBuilder = StringBuilder()
      return with(stringBuilder) {
          for (letter in 'A'..'Z') {
              this.append(letter)
          }
          append("\nNow I know the alphabet!")
          this.toString()
      }
  }
  ```

* `apply`函数：和`with`函数差不多，但是返回值为接收者对象，即第一个参数。

  ```kotlin
  fun alphabet() = StringBuilder().apply {
      for (letter in 'A'..'Z') {
          append(letter)
      }
      append("\nNow I know the alphabet!")
  }.toString()
  ```
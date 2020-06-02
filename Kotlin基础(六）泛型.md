### Kotlin基础(六) 泛型

#### 泛型类型参数

可以给类和接口及其方法，顶层函数，以及扩展函数和扩展属性声明类型参数。

* 泛型函数

  ```kotlin
  public fun <T> List<T>.slice(indices: IntRange): List<T> {
      if (indices.isEmpty()) return listOf()
      return this.subList(indices.start, indices.endInclusive + 1).toList()
  }
  ```

* 扩展属性

  ```kotlin
  val <T> List<T>.penultimate: T
      get() = this[size - 2]
  ```

* 类和接口

  ```kotlin
  public interface List<out E> : Collection<E> {
      /*...*/
  }
  ```

#### 类型参数约束

类型参数约束可以限制作为泛型类和泛型函数的类型实参的类型。与`java`中`extends`关键字类似，`kotlin`中使用`:`来表示**上界约束**。

```kotlin
//限定T的类型必须为Number的子类
fun <T : Number> oneHalf(value: T): Double {
    return value.toDouble() / 2.0
}
```

当有多个限定条件即约束时，`Java`中表示方式为：

```java
public static <T extends CharSequence & Appendable> void ensureTrailingPeriod(T seq) {
        /*....*/
}
```

而在`Kotlin`中则使用关键字`where`表示：

```kotlin
fun <T> ensuringTrailingPeriod(seq: T) where T : CharSequence, T : Appendable {
    if (!seq.endsWith(".")) {
        seq.append(".")
    }
}
```

#### 泛型擦除

学习`Kotlin`的泛型前，先了解下`Java`泛型是如何实现的：

```java
Apple[] appleArr = new Apple[10];
Fruit[] fruitArr = appleArr;//编译通过
List<Apple> appleList = new ArrayList<>();
List<Fruit> fruitList = appleList;//编译不通过
```

这是因为`java`中数组是协变的，即A是B的子类型，则A[]是B[]的子类型，而在`Kotlin`中数组是支持泛型的，不再协变。`Java`中的泛型是类型擦除的，无法在运行时获取到具体类型：

```java
System.out.println(appleArr.getClass());
System.out.println(appleList.getClass());
//运行结果
class [Lcom.showdy.kotlinDemo.Apple;
class java.util.ArrayList
```

可以发现数组在运行时获取到自身的具体类型，而`List<Apple>`运行只知道是一个`List`,而无法获取自身具体类型。

`Kotlin`中的泛型机制与`Java`中是一样的，但不同的是`Kotlin`数组是协变的。

#### 获取泛型参数类型

泛型擦除后并不是将类的全部信息都擦除，还是会将部分信息放入对应的class常量池中。

* 使用匿名内部获取具体类型

  ```kotlin
  open class GenericToken<T> {
      var type: Type = Any::class.java
  
      init {
          val superClass = this.javaClass.genericSuperclass
          type = (superClass as ParameterizedType).actualTypeArguments[0]
      }
  }
  fun main() {
      val gt = object : GenericToken<Map<String, String>>() {}
      println(gt.type)
      //运行结果：java.util.Map<java.lang.String, ? extends java.lang.String>
  }
  ```

* 使用内联函数获取泛型参数类型

  ```kotlin
  inline fun <reified T : Any> Gson.fromJson(json: String): T {
      return Gson().fromJson(json, T::class.java)
  }
  ```

  当然，内联函数被标记为`inline`，编译器会把每一次调用换成函数的实际代码实现。因为生成的内联函数的字节码引用了具体类，而不是类型参数，他不会被运行时发生的类型参数擦除影响，也就是内联函数另一大用处：**类型参数被实化。**

  ```kotlin
  inline fun <reified T> Context.startActivity() {
      val intent = Intent(this, T::class.java)
      startActivity(intent)
  }
  ```

#### 变型

变型的概念是描述拥有相同基础类型和不同类型实参的泛型类型之间是如何关联的，如`List<Any>`和`List<String>`之间是如何关联。

子类型：**任何时候如果需要一个类型A的值，都能用类型B的值（当作类型A的值），类型B就是A的子类型。**

如，只有值的类型是变量类型的子类型时，才允许变量存储该值；只有表达式的类型是函数参数的类型的子类型时，才允许把该表达式传给函数。

* 不变（`Invariant`)

  `Java`中所有类都是不变型。

* 协变 (`Covariant`)

  **类型A是类型B的子类型，那么`Generic<A>`也是`Generic<B>`的子类型，使用关键字`out`表示**，与`java`中`? extends T`含义一致：**只能读取，不能修改**。符合`PECS`原则中**`Producer extends`**

  ```kotlin
  interface Producer<out T> {
      fun produce(): T
  }
  ```

  类型参数T上的关键字out的含义：

  * 子类型化会被保留（`Producer<Cat>`是`Producer<Animal>`的子类型）
  * T 只能使用在out位置上

   另外

  * 构造函数的参数即不在in位置，也不在`out`位置，即类型参数声明使用了`out`，构造方法中也能使用。
  * 私有方法的参数既不在in位置也不在`out`位置，变型规则只会防止外部使用者对类的误用，不会对类自己的实现起作用。

* 逆变 （`Contravariant`）

  **类型A是类型B的子类型，那么`Generic<A>`也是`Generic<B>`的超类型，使用关键字`in`表示**，与`java`中`? super T`含义一致：只能修改，不能读取，符合`PECS`原则中**`Consumer Super`**

  ```kotlin
  interface Comparator<in T> {
      fun compare(e1: T, e2: T):Int
  }
  ```

#### 变型定义位置

* 声明点变型：在类声明时指定变型修饰符，这些修饰符被应用到类被使用的地方。

  ```kotlin
  public interface List<out E> : Collection<E> {
   /*...*/   
  }
  ```

* 使用点变型：允许类型参数出现的具体位置指定变型。

  ```kotlin
  fun <T> copy(source: MutableList<out T>, des: MutableList<T>){
      for (item in source) {
          des.add(item)
      }
  }
  ```

  `Kotlin`中的使用点变型直接对应`java`中的限界通配符。

#### 星号投影

当类型参数的信息不重要的时候，可以使用星号投影：不需要使用任何在签名中引用类型的方法，或者只是读取数据而不关心它的具体类型。

```kotlin
fun printFirst(list: List<*>) {
    if (list.isNotEmpty()) {
        println(list.first())
    }
}
```

`Kotlin` 为此提供了所谓的星投影语法：

* 对于 `Foo <out T : TUpper>` ，其中 T 是⼀个具有上界 `TUppe`r 的协变类型参数，`Foo <*>`等价于` Foo <out TUpper>` 。 这意味着当 T 未知时，你可以安全地从 `Foo <*>` 读取 `TUpper`的值。
  
* 对于 `Foo <in T>` ，其中 T 是⼀个逆变类型参数，`Foo <*> `等价于` Foo <in Nothing>` 。 这意味着当 `T `未知时，没有什么可以以安全的⽅式写⼊` Foo <*>` 。
  
* 对于` Foo <T : TUpper>` ，其中 T 是⼀个具有上界` TUpper` 的不型变类型参数，`Foo<*> `对于读取值时等价于` Foo<out TUpper>` ⽽对于写值时等价于` Foo<in Nothing>` 。
  

如果泛型类型具有多个类型参数，则每个类型参数都可以单独投影。 例如，如果类型被声明为` interface Function <in T, out U>` ，我们可以想象以下星投影：

* `Function<*, String>` 表⽰` Function<in Nothing, String`> ；
* `Function<Int, *> `表⽰ `Function<Int, out Any?> `；
* `Function<*, *>` 表⽰ `Function<in Nothing, out Any?> `。
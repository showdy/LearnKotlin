###    `Koltin`基础(五）运算符重载及其他约定

#### 重载算术运算符

`Kotlin` 允许我们为自己的类型提供预定义的一组操作符的实现。这些操作符具有固定的符号表示 （如 `+ `或` * `）和固定的优先级。为实现这样的操作符，我们为相应的类型（即二元操作符左侧的类型和一元操作符的参数类型）提供了一个固定名字的成员函数或扩展函数。 重载操作符的函数需要用 `operator` 修饰符标记。

* 一元运算符

  | 表达式   | 函数名       |
  | -------- | ------------ |
  | +a       | `unaryPlus`  |
  | -a       | `unaryMinus` |
  | !a       | `not`        |
  | ++a,a++  | `inc`        |
  | --a, a-- | `dec`        |

  ```kotlin
  operator fun BigDecimal.inc()= this + BigDecimal.ONE
  ```

* 二元运算符

  | 表达式 | 函数名     |
  | ------ | ---------- |
  | a*b    | `times`    |
  | a/b    | `div`      |
  | a%b    | `a.rem(b)` |
  | a+b    | `plus`     |
  | a-b    | `minus`    |

  ```kotlin
  operator fun Pointer.plus(other: Point): Point {
          return Point(x + other.x, y + other.y)
  }
  ```

* 二元复合赋值运算符

  | 表达式 | 函数名        |
  | ------ | ------------- |
  | a+=b   | `plusAssign`  |
  | a*=b   | `timesAssign` |
  | a-=b   | `minusAssign` |

  ```kotlin
  public inline operator fun <T> MutableCollection<in T>.plusAssign(element: T) {
      this.add(element)
  }
  ```

#### 重载比较运算符

* `=`运算符：`equals`

  | 表达式 | 翻译                              |
  | ------ | --------------------------------- |
  | a == b | `a?.equals(b) ?: (b === null)`    |
  | a != b | `!(a?.equals(b) ?: (b === null))` |

  操作符只使用函数 `equals(other: Any?): Boolean` ，可以覆盖它来提供自定义的相等性检测实现。不会调用任何其他同名函数（如 `equals(other: Foo`) ）。

   === 和 !== （同一性检测）不可重载，因此不存在对他们的约定。

* `排序运算符`：`compareTo`

    | 表达式 | 翻译                |
    | ------ | ------------------- |
    | a<b    | `a.compareTo(b)<0`  |
    | a>b    | `a.compareTo(b)>0`  |
    | a>=b   | `a.compareTo(b)>=0` |
    | a<=b   | `a.compareTo(b)<=0` |

    所有的比较都转换为对 `compareTo` 的调用，这个函数需要返回 Int 值

#### 集合与区间的约定

* 下标访问元素: `get`和`set`

  | 表达式           | 函数名             |
  | ---------------- | ------------------ |
  | `p[index]`       | `get(index)`       |
  | `p[index]=value` | `set(index,value)` |

  ```kotlin
  //get参数可以是任何类型，而不是只是Int
  operator fun Point.get(index: Int): Int {
      return when(index) {
          0 -> x
          1 -> y
          else ->
              throw IndexOutOfBoundsException("Invalid coordinate $index")
      }
  }
  operator fun MutablePoint.set(index: Int, value: Int) {
      when(index) {
          0 -> x = value
          1 -> y = value
          else ->
              throw IndexOutOfBoundsException("Invalid coordinate $index")
      }
  }
  fun main(args: Array<String>) {
      val p = MutablePoint(10, 20)
      println(p[1])
      p[1] = 42
      println(p)
  }
  ```

* `in`约定

  | 表达式    | 翻译             |
  | --------- | ---------------- |
  | `a in b`  | `b.contains(a)`  |
  | `a !in b` | `!b.contains(a)` |

  ```kotlin
  operator fun Rectangle.contains(p: Point): Boolean {
      return p.x in upperLeft.x until lowerRight.x &&
             p.y in upperLeft.y until lowerRight.y
  }
  
  fun main(args: Array<String>) {
      val rect = Rectangle(Point(10, 20), Point(50, 50))
      println(Point(20, 30) in rect)
      println(Point(5, 5) in rect)
  }
  ```

* `rangTo`约定

  `rangTo`定义一个区间，可为自己的类定义这个运算符。但如果类实现了`Comparable`接口就不必要了，可使用标准库函数创建可比较元素区间。

  | 表达式 | 函数名   |
  | ------ | -------- |
  | `..`   | `rangTo` |

  ```kotlin
  public operator fun <T : Comparable<T>> T.rangeTo(that: T): ClosedRange<T> = ComparableRange(this, that)
  ```

* `for`循环中使用`iterator`约定

  若让一个类可在`for`循环中使用`in`语法迭代，可为自己的类定义`iterator`方法

  ```kotlin
  operator fun ClosedRange<LocalDate>.iterator(): Iterator<LocalDate> =
          object : Iterator<LocalDate> {
              var current = start
  
              override fun hasNext() =
                  current <= endInclusive
  
              override fun next() = current.apply {
                  current = plusDays(1)
              }
          }
  
  fun main(args: Array<String>) {
      val newYear = LocalDate.ofYearDay(2017, 1)
      val daysOff = newYear.minusDays(1)..newYear
      for (dayOff in daysOff) { println(dayOff) }
  }
  ```

* 函数调用：`invoke`

  ```kotlin
  interface GithubService {
      @GET("search/repositories?sort=stars")
      fun searchRepos(
              @Query("q") query: String,
              @Query("page") page: Int,
              @Query("per_page") itemsPerPage: Int
      ): Call<RepoSearchReponse>
  
  
      companion object {
          private const val BASE_URL = "https://api.github.com/"
  
          operator fun invoke(): GithubService {
  
              val logger = HttpLoggingInterceptor().apply {
                  level = HttpLoggingInterceptor.Level.BODY
              }
  
              val client = OkHttpClient.Builder()
                      .addInterceptor(logger)
                      .build()
              return Retrofit.Builder()
                      .baseUrl(BASE_URL)
                      .client(client)
                      .addConverterFactory(GsonConverterFactory.create())
                      .build()
                      .create(GithubService::class.java)
          }
      }
  }
  
  //调用
  private fun provideGithubRepository(context: Context): GithubRepository {
    	return GithubRepository(GithubService(), provideCache(context))
  }
  ```

####  解构声明和组件函数和

解构声明允许展开单个复合值，并使用它来初始化多个单独的变量。要在解构声明中初始化每个变量，将调用名为`componetN`的函数,其中`N`为变量的位置。

```kotlin
 val p = Point(10, 20)
 val (x, y) = p
```

数据类，编译器为每个在主构造函数中声明的属性生成一个`componentN`函数，而非数据类可手动声明：

```kotlin
class Point(val x:Int,val y:Int){
    operator fun component1()=x
    operator fun component2()=y
}
```

解构声明还可用在循环中声明变量：

```kotlin
//两个约定用到：
//1. （key，value）--解构声明
//2. in---iterator函数
fun printEntries(map: Map<String, String>) {
    for ((key, value) in map) {
        println("$key -> $value")
    }
}
```




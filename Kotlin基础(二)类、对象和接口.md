

#### 修饰符

##### 访问修饰符

| 修饰符   | 相关成员               | 评注                                  |
| -------- | ---------------------- | ------------------------------------- |
| final    | 不能被重写             | 类中成员默认使用                      |
| open     | 可被重写               | 需要明确什么                          |
| abstract | 必须被重写             | 在抽象类中使用                        |
| override | 重写父类或接口中的成员 | 若没有final声明，重写的成员默认是open |

`Java`中可以创建任意类的子类并重写任意方法，除非显示声明`final`。对基类的修改会导致子类不正确的行为，即脆弱的基类问题。`Effective Java`建议“要么为继承做好设计并记录文档，要么禁止。”`Kotlin`采用该思想哲学，`Java`中类和方法默认是`open`的，而`Kotlin`中类和方法默认是`final`。

```kotlin
open class RichButtion:Clickable{
    //默认是final不能被重写
    fun disable(){}
    //open可重写
    open fun animate(){}
    //override 方法默认是open
    override fun click(){}
}
```

接口和抽象类默认是`open`, 其抽象成员默认是`open`

```kotlin
abstract class Animate{
    //默认是open
    abstract fun animate()
    //非抽象方法默认是final
    fun animateTwice(){}
}
```

##### 可见性修饰符

| 修饰符         | 类成员       | 顶层声明     |
| -------------- | ------------ | ------------ |
| public（默认） | 所有地方可见 | 所有地方可见 |
| internal       | 模块中可见   | 模块中可见   |
| protected      | 子类中可见   | ——           |
| private        | 类中可见     | 文件中可见   |

`Java`中默认可见性——包私有，在kotlin中并没有。`Kotlin`只把包作为命名空间里组织代码的一种方式，并没有将其用作可见性控制。作为替代方案，`Koltin`是使用新的修饰符`internal`,表示“只能在模块内可见。”`internal`优势在于它对模块实现细节提供真正的封装。

##### 接口

接口包含抽象方法的定义和非抽象方法的实现，但是他们都不能包含任何状态。

```kotlin
interface Clickable {
    //不支持backing-field，不能存储值
    var clickable: Boolean
    //默认open，可被重写
    fun click()
    //默认final，不能被重写
    fun showOff() = println("I'm Clickable")
}
```

由于`Koltin 1.0`是`Java 6`为目标设计，其并不支持接口中的默认方法，因此会把每个默认方法的接口编译成**一个普通接口和一个将方法体作为静态函数的类的结合体**，如上面的接口反编译后看到：

```java
public interface Clickable {
   boolean getClickable();
   void setClickable(boolean var1);
   void click();
   void showOff();
 
   public static final class DefaultImpls {
      public static void showOff(Clickable $this) {
         String var1 = "I'm Clickable";
         boolean var2 = false;
         System.out.println(var1);
      }
   }
}
```

#### 构造函数

`Kotlin`构造函数相对于`Java`做了部分修改，区分**主构造函数**和**从构造函数**。**初始化块中的代码实际上会成为主构造函数的⼀部分。委托给主构造函数会作为次构造函数的第⼀条语句**，因此所有初始化块中的代码都会在次构造函数体之前执⾏。

```kotlin
class Person {
	init {
		println("Init block")
	}
	constructor(i: Int) {
		println("Constructor")
	}
}
```

在大多数场景中，类的构造函数非常简明：**要么没有参数，要么直接把参数于对应的属性关联**

```kotlin
class User(val nickname:String,val isSubscribed:Boolean=false)
```

如果类有主构造函数，每个从构造函数需要委托主构造函数，可直接委托或者间接委托。

```kotlin
class User(val nickname: String) {
    var isSubscribed: Boolean?=null
    constructor(_nickname: String, _isSubscribed: Boolean) : this(_nickname) {
        this.isSubscribed = _isSubscribed
    }
}
```

如何该类有父类，应该显式的调用父类的构造方法

```kotlin
//Clickable为接口，没有构造函数
class Buttion:Clickable{

}
//即便没有任何参数，也要显示调用父类构造函数
class RiseButton:Button(){
    
}
//如果有多级构造函数，可以super关键字调用父类构造
class MyButton: View {
    constructor(ctx:Context):super(ctx)
    constructor(ctx: Context,attributes: AttributeSet?):super(ctx,attributes)
}
```

##### 内部类、嵌套类、密封类、数据类·

###### 内部类和嵌套类

`Kotlin`中嵌套类不能访问外部类的实例，类似`Java`静态内部类；而`Kotlin`中的内部类需要用`inner`关键字修饰才能访问外部类的实例。

```kotlin
class Outer {
    private val bar: Int = 1
    //内部类
    inner class Inner {
        fun foo() = bar
    }
}
class Outer2 {
    private val bar: Int = 1
    //嵌套类，不持有外部类的引用
    class Nested {
        fun foo() = 2
    }
}
val demo = Outer().Inner().foo() // == 1
val demo2 = Outer2.Nested().foo() // == 2
```

###### 密封类

密封类⽤来表⽰**受限的类继承结构**：当⼀个值为有限集中的类型、⽽不能有任何其他类型时。在某种意义上，他们是枚举类的扩展：枚举类型的值集合也是受限的，但每个枚举常量只存在⼀个实例，⽽密封类的⼀个⼦类可以有可包含状态的多个实例。

```kotlin
sealed class Expr
data class Const(val number: Double) : Expr()
data class Sum(val e1: Expr, val e2: Expr) : Expr()
object NotANumber : Expr()
```

⼀个密封类是⾃⾝抽象的，它不能直接实例化并可以有抽象（abstract）成员。
密封类不允许有⾮-private 构造函数（其构造函数默认为 private）。
请注意，扩展密封类⼦类的类（间接继承者）可以放在任何位置，⽽⽆需在同⼀个⽂件中。

###### 数据类

创建⼀些只保存数据的类。 在这些类中，⼀些标准函数往往是从数据机械推导⽽来的。在
`Kotlin` 中，这叫做 数据类 并标记为 data ：

```kotlin
data class User(val name: String, val age: Int)
```

编译器⾃动从主构造函数中声明的所有属性导出以下成员：

- `equals() / hashCode()` 对；
- `toString()` 格式是 "User(name=John, age=42)" ；
- `componentN() `函数 按声明顺序对应于所有属性；
- copy() 函数。

为了确保⽣成的代码的⼀致性以及有意义的⾏为，数据类必须满⾜以下要求：

- 主构造函数需要⾄少有⼀个参数；
- 主构造函数的所有参数需要标记为 val 或 var ；
- 数据类不能是抽象、开放、密封或者内部的；

###### 属性与字段

声明一个属性的完整语法为：

```kotlin
var <propertyName>[: <PropertyType>] [= <property_initializer>]
[<getter>]
[<setter>]
```

其初始器（initializer）、getter 和 setter 都是可选.

一个只读属性的语法和一个可变的属性的语法有两方面的不同：

- 只读属性用`val`,而可变属性用`var`声明
- 只读属性不允许有`setter`方法

默认的属性的声明为：

```kotlin
var name: String = "Kotlin"
        get() = field
        set(value) {
            field = value
        }
```

##### Object关键字

`Object`关键字定义一个类并同时创建一个实体：

- 对象声明：定义单例的方式
- 伴生对象：可持有工厂方法及其他与类相关
- 对象表达式：代替`Java`的匿名内部类

对象表达式和对象声明之间有⼀个重要的语义差别：

- 对象表达式是在使⽤他们的地⽅⽴即执⾏（及初始化）的；
-   对象声明是在第⼀次被访问到时延迟初始 化的；
-   伴⽣对象的初始化是在相应的类被加载（解析）时，与 Java 静态初始化器的语义相匹配。

###### 对象声明

对象声明将类的声明与该类的单一实例声明结合在一起。与普通类的实例不同，对象声明在定义的时候就创建了实例。

```kotlin
object PayRoll {
    val allEmployees = arrayListOf<Person>()

    fun calculateSalary(){  
    }
}
```

可以反编译看到：

**对象声明被编译成通过静态字段来持有它的单一实例的类，字段名始终为INSTANCE**

```kotlin
public final class PayRoll {
   @NotNull
   private static final ArrayList allEmployees;
   public static final PayRoll INSTANCE;

   @NotNull
   public final ArrayList getAllEmployees() {
      return allEmployees;
   }

   public final void calculateSalary() {
   }
	//构造函数私有
   private PayRoll() {
   }

   static {
      PayRoll var0 = new PayRoll();
       //静态代码块初始化化实例对象
      INSTANCE = var0;
      boolean var1 = false;
      allEmployees = new ArrayList();
   }
}
```

###### 伴生对象

`Java`的`static`关键字并不是`kotlin`的一部分，作为替代，**`kotlin`依赖包级别的函数和对象声明**，但是顶层函数不能访问类的私有成员, 需要写一个没有类实例情况下调用但需要访问类内部的函数，可以将其写为类中的对象声明的成员。

```KOTLIN
fun getFacebookName(accountId: Int) = "fb:$accountId"

class User private constructor(val nickname: String) {
    companion object {
        fun newSubscribingUser(email: String) =
            User(email.substringBefore('@'))

        fun newFacebookUser(accountId: Int) =
            User(getFacebookName(accountId))
    }
}

fun main(args: Array<String>) {
    val subscribingUser = User.newSubscribingUser("bob@gmail.com")
    val facebookUser = User.newFacebookUser(4)
    println(subscribingUser.nickname)
}
```

伴生对象作为普通对象，一样可以实现接口和扩展函数和属性

```kotlin
data class Person(val name: String) {
    object NameComparator : Comparator<Person> {
        override fun compare(p1: Person, p2: Person): Int =
            p1.name.compareTo(p2.name)
    }
}

class Person(val firstname:String,val lastname:String){
    companion object{
        //...可空，但不能省略
    }
}
fun Person.Companion.fromJson(json:String):String{
    return json.substring(4)
}
```

###### 对象表达式

object不仅可用来声明单例对象，还可以声明匿名对象，替代java内部类的用法

```kotlin
fab.setOnClickListener(
     object : View.OnClickListener {
     	override fun onClick(view: View?) {
         //....
        }
      })
```

当然，也可以将其存储到一个变量中：

```kotlin
val listener = object : View.OnClickListener {
    override fun onClick(p0: View?) {
       //....
    }
}
```

`Java`匿名内部类只能扩展一类或者实现一个接口，`kotlin`的匿名对象可以实现多个接口或者实现不同的接口。 

#### 扩展

`Kotlin` 能够扩展⼀个类的新功能⽽⽆需继承该类或者使⽤像装饰者这样的设计模式。 这通过叫做 扩展
的特殊声明完成。

###### 扩展函数

声明⼀个扩展函数，我们需要⽤⼀个 接收者类型 也就是被扩展的类型来作为他的前缀。

```kotlin
fun MutableList<Int>.swap(index1: Int, index2: Int) {
    //this 关键字在扩展函数内部对应到接收者对象
    val tmp = this[index1] // “this”对应该列表
    this[index1] = this[index2]
    this[index2] = tmp
}
```

- 扩展不能真正的修改他们所扩展的类。通过定义⼀个扩展，你并没有在⼀个类中插⼊新成员， 仅仅是可以通过该类型的变量⽤点表达式去调⽤这个新函数。
- 扩展函数是**静态分发**的，即他们不是根据接收者类型的虚⽅法。 这意味着调⽤的扩展函数是由函数调⽤所在的表达式的类型来决定的， ⽽不是由表达式运⾏时求值结果决定的
- 如果⼀个类定义有⼀个成员函数与⼀个扩展函数，⽽这两个函数⼜有相同的接收者类型、 相同的名字，
  并且都适⽤给定的参数，这种情况总是取**成员函数**。

###### 扩展属性

与函数类似，`kotlin`支持扩展属性：

```kotlin
val <T> List<T>.lastIndex: Int
	get() = size - 1
```

由于扩展没有实际的将成员插⼊类中，因此对**扩展属性来说幕后字段**是⽆效的。这就是为什么**扩展属性不能有初始化器**。他们的⾏为只能由显式提供的 getters/setters 定义。

```kotlin
val House.number = 1 // 错误：扩展属性不能有初始化器
```

实际上，扩展属性会被编译成getter/setter方法，但是没有字段：

```java
public final class KtKt {
   public static final int getLastIndex(@NotNull List $this$lastIndex) {
      Intrinsics.checkParameterIsNotNull($this$lastIndex, "$this$lastIndex");
      return $this$lastIndex.size() - 1;
   }
}
```

##### 委托

委托模式已经证明是实现继承的⼀个很好的替代⽅式， ⽽ Kotlin 可以零样板代码地原⽣⽀持它。

###### 类委托

可使用**`by`关键字**将接口的实现委托到另一个对象：

```kotlin
class CountingSet<T>(
        val innerSet: MutableCollection<T> = HashSet<T>()
) : MutableCollection<T> by innerSet {

    var objectsAdded = 0

    override fun add(element: T): Boolean {
        objectsAdded++
        return innerSet.add(element)
    }

    override fun addAll(c: Collection<T>): Boolean {
        objectsAdded += c.size
        return innerSet.addAll(c)
    }
}
```

###### 委托属性

`Koltin`支持委托属性，其语法为：

```kotlin
class Foo{
    //属性p将其访问器逻辑委托给另一个Delegate对象
    //通过by对其后的表达式求值获取代理对象
    var p:Type by Delegate()
}
//编译器创建一个隐藏的辅助属性，并使用委托对象的实例进行初始化，初始属性p会委托给该实例
class Foo{
    private val delegate = Delegate()
    var p: Type
    	set(value) = delegate.setValue(...,value)
    	get()=delegate.getvalue(...)
}

```

Delegate必须具有`getValue`和`setValue`方法，标准签名：

```kotlin
class Delegate {
    //thisRef: 被代理对象
    //property：被代理对象的描述
	operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
		return "$thisRef, thank you for delegating '${property.name}' to me!"
	}
	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
		println("$value has been assigned to '${property.name}' in $thisRef.")
	}
}
```

###### 延迟属性Lazy

backing-field技术实现延迟属性，比较啰嗦：

```kotlin
data class Email(val name: String)
fun loadEmails(person: Person): List<Email> {
    return listOf()
}
class Person(val name: String) {
    //backing-filed用来保存数据
    private var _emails: List<Email>? = null
    //用于对属性读取的访问
    val emails: List<Email>
        get() {
            if (_emails == null) {
                _emails = loadEmails(this)
            }
            return _emails!!
        }
}
```

lazy() 是接受⼀个 lambda 并返回⼀个 Lazy <T> 实例的函数，返回的实例可以作为实现延迟属性的委托： 第⼀次调⽤ get() 会执⾏已传递给 lazy() 的 lambda 表达式并记录结果， **后续调⽤get() 只是返回记录的结果**

```kotlin
class Person(val name: String) {
    val emails by lazy { 
        loadEmails(this)
    }
}
```

默认情况下，对于 lazy 属性的求值是同步锁的（synchronized）：该值只在⼀个线程中计算，并且所有线程会看到相同的值。如果初始化委托的同步锁不是必需的，这样多个线程可以同时执⾏，那么将
`LazyThreadSafetyMode.PUBLICATION` 作为参数传递给 lazy() 函数。 ⽽如果你确定初始化将总是发⽣在与属性使⽤位于相同的线程， 那么可以使⽤ `LazyThreadSafetyMode.NONE` 模式：它不会有任何线程安全的保证以及相关的开销

`lazy`实现延迟属性的原理：

```kotlin
class ObservableChangeProperty(
    val propName: String,
    var propValue: Int,
    val changeSupport: PropertyChangeSupport
) {
    fun getValue(): Int = propValue
    fun setValue(newValue: Int) {
        val oldValue = propValue
        propValue = newValue
        changeSupport.firePropertyChange(propName, oldValue, newValue)
    }
}

open class PropertyChangeAware {
    protected val changeSupport = PropertyChangeSupport(this)
    fun addPropertyChangeListener(listener: PropertyChangeListener) {
        changeSupport.addPropertyChangeListener(listener)
    }
}

class Person(val name: String, age: Int, salary: Int) : PropertyChangeAware() {
    val _age = ObservableChangeProperty("age", age, changeSupport)
    var age: Int
        get() = _age.getValue()
        set(value) {
            _age.setValue(value)
        }

    val _salary = ObservableChangeProperty("salary", salary, changeSupport)
    var salary: Int
        get() = _salary.getValue()
        set(value) {
            _salary.setValue(value)
        }
}
```

如果将`ObservaleProperty`方法签名改为适配`kotlin`的约定：

```kotlin
class ObservableChangeProperty(
    var propValue: Int,
    val changeSupport: PropertyChangeSupport
) {
    operator fun getValue(p: Person, prop: KProperty<*>): Int = propValue
    operator fun setValue(p: Person, prop: KProperty<*>, newValue: Int) {
        val oldValue = propValue
        propValue = newValue
        changeSupport.firePropertyChange(prop.name, oldValue, newValue)
    }
}
class Person(val name: String, age: Int, salary: Int) : PropertyChangeAware() {
    var age: Int by ObservableChangeProperty(age, changeSupport)
    val salary: Int by ObservableChangeProperty(salary, changeSupport)
}
```

###### 可观察属性 Observable

`Delegates.observable()` 接受两个参数：初始值与修改时处理程序（`handler`）。 每当我们给属性赋值时会调⽤该处理程序（在赋值后执⾏). 如果你想能够截获⼀个赋值并“否决”它，就使⽤ `vetoable()` 取代 `observable() `。 在属性被赋新值⽣效之前会调⽤传递给` vetoable` 的处理程序。

```kotlin
class Person(
    val name: String, age: Int, salary: Int
) :PropertyChangeAware(){
    private val observer = { prop: KProperty<*>, oldvalue: Int, newValue: Int ->
		 changeSupport.firePropertyChange(prop.name, oldValue, newValue)
    }
    var age: Int by Delegates.observable(age, observer)
    var salary: Int by Delegates.observable(salary, observer)
}
```

###### 把属性存储在映射中

委托属性另一种常用用法, 是用在动态定义的属性集的对象中，这种对象有时被称为自订对象(`Expando`)。

```kotlin
class Person {
    private val _attributes = hashMapOf<String, String>()

    fun setAttribute(attrName: String, value: String) {
        _attributes[attrName] = value
    }

    val name: String
        get() = _attributes["name"]!!
}
```

使用委托属性把值存储到map中

```kotlin
class Person {
    private val _attributes = hashMapOf<String, String>()

    fun setAttribute(attrName: String, value: String) {
        _attributes[attrName] = value
    }
    val name: String by _attributes
}
```

###### 委托属性的要求

- 对于⼀个只读属性（即 val 声明的），委托必须提供⼀个名为` getValue `的函数，该函数接受以下参
  数, 函数的返回值与属性相同的类型或其子类型：
  - `thisRef `—— 必须与 属性所有者 类型（对于扩展属性——指被扩展的类型）相同或者是它的超类
    型；
  - `property` —— 必须是类型` KProperty<*>` 或其超类型。
- 对于⼀个可变属性（即 var 声明的），委托必须额外提供⼀个名为 `setValue` 的函数，该函数接受以下
  参数：
  - `thisRef` —— 必须与 属性所有者 类型（对于扩展属性——指被扩展的类型）相同或者是它的超类
    型；
  - `property` —— 必须是类型` KProperty<*>` 或其超类型
  - `newValue` —— 必须与属性同类型或者是它的⼦类型。

`getValue() `或/与 `setValue() `函数可以通过委托类的成员函数提供或者由扩展函数提供。 当你需要委托属性到原本未提供的这些函数的对象时后者会更便利。 两函数都需要⽤ `operator` 关键字来进⾏标记.

委托类可以实现包含所需 operator ⽅法的 `ReadOnlyProperty` 或 `ReadWriteProperty` 接⼝之⼀.

```kotlin
interface ReadOnlyProperty<in R, out T> {
	operator fun getValue(thisRef: R, property: KProperty<*>): T
}
interface ReadWriteProperty<in R, T> {
	operator fun getValue(thisRef: R, property: KProperty<*>): T
	operator fun setValue(thisRef: R, property: KProperty<*>, value: T)
}
```


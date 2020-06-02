### Kotlin基础(七）Java与Kotlin互操作性

#### `Java`中调用`Kotlin`

##### 属性

kotlin属性会编译成`java`元素：

* `getter`方法，名称通过加前缀`get`算出
* `setter`方法，名称通过加前缀`set`算出（`var`属性）

* 一个私有字段，与属性名相同（具有幕后字段的属性）

```kotlin
//kotlin
var firstName:String
```

```java
//java
private String firstName;
public String getFirstName(){
    return firstName;
}
public void setFirstName(String firstName){
    this.firstName= firstName;
}
```

若属性的名称是`is`开头，则使用不同的名称映射规则：`getter`的名称与属性相同，并且`setter`的名称通过将`is`替换为`set`获取。

##### 包级函数

在`com.showdy`包内的`app.kt`文件中声明的所有函数和属性，包含扩展函数和属性，都编译成为`com.showdy.AppKt`的`java`类静态方法：

```kotlin
//kotlin
@file: JvmName("AppUtils")

package com.showdy

import java.lang.reflect.Type

val String.lastChar: Char
    get() = this[length - 1]


fun Type.isPrimitiveOrString(): Boolean {
    val cls = this as? Class<Any> ?: return false
    return cls.kotlin.javaPrimitiveType != null || cls == String::class.java
}
```

```java
//java
public final class AppUtils {
   public static final char getLastChar(@NotNull String $receiver) {
      Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
      return $receiver.charAt($receiver.length() - 1);
   }

   public static final boolean isPrimitivesOrString(@NotNull Type $receiver) {
      //....
   }
}
```

可以使用`fiel:JvmName("xxx")`注解修改`Java`类的类名。如果多个文件中生成相同的`java`类名，通常是错误的，然而编译器能够生成一个单一的`java`外观类，它具有指定的名称且包含来自所有文件中具有该名称的所有声明。启用该外观类，使用`@JvmMultifileClass`

##### 实例字段

如果需要在 `Java` 中将`Kotlin` 属性作为字段暴露，那就使⽤ `@JvmField` 注解对其标注。 该字段将具有与底层属性相同的可⻅性。如果⼀个属性有幕后字段（`backing field`）、⾮私有、没有 `open / override`或者 `const` 修饰符并且不是被委托的属性，那么你可以⽤ `@JvmField` 注解该属性。

```kotlin
class User(id:String){
    @JvmField 
    val ID= id
    
    lateinit var name:String

    init {
        name= "showdy"
    }
}
```

```java
public final class User {
   @JvmField
   @NotNull
   public final String ID; //字段，无getter方法
   @NotNull
   public String name; //可见性为public

   @NotNull
   public final String getName() {
       //...
   }

   public final void setName(@NotNull String var1) {
    //...
   }

   public User(@NotNull String id) {
      this.ID = id;
      this.name = "showdy";
   }
}
```

##### 静态字段

在**命名对象或伴⽣对象**中声明的 Kotlin 属性会在该命名对象或包含伴⽣对象的类中具有静态幕后字段。
通常这些字段是私有的，但可以通过以下⽅式之⼀暴露出来：

- `@JvmField` 注解；

- `lateinit` 修饰符；

- `const` 修饰符。

使⽤ `@JvmField `标注这样的属性使其成为与**属性本⾝具有相同可⻅性的静态字段**。

  ```kotlin
class Key(val value: Int) {
    companion object {
        @JvmField
        val COMPARATOR: Comparator<Key> = compareBy<Key> { it.value }
    }
}
  ```

```java
//java
Key.COMPARATOR.compare(key1, key2);
// Key 类中的 public static final 字段
```

在命名对象或者伴⽣对象中的⼀个延迟初始化的属性具有与属性 setter 相同可⻅性的静态幕后字段

```kotlin
object Singleton {
	lateinit var provider: Provider
}
```

```java
//java
Singleton.provider = new Provider();
// 在 Singleton 类中的 public static ⾮-final 字段
```

（在类中以及在顶层）以 const 声明的属性在 Java 中会成为静态字段：

```kotlin
//文件 example.kt
object Obj {
	const val CONST = 1
}
class C {
	companion object {
		const val VERSION = 9
	}
}
const val MAX = 239
```

```java
//java
int const = Obj.CONST;
int max = ExampleKt.MAX;
int version = C.VERSION;
```


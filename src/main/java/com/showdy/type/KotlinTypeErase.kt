package com.showdy.type

import java.awt.image.renderable.ParameterBlock
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.typeOf

// 3。kotlin中泛型运行时的状态：类型擦除


@UseExperimental(ExperimentalStdlibApi::class)
fun main() {
    val appleList = ArrayList<Apple>()
    println(appleList.javaClass)

    //koltin中数组是支持泛型的，当然也不再协变
    val appleArr = arrayOfNulls<Apple>(3)
    //    val anyArr:Array<Any?> = appleArr


    val applePlate = Plate(Apple(1.0),Apple::class.java)

    //class com.showdy.type.Apple
    applePlate.getType()


    //2. 使用匿名内部类的方式获取类型参数
    val list1 = ArrayList<String>()
    val list2 = object :ArrayList<String>(){}
    //java.util.AbstractList<E>
    println(list1.javaClass.genericSuperclass)
    //java.util.ArrayList<java.lang.String>
    println(list2.javaClass.genericSuperclass)

    //3. typeof
    println("----------------")

    val kType = typeOf<Map<String,Int>>()
    kType.arguments.forEach { println(it) }


}

//1. 在运行时主动指定参数来达到获取运行类型
open class Plate<T>(val t:T,val clazz: Class<T>){

    fun getType()= println(clazz)
}

//2。匿名内部类的方式获取泛型参数
open class GenericToken<T>{
    var type:Type= Any::class.java

    init {
        val superClass = this.javaClass.genericSuperclass
        type = (superClass as ParameterizedType).actualTypeArguments[0]
    }

    val typeParamter by lazy {
        this::class.supertypes.first().arguments.first().type
    }

    val javaType by lazy {
        ((this.javaClass.genericSuperclass) as ParameterizedType).actualTypeArguments.first()
    }
}

//3. typeof





//内联特化
inline fun <reified T> genericMethod(t:T){

//    val t= T()

    val  ts = Array<T>(3){ TODO()}

    val jClass = T::class.java

    val list = ArrayList<T>()
}
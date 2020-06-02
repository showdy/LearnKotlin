package com.showdy.type

import java.lang.IndexOutOfBoundsException
import java.util.*


//OUT: 协变，可读，不可写，生产者
//java中类似: <? extend T>
//声明处型变
class PlateOut<out T> {

    val t: T? = null

    fun fruits(): T = TODO()
}

//IN-逆变，可写不可读，消费者
//java中类似：<? super T>
interface WritableList<in T> {

    fun add(t: T)

    //可读功能受限，只能是Any类型
    fun get(index: Int): Any
}


//可以使用Number代替所有数字的比较
//使用处型变
fun <T> MutableList<T>.sortWith(comparator: Comparator<in T>): Unit {

    if (size > 1) Collections.sort(this, comparator)
}


//将一个double数组拷贝到另一个double数组
fun <T> copyArr(dest: Array<in T>, src: Array<out T>) {
    if (dest.size < src.size) {
        throw IndexOutOfBoundsException()
    } else {
        src.forEachIndexed { index, value ->
            dest[index] = src[index]
        }
    }
}

fun main() {
    //将int数组，拷贝进Number数组，反过来不行
    val dest = arrayOfNulls<Number>(3)
    val src = arrayOfNulls<Int>(3)
    copyArr(dest,src)
}

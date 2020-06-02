package com.showdy.type

// 1. kotlin中使用泛型
fun <T> ArrayList<T>.find(t: T): T? {
    val index = this.indexOf(t)
    return if (index>=0) this.get(index) else null
}

fun main() {
    val arrayList = ArrayList<String>()
    arrayList.add("hello")
    println(arrayList.find("hello"))
    println(arrayList.find("kotlin"))
}


package com.showdy.type

// 通配符

fun main() {
    //不可添加元素，会导致类型不安全
    //本质上是：<out Any?>
    val list1:MutableList<*> = mutableListOf(1,"kotlin")
//    list1.add(2)
    val list2: MutableList<Any?> = mutableListOf(1,"koltin")
    list2.add(2)

    //--------------------

    val queryMap:QuerMap<*,*> = QuerMap<String,Int>()
    val key:CharSequence = queryMap.getKey()
    val value:Any = queryMap.getValue()

    //无法调用。。。
    val function:Functions<*,*> = Functions<Number,Int>()
//    function.invoke()

}

// * 所替换的类型在：
// 1. 协变点返回泛型参数上限类型
// 2. 逆变点接受泛型参数下限类型-->Nothing

class QuerMap<out K:CharSequence,out V:Any>{
    fun getKey():K = TODO()
    fun getValue():V = TODO()
}


class Functions<in P1,in P2>{
    fun invoke(p1:P1,p2: P2)= Unit
}

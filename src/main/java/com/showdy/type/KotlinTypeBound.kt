package com.showdy.type

//2.kotlin中泛型类型约束

sealed class Fruit(val weight:Double)

class Apple(weight: Double):Fruit(weight)

class Banana(weight: Double):Fruit(weight)

class FruitPlate<T:Fruit?>(val t:T)

//地上长的水果，起标记作用
interface Ground{}

class Watermelon(weight: Double):Fruit(weight),Ground

//多重约束使用where表示
fun <E> cut(e:E) where E:Fruit,E:Ground{
    println("you can cut me")
}



fun main() {

    val applePlate = FruitPlate<Apple>(Apple(1.0))
    val bananaPlate = FruitPlate<Banana>(Banana(2.0))

    cut(Watermelon(3.0))

//    cut(Apple(1.0))
}
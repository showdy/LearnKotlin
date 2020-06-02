package com.showdy.type

import java.util.concurrent.ConcurrentHashMap
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

//通过泛型注入

inline fun <reified T: AbsModel> modelOf(): ModelDelegate<T> {
    return ModelDelegate(T::class)
}

class ModelDelegate<T : AbsModel>(val kClass: KClass<T>) : ReadOnlyProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return Models.run { kClass.get() }
    }
}

object Models {
    private val modelMap = ConcurrentHashMap<Class<out AbsModel>, AbsModel>()

    fun <T : AbsModel> KClass<T>.get(): T {
        return modelMap[this.java] as T
    }

    fun AbsModel.register() {
        modelMap[this.javaClass] = this
    }
}

abstract class AbsModel {
    init {
        Models.run { register() }
    }
}

class DatabaseModel : AbsModel() {
    fun query(sql: String): Int = 0
}

class NetworkModel : AbsModel() {
    fun get(url: String): String = """{"code": 0}"""
}

val x: Int
    get() {
        return Math.random().toInt()
    }

class MainViewModel {
    val databaseModel by modelOf<DatabaseModel>()
    val networkModel by modelOf<NetworkModel>()
}

fun initModels() {
    DatabaseModel()
    NetworkModel()
}

fun main() {
    initModels()
    val mainViewModel = MainViewModel()
    mainViewModel.databaseModel.query("select * from mysql.user").let(::println)
    mainViewModel.networkModel.get("https://www.imooc.com").let(::println)
}
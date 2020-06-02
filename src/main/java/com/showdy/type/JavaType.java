package com.showdy.type;

import java.util.ArrayList;
import java.util.List;

public class JavaType {

    public static void main(String[] args) {

        List stringList = new ArrayList();
        //编译未检测类型
        stringList.add("1");
        //需要强制转换，容易报错
        String str = (String) stringList.get(0);

        //泛型带来的好处：
        //1. 编译时期类型检测
        //2.运行时类型自动转换
        List<String>  arrs = new ArrayList<>();
        arrs.add("kotlin");
        String kotlin = arrs.get(0);
        System.out.println(kotlin);


        // 2. java类型擦除
        Apple[] apples = new Apple[10];
        Fruit[] fruits = apples; //允许，java数组是协变的

        List<Apple> appleList = new ArrayList<>();
        //不允许，因为java中List是不变的
//        List<Fruit>  fruitList = appleList;

        //java中泛型擦除，导致无法在允许时获取一个对象的具体类型
        //class java.util.ArrayList
        //class [Lcom.showdy.type.Apple;
        System.out.println(appleList.getClass());
        System.out.println(apples.getClass());


    }

//    class Fruit{
//        double weight;
//
//        public Fruit(double weight) {
//            this.weight = weight;
//        }
//    }
//
//    class Apple extends Fruit{
//
//        public Apple(double weight) {
//            super(weight);
//        }
//    }

}

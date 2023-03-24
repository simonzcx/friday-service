package com.friday;

import javassist.*;

import java.io.IOException;

/**
 * @Author chengxu.zheng
 * @Create ${DATE} ${TIME}
 * @Description
 */
public class Main {

    public static void main(String[] args) {
        updateMethod();
    }

    public static void updateMethod(){
        try {
            ClassPool cPool = new ClassPool(true);
            //如果该文件引入了其它类，需要利用类似如下方式声明
            //cPool.importPackage("java.util.List");

            //设置class文件的位置
            cPool.insertClassPath("D:\\Java\\eclipse\\newworkspace\\test\\bin");

            //获取该class对象
            CtClass cClass = cPool.get("com.lucumt.Test1");

            //获取到对应的方法
            CtMethod cMethod = cClass.getDeclaredMethod("addNumber");

            //更改该方法的内部实现
            //需要注意的是对于参数的引用要以$开始，不能直接输入参数名称
            cMethod.setBody("{ return $1*$1*$1+$2*$2*$2; }");

            //替换原有的文件
            cClass.writeFile("D:\\Java\\eclipse\\newworkspace\\test\\bin");

            System.out.println("=======修改方法完=========");
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
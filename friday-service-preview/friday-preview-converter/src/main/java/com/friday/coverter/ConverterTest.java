package com.friday.coverter;

import com.friday.coverter.pojo.Student;
import net.sf.cglib.beans.BeanCopier;
import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Test;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * @Author chengxu.zheng
 * @Create 2022/8/23 15:56
 * @Description
 */
public class ConverterTest {

    private Student source = new Student("simon", "小明", 20);

    private Student target = new Student();

    private Integer count = 1000000;

    @Test
    public void testCommonsPropertyUtils() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        long startTimes = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            PropertyUtils.copyProperties(target, source);
        }
        long endTimes = System.currentTimeMillis();
        System.out.println("耗时: " + (endTimes - startTimes) + "ms");
        //881ms
    }


    @Test
    public void testCommonsBeanUtils() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        long startTimes = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            org.apache.commons.beanutils.BeanUtils.copyProperties(target, source);
        }
        long endTimes = System.currentTimeMillis();
        System.out.println("耗时: " + (endTimes - startTimes) + "ms");
        //17790ms
    }

    @Test
    public void testSpringBeanUtils() {
        long startTimes = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            BeanUtils.copyProperties(source, target);
        }
        long endTimes = System.currentTimeMillis();
        System.out.println("耗时: " + (endTimes - startTimes) + "ms");
        //252ms
    }

    @Test
    public void testCglib() {
        long startTimes = System.currentTimeMillis();
        BeanCopier copier = BeanCopier.create(Student.class, Student.class, false);
        for (int i = 0; i < count; i++) {
            copier.copy(source, target, null);
        }
        long endTimes = System.currentTimeMillis();
        System.out.println("耗时: " + (endTimes - startTimes) + "ms");
        //初始化BeanCopier耗时: 148ms
        //不初始化BeanCopier耗时: 43ms
    }

    @Test
    public void testGetterAndSetter() {
        long startTimes = System.currentTimeMillis();
        for (int i = 0; i < count; i++) {
            target.setCode(source.getCode());
            target.setName(source.getName());
            target.setAge(source.getAge());
        }
        long endTimes = System.currentTimeMillis();
        System.out.println("耗时: " + (endTimes - startTimes) + "ms");
        //12ms
    }

}

package com.friday.coverter.pojo;

import java.io.Serializable;

/**
 * @Author chengxu.zheng
 * @Create 2022/8/23 15:55
 * @Description
 */
public class Student implements Serializable {

    private String code;

    private String name;

    private Integer age;

    public Student(){}

    public Student(String code, String name, Integer age) {
        this.code = code;
        this.name = name;
        this.age = age;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}

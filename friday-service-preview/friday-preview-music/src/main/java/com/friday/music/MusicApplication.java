package com.friday.music;

import org.python.util.PythonInterpreter;

/**
 * @Author chengxu.zheng
 * @Create ${DATE} ${TIME}
 * @Description
 */
public class MusicApplication {

    public static void main(String[] args) {
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("a=[5,4,3,2,1]");
        interpreter.exec("print(sorted(a))");
    }

}
package com.friday.fiction.util;

import com.sun.xml.internal.ws.util.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author chengxu.zheng
 * @Create 2022/10/24 10:46
 * @Description
 */
public class ReadTxtUtil {

    private ReadTxtUtil() {
    }

    public static final String DEFAULT_CHARSET = "\t";
    public static final String GBK = "GBK";
    public static final String UTF_8 = "UTF-8";
    public static final String UTF_16 = "UTF-16";
    public static final String UNICODE = "Unicode";
    public static final String HTTP = "http";

    public static StringBuilder readTxtFile(String filePath) throws IOException {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = getInputStream(filePath);
        if (inputStream == null) {
            return sb;
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, resolveCharset(filePath));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String text;
        // 使用readLine方法，一次读一行
        while ((text = bufferedReader.readLine()) != null) {
            sb.append(text).append(System.lineSeparator());
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        return sb;
    }

    /**
     * 优化读取txt工具，防止io阻塞
     *
     * @param filePath
     * @return
     */
    public static String readTxtFileString(String filePath) {
        String content = null;
        try (Reader reader = new InputStreamReader(Files.newInputStream(Paths.get(filePath)), resolveCharset(filePath))) {
            StringBuilder sb = new StringBuilder();
            char[] tempChars = new char[1024];
            while (reader.read(tempChars) != -1) {
                sb.append(tempChars);
            }
            content = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static <T> List<T> readTxtFile(String filePath, Class<T> clazz) throws Exception {
        return readTxtFile(filePath, clazz, DEFAULT_CHARSET);
    }

    public static <T> List<T> readTxtFile(String filePath, Class<T> clazz, String separator) throws Exception {
        return readTxtFile(getInputStream(filePath), clazz, separator, resolveCharset(filePath));
    }

    public static <T> List<T> readTxtFile(InputStream inputStream, Class<T> clazz) throws Exception {
        return readTxtFile(inputStream, clazz, DEFAULT_CHARSET);
    }

    public static <T> List<T> readTxtFile(InputStream inputStream, Class<T> clazz, String separator) throws Exception {
        String charset = resolveCharset(inputStream);
        return readTxtFile(inputStream, clazz, separator, charset);
    }

    public static <T> List<T> readTxtFile(InputStream inputStream, Class<T> clazz, String separator, String charset) throws Exception {
        List<T> list = new ArrayList<>();
        if (inputStream == null) {
            return list;
        }
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String text;
        int line = 0;
        // 使用readLine方法，一次读一行
        while ((text = bufferedReader.readLine()) != null) {
            if (line > 0) {
                String[] split = text.split(separator);
                T t = clazz.getDeclaredConstructor().newInstance();
                Field[] declaredFields = t.getClass().getDeclaredFields();
                for (int i = 0; i < split.length; i++) {
                    declaredFields[i].setAccessible(true);
                    declaredFields[i].set(t, split[i]);
                }
                list.add(t);
            }
            line++;
        }
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        return list;
    }


    /**
     * 获取txt文件编码
     *
     * @param filePath
     * @return
     */
    public static String resolveCharset(String filePath) {
        return resolveCharset(getInputStream(filePath));
    }

    /**
     * 获取txt文件编码
     *
     * @param inputStream
     * @return
     */
    public static String resolveCharset(InputStream inputStream) {
        if (inputStream == null) {
            return GBK;
        }
        byte[] head = new byte[3];
        try {
            inputStream.read(head);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (head[0] == -1 && head[1] == -2) {
            return UTF_16;
        } else if (head[0] == -2 && head[1] == -1) {
            return UNICODE;
        } else if (head[0] == -17 && head[1] == -69 && head[2] == -65) {
            return UTF_8;
        }
        return GBK;
    }

    public static InputStream getInputStream(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        // 区分本地文件和本地文件
        if (filePath.contains(HTTP)) {
            //oss
            try {
                return new URL(filePath).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 本地文件上传测试
            try {
                return new FileInputStream(filePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}

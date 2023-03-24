package com.friday.fiction.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author chengxu.zheng
 * @Create 2022/10/23 18:47
 * @Description
 */
public class TxtUtil {

    private static final String REG_STR = "^[0-9].*";
    private static final String REG_STR2 = "\\d+";


    public static void main(String[] args) throws IOException {
        // 要替换的文本
        String filePath = "/Users/simon/workspace/idea/friday/friday-service/friday-service-preview/friday-preview-api-tool/src/main/resources/八一物流誉满全球.txt";
        // 替换之后要保存的路径
        String tmpFilePath = "/Users/simon/workspace/idea/friday/friday-service/friday-service-preview/friday-preview-api-tool/src/main/resources/八一物流誉满全球1.txt";
        saveAsUTF8(filePath, tmpFilePath);
    }

    /*public static void main(String[] args) throws IOException {
        // 要替换的文本
        String filePath = "/Users/simon/workspace/idea/friday/friday-service/friday-service-preview/friday-preview-api-tool/src/main/resources/八一物流誉满全球1.txt";
        // 替换之后要保存的路径
        String tmpFilePath = "/Users/simon/workspace/idea/friday/friday-service/friday-service-preview/friday-preview-api-tool/src/main/resources/八一物流誉满全球2.txt";
        // 读取文件内容
        //List<String> lines = Files.readAllLines(Paths.get(filePath), Charset.forName("Unicode"));
        List<String> lines = readFileByLines(filePath);
        // 匹配 开头为数字的行
        Pattern pattern = Pattern.compile(REG_STR);

        List<String> finalLines = new ArrayList<>();
        String temp;
        String newLine;
        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            newLine = line;
            if (matcher.find()) {
                temp = matcher.group();
                newLine = gen(temp);
            }
            if (Objects.nonNull(newLine)) {
                finalLines.add(newLine);
            }
        }

        // 输出
        Files.write(Paths.get(tmpFilePath), finalLines, StandardCharsets.UTF_8);
    }*/

    public static String gen(String str) {
        Pattern p = Pattern.compile(REG_STR2);
        Matcher m = p.matcher(str);
        if (m.find()) {
            // 提取数字
            String group = m.group();
            // 进行替换
            return str.replaceFirst(group, "第" + group + "章");
        }
        return null;
    }

    /**
     * @param inputFileUrl
     * @param outputFileUrl
     * @throws IOException
     */
    public static void saveAsUTF8(String inputFileUrl, String outputFileUrl) throws IOException {
        String inputFileEncode = ReadTxtUtil.resolveCharset(inputFileUrl);
        System.out.println("inputFileEncode===" + inputFileEncode);
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(Paths.get(inputFileUrl)), inputFileEncode));
        BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(Files.newOutputStream(Paths.get(outputFileUrl)), StandardCharsets.UTF_8));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            bufferedWriter.write(line + "\r\n");
        }
        bufferedWriter.close();
        bufferedReader.close();
        String outputFileEncode = ReadTxtUtil.resolveCharset(outputFileUrl);
        System.out.println("outputFileEncode===" + outputFileEncode);
        System.out.println("txt文件格式转换完成");
    }

    public static List<String> readFileByLines(String fileName) {
        List<String> list = new ArrayList<>();
        File file = new File(fileName);

        BufferedReader reader = null;
        InputStream inputStream = null;
        try {
            // 判断的文件输入流
            inputStream = Files.newInputStream(file.toPath());
            byte[] head = new byte[3];
            inputStream.read(head);
            //判断TXT文件编码格式
            reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), getFileCharset(file)));
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                list.add(tempString);
            }
            inputStream.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return list;
    }

    private static String getFileCharset(File sourceFile) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(sourceFile.toPath()));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                //文件编码为 ANSI
                return charset;
            } else if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                //文件编码为 Unicode
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                //文件编码为 Unicode big endian
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
                //文件编码为 UTF-8
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0) {
                        break;
                    }
                    if (0x80 <= read && read <= 0xBF) {
                        // 单独出现BF如下的，也算是GBK
                        break;
                    }
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            // 双字节 (0xC0 - 0xDF)
                            // (0x80
                            // - 0xBF),也可能在GB编码内
                            continue;
                        } else {
                            break;
                        }
                    } else if (0xE0 <= read && read <= 0xEF) {
                        // 也有可能出错，可是概率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }


}

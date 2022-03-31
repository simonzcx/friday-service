package com.friday.freemarker.util;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * FreeMark模板解析工具类
 *
 * @author Simon.z
 * @since 2021/03/31
 */
public class FreeMarkTemplateLoader implements TemplateLoader {

    private static final String DEFAULT_TEMPLATE_KEY = "_default_template_key";

    private static final String DEFAULT_FREE_MARK_NUMBER_FORMAT = "0";

    private static final String SYSTEM_DEFAULT_CHARSET = "UTF-8";

    private static final String STRING_EMPTY = "";

    private static final String NUMBER_FORMAT;

    private static final String PARAM_PREFIX = "${";

    private static final String PARAM_SUFFIX = "}";

    static {
        NUMBER_FORMAT = System.getProperty("com.friday.free-mark.number-format", DEFAULT_FREE_MARK_NUMBER_FORMAT);
    }

    private Map templates = new HashMap();

    public FreeMarkTemplateLoader(String defaultTemplate) {
        if (defaultTemplate != null && !defaultTemplate.equals(STRING_EMPTY)) {
            templates.put(DEFAULT_TEMPLATE_KEY, defaultTemplate);
        }
    }

    public void AddTemplate(String name, String template) {
        if (name == null || template == null || name.equals(STRING_EMPTY) || template.equals(STRING_EMPTY)) {
            return;
        }
        if (!templates.containsKey(name)) {
            templates.put(name, template);
        }
    }

    @Override
    public Object findTemplateSource(String name) {
        if (name == null || name.equals(STRING_EMPTY)) {
            name = DEFAULT_TEMPLATE_KEY;
        }
        return templates.get(name);
    }

    @Override
    public long getLastModified(Object o) {
        return 0;
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        return new StringReader((String) templateSource);
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {

    }

    /**
     * 解析模板
     *
     * @param temp "欢迎：${user}"
     * @param cond cond.put("user", "Simon.z")
     * @return 解析后的值
     */
    public static String resolve(String temp, Map<String, Object> cond) {
        if (temp == null || STRING_EMPTY.equals(temp)) {
            return temp;
        }

        Configuration config = new Configuration(Configuration.VERSION_2_3_28);
        config.setTemplateLoader(new FreeMarkTemplateLoader(temp));
        config.setDefaultEncoding(SYSTEM_DEFAULT_CHARSET);
        // 如果填充值中与模板不匹配，或者为null值，则默认此值为空；
        config.setClassicCompatible(true);
        // freemarker在解析数据格式的时候，默认将数字按3位来分割 例如1000被格式化为1,000
        config.setNumberFormat(NUMBER_FORMAT);
        // 简单重新抛出所有异常而不会做其他的事情
        config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        try {
            Template template = config.getTemplate(STRING_EMPTY);
            StringWriter writer = new StringWriter();
            template.process(cond, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("消息模板解析失败" + e.getMessage(), e);
        }
    }

    /**
     * 校验解析模板
     *
     * @param temp "欢迎：${user}"
     * @param cond cond.put("user", "Simon.z")
     * @return 解析后的值
     */
    public static String verifyResolve(String temp, Map<String, Object> cond) {
        if (temp == null || STRING_EMPTY.equals(temp)) {
            return temp;
        }

        String result = temp;
        if (temp.contains(PARAM_PREFIX) && temp.contains(PARAM_SUFFIX)) {
            //${user} -> ${user!}
            //我是${user!} -> user不存在 -> 我是
            result = temp.replaceAll(PARAM_SUFFIX, "!}");
        }

        return resolve(result, cond);
    }

    public static void main(String[] args) {
        String temp = "我是${user}";
        HashMap<String, Object> map = new HashMap<String, Object>() {{
            //put("user", "Simon");
            put("name", "Simon");
        }};

        String verifyResolve = verifyResolve(temp, map);
        System.out.println(verifyResolve);
    }
}

package com.friday.elasticsearch.model;

/**
 * document是ES里的一个JSON对象，
 * 包括零个或多个field，类比关系数据库的一行记录
 *
 * @author Simon.z
 * @since 2021/12/15
 */
public class BaseDocument {

    /**
     * 数据唯一标识
     */
    private String id;

    /**
     * 索引名称
     */
    private String index;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}

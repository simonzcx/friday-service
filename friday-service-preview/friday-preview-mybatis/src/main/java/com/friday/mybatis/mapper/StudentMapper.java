package com.friday.mybatis.mapper;

import com.friday.mybatis.pojo.data.StudentDO;

public interface StudentMapper {

    StudentDO selectById(Integer id);
}

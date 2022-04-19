package com.friday.mybatis;

import com.friday.mybatis.mapper.StudentMapper;
import com.friday.mybatis.pojo.data.StudentDO;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class MyBatisTest {

    @Test
    public void testMyBatisBuild() throws IOException {
        InputStream input = Resources.getResourceAsStream("SqlSessionConfig.xml");
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(input);
        SqlSession sqlSession = sessionFactory.openSession();
        StudentMapper mapper = sqlSession.getMapper(StudentMapper.class);
        StudentDO student = mapper.selectById(1);
        System.out.println(student);
    }
}

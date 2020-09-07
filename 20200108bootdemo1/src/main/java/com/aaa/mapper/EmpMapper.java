package com.aaa.mapper;

import com.aaa.entity.Emp;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Administrator
 */
public interface EmpMapper {


    List<Emp> query();
}

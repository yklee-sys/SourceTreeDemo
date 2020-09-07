package com.aaa.controller;

import com.aaa.entity.Emp;
import com.aaa.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Administrator
 */
@Controller
@RequestMapping("/emp")
//@RestController    @Controller+@ResponseBody:类中的所有方法的返回值都转成json数据
public class EmpController {
    @Autowired
    private EmpService empService;

    @RequestMapping("/query")
    @ResponseBody
    public List<Emp> query(){
<<<<<<< HEAD
System.out.println("B修改了该文件");
=======
System.out.println("A修改了该文件");
>>>>>>> origin/master
        List<Emp> empList = empService.query();
        return empList;
    }
}

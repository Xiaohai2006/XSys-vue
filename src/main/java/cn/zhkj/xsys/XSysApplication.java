package cn.zhkj.xsys;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.zhkj.xsys.mapper")
public class XSysApplication {

    public static void main(String[] args) {
        SpringApplication.run(XSysApplication.class, args);
    }
}

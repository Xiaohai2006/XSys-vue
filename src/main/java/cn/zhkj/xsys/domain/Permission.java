package cn.zhkj.xsys.domain;

import lombok.Data;

@Data
public class Permission {
    private Long id;
    private String code;
    private String name;
    private String description;
}

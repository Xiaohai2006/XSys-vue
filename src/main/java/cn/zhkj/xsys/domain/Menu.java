package cn.zhkj.xsys.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class Menu {
    private Long id;
    private Long parentId;
    private String title;
    private String path;
    private String icon;
    private Integer sortOrder;
    private String permissionCode;
    private String menuType;
    private Boolean enabled;
    private List<Long> visibleRoles;
}

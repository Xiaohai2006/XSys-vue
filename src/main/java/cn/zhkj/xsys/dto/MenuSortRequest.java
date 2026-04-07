package cn.zhkj.xsys.dto;

import java.util.List;

public record MenuSortRequest(List<MenuItem> items) {
    public record MenuItem(Long id, Long parentId, Integer sortOrder) {}
}

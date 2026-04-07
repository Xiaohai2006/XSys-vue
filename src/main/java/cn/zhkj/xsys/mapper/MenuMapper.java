package cn.zhkj.xsys.mapper;

import cn.zhkj.xsys.domain.Menu;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MenuMapper {

    Menu findById(@Param("id") Long id);

    List<Menu> findAllOrdered();

    long count();

    int insert(Menu menu);

    int update(Menu menu);

    int updateSortOrder(@Param("id") Long id, @Param("parentId") Long parentId, @Param("sortOrder") Integer sortOrder);

    int deleteById(@Param("id") Long id);
}

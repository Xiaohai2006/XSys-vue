package cn.zhkj.xsys.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SettingMapper {

    String getValue(@Param("key") String key);

    int upsert(@Param("key") String key, @Param("value") String value);
}

package cn.zhkj.xsys.dto;

import java.time.LocalDate;

public record DailyUserStatsDto(LocalDate date, long count) {}

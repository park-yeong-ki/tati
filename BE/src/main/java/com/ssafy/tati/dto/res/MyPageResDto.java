package com.ssafy.tati.dto.res;

import com.ssafy.tati.entity.Attendance;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyPageResDto {

    @Schema(description = "회원 이미지")
    private String img;

    @Schema(description = "오늘 공부시간")
    private String todayStudyTime;

    @Schema(description = "총 공부시간")
    private String totalStudyTime;

    @Schema(description = "열정지수")
    private Integer totalScore;

    @Schema(description = "스터디 일정 리스트")
    private List<StudyResDto> studyScheduleList;

    @Schema(description = "할 일 리스트")
    private List<ScheduleResDto> scheduleList;

    @Schema(description = "입퇴실 리스트")
    private List<AttendanceResDto> attendanceList;

}

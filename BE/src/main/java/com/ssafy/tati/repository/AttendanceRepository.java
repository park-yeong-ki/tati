package com.ssafy.tati.repository;

import com.ssafy.tati.entity.Attendance;
import com.ssafy.tati.entity.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    Optional<Attendance> findByStudyMemberAndInTimeBetween(StudyMember studyMember, LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Attendance> findAllByIsAttendedIsNull();

    @Query("select a from Attendance as a where a.studyMember.studyMemberId = :studyMemberId")
    List<Attendance> findByStudyMemberId(@Param("studyMemberId") Integer studyMemberId);

    @Query("select a from Attendance as a where a.member.memberId = :memberId order by a.attendanceId desc")
    List<Attendance> findByIdOOrderByAttendanceId(@Param("memberId") Integer memberId);
}

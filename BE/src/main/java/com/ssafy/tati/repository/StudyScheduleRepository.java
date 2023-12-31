package com.ssafy.tati.repository;

import com.ssafy.tati.entity.Study;
import com.ssafy.tati.entity.StudySchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyScheduleRepository extends JpaRepository<StudySchedule, Integer> {
    Optional<StudySchedule> deleteStudySchedulesByStudyStudyId(Integer studyId);

    Optional<StudySchedule> findByStudyAndStudyDay(Study study, String studyDay);
}

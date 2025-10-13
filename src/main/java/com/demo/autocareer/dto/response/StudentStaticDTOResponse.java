package com.demo.autocareer.dto.response;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StudentStaticDTOResponse {
    private Long totalStudent;
    private Long totalApproved;
    private Long totalWaiting;
    private Long totalNotYet;
    private Map<Integer, Map<String, Long>> monthlyStudentStats;
    private Map<String, Map<String, Long>> facultyStats;

    public StudentStaticDTOResponse(Long totalStudent,
                                    Long totalApproved,
                                    Long totalWaiting,
                                    Long totalNotYet,
                                    Map<Integer, Map<String, Long>> monthlyStudentStats,
                                    Map<String, Map<String, Long>> facultyStats) {
        this.totalStudent = totalStudent;
        this.totalApproved = totalApproved;
        this.totalWaiting = totalWaiting;
        this.totalNotYet = totalNotYet;
        this.monthlyStudentStats = monthlyStudentStats;
        this.facultyStats = facultyStats;
    }
}
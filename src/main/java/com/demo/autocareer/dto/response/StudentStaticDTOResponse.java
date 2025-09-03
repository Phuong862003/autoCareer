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
    private Map<Integer, Long> monthStudentIntern;

    public StudentStaticDTOResponse(Long totalStudent,
                                    Long totalApproved,
                                    Long totalWaiting,
                                    Long totalNotYet,
                                    Map<Integer, Long> monthStudentIntern) {
        this.totalStudent = totalStudent;
        this.totalApproved = totalApproved;
        this.totalWaiting = totalWaiting;
        this.totalNotYet = totalNotYet;
        this.monthStudentIntern = monthStudentIntern;
    }
}
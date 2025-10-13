package com.demo.autocareer.dto.response;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserStaticDTOResponse {
    private long total;
    private long activeUser;
    private long inActiveUser;
    private long pendingUser;
    private long compUser;
    private long uniUser;
    private long studUser;
    private Map<String, Long> monthUser;
    private Map<String, Map<String, Long>> monthRoleUser;
}

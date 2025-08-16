package com.demo.autocareer.dto.request;

import java.io.Serializable;

import com.demo.autocareer.model.enums.ApplyJobStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ApplyDTORequest implements Serializable{
    private ApplyJobStatus applyJobStatus;

    public ApplyJobStatus getApplyJobStatus(){
        return applyJobStatus;
    }
}

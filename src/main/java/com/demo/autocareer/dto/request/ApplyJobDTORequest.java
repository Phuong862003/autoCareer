package com.demo.autocareer.dto.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Data
public class ApplyJobDTORequest implements Serializable{
    private Long id;
    private String cover_letter;
    private String attachment;
    public Long getId(){
        return id;
    }
}

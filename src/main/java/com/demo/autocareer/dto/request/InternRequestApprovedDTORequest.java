package com.demo.autocareer.dto.request;

import java.io.Serializable;

import com.demo.autocareer.model.enums.StatusIntern;

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
public class InternRequestApprovedDTORequest implements Serializable{
    private StatusIntern statusIntern;
    private String note;

    public StatusIntern getStatusIntern(){
        return statusIntern;
    }

    public String getNote(){
        return note;
    }
}

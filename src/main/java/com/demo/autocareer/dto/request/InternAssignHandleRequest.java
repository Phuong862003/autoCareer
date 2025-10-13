package com.demo.autocareer.dto.request;

import java.io.Serializable;

import com.demo.autocareer.model.enums.StatusIntern;
import com.demo.autocareer.model.enums.StatusRequestFile;

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
public class InternAssignHandleRequest implements Serializable{
    private StatusRequestFile status;
    private String note;

    public StatusRequestFile getStatus(){
        return status;
    }

    public String getNote(){
        return note;
    }
}
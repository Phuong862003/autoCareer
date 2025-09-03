package com.demo.autocareer.dto.response;

import java.io.Serializable;
import java.util.List;

import com.demo.autocareer.dto.ProvinceDTO;

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
public class JobProvinceDTOResponse implements Serializable{
    private JobDTOResponse job;
    private List<ProvinceDTO> provinces;
}

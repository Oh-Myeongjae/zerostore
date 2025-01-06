package com.zerobase.zerostore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StoreUpdateRequest {

    @NotBlank(message = "매장 명은 필수입니다.")
    private String name;

    @NotBlank(message = "상점 위치는 필수입니다.")
    private String location;

    @NotBlank(message = "상점 설명은 필수입니다.")
    private String description;
}


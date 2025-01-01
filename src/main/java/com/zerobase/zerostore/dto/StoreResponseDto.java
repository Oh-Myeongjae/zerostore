package com.zerobase.zerostore.dto;

import com.zerobase.zerostore.domain.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreResponseDto {
    private Long id;
    private String name;
    private String location;
    private String description;

    public static StoreResponseDto entityToDto(Store store){
        return StoreResponseDto.builder()
                .id(store.getId())
                .name(store.getName())
                .location(store.getLocation())
                .description(store.getDescription())
                .build();
    }
}

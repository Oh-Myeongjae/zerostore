package com.zerobase.zerostore.domain;

import com.zerobase.zerostore.type.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "\\d{10,11}", message = "전화번호는 10자리 또는 11자리 숫자여야 합니다.")
    @Column(unique = true)
    private String phoneNumber;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public void changeRole(){
        this.role = Role.PARTNER;
    }
}


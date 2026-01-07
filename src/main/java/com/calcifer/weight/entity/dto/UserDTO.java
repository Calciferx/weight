package com.calcifer.weight.entity.dto;

import com.calcifer.weight.entity.enums.UserStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户请求对象")
@Data
public class UserDTO {
    @Schema(description = "用户ID", example = "12345")
    private String userId;
    @Schema(description = "用户名", example = "jack", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
    @JsonIgnore
    @Schema(description = "密码", example = "pwd1234")
    private String password;
    @Schema(description = "角色", example = "ADMIN")
    private String role;
    @Schema(description = "账号状态", example = "ACTIVATED")
    private UserStatusEnum status;
    @Schema(description = "创建时间", example = "2023-01-01 00:00:00")
    private String createTime;
    @Schema(description = "用户姓名", example = "张三")
    private String realName;
    @Schema(description = "手机号", example = "12345678901")
    private String phoneNumber;
}

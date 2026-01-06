package com.calcifer.weight.entity.dto;

import com.calcifer.weight.entity.enums.UserStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户请求对象")
@Data
public class UserDTO {
    @Schema(description = "用户ID", example = "12345", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;
    @Schema(description = "用户名", example = "jack", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
    @JsonIgnore
    @Schema(description = "密码", example = "pwd1234", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    @Schema(description = "角色", example = "ADMIN", requiredMode = Schema.RequiredMode.REQUIRED)
    private String role;
    @Schema(description = "账号状态", example = "ACTIVATED", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserStatusEnum status;
    @Schema(description = "创建时间", example = "2023-01-01 00:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private String createTime;
    @Schema(description = "用户姓名", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    private String realName;
    @Schema(description = "手机号", example = "12345678901", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phoneNumber;
}

package com.misha.sh.devicemanagementmicroservice.request.userRequests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserIdResponse {
    private Integer currentId;
}

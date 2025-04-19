package com.pos.be.request;

import com.pos.be.entity.order.ConsignmentStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsignmentStatusUpdateRequest {
    private String consignmentNumber;
    private ConsignmentStatus status;
}

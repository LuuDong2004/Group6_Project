package group6.cinema_project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VnpayRequest {
    private Integer bookingId;
    private String amount;
    private String bankCode;
    private String locale;
    private String orderInfo;
    private String orderType;
    private String ipAddress;
    private String txnRef;
    private String createDate;
    private String expireDate;
}

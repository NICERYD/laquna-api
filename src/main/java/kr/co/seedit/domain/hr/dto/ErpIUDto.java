package kr.co.seedit.domain.hr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

public class ErpIUDto {

    @AllArgsConstructor
    @Data
    @Builder
    @NoArgsConstructor
    public static class RequestDto implements Serializable {
        private Integer companyId;
        private Integer estId;
        private String yyyymm;
        private int loginUserId;
    }

    @AllArgsConstructor
    @Data
    @Builder
    @NoArgsConstructor
    public static class DepartmentDto {
        private Integer companyId;
        private String departmentName;
        private String distalYn;
        private String departmentCode;
        private String estId;
        private String startDate;
        private String endDate;
        private String yyyymm;
        private Integer loginUserId;
        private String orderIndex;
    }

    @AllArgsConstructor
    @Data
    @Builder
    @NoArgsConstructor
    public static class EmployemeeDto {
        private Integer companyId;
        private String koreanName;
        private String employeeNumber;
        private String positionCode;
        private String positionType;
        private String dutyType;
        private String hireDate;
        private String retireDate;
        private String employmentCode;
        private String employeeType;
        private String estId;
        private String departmentCode;
        private String yyyymm;
        private Integer loginUserId;
        private String noEmail;
        private String noRes;
    }
    @AllArgsConstructor
    @Data
    @Builder
    @NoArgsConstructor
    public static class SalaryItemDto {
        private Integer companyId;
        private String yyyymm;
        private String itemCode;
        private String itemName;
        private String useYn;
        private Integer loginUserId;
    }

    @AllArgsConstructor
    @Data
    @Builder
    @NoArgsConstructor
    public static class SalaryBasicDto {
        private Integer companyId;
        private String startDate;
        private String basicCode;
        private String basicName;
        private String basicAmount;
        private String attribute1;
        private Integer loginUserId;
        private String yyyymm;
    }
    @AllArgsConstructor
    @Data
    @Builder
    @NoArgsConstructor
    public static class BonusDtos {
        private Integer companyId;
        private String yyyymm;
        private String attribute1;
        private String bonusAmount;
        private Integer loginUserId;
    }

    @AllArgsConstructor
    @Data
    @Builder
    @NoArgsConstructor
    public static class CalendarDto {
        private Integer companyId;
        private String yyyymmdd;
        private String dateType;
        private String dateWeek;
        private String description;
        private String yyyymm;
        private Integer loginUserId;
    }

    @AllArgsConstructor
    @Data
    @Builder
    @NoArgsConstructor
    public static class CalcTaxDto {
        private Integer companyId;
        private String cdCompany;
        private String yyyymm;
        private String employeeNumber;
        private double amDeduct01;
        private double amDeduct02;
        private double amDeduct03;
        private double amDeduct04;
        private double amDeduct05;
        private double amDeduct06;
        private double amDeduct07;
        private double amDeduct08;
        private double amDeduct09;
        private double amDeduct10;
        private double amDeduct11;
        private double amDeduct12;
        private double amIncomTax;
        private double amResidTax;
        private String dtPay;
        private String tpPay;
        private Integer loginUserId;
    }
}

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
        private String yyyymmdd;
        private String employeeNumber;
        private double AM_DEDUCT01;
        private double AM_DEDUCT02;
        private double AM_DEDUCT03;
        private double AM_DEDUCT04;
        private double AM_DEDUCT05;
        private double AM_DEDUCT06;
        private double AM_DEDUCT07;
        private double AM_DEDUCT08;
        private double AM_DEDUCT09;
        private double AM_DEDUCT10;
        private double AM_DEDUCT11;
        private double AM_DEDUCT12;
        private double AM_INCOMTAX;
        private double AM_RESIDTAX;
    }
}

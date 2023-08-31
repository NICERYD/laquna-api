package kr.co.seedit.domain.hr.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class PayStubMailDto implements Serializable {

	/**
	 * DTO for Mail service
	 */
	private static final long serialVersionUID = 1L;
	
	@Builder.Default
	private String from = "";
    private String[] address;
    private String[] ccAddress;
    @Builder.Default
    private String subject = "";
    @Builder.Default
    private String content = "";
    
    @Builder.Default
    private String protocol = "smtp";
    @Builder.Default
    private String auth = "true";
    @Builder.Default
    private String ssl = "false";
    @Builder.Default
    private String starttls = "true";
    @Builder.Default
    private String debug = "true";
    @Builder.Default
    private String encoding = "UFT-8";
    @Builder.Default
    private String host = "";
    @Builder.Default
    private String port = "";
    @Builder.Default
    private String username = "";
    @Builder.Default
    private String password = "";
}


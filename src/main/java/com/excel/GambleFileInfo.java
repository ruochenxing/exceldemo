package com.excel;

@ExcelSheet
public class GambleFileInfo {
	@ExcelField(name = "政策ID")
	private Long salesPolicyId;
	@ExcelField(name = "行程类型")
	private String tripType;

	public Long getSalesPolicyId() {
		return salesPolicyId;
	}

	public void setSalesPolicyId(Long salesPolicyId) {
		this.salesPolicyId = salesPolicyId;
	}

	public String getTripType() {
		return tripType;
	}

	public void setTripType(String tripType) {
		this.tripType = tripType;
	}

}

package com.excel;

@ExcelSheet(name = "gamle")
public class DownGamleFileInfo {

	@ExcelField(name = "销售ID", rank = 1)
	private String salesPolicyID;
	@ExcelField(name = "政策ID", rank = 2)
	private String gamblingPolicyID;

	public String getSalesPolicyID() {
		return salesPolicyID;
	}

	public void setSalesPolicyID(String salesPolicyID) {
		this.salesPolicyID = salesPolicyID;
	}

	public String getGamblingPolicyID() {
		return gamblingPolicyID;
	}

	public void setGamblingPolicyID(String gamblingPolicyID) {
		this.gamblingPolicyID = gamblingPolicyID;
	}

}

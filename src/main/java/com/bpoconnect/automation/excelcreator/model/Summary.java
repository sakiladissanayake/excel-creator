package com.bpoconnect.automation.excelcreator.model;

public class Summary {
	private String company;
	private String account;
	private String documentCurrency;
	private double amountDoc;
	private String localCurrency;
	private double amountLocal;
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getDocumentCurrency() {
		return documentCurrency;
	}
	public void setDocumentCurrency(String documentCurrency) {
		this.documentCurrency = documentCurrency;
	}
	public double getAmountDoc() {
		return amountDoc;
	}
	public void setAmountDoc(double amountDoc) {
		this.amountDoc = amountDoc;
	}
	public String getLocalCurrency() {
		return localCurrency;
	}
	public void setLocalCurrency(String localCurrency) {
		this.localCurrency = localCurrency;
	}
	public double getAmountLocal() {
		return amountLocal;
	}
	public void setAmountLocal(double amountLocal) {
		this.amountLocal = amountLocal;
	}

}

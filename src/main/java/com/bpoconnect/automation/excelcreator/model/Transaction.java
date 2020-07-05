package com.bpoconnect.automation.excelcreator.model;

public class Transaction {
	
	private String company;
	private String account;
	private String entryDate;
	private String documentDate;
	private String documentType;
	private String text;
	private String documentCurrency;
	private double ammountDoc;
	private String localCurrency;
	private double amountLocal;
	private String yearMonth;
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
	public String getEntryDate() {
		return entryDate;
	}
	public void setEntryDate(String entryDate) {
		this.entryDate = entryDate;
	}
	public String getDocumentDate() {
		return documentDate;
	}
	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getDocumentCurrency() {
		return documentCurrency;
	}
	public void setDocumentCurrency(String documentCurrency) {
		this.documentCurrency = documentCurrency;
	}
	public double getAmmountDoc() {
		return ammountDoc;
	}
	public void setAmmountDoc(double ammountDoc) {
		this.ammountDoc = ammountDoc;
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
	public String getYearMonth() {
		return yearMonth;
	}
	public void setYearMonth(String yearMonth) {
		this.yearMonth = yearMonth;
	}
	
	

}

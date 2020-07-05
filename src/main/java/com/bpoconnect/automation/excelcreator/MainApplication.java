package com.bpoconnect.automation.excelcreator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

import com.bpoconnect.automation.excelcreator.model.Summary;
import com.bpoconnect.automation.excelcreator.model.Transaction;
import com.bpoconnect.automation.excelcreator.utils.GetPropertyValues;
import com.bpoconnect.automation.excelcreator.xlsx.StreamingReader;

public class MainApplication {
	
	private static final Logger log = LoggerFactory.getLogger(MainApplication.class);
	public static final String PATTERN1 = "#,##0.00;(#,##0.00)";

	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException,
			NoSuchFieldException, SecurityException, ParseException {

		GetPropertyValues properties = new GetPropertyValues();
		Properties config = properties.getPropValues();

		try (InputStream is = new FileInputStream(new File(config.getProperty("sourcefilepath")));

				Workbook workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(is)) {

			Sheet sheet = workbook.getSheet(config.getProperty("sheetname"));
			
			log.info("Read excel finished... Sheet name is "+sheet);
			
			//Read excel and create maps with data
			HashMap<String, ArrayList> userMap = new HashMap<String, ArrayList>();

			for (Row r : sheet) {
				if (r.getRowNum() != 0) {

					Transaction transaction = new Transaction();

					for (Cell c : r) {

						switch (c.getColumnIndex()) {
						case 0:
							transaction.setCompany(c.getStringCellValue());
							break;
						case 1:
							transaction.setAccount(c.getStringCellValue());
							break;
						case 2:
							transaction.setEntryDate(c.getStringCellValue());
							break;
						case 3:
							Date date1 = new SimpleDateFormat("dd.MM.yyyy").parse(c.getStringCellValue());
							String dateStr = date1.toString();
							DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
							Date date = (Date) formatter.parse(dateStr);
							Calendar cal = Calendar.getInstance();
							cal.setTime(date);
							String formatedDate = (cal.get(Calendar.MONTH) + 1) + "/" + (cal.get(Calendar.DATE)) + "/"
									+ cal.get(Calendar.YEAR);
							transaction.setDocumentDate(formatedDate);
							break;
						case 4:
							transaction.setDocumentType(c.getStringCellValue());
							break;
						case 5:
							transaction.setText(c.getStringCellValue());
							break;
						case 6:
							transaction.setDocumentCurrency(c.getStringCellValue());
							break;
						case 7:
							transaction.setAmmountDoc(c.getNumericCellValue());
							break;
						case 8:
							transaction.setLocalCurrency(c.getStringCellValue());
							break;
						case 9:
							transaction.setAmountLocal(c.getNumericCellValue());
							break;
						case 10:
							transaction.setYearMonth(c.getStringCellValue());
							break;
						default:
							break;
						}

					}

					//check whether key is available, if available get the list and put new value, else add new entry
					if (transaction.getAccount() != null) {
						if (userMap.containsKey(transaction.getAccount())) {
							ArrayList<Transaction> list = userMap.get(transaction.getAccount());
							list.add(transaction);
							userMap.put(transaction.getAccount(), list);
						} else {
							ArrayList<Transaction> list = new ArrayList<Transaction>();
							list.add(transaction);
							userMap.put(transaction.getAccount(), list);
						}
					}

				}

			}
			log.info("Data map created...");
			//Create summary map using userMap
			HashMap<String, Summary> summaryMap = new HashMap<>();

			for (String key : userMap.keySet()) {
				Summary summary = new Summary();

				double d = userMap.get(key).stream().mapToDouble(t -> ((Transaction) t).getAmmountDoc()).sum();
				double l = userMap.get(key).stream().mapToDouble(t -> ((Transaction) t).getAmountLocal()).sum();

				summary.setAccount(key);
				summary.setAmountDoc(d);
				summary.setAmountLocal(l);
				summary.setCompany(((Transaction) userMap.get(key).get(0)).getCompany());
				summary.setDocumentCurrency(((Transaction) userMap.get(key).get(0)).getDocumentCurrency());
				summary.setLocalCurrency(((Transaction) userMap.get(key).get(0)).getLocalCurrency());

				summaryMap.put(key, summary);
			}

			log.info("Summary map created...");
			
			// Write Excel
			DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
			df.applyPattern(PATTERN1);

			// Blank workbook
			XSSFWorkbook workbook2 = new XSSFWorkbook();
			CellStyle style = workbook2.createCellStyle();

			// Create a blank sheet
			XSSFSheet sheet2 = workbook2.createSheet("Summary");

			// This data needs to be written (Object[])
			//Create Summary Sheet
			Map<Integer, Object[]> data = new TreeMap<Integer, Object[]>();
			data.put(1, new Object[] { "Comapany", "Account", "Document currency", "Amount in doc. curr.",
					"Local Currency", "Amount in local currency" });
			int idx = 2;
			for (String summaryKey : summaryMap.keySet()) {
				Summary summaryItem = summaryMap.get(summaryKey);
				data.put(idx,
						new Object[] { summaryItem.getCompany(), summaryItem.getAccount(),
								summaryItem.getDocumentCurrency(), df.format(summaryItem.getAmountDoc()),
								summaryItem.getLocalCurrency(), df.format(summaryItem.getAmountLocal()) });
				idx++;
			}

			Row rowheading = sheet2.createRow(1);
			Cell cellHeading = rowheading.createCell(1);
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
			String strDate = formatter.format(date);
			cellHeading.setCellValue("Document Ageing Report as at " + strDate);

			// Iterate over data and write to sheet
			Set<Integer> keyset = data.keySet();
			int rownum = 3;

			for (Integer key : keyset) {
				Row row = sheet2.createRow(rownum++);
				Object[] objArr = data.get(key);
				int cellnum = 1;
				for (Object obj : objArr) {
					Cell cell = row.createCell(cellnum++);
					if (key == 1) {
						style.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());
					}
					style.setBorderBottom(BorderStyle.THIN);
					style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
					style.setBorderRight(BorderStyle.THIN);
					style.setRightBorderColor(IndexedColors.BLACK.getIndex());
					style.setBorderTop(BorderStyle.THIN);
					style.setTopBorderColor(IndexedColors.BLACK.getIndex());
					style.setBorderLeft(BorderStyle.THIN);
					style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
					cell.setCellStyle(style);
					if (obj instanceof String) {
						cell.setCellValue((String) obj);
					} else if (obj instanceof Integer) {
						cell.setCellValue((Integer) obj);
					} else if (obj instanceof Double) {
						cell.setCellValue((Double) obj);
					}
				}
			}
			log.info("Summary sheet created...");

			
			//Create Other sheets
			for (String currentSheet : userMap.keySet()) {
				ArrayList currentSheetData = userMap.get(currentSheet);

				// Create a blank sheet
				XSSFSheet tempSheet = workbook2.createSheet(((Transaction) currentSheetData.get(0)).getAccount());

				// This data needs to be written (Object[])
				Map<Integer, Object[]> tempData = new TreeMap<Integer, Object[]>();
				tempData.put(1,
						new Object[] { "Comapany", "Account", "Document Date", "Document Type", "Document currency",
								"Amount in doc. curr.", "Local Currency", "Amount in local currency", "Text",
								"Doc Ageing" });
				int counter = 0;
				for (int i = 0; i < currentSheetData.size(); i++) {
					Transaction tempSheetData = (Transaction) currentSheetData.get(i);

					tempData.put(i + 2,
							new Object[] { tempSheetData.getCompany(), tempSheetData.getAccount(),
									tempSheetData.getDocumentDate(), tempSheetData.getDocumentType(),
									tempSheetData.getDocumentCurrency(), df.format(tempSheetData.getAmmountDoc()),
									tempSheetData.getLocalCurrency(), df.format(tempSheetData.getAmountLocal()),
									tempSheetData.getText(), "TODAY()- C" + (i + 2) });
					counter = i + 2;
				}

				tempData.put(counter + 1, new Object[] { "", "", "", "", "",
						df.format(((Summary) summaryMap.get(((Transaction) currentSheetData.get(0)).getAccount()))
								.getAmountDoc()),
						"", df.format(((Summary) summaryMap.get(((Transaction) currentSheetData.get(0)).getAccount()))
								.getAmountLocal()),
						"", "" });

				// Iterate over data and write to sheet
				Set<Integer> keysetsheet = tempData.keySet();
				int rownumsheet = 0;
				for (Integer key : keysetsheet) {
					Row row = tempSheet.createRow(rownumsheet++);
					Object[] objArr = tempData.get(key);
					int cellnum = 0;
					for (Object obj : objArr) {
						Cell cell = row.createCell(cellnum++);
						style.setBorderBottom(BorderStyle.THIN);
						style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
						style.setBorderRight(BorderStyle.THIN);
						style.setRightBorderColor(IndexedColors.BLACK.getIndex());
						style.setBorderTop(BorderStyle.THIN);
						style.setTopBorderColor(IndexedColors.BLACK.getIndex());
						style.setBorderLeft(BorderStyle.THIN);
						style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
						cell.setCellStyle(style);
						if (obj instanceof String) {
							if (((String) obj).toString().contains("TODAY()")) {
								cell.setCellFormula((String) obj);
							} else {
								cell.setCellValue((String) obj);
							}
						} else if (obj instanceof Integer) {
							cell.setCellValue((Integer) obj);
						} else if (obj instanceof Double) {
							cell.setCellValue((Double) obj);
						}
					}
				}
				log.info("Sheet "+currentSheet+" created...");
			}

			try {
				// Write the workbook in file system
				FileOutputStream out = new FileOutputStream(new File(config.getProperty("outputfilepath")));
				workbook2.write(out);
				out.close();
				
				log.info("Final report created...");
				

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

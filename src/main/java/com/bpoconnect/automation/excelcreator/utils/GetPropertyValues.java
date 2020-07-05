package com.bpoconnect.automation.excelcreator.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

public class GetPropertyValues {

	String result = "";

	public Properties getPropValues()  {

		Properties prop = new Properties();

		String propFileName = "config.properties";

		String file = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator
				+ "resources" + File.separator + "config.properties";

		try (InputStream inputStream = new FileInputStream(file)) {

			prop.load(inputStream);

		} catch (Exception e) {
			try {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		return prop;

	}

}

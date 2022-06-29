package com.example.util;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvDataSet;

import com.github.springtestdbunit.dataset.AbstractDataSetLoader;

public class CsvDataSetLoader extends AbstractDataSetLoader {
	@Override
	protected IDataSet createDataSet(org.springframework.core.io.Resource resource) throws Exception {
		return new CsvDataSet(resource.getFile());
	}

}
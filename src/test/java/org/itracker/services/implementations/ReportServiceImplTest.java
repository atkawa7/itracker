package org.itracker.services.implementations;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Report;
import org.itracker.persistence.dao.ReportDAO;
import org.itracker.services.ReportService;
import org.junit.Test;

public class ReportServiceImplTest extends AbstractDependencyInjectionTest {

	private static final Logger log = Logger.getLogger(ReportServiceImplTest.class);

	private ReportDAO reportDAO;

	/**
	 * Object to be Tested: ReportService
	 */
	private ReportService reportService;

	@Test
	public void testGetAllReports() {
		// defined in reportbean_dataset.xml
		try {
			List<Report> reports = reportService.getAllReports();
		
			assertNotNull("reports ",reports);
			assertEquals("reports size ", 1, reports.size());
			Report report = reports.get(0);
			assertEquals("id", 1000, report.getId().intValue());
			assertEquals("name", "DailayReport Report", report.getName());
			assertEquals("nameKey", "0001", report.getNameKey());
			assertEquals("description", "This is a daily report", report.getDescription());
			assertEquals("dataType", 1, report.getDataType());
			assertEquals("reportType", 2, report.getReportType());
			assertEquals("className", "org.itracker.model.Report", report.getClassName());
			DateFormat format = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
			try {
				assertEquals("createDate", format.parse("2004-01-01 13:00:00"), report.getCreateDate());
				assertEquals("modified date", format.parse("2005-01-01 15:00:00"), report.getLastModifiedDate());
			} catch (ParseException e) {
				log.error("testGetAllReports: failed to parse date for assertion",e);
				fail("failed to parse date for assertion: " + e.getMessage());
			}
			
		} catch (Exception e) {
			log.error("testGetAllReports: failed to getAllReports in ReportService",e);
			fail("failed to getAllReports: " + e.getMessage());
		}

	}
	
	@Test
	public void testGetNumberReports(){
		try {
			// defined in reportbean_dataset.xml
			assertEquals("reports size", 1, reportService.getNumberReports());
		} catch (Exception e) {
			log.error("testGetNumberReports: failed to getNumberReports in ReportService",e);
			fail("failed to getNumberReports: " + e.getMessage());
		}
	} 
	
	@Test
	public void testGetReportDAO(){
		assertNotNull("getReportDAO", reportService.getReportDAO());
	}
	
	@Test
	public void testCreateReport(){
		//test save
		Report report = new Report();
		report.setName("weekly report");
		report.setNameKey("0002");
		report.setDescription("This is a weekly report");
		report.setDataType(1);
		report.setReportType(2);
		report.setClassName("xxx.xxx.Weekly");
		Report result = reportService.createReport(report);
		Report reportFind = reportDAO.findByPrimaryKey(result.getId());
		assertNotNull(reportFind);
		assertEquals("id", result.getId(), reportFind.getId());
		assertEquals("name", "weekly report", reportFind.getName());
		
		//test update
		report.setName("monthly report");
		Report result1 = reportService.createReport(report);
		reportFind = reportDAO.findByPrimaryKey(result1.getId());
		assertEquals("name", "monthly report", reportFind.getName());
		
		//test null		
		try{
			report = null;
			reportService.createReport(report);		
			fail("do not throw NullPointerException ");
		} catch(NullPointerException e){
			assertTrue(true);
		}
		
	}

	

	@Override
	public void onSetUp() throws Exception {
		super.onSetUp();		
		reportService = (ReportService) applicationContext.getBean("reportService");		
		this.reportDAO = (ReportDAO) applicationContext.getBean("reportDAO");

	}

	protected String[] getDataSetFiles() {
		return new String[] { 
				"dataset/reportbean_dataset.xml"				
		};
	}

	protected String[] getConfigLocations() {
		return new String[] { "application-context.xml" };
	}

}

package sky.sipder.bk.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PoiServiceImpl
{
	private Logger logger = LoggerFactory.getLogger(PoiServiceImpl.class);
	
	@Value("${path.source-txt}")
	private String sourceTxtPath;
	
	@Value("${path.target-dir}")
	private String targetDirPath;
	
	private Workbook workBook;
	
	private AtomicInteger rowNum;
	
	public void initWorkBook()
	{
		logger.info("开始初始化表格组件");
		workBook = new HSSFWorkbook();
		workBook.createSheet("sheet1");
		rowNum = new AtomicInteger(0);
		logger.info("表格组件初始化完成");
	}
	
	public void writeRow(String resblockName , String locat , String buildArea , String part , String url , String picName , String picUrl)
	{
		logger.info("开始写入第{}行表格数据" , String.valueOf(rowNum.get()));
		Sheet sheet = workBook.getSheetAt(0);
		Row row = sheet.createRow(rowNum.get());
		Cell cell = row.createCell(0);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(String.valueOf(rowNum.incrementAndGet()));
		
		cell = row.createCell(1);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(resblockName);
		
		cell = row.createCell(2);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(locat);
		
		cell = row.createCell(3);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(buildArea);
		
		cell = row.createCell(4);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(part);
		
		cell = row.createCell(5);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(url);
		
		cell = row.createCell(6);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(picName);
		
		cell = row.createCell(7);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(picUrl);
		logger.info("第{}行表格数据写入完成" , String.valueOf(rowNum.get() - 1));
	}
	
	public void output()
	{
		logger.info("开始生成表格文件");
		File tarDir = new File(targetDirPath);
		String sourceName = new File(sourceTxtPath).getName();
		String outputFileName = sourceName.substring(0, sourceName.lastIndexOf(".")).concat(".xls");
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(new File(tarDir, outputFileName));
			workBook.write(fos);
			workBook.close();
			fos.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
		logger.info("表格文件生成完毕");
	}
}

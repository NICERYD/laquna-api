package kr.co.seedit.global.utils;

import java.io.IOException;

import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class SeedXSSFUtil {
	
	/**
	 * 
	 * @param sheet	format 설정할
	 * @param headLine 복사할 큐크형 양식
	 * @param countData 입력된 데이터 수
	 * @param maxInRow 1열 최대 출력 개수
	 * @param dataOffsetCell 열간 간격
	 * @throws IOException
	 */
	public void setFulltimeUserPayroll(XSSFSheet sheet,
			int headLine,
			int countData,
			int maxInRow, int dataOffsetCell) throws IOException
	{
		System.out.print("Math.floorDiv(8, 3)="+Math.floorDiv(8, 3)+"\n");
	}

	public static XSSFSheet setPayrollFormat(XSSFSheet sheet,
			int sheetIndex,
			int headLine,
			int countData,
			int maxInRow, int dataOffsetCell) throws IOException
	{
		CellCopyPolicy options = new CellCopyPolicy();
		options.setCondenseRows(true);
		options.setMergeHyperlink(true);

		System.out.print("Math.floorDiv(1, 3)="+Math.floorDiv(1, 3)+"\n");
		System.out.print("Math.floorDiv(2, 3)="+Math.floorDiv(2, 3)+"\n");
		System.out.print("Math.floorDiv(3, 3)="+Math.floorDiv(3, 3)+"\n");
		System.out.print("Math.floorDiv(4, 3)="+Math.floorDiv(4, 3)+"\n");
		System.out.print("Math.floorDiv(5, 3)="+Math.floorDiv(5, 3)+"\n");
		System.out.print("Math.floorDiv(6, 3)="+Math.floorDiv(6, 3)+"\n");
		System.out.print("Math.floorDiv(7, 3)="+Math.floorDiv(7, 3)+"\n");
		System.out.print("Math.floorDiv(8, 3)="+Math.floorDiv(8, 3)+"\n");

		// count loop
		int curRowPoint = 0;
		int lastRowPoint = Math.floorDiv(countData-1, maxInRow);
System.out.print("Math.floorDiv(countData, maxInRow)="+Math.floorDiv(countData, maxInRow)+" countData="+countData+" maxInRow="+maxInRow+"\n");
		for (int i=curRowPoint;i<lastRowPoint;i++)
		{
			curRowPoint += headLine;
			sheet.copyRows(0, headLine-1, curRowPoint, options);
System.out.print("["+sheetIndex+"-"+i+"] sheet.copyRows(0, headLine="+headLine+", curRowPoint="+curRowPoint+"\n");
		}
		
		if (0 != countData%maxInRow)
		{
			int lastPoint = curRowPoint+headLine;
			for (int i=curRowPoint;i<lastPoint;i++)
			{
				XSSFRow row = sheet.getRow(i);
				if (null == row) {
System.out.print("DELETE ["+sheetIndex+"-"+i+"] row is null"+"\n");
					continue;
				}
				for (int j=(countData%maxInRow)*dataOffsetCell;j<dataOffsetCell*maxInRow;j++)
				{
//System.out.print("DELETE ["+sheetIndex+"-"+i+"/"+j+"] row.removeCell("+j+") "+"\n");
					if (null != row.getCell(j))
						row.removeCell(row.getCell(j));
				}
			}
		}
		
System.out.print("workbook.setPrintArea("+sheetIndex+", 0, "+(maxInRow*dataOffsetCell-1)+", 0, "+lastRowPoint*headLine+"="+lastRowPoint+"*"+headLine+")"+"\n");
//		workbook.removePrintArea(curRowPoint);
		sheet.getWorkbook().setPrintArea(sheetIndex, 0, maxInRow*dataOffsetCell-2, 0, (lastRowPoint+1)*headLine -1);

		return sheet;
	}

}

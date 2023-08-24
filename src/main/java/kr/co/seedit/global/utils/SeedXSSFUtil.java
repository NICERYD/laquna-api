package kr.co.seedit.global.utils;

import java.io.IOException;

import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class SeedXSSFUtil {
	
	/**
	 * 블록형태의 엑셀포멧을 복사한다.
	 * 열단위 복사를 위해서 최상단은 동일 포멧으로 생성된 엑셀파일이 필요하다
	 * 
	 * @param sheet	format 설정할
	 * @param countHeadLine 복사할 큐크형 양식
	 * @param countData 입력된 데이터 수
	 * @param maxInRow 1열 최대 출력 개수
	 * @param dataOffsetCell 열간 간격
	 * @throws IOException
	 */
	public static XSSFSheet copyHeadlineCubeFormat(XSSFWorkbook workbook,
			int sheetIndex,
			int countHeadLine,
			int countData,
			int maxInRow, int dataOffsetCell) throws IOException
	{
		XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
		CellCopyPolicy options = new CellCopyPolicy();
		options.setCondenseRows(true);
		options.setMergeHyperlink(true);

		// count loop
		int curRowPoint = 0;
		int lastRowPoint = Math.floorDiv(countData-1, maxInRow);
//System.out.print("Math.floorDiv(countData, maxInRow)="+Math.floorDiv(countData, maxInRow)+" countData="+countData+" maxInRow="+maxInRow+"\n");
		for (int i=curRowPoint;i<lastRowPoint;i++)
		{
			curRowPoint += countHeadLine;
//System.out.print("["+sheetIndex+"-"+i+"] sheet.copyRows(0, countHeadLine="+countHeadLine+", curRowPoint="+curRowPoint+"\n");
			sheet.copyRows(0, countHeadLine-1, curRowPoint, options);
		}
		
		if (0 != countData%maxInRow)
		{
			int lastPoint = curRowPoint+countHeadLine;
			for (int i=curRowPoint;i<lastPoint;i++)
			{
				XSSFRow row = sheet.getRow(i);
				if (null == row) {
//System.out.print("DELETE ["+sheetIndex+"-"+i+"] row is null"+"\n");
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
		
//		workbook.removePrintArea(curRowPoint);
		if (maxInRow*dataOffsetCell-2 < (lastRowPoint+1)*countHeadLine -1) {
			sheet.getWorkbook().setPrintArea(sheetIndex, 0, maxInRow*dataOffsetCell-2, 0, (lastRowPoint+1)*countHeadLine -1);
//System.out.print("workbook.setPrintArea("+sheetIndex+", 0, "+(maxInRow*dataOffsetCell-1)+", 0, "+lastRowPoint*countHeadLine+"="+lastRowPoint+"*"+countHeadLine+")"+"\n");
		}

		return sheet;
	}

}

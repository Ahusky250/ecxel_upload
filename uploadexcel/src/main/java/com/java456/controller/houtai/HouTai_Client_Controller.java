package com.java456.controller.houtai;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.java456.dao.ClientDao;
import com.java456.entity.Client;
import com.java456.util.DateUtil;
import com.java456.util.ExcelUtil;
import com.java456.util.ResponseUtil;


@Controller
@RequestMapping("/houtai/client")
public class HouTai_Client_Controller {
	
	@Resource
	private ClientDao clientDao;
	
	
	/**
	 * /houtai/client/manage
	 * 
	 */
	@RequestMapping("/manage")
	public ModelAndView manage() throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("/page/client/client_manage");
		return mav;
	}
	
	/**
	 *   /houtai/client/add
	 */
	@RequestMapping("/add")
	public ModelAndView add() throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.addObject("btn_text", "添加");
		mav.addObject("save_url", "/admin/client/add");
		mav.setViewName("/page/client/add_update");
		return mav;
	}
	
	/**
	 *    /houtai/client/edit?id=1
	 */
	@RequestMapping("/edit")
	public ModelAndView edit(@RequestParam(value = "id", required = false) Integer id, HttpServletResponse response,
			HttpServletRequest request) throws Exception {
		ModelAndView mav = new ModelAndView();
		Client client     = clientDao.findId(id);
		mav.addObject("client", client);
		mav.addObject("btn_text", "修改");
		mav.addObject("save_url", "/admin/client/update?id=" + id);
		mav.setViewName("/page/client/add_update");
		return mav;
	}
	 
	/**
	 *     下载客户资料    导出 excel 使用我们的模板导出
	 * /houtai/client/excel_down  
	 */
	@RequestMapping("/excel_down")
	public String excel_down(HttpServletResponse response,HttpServletRequest  request)
			throws Exception {
		String webPath=request.getServletContext().getRealPath("/");
		List<Client> list = clientDao.findAll();
		
		Workbook wb = fillExcelDataWithTemplate(list, webPath+"/static/client_down_model.xls");
		
		ResponseUtil.export(response,wb,"客户.xls");
		return null;
	}
	
	
	
	/**
	 * @param templateFileUrl
	 *            excel模板的路径 /admin/page/JYZ/client/client_info.xls
	 * @return
	 */
	public static Workbook fillExcelDataWithTemplate(List<Client> list ,String templateFileUrl) {
		
		Workbook wb = null ;
		try {
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(templateFileUrl));
			 wb = new HSSFWorkbook(fs);
			// 取得 模板的 第一个sheet 页
			Sheet sheet = wb.getSheetAt(0);
			// 拿到sheet页有 多少列
			int cellNums = sheet.getRow(0).getLastCellNum();
			// 从第2行 开搞    下标1  就是第2行
			int rowIndex = 1;
			Row row ; 
			for(Client client : list){
				row = sheet.createRow(rowIndex);
				rowIndex ++;
				row.createCell(0).setCellValue(client.getId());
				
				row.createCell(1).setCellValue(client.getBianhao());
				row.createCell(2).setCellValue(client.getName());
				row.createCell(3).setCellValue(client.getPhone());
				row.createCell(4).setCellValue(client.getRemark());
				row.createCell(5).setCellValue(DateUtil.formatDate(client.getCreateDateTime(), "yyyy-MM-dd HH:mm:ss"));
				
				
				
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return wb;
	}
	
	
	
	
	
}

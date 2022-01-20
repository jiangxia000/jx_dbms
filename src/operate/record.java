package operate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class record {

	// 插入一条记录
	/* INSERT INTO Student VALUES ('S21W001','Alice','female',18);
	 * INSERT INTO Student (Sno,Sname,Ssex,Sage) VALUES ('S21W002','Bob','male',18);
	 * INSERT INTO Student (Sno,Sname,Sage) VALUES ('S21W004','Dora',20);
    */
	public static boolean insert_record(String tablename, ArrayList<String> list_clo_name, ArrayList<SupportedValue> list_clo_value) {
		System.out.println("语句识别成功");
    	String currentPath = SQLConstant.getNowPath();
        List<String> dbList = utils.getAllFiles(currentPath);
        //判断要插入的表是否存在
        boolean b = dbList.contains(tablename);
       
        if (b) {
        	File table = new File(currentPath, tablename+".xls");
	        // 要插入的表描述,
	        List<List<String>> lists = new ArrayList<>(); 
	        lists = utils.describle(table);
	        ArrayList<String> t_clo_name = new ArrayList<>();
	        int clo_num = lists.size();
	        // 该表的所有属性名
	        for(int c=0; c<clo_num; c++) 
	        	t_clo_name.add(lists.get(c).get(0));
	        // 初步判断插入的列（1、是不是都是这个表的列，列数是否超过插入表的列数，列名与列值长度是否相等）
	        if((0 <list_clo_value.size()) && (list_clo_value.size()<= clo_num)) {
		        for (String i : list_clo_name) {
		        	if (t_clo_name.contains(i)) ;
		        	else {
		        		System.out.println("ERROR: "+ i +"不是表" + tablename +"的列");
			        	return false;
		        	}
		        }
	        }else {
	        	System.out.println("ERROR: 插入列数数值项数与表列数不匹配");
	        	return false;
	        }
	       //将省略了的列名填补上
	        if (list_clo_name.size() == 0)  list_clo_name = t_clo_name; 
	        if (list_clo_name.size() != list_clo_value.size()) {
	        	System.out.println("ERROR: 插入列数与值不匹配");
	        	return false;
	        }	        
	        
	        //存储要写入的Label
	        List<Label> labelList = new ArrayList<>();
	        //经过上面这些判断之后，list_clo_name中的列名只会是表的列名中的，且list_clo_name和list_clo_value长度匹配
	         try {
	        	//设置单元格居中
				 WritableFont font = new WritableFont(WritableFont.ARIAL);
				 WritableCellFormat cellFormat = new WritableCellFormat(font);
				 cellFormat.setAlignment(jxl.format.Alignment.CENTRE);   //这里强制要我执行try catch
	 	    	// 打开文件  
	 	        Workbook book = Workbook.getWorkbook(table); 
	 	        // 获取sheet1(data) 
	 	        Sheet sheet = book.getSheet("data");

	 	        int data_rows = sheet.getRows(); // 从第row开始写
		        int data_cols = sheet.getColumns();

	            // 向列中写数据
		        /*
		         * 遍历表中的属性值，然后去list_clo_name中寻找到这个cloname的位置
		         * 1、如果能找到，在value中拿到对应的数值，再判断这个数值是否满足char/int、是否满足primary
		         * 如果满足，就添加到label中
		         * 2、如果找不到，那么说明没有赋值，也检查这个属性的not null, unique,primary
		         * ps: not null很好判断，是否给了value，unique就是拿到当前的值，然后从第一个开始比依次比下去，
		         * primary就是先notnull，后unique
		         * */
				 for (int col = 0; col < t_clo_name.size(); col++) {
					 String current_clo_name = t_clo_name.get(col);
					 // 完整性约束
					 String current_constraint = lists.get(col).get(3);
//					 System.out.println( current_clo_name + "  " + current_constraint);
					 //先定义默认t_value ="";
					 String t_value = "";
					// 如果list_clo_name有当前列名
					 if (list_clo_name.contains(current_clo_name)) {
//						 System.out.println("contains "+ current_clo_name);
						 int index_v = list_clo_name.indexOf(current_clo_name);
						 SupportedValue value = list_clo_value.get(index_v);
						 //拿到 赋值的 string值
						 t_value = value.getValue(); 
						//如果赋进来的value是个数字，列属性也是int
						//我在jj中已经默认其是个int了,转型int
						 //int 检验
						 if ((value.getDataType() ==  DataType.INT) && ((lists.get(col).get(1)).equalsIgnoreCase("int"))) { 
//							 System.out.println("判断int");
							 try {
								  int temp1 = Integer.parseInt(t_value);
								} catch (NumberFormatException e) {
									System.out.println("ERROR:属性" + current_clo_name +" int转型出错！");
								    e.printStackTrace();
								}
							 //转型成功，符合规则int
						 }
						//如果赋进来的value是个String，列属性是char
						//因为jj文件中知识简单判断是不是字母数字的组合，这里我直接判断string长度似乎不是超了作为合不合格的基准
						 //char 检验
						 if ((value.getDataType() ==  DataType.String) && ((lists.get(col).get(1)).equalsIgnoreCase("vachar"))) { 
//							 System.out.println("判断vachar");
							 int char_lenth = 0;
		                     try {
		                    	  char_lenth = Integer.parseInt(lists.get(col).get(2));
								} catch (NumberFormatException e) {
								    e.printStackTrace();
								}
		                     if (t_value.length() > char_lenth) {
		         	        	System.out.println("ERROR:属性" + current_clo_name +" 插入字符串长度超过定义！");
		         	        	return false;
		                     }
							  //转型成功，符合规则char
						 }
						 // not null检验，如果有属性名字在的话，定义的value一定会存在，这里一定t_value一定不是nulll
//						 if (current_constraint == "not null") {}
					     // unique,检查唯一性,这里有赋值的情况下，unique==primary key						 
						 if ((current_constraint.equalsIgnoreCase("unique")) || (current_constraint.equalsIgnoreCase("primary key"))) {
//							 System.out.println("进入判断primary或者unique");
							 ArrayList<point> target_location = new ArrayList<>();
				        	 target_location = utils.search(table, current_clo_name, "=", t_value);	
				        	 //这里属性current_clo_name一定会在tablename中，所以只要长度!=0，说明一定存在相同值的单元格
				        	 if(target_location.size()!=0) {
				        		 System.out.println("ERROR: 已经存在" + current_clo_name +"=="+t_value+"的元组了！");
				        		 return false;
				        	 } 
					     }						 						 
					 }else { // 如果list_clo_name没有当前列名，说明没赋值，检查这个属性的not null, unique,primary
//						 System.out.println("not contains "+ current_clo_name);
						 t_value = "";
						 if ((current_constraint.equalsIgnoreCase("not null")) || (current_constraint.equalsIgnoreCase("primary key"))) {
//							System.out.println("进入判断primary或者not null");
	         	        	System.out.println("ERROR: 列" + current_clo_name + "不能是空值!");
	         	        	return false;
						 }
						 if (current_constraint.equalsIgnoreCase("unique")) {
//							 System.out.println("进入判断unique");
							 ArrayList<point> target_location = new ArrayList<>();
				        	 target_location = utils.search(table, current_clo_name, "=", t_value);	
				        	 //这里属性current_clo_name一定会在tablename中，所以只要长度!=0，说明一定存在相同值的单元格
				        	 if(target_location.size()!=0) {
				        		 System.out.println("ERROR: 已经存在" + current_clo_name +"=="+t_value+"的元组了！");
				        		 return false;
				        	 } 
						 }
					 }
					 //说明这个单元格满足条件，但是可能后面的单元格有问题，所以这一行还是不能inesrt
					 Label label = new Label(col, data_rows, t_value, cellFormat);
					 labelList.add(label);
//					 System.out.println(t_value); 
				}//for
		        } catch (Exception e) {  
		            System.out.println(e);  
		        }
	         try {
		        // 获得文件  
	             Workbook wb = Workbook.getWorkbook(table); 
	            // 打开文件的副本并将指定数据写回原文件？
	             WritableWorkbook book = Workbook.createWorkbook(table, wb); 
	            // 拿到名为“data”的工作表  
	            WritableSheet sheet = book.getSheet("data"); 
				//说明没有任何异常退出情况，可以将labelList里面的东西写进来
//	            System.out.println(labelList.size()); 
//	            System.out.println(list_clo_name.size());  
				if (labelList.size() == t_clo_name.size()) {  
				  System.out.println("写入");  
			      for (Label i : labelList) 
			    	  sheet.addCell(i);
				} 
				book.write();  
	            book.close();   	 
	         }catch (Exception e) {  
		           System.out.println(e);  
		     }
				 
        }else {
        	System.out.println("ERROR: 该数据表不存在");
        }
        return true;      
	} //insert_record
	
	// 删除某条记录
	/* DELETE FROM table_name WHERE column_name=value;
       DELETE FROM table_name;
	 * 判断表是否存在，如果不存在，退出
	 * 判断是否有条件查询，如果有，查询是否有满足查询条件的元组, 没有直接退出，
	 *（此时一定会修改文件）
	 *                 1、如果没有条件查询，直接将表中的元组全部删除
	 *                 2、else(有条件查询)有满足条件的元组，删除对应元组
	 * */
	public static boolean delete_record(String tablename, String columnname, String op, SupportedValue clo_value) {
		System.out.println("语句识别成功");
    	String currentPath = SQLConstant.getNowPath();
        List<String> dbList = utils.getAllFiles(currentPath);
        //判断要先显示的表是否存在
        boolean b = dbList.contains(tablename);
        if (b) {
	        	 File table = new File(currentPath, tablename+".xls");
	        	 ArrayList<point> target_location = new ArrayList<>();
	        	 // 如果有列名,则表示有查询条件
	        	 if(! columnname.equals("")) {		        		  
		        	 // 直接去查有没有符合条件的元组		        	
		        	 target_location = utils.search(table, columnname, op, clo_value.getValue());		        	 
		        	 if(target_location.size()==0) {
		        		 System.out.println(tablename + "中没有符合删除条件元组！");
		        		 return false; 
		        	 }		        		 		        	 
		        	 else if(target_location.get(0).get_row()== -1) {
		        		 System.out.println("属性列"+ columnname +"不在"+tablename+"表中"); 
		        		 return false;
		        	 }
		        	 //如果这里没有return,说明存在元组要删除
	        	 }
	        	try {
		        	// 获得文件
		             Workbook wb = Workbook.getWorkbook(table); 
		            // 打开文件的副本并将指定数据写回原文件？
		             WritableWorkbook book = Workbook.createWorkbook(table, wb); 
		            // 拿到名为“data”的工作表  
		             WritableSheet sheet = book.getSheet("data"); 
		 	         int data_rows = sheet.getRows()-1;//最有一行行号
//		 	         System.out.println(tablename + "共有数据: "+ data_rows +"行"); 
			         int data_cols = sheet.getColumns();
		
		        	 // 如果没有查询条件，把表记录都删了，从最后一行开始删
		        	 if(columnname.equals("")) {
		        		 for(int dr= data_rows; dr > 0; dr--) {
		        			 sheet.removeRow(dr);
			        		 System.out.println("删除第"+ dr +"行"); 
		        		 }						 
		        	 }
		        	 else { //有列名，删除找到的元组
		        	     for (point i : target_location) {
		        	    	 sheet.removeRow(i.get_row());
		        	    	 System.out.println("删除第"+ i.get_row() +"行"); 
		                 }
		        	 }
					book.write();  
		            book.close();  
		         }catch (Exception e) {  
			        System.out.println(e);  
			     }
        }else {
        	System.out.println("ERROR: 该数据表不存在");
        }
        return true;
	}//delete_record
	
	// 查询表记录
	/* 支持
		SELECT Sno,Sname FROM Student;
		SELECT * FROM Student;
		SELECT Sno,Sname FROM Student WHERE Sno='S21W001';		
	 * 判断表是否存在，如果不存在，退出
	 * 判断是否有条件查询，如果有，查询是否有满足查询条件的元组, 没有直接退出，
	 *（此时一定会读文件）：
	 *                 1、如果list_clo_name为空，补全，
	 *                 2、判断list_clo_name中有没有不是表的属性，如果有，函数直接退出
	 *                 3、如果没有条件查询，target_location长度正好是0，调用show_data会显示全部元祖
	 *                 4、else(有条件查询)有满足条件的元组，target_location里面会记录要记录满足条件单元格的位置，调用show_data会显示该行即可
	 * */
	public static boolean select_record(String tablename, ArrayList<String> list_clo_name,String columnName ,String op, SupportedValue clo_value) {
		System.out.println("语句识别成功");
    	String currentPath = SQLConstant.getNowPath();
        List<String> dbList = utils.getAllFiles(currentPath);
        //判断要先显示的表是否存在
        boolean b = dbList.contains(tablename);
        if (b) {
	       	 File table = new File(currentPath, tablename+".xls");
	       	 ArrayList<point> target_location = new ArrayList<>();
	       	 // 如果有列名,则表示有查询条件
	       	 if(! columnName.equals("")) {		        		  
		        	 // 直接去查有没有符合条件的元组		        	
		        	 target_location = utils.search(table, columnName, op, clo_value.getValue());		        	 
		        	 if(target_location.size()==0) {
		        		 System.out.println(tablename + "中没有符合删除条件元组！");
		        		 return false; 
		        	 }		        		 		        	 
		        	 else if(target_location.get(0).get_row()== -1) {
		        		 System.out.println("属性列"+ columnName +"不在"+tablename+"表中"); 
		        		 return false;
		        	 }
		        	 //如果这里没有return,说明存在元组要show
	       	 }
	         try {
	 	    	// 打开文件  
	 	         Workbook book = Workbook.getWorkbook(table); 
	 	       // 获取sheet1(data) 
	 	         Sheet sheet = book.getSheet("data");
	 	         int data_rows = sheet.getRows()-1;//最有一行行号
	 	         System.out.println(tablename + "共有数据: "+ data_rows +"行"); 
		         int data_cols = sheet.getColumns();
		         
        		// 该表的所有属性名 
     	        ArrayList<String> t_clo_name = new ArrayList<>();
    	        for(int c=0; c<data_cols; c++) 
    	        	t_clo_name.add(sheet.getCell(c, 0).getContents()); //前面是列
    	        //将省略了的列名填补上     	        
		        if(list_clo_name.size()==0) list_clo_name=t_clo_name;
		        else { //判断list_clo_name中有没有不是表的属性，如果有，函数直接退出
			        for (String i : list_clo_name) {
			        	if (t_clo_name.contains(i)) ;
			        	else {
			        		System.out.println("ERROR: "+ i +"不是表" + tablename +"的列");
			        		return false;
			        	} 
			        }
		        }
		        //能运行到这里，说明一定会有输出
		        utils.show_data(table,list_clo_name,target_location);

	         }catch (Exception e) {  
		        System.out.println(e);  
		     }
        }else {
        	System.out.println("ERROR: 该数据表不存在");
        }	
        return true;
	}//select_record

	
	// 更新记录
	/* UPDATE Student SET Sage=18,Ssex='female' WHERE Sno='S21W001';
	 * UPDATE Student SET Sage=18,Ssex='female';
	 * 判断表是否存在，如果不存在，退出
	 * 拿到表描述，初步判断插入的列（1、列数超过插入表的列数，退出2、不都是这个表的列， 退出）
	 * 判断是否有条件查询，如果有，查询是否有满足查询条件的元组, 没有直接退出，
	 * （此时打开读文件）
	 *        for: list_clo_name中每一个要修改的属性：
	 *                 1、判断int/char是否符合条件，不符合直接退出
	 *                 2、是主键/Unique
	 *                    1、没有条件查询，数据行数==1，该属性值可以插入
	 *                    2、(有条件查询)此时一定有满足条件的元组，如果满足wherer条件的元组只有一个
	 *                              去找当前列有没有与插入值相等的元组，如果没有，该属性值可以插入
	 *                                                                如果有，只接受和要出入单元格值相等的情况                   
	 *                       否则退出
	 *                 3、else不是主键/Unique
	 *                    1、该属性可以直接执行set  
	 *  （此时打开写文件）：
	 *        1、如果可以插入的列数== list_clo_name.size(),开始插入                 
	 *                                       
	 * */
	public static boolean update_record(String tablename, ArrayList<String> list_clo_name,ArrayList<SupportedValue> list_clo_value,
			                            String columnName ,String op, SupportedValue clo_value) {
		System.out.println("语句识别成功");
    	String currentPath = SQLConstant.getNowPath();
        List<String> dbList = utils.getAllFiles(currentPath);
        //判断要修改的表是否存在
        boolean b = dbList.contains(tablename);
       
        if (b) {
        	File table = new File(currentPath, tablename+".xls");
	        // 要修改的表描述
	        List<List<String>> lists = new ArrayList<>(); 
	        lists = utils.describle(table);
	        ArrayList<String> t_clo_name = new ArrayList<>();
	        int clo_num = lists.size();
	        // 该表的所有属性名
	        for(int c=0; c<clo_num; c++) 
	        	t_clo_name.add(lists.get(c).get(0));
	        // 初步判断插入的列（1、列数超过插入表的列数，退出2、不都是这个表的列， 退出）
	        if((0 <list_clo_value.size()) && (list_clo_value.size()<= clo_num)) {
		        for (String i : list_clo_name) {
		        	if (t_clo_name.contains(i)) ;
		        	else {
		        		System.out.println("ERROR: "+ i +"不是表" + tablename +"的列");
			        	return false;
		        	}
		        }
	        }else {
	        	System.out.println("ERROR: 要修改列数超过表列数！");
	        	return false;
	        }
	        //判断是否有条件查询，如果有，查询是否有满足查询条件的元组, 没有直接退出，
	       	 // 如果有列名,则表示有查询条件
	         ArrayList<point> target_location1 = new ArrayList<>();
	       	 if(! columnName.equals("")) {		        		  
		        	 // 直接去查有没有符合条件的元组		        	
		        	 target_location1 = utils.search(table, columnName, op, clo_value.getValue());		        	 
		        	 if(target_location1.size()==0) {
		        		 System.out.println(tablename + "中没有符合修改条件元组！");
		        		 return false; 
		        	 }		        		 		        	 
		        	 else if(target_location1.get(0).get_row()== -1) {
		        		 System.out.println("属性列"+ columnName +"不在"+tablename+"表中"); 
		        		 return false;
		        	 }
		        	 //如果这里没有return,说明存在元组要show
	       	 }
	       	 
	       	 //可插入属性列表
	       	ArrayList<String> list_accept_clo = new ArrayList<>();  
	        
	        //经过上面这些判断之后，list_clo_name中的列名只会是表的列名中的
	         try {
	 	    	// 打开文件  
	 	        Workbook book = Workbook.getWorkbook(table); 
	 	        // 获取sheet1(data) 
	 	        Sheet sheet = book.getSheet("data");

	 	        int data_rows = sheet.getRows(); //数据行数
		        int data_cols = sheet.getColumns();
		        int t_clo_num=-1;
		        

	            // 向列中写数据
		        /*
				 * for: list_clo_name中每一个要修改的属性：
				 *     1、判断int/char是否符合条件
				 *     2、是主键/Unique
				 *       1、没有条件查询，数据行数==1，该属性可以执行set
				 *       2、(有条件查询)有满足条件的元组，target_location长度==1&&
				 *          查询excel表中的当前列是否存在==对应list_clo_value的单元格（除了要插入单元格外）
				 *          如果不存在，就可以插入
				 *     3、else不是主键/Unique
				 *       1、该属性可以直接执行set  
		         * */
				 for (int col = 0; col < list_clo_name.size(); col++) {		
				     //该列名在data表中的列号
					 t_clo_num = t_clo_name.indexOf(list_clo_name.get(col));
					 String current_clo_name = list_clo_name.get(col);
					 // 完整性约束
					 String current_constraint = lists.get(t_clo_num).get(3);
//					 System.out.println( current_clo_name + "  " + current_constraint);
					 //先定义默认t_value ="";
					 String t_value = "";
					 SupportedValue value = list_clo_value.get(col);
					 //拿到 赋值的 string值
					 t_value = value.getValue(); 
					//如果赋进来的value是个数字，列属性也是int
					//我在jj中已经默认其是个int了,转型int
					//int 检验
					if ((value.getDataType() ==  DataType.INT) && ((lists.get(t_clo_num).get(1)).equalsIgnoreCase("int"))) { 
//						 System.out.println("判断int");
						 try {
							  int temp1 = Integer.parseInt(t_value);
							} catch (NumberFormatException e) {
								System.out.println("ERROR:属性" + current_clo_name +" int转型出错！");
							    e.printStackTrace();
							}
						 //转型成功，符合规则int
					 }
					//如果赋进来的value是个String，列属性是char
					//因为jj文件中知识简单判断是不是字母数字的组合，这里我直接判断string长度似乎不是超了作为合不合格的基准
					 //char 检验
					 if ((value.getDataType() ==  DataType.String) && ((lists.get(t_clo_num).get(1)).equalsIgnoreCase("vachar"))) { 
//						 System.out.println("判断vachar");
						 int char_lenth = 0;
	                     try {
	                    	  char_lenth = Integer.parseInt(lists.get(t_clo_num).get(2));
							} catch (NumberFormatException e) {
							    e.printStackTrace();
							}
	                     if (t_value.length() > char_lenth) {
	         	        	System.out.println("ERROR:属性" + current_clo_name +" 插入字符串长度超过定义！");
	         	        	return false;
	                     }
						  //转型成功，符合规则char
					 }
			        /*
			         *    2、是主键/Unique
					 *       1、没有条件查询，数据行数==1，该属性可以执行set
					 *       2、(有条件查询)有满足条件的元组，target_location长度==1&&
					 *          查询excel表中的当前列是否存在==对应list_clo_value的单元格（除了要插入单元格外）
					 *          如果不存在，就可以插入    
			         * */				 
					 if ((current_constraint.equalsIgnoreCase("unique")) || (current_constraint.equalsIgnoreCase("primary key"))) {
				       	 // 没有条件查询
						 if(columnName.equals("")) {		        		  
				        	 if(data_rows == 1) {list_accept_clo.add(current_clo_name); }	
				        	 else {
				        	     System.out.println("ERROR:修改失败！unique/primary key");
				        	     return false;
				        	 }
						 }else { // 有条件查询,满足条件的元组，且只有一行
							 if (target_location1.size() ==1) {
								 ArrayList<point> target_location2 = new ArrayList<>();
								 // 去查有没有与要插入单元格相等的元组		        	
						         target_location2 = utils.search(table, current_clo_name,"=", t_value);
						         if(target_location2.size() > 1) {
						        	 System.out.println("ERROR:修改失败！");
						        	 return false;
						         }
						         if(target_location2.size()==0) list_accept_clo.add(current_clo_name); //可以插入
						         // 找到的单元格是自己
						         else if( target_location2.size()==1 && 
						        	  ((target_location2.get(0).get_row())==(target_location1.get(0).get_row())))
						        	      list_accept_clo.add(current_clo_name);//可以插入
							         else{
							        	 System.out.println("ERROR:修改失败！unique/primary key");
							        	 return false;
							         }
							 }else {
								 System.out.println("ERROR:修改失败！unique/primary key");
								 return false; 
							 } 
						 }
					 }else { //else 不是主键/Unique,该属性可以直接执行set
						 list_accept_clo.add(current_clo_name);//可以插入
					 } 
				}//for
		        } catch (Exception e) {  
		            System.out.println(e);  
		        }
	         try {
	        	//设置单元格居中
				 WritableFont font = new WritableFont(WritableFont.ARIAL);
				 WritableCellFormat cellFormat = new WritableCellFormat(font);
				 cellFormat.setAlignment(jxl.format.Alignment.CENTRE);   //这里强制要我执行try catch
		        // 获得文件  
	             Workbook wb = Workbook.getWorkbook(table); 
	            // 打开文件的副本并将指定数据写回原文件？ 
	             WritableWorkbook book = Workbook.createWorkbook(table, wb); 
	            // 拿到名为“data”的工作表  
	            WritableSheet sheet = book.getSheet("data"); 
	 	        int data_rows = sheet.getRows(); //数据行数
	            // 开始写
				if (list_accept_clo.size() == list_clo_name.size()) {  
				  int current_clo_num=-1;
				  
				  //对于每一个要写入的列
				  for (int col = 0; col < list_clo_name.size(); col++) {
					     //该列名在data表中的列号
						 current_clo_num = t_clo_name.indexOf(list_clo_name.get(col));
						 if(columnName.equals("")) { //写入所有行
							 for(int i = 1; i<data_rows; i++) {
								 sheet.addCell(new Label(current_clo_num, i, list_clo_value.get(col).getValue(), cellFormat));
								 System.out.println("修改第"+i+"行"); 
							 }
						 }else{ //写入找到的元组
							  for(int i = 0; i<target_location1.size(); i++) {
								  Label label = new Label(current_clo_num, target_location1.get(i).get_row(),
										                  list_clo_value.get(col).getValue(), cellFormat);
								  sheet.addCell(label);
								  System.out.println("修改第"+target_location1.get(i).get_row()+"行");
							  }
						 }	   	
			      }
				} 
				book.write();  
	            book.close();   	 
	         }catch (Exception e) {  
		           System.out.println(e);  
		     }		 
        }else {
        	System.out.println("ERROR: 该数据表不存在");
        	return false; 
        }
        return true;      
	}//update_record

}

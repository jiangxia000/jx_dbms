package operate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;  
import jxl.write.Label;  
import jxl.write.WritableSheet;  
import jxl.write.WritableWorkbook;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;


public class table {
	
    // 创建表（在对应数据库下面创建一个excel文件）
    // 需要的属性：表名、列名(list)、列属性(list)、列完整性约束（list） 
	public static boolean create_table(String table_name, ArrayList<String> list_clo_name, 
		                        ArrayList<Integer> list_clo_type2, ArrayList<String> list_clo_constraint){
		 System.out.println("语句识别成功");
		// 获取当前数据库所在的路径（正常情况下）
		String currentPath = SQLConstant.getNowPath();
		 List<String> tableList = utils.getAllFiles(currentPath);
		 boolean a = tableList.contains(table_name);
		// 如果同名table不存在
		 if (!a) {
			//因为当时在.jj中只记录了数字0（int）和char的数字，这里要把char和int给再提出来
			 List<String> list_clo_type1 = new ArrayList<String>();
			 for (Integer i : list_clo_type2) {
			        if(i==0) list_clo_type1.add("int");
			        else list_clo_type1.add("vachar");
			       }

	        //创建表,以一个excel文件表示一个表
	         try {
	        	//设置单元格居中
				 WritableFont font = new WritableFont(WritableFont.ARIAL);
				 WritableCellFormat cellFormat = new WritableCellFormat(font);
				 cellFormat.setAlignment(jxl.format.Alignment.CENTRE);   //这里强制要我执行try catch
				 
				//当前数据库的路径 = 当前路径 + 数据库名称
				 File table = new File(currentPath, table_name+".xls");
	        	// 打开文件  
	             WritableWorkbook book = Workbook.createWorkbook(table); 
	            // 生成名为“define”的工作表，存放表定义，参数0表示这是第一页  
	             WritableSheet sheet1 = book.createSheet("define", 0); 
	            // 生成名为“data”的工作表，存放表数据，参数0表示这是第二页 
	             WritableSheet sheet2 = book.createSheet("data", 1); 
	            // 向行和列中写数据
	            // 四行：sname, char ,10, not null
				 for (int col = 0; col < list_clo_name.size(); col++) {
					//第一行
					sheet1.addCell(new Label(col, 0, list_clo_name.get(col), cellFormat));
					// 第二行
					sheet1.addCell(new Label(col, 1, list_clo_type1.get(col), cellFormat));
					// 第三行
					int clo_num = list_clo_type2.get(col);
					if (! (clo_num == 0)) 
					sheet1.addCell(new Label(col, 2, Integer.toString(clo_num), cellFormat));						
					// 第四行
					String constraint = list_clo_constraint.get(col);
					if (! (constraint == "none"))
					sheet1.addCell(new Label(col, 3, list_clo_constraint.get(col), cellFormat));
				}	
				// sheet2(data)第一行
				for (int col = 0; col < list_clo_name.size(); col++)
			        sheet2.addCell(new Label(col, 0, list_clo_name.get(col), cellFormat));
				
				book.write();  
	            book.close();    
	        } catch (Exception e) {  
	            System.out.println(e);  
	        }
	    }
	    else {
	          System.out.println("ERROR: table " + table_name +" already exist!" );
	         }
		 return true;         
	}
	
	// 删除当前数据库下的某个表
	public static void drop_table(String tablename) {
		System.out.println("语句识别成功");
    	String path = SQLConstant.getNowPath();
        List<String> dbList = utils.getAllFiles(path);
        boolean a = dbList.contains(tablename);
        if(a) {
            // 确认是否删除
            boolean b = utils.confirm();
            if (b) {
                String nowPath = path + "\\" + tablename + ".xls";
                utils.deleteFile(new File(nowPath));
                System.out.println("table "+ tablename + " is droped successfully");
            }
            else{
                System.out.println("撤回成功,表未被删除");
            }
        }
        else{
            System.out.println("ERROR: 该数据表不存在");
        }
	}
	
	// 显示当前数据库下的所有表
	public static void show_tables() {
		System.out.println("语句识别成功");
    	String path = SQLConstant.getNowPath();
    	// 当前数据库的所有表名放在tableList里面
        List<String> tableList = utils.getAllFiles(path);
        List<String> table = new ArrayList<>();
        table.add("Tables");
        List<List<String>> list = new ArrayList<>();
        for(int i = 0; i < tableList.size(); i++){
            List<String> ls = new ArrayList<>();
            ls.add(tableList.get(i));
            list.add(ls);
        }
        System.out.println(Tab.tab(table, list));
	}
	
	
	// 返回该表的详细描述
	/* 1、查找表（excel表是否存在）
	 * 2、存在，则读取sheet0(表定义)
	 * */
	public static void desc_table(String tablename) {
		System.out.println("语句识别成功");
    	String currentPath = SQLConstant.getNowPath();
        List<String> dbList = utils.getAllFiles(currentPath);
        //判断要先显示的表是否存在
        boolean b = dbList.contains(tablename);
        if (b) {
	        File table = new File(currentPath, tablename+".xls");
	     // 要打印的表内容,
	        List<List<String>> lists = new ArrayList<>(); 
	        lists = utils.describle(table);
	
	        List<String> list4 = new ArrayList<>();
	        list4.add("Filed");
	        list4.add("Type1");
	        list4.add("Type2");
	        list4.add("Extra");
	        System.out.println(Tab.tab(list4, lists));
        }else {
        	System.out.println("ERROR: 该数据表不存在");
        }
	}	
}

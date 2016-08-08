package TaxiDespatch;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReqThread extends Thread {
	
	public ReqThread() {
		super();
	}
	
	public void run () {
		
		Scanner sc = new Scanner(System.in);
		BlockingQueue<PassengerReq> bqList = new LinkedBlockingQueue<PassengerReq>();
		
		
		//在此处构建请求队列
//		bqList.add(new PassengerReq("(1,2)(56,7)"));
//		bqList.add(new PassengerReq("(1,7)(6,17)"));
		
		while ( true ) {
			
			// 为请求启动调度器线程
			try {
				
				while( !bqList.isEmpty() ) {
					new Despatcher(bqList.take()).start();
				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

			// 读入请求
			String s = sc.nextLine();
			
			String regex = "(\\(\\d+,\\d+\\)\\(\\d+,\\d+\\))";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(s);
			
			String regex2 = "((open|close)\\(\\d+,\\d+\\)\\(\\d+,\\d+\\))";
			Pattern pattern2 = Pattern.compile(regex2);
			Matcher matcher2 = pattern2.matcher(s);
			
			
			if ( matcher.matches() ) {
				PassengerReq req = new PassengerReq(s);
				if( !req.repOK() ) {
					System.err.println("请求不合法，请再输入一次");
					continue;
				}
				else {
					bqList.offer(req);
				}
			}
			
			else if ( matcher2.matches() ) {
				String[] str = s.split("[(,)]");
				
				Point p1 = new Point( Short.parseShort(str[1]) , Short.parseShort(str[2]) );
				Point p2 = new Point( Short.parseShort(str[4]) , Short.parseShort(str[5]) );
				
				if ( str[0].equals("open") ) {
					MapInfo.openRoad(p1, p2);
				}
				else if ( str[0].equals("close") ) {
					MapInfo.closeRoad(p1, p2);
				}
				
			}
			
			else {
				System.err.println("请求格式有误，请输入正确的请求");
				continue;
			}
			
	
			
		}
	}

}

class PassengerReq {
	
	// Overview：乘客请求，用于向请求处理线程发送请求
	
	// 表示对象：fromPoint, toPoint
	// 抽象函数：AF(c) = (fromPoint,toPoint) 
	// 输入 的字符串分割后的第1，2位为fromPoint.x,fromPoint.y 第3，4位为toPoint.x,toPoint.y
	
	// invariant： fromPoint.repOK == true && toPoint.repOK() == true
 	
	Point fromPonit;
	Point toPoint;
	
	// constructor
	// EFFECTS: 根据输入的字符串获取请求的信息，对fromPoint和toPoint进行初始化
	public PassengerReq ( String s ) {
		String[] str = s.split("[(,)]");
		try {
			fromPonit = new Point( Short.parseShort(str[1]) , Short.parseShort(str[2]) );
			toPoint = new Point( Short.parseShort(str[4]) , Short.parseShort(str[5]) );
		} catch (Exception e) {
			System.err.println("数字超出short范围");
			System.exit(0);
		}
		
	}
	
	/*  
	 *  EFFECTS:检查一个请求是否合法（即是否符合不变式）,合法返回true，非法则返回false
	 */
	public boolean repOK () {
		return fromPonit.repOK() && toPoint.repOK();
	}
	
	
}

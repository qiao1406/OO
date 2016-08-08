package TaxiDespatch;

public class TaxiSystem {
	
	static long sysStartTime;
	final static int TAXI_NUM = 100; // 出租车的总数量
	static Taxi[] taxis = new Taxi[TAXI_NUM];
	
	public static void main(String[] args) {
		
		System.out.println("欢迎使用打车系统v3.0");
		// 载入地图
		MapInfo.readMap(); // 这个方法读入的地图是含有空格的
//		MapInfo.readMap2(); // 这个方法读入的地图是不含空格的
		MapInfo.readCross(); // 这个方法读入的交叉信息文件是含空格的
//		MapInfo.readCross2(); // 这个方法读入的交叉信息文件是不含空格的
		MapInfo.initCost();
		if( !MapInfo.testMap() ) {
			System.err.println("给定的地图是不连通的，请导入符合要求的地图");
			System.exit(0);
		}
		System.out.println("地图文件以及交叉信息文件导入成功");
		
		sysStartTime = System.currentTimeMillis(); // 系统启动时间
		
		// 启动红绿灯模块
		new TrafficLight().start();
		// 开始进行车流量的统计（需先于出租车的启动）
		new Flow().start();
		
		// 初始化出租车
		for ( short i = 0; i < TAXI_NUM; ++i ) {
			TaxiSystem.taxis[i] = new Taxi((short) (i+1));
			TaxiSystem.taxis[i].start();
		}
		System.out.println("出租车已就绪");
		
		// 开始处理乘客请求
		new ReqThread().start();
	}
	
	// EFFECTS:输出当前的系统时间
	public static void printRuntime  () {
		System.out.println("[时间：" + (System.currentTimeMillis() - TaxiSystem.sysStartTime)/1000.0 + "s]");
	}
	
	/*
	 * 测试者可使用
	 * EFFECTS：访问所有处于等待状态的出租车，输出其信息
	 */
	public static void printWaitttingTaxis () {
		for( Taxi t : TaxiSystem.taxis ) {
			if( t.isTaxiWaitting() ) {
				System.out.println(t.taxiInfo());
			}
		}
	}

}

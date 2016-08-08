package TaxiDespatch;

import java.util.Random;

public class TrafficLight extends Thread {
	
	//Overview: 这个类是用来实现红绿灯的控制，用一个short的二维数组来表示红绿灯
	
	// 表示对象：short[][] tflight
	// 抽象函数：AF(c) = tflight 
	//( tflight[i][j] 表示坐标为（i,j）点 处的红绿灯状况，
	// 1表示上下方向可以通行，左右方向被阻塞 -1表示左右方向可以通行，上下方向被阻塞,0表示该点不存在红绿灯)
	
	// invariant： tflight[i][j] in {0,1,-1}
	
	final static long LIGHT_ROUND_TIME = 300;
	static short[][] tflight = new short[MapInfo.MAP_ROW][MapInfo.MAP_COLUMN];
	
	// constructor
	/*
	 * MODIFIES：Traffic.tflight
	 * EFFECTS：利用MapInfo类中的信息对tflight数组进行初始化，当一个点是平面交叉且有三条或四条道路的时候
	 * 		为它在tflight相应的位置随机设置红绿灯（值设成1或-1），否则不设置红绿灯（值设成0）
	 */
	public TrafficLight ( ) {
		Random r = new Random();
		
		for ( short i = 0; i < MapInfo.MAP_ROW; ++i ) {
			for ( short j = 0; j < MapInfo.MAP_COLUMN; ++j ) {
				
				if ( MapInfo.cross[i][j] == true ) { // 平面交叉
					Point p = new Point( (short)i , (short)j );
					tflight[i][j] = (short) (( MapInfo.isCrossRoad(p) )? (2*r.nextInt(2) - 1 ): 0);
				}
				else { // 立体交叉
					tflight[i][j] = 0;
				}
				
				
			}
		}
		
	}
	
	// EFFECTS：判断是否符合不变式，是则返回true，否则返回false
	public static boolean repOK () {
		
		for ( short i = 0; i < MapInfo.MAP_ROW; ++i ) {
			for ( short j = 0; j < MapInfo.MAP_COLUMN; ++j ) {
				if ( tflight[i][j] < -1 || tflight[i][j] > 1 ) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	//mutator
	/*
	 * MODIFIES：TrafficLight.tflight
	 * EFFECTS：改变红绿灯的状态，红灯变绿灯，绿灯变红灯
	 */
	public static void changeLightState () {
		
		for ( short i = 0; i < MapInfo.MAP_ROW; ++i ) {
			for ( short j = 0; j < MapInfo.MAP_COLUMN; ++j ) {
				tflight[i][j] *= -1;
			}
		}
		
	}
	
	/*
	 * EFFECTS：根据出租车的位置和它下一步要去的点以及红绿灯，判断出租车是否需要等待
	 * 	1.任何情况下右转都不需要等待 ；2.直行、左转和掉头的时候，看将要去的路：是绿灯不等待，红灯等待
	 */
	public static boolean waitLight (Point lastPosition, Point position, Point p ) {
		
		// 判断是否是直行、左转或者掉头 
		
		//  1. 左右 方向上的直行 或者掉头
		if ( position.x == lastPosition.x && position.x == p.x  ) {
			return ( tflight[position.x][position.y] == 1 )? true: false;
		}
		//  2. 上下 方向上的直行 或者掉头
		else if ( position.y == lastPosition.y && position.y == p.y ) {
			return ( tflight[position.x][position.y] == -1 )? true: false;
		}
		// 3. 从上下方向---- 左转到 ----左右方向
		else if ( p.equals( position.getLeftPoint()) && lastPosition.equals(position.getDownPoint())
				||  p.equals( position.getRightPoint()) && lastPosition.equals(position.getUpPoint()) ) {
			return ( tflight[position.x][position.y] == 1 )? true: false;
		}
		// 4.从左右方向 ---- 左转到----上下方向
		else if ( p.equals( position.getDownPoint()) && lastPosition.equals(position.getRightPoint())
				||  p.equals( position.getUpPoint()) && lastPosition.equals(position.getLeftPoint()) ) {
			return ( tflight[position.x][position.y] == -1 )? true: false;
		}
		//  5.右转
		else { 
			return false;
		}
		
	}
	 
	public void run() {
		
		System.out.println("红绿灯模块已就绪");
		while ( true ) {
			
			try {
				sleep(LIGHT_ROUND_TIME);
			} catch (InterruptedException e) {
				System.out.println("红绿灯信号出错");
			}
			changeLightState();
		}
	}
	
	
}

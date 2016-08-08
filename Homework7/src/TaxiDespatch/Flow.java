package TaxiDespatch;


public class Flow extends Thread {
	
	// Overview：用一个short型的二维数组来存储道路流量信息
	
	// 表示对象: short[][] roadFLow
	// 抽象函数： AF(c) = roadFLow (roadFlow[i][j]表示索引值为i的点的j方向的车流量，j方向：0上，1下，2左，3右)、
	
	// invariant：roadFlow[i][j] >= 0 for all (i,j)
		
	static volatile short[][] roadFlow = new short[MapInfo.MAP_COLUMN*MapInfo.MAP_ROW][4];
	
	// constructor
	// EFFECTS:构造一个记录车流量的进程
	public Flow() {
		super();
	}
	
	// EFFECTS：判断是否符合不变式，是则返回true，否则返回false
	public static boolean repOK () {
		
		for ( short i = 0; i < MapInfo.MAP_COLUMN*MapInfo.MAP_ROW; ++i ) {
			for ( short j = 0; j < 4; ++j ) {
				if ( roadFlow[i][j] < 0 ) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	/*
	 * EFFECTS：初始化道路流量信息数组
	 */
	public static void initRoadFlow () {
		
		for ( short i = 0; i < MapInfo.MAP_ROW; ++i ) {
			for ( short j = 0; j < MapInfo.MAP_COLUMN; ++j ) {
				
				int index = MapInfo.calIndex(i, j);
				int indexUp = MapInfo.calIndex((short) (i-1),j);
				int indexDown = MapInfo.calIndex((short) (i+1), j);
				int indexLeft = MapInfo.calIndex(i, (short) (j-1));
				int indexRight = MapInfo.calIndex(i, (short) (j+1));
				
				roadFlow[index][MapInfo.UP_ROAD] =
						(short) (( indexUp >= 0 && indexUp < MapInfo.MAP_COLUMN*MapInfo.MAP_ROW 
						&& MapInfo.cost[index][0] == true )? 0: -1);
				roadFlow[index][MapInfo.DOWN_ROAD] =
						(short) (( indexDown >= 0 && indexDown < MapInfo.MAP_COLUMN*MapInfo.MAP_ROW 
						&& MapInfo.cost[index][1] == true )? 0: -1);
				roadFlow[index][MapInfo.LEFT_ROAD] = 
						(short) (( indexLeft >= 0 && indexLeft < MapInfo.MAP_COLUMN*MapInfo.MAP_ROW 
						&& MapInfo.cost[index][2] == true )? 0: -1);
				roadFlow[index][MapInfo.RIGHT_ROAD] = 
						(short) (( indexRight >= 0 && indexRight < MapInfo.MAP_COLUMN*MapInfo.MAP_ROW 
						&& MapInfo.cost[index][3] == true )? 0: -1);
			
			}
		}
		
	}
	
	/*
	 * 车流量+1
	 * REQUIRES: 点start和点end必须都在地图内
	 * MODIFIES： None
	 * EFFECTS： 对应的roadFlow增加1，表示经过的车流量增加
	 */
	public static void addFlow( Point start, Point end ) {
		int index1 = MapInfo.calIndex(start);
		int index2 = MapInfo.calIndex(end);
		
		
		if ( Point.dirInCross(start, end) == 1 ) {
			roadFlow[index1][MapInfo.UP_ROAD]++;
			roadFlow[index2][MapInfo.DOWN_ROAD]++;
		}
		else if ( Point.dirInCross(start, end) == 2 ) {
			roadFlow[index1][MapInfo.DOWN_ROAD]++;
			roadFlow[index2][MapInfo.UP_ROAD]++;
		}
		else if ( Point.dirInCross(start, end) == 3 ) {
			roadFlow[index1][MapInfo.LEFT_ROAD]++;
			roadFlow[index2][MapInfo.RIGHT_ROAD]++;
		}
		else if ( Point.dirInCross(start, end) == 4 ) {
			roadFlow[index1][MapInfo.RIGHT_ROAD]++;
			roadFlow[index2][MapInfo.LEFT_ROAD]++;
		}
		
	}
	
	/*
	 * 车流量-1
	 * REQUIRES: 点start和点end必须都在地图内
	 * MODIFIES： None
	 * EFFECTS： 对应的roadFlow减少1，表示经过的车流量减少
	 */
	public static void subFlow( Point start, Point end ) {
		int index1 = MapInfo.calIndex(start);
		int index2 = MapInfo.calIndex(end);
		
		
		if ( Point.dirInCross(start, end) == 1 ) {
			roadFlow[index1][MapInfo.UP_ROAD]--;
			roadFlow[index2][MapInfo.DOWN_ROAD]--;
		}
		else if ( Point.dirInCross(start, end) == 2 ) {
			roadFlow[index1][MapInfo.DOWN_ROAD]--;
			roadFlow[index2][MapInfo.UP_ROAD]--;
		}
		else if ( Point.dirInCross(start, end) == 3 ) {
			roadFlow[index1][MapInfo.LEFT_ROAD]--;
			roadFlow[index2][MapInfo.RIGHT_ROAD]--;
		}
		else if ( Point.dirInCross(start, end) == 4 ) {
			roadFlow[index1][MapInfo.RIGHT_ROAD]--;
			roadFlow[index2][MapInfo.LEFT_ROAD]--;
		}
		
	}
	
	/*
	 * EFFECTS： 返回道路（p1,p2）的实时车流量,若该道路不存在则返回-1
	 */
	public static short getRoadFlow ( Point p1, Point p2 ) {
		int index1 = MapInfo.calIndex(p1);
		
		if ( Point.dirInCross(p1, p2) == 1 ) {
			return  Flow.roadFlow[index1][MapInfo.UP_ROAD];
		}
		else if ( Point.dirInCross(p1, p2) == 2 ) {
			return Flow.roadFlow[index1][MapInfo.DOWN_ROAD];
		}
		else if ( Point.dirInCross(p1, p2) == 3 ) {
			return Flow.roadFlow[index1][MapInfo.LEFT_ROAD];
		}
		else if ( Point.dirInCross(p1, p2) == 4 ) {
			return Flow.roadFlow[index1][MapInfo.RIGHT_ROAD];
		}
		
		return -1;
	}
	
	public void run () {
		
		System.out.println("车流量统计模块已就绪");
		
		while ( true ) {
			initRoadFlow();
			try {
				sleep(50);
			} catch (Exception e) {
				System.err.println("（Flow.class）时间窗出现错误");
			}
		}
	}
	
}

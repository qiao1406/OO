package TaxiDespatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class MapInfo {
	
	// Overview： 利用info数组来存储从地图文件中读取的关于地图的信息，用cross数组来存储从文件中读取的交叉路口信息
	// 用cost存储每个点与其邻接边的信息
	
	// 表示对象： short[][] info, boolean[][] cross, boolean[][] cost
	// 抽象函数：AF(c) = (info,cross,cost) info[i][j] in {0,1,2,3} cross[i][j] = true:1,false:0;
	// (以上两个二维数组取决于读入的文件)
	// cost[i][j]:j方向上是否有道路，是true，否false；（j方向：0上，1下，2左，3右）
	
	// invariant： 地图是连通的 && info[i][j] in {0,1,2,3}
	
	// 一些常量
	final static short MAP_COLUMN = 80; // 列数
	final static short MAP_ROW = 80; // 行数
	final static int UP_ROAD = 0;
	final static int DOWN_ROAD = 1;
	final static int LEFT_ROAD = 2;
	final static int RIGHT_ROAD = 3;
	
	static short[][] info = new short[MAP_ROW][MAP_COLUMN]; // 存地图的信息
	static boolean[][] cross = new boolean[MAP_ROW][MAP_COLUMN]; // 存交叉路口的信息
	static volatile boolean[][] cost = new boolean[MAP_ROW*MAP_COLUMN][4]; // 存道路长度  
	
	// EFFECTS： 判断是否符合不变式，是则返回true，否则返回false
	public static boolean repOK () {
		if ( !testMap() ) {
			return false;
		}
		for( short i = 0; i < MAP_ROW; ++i ) {
			for ( short j = 0; j < MAP_COLUMN; ++j ) {
				if ( info[i][j] < 0 || info[i][j] > 4 )
					return false;
			}
		}
		return true;
	}
	
	/*
	 *  可修改
	 * 从文件中读入地图
	 * 读入的地图是含空格的
	 * MODIFIES： MapInfo.info
	 * EFFECTS：从地图文件中读入地图的信息，将其存到MapInfo.info数组中
	 */
	public static void readMap() {

		try {
			
			String dir = System.getProperty("user.dir");
			File f = new File(dir+"//map.txt"); // 此处为地图所在的路径
			FileReader fr = new FileReader(f);
			BufferedReader bf = new BufferedReader(fr);

			for (int i = 0; i < MAP_ROW; i++) {
				String[] s = bf.readLine().split(" ");
				for (int j = 0; j < MAP_COLUMN; j++) {
					MapInfo.info[i][j] = Short.parseShort(s[j]);
				}
			}
			bf.close();

		} catch (IOException e) {
			System.err.println("地图文件错误");
			System.exit(0);
		} catch (IndexOutOfBoundsException e) {
			System.err.println("地图点数错误，请检查地图");
			System.exit(0);
		} catch (Exception e) {
			System.err.println("地图存在未知错误，程序已退出");
			System.exit(0);
		}

	}
	
	/*
	 *  可修改
	 * 从文件中读入地图
	 * 读入的地图是不含空格的
	 * MODIFIES： MapInfo.info
	 * EFFECTS：从地图文件中读入地图的信息，将其存到MapInfo.info数组中
	 */
	public static void readMap2() {

		try {

			String dir = System.getProperty("user.dir");
			File f = new File( dir + "//map1.txt"); // 此处为地图所在的路径
			FileReader fr = new FileReader(f);
			BufferedReader bf = new BufferedReader(fr);

			for (int i = 0; i < MAP_ROW; i++) {
				String s = bf.readLine();
				
				
				for (int j = 0; j < MAP_COLUMN; j++) {
					MapInfo.info[i][j] = (short) (s.charAt(j) - '0');
				}
			
			}
			bf.close();

		} catch (IOException e) {
			System.err.println("地图文件错误");
			System.exit(0);
		} catch (IndexOutOfBoundsException e) {
			System.err.println("地图点数错误，请检查地图");
			System.exit(0);
		} catch (Exception e) {
			System.err.println("地图存在未知错误，程序已退出");
			System.exit(0);
		}

	}

	/*
	 * 可修改
	 * 读入的是带空格的文件
	 * MODIFIES：MapInfo.cross
	 * EFFECTS：从文件中读入交叉路口的信息，存到Mapinfo.cross数组中，平面交叉：true，立体交叉：false
	 */
	public static void readCross () {
		try {

			String dir = System.getProperty("user.dir");
			File f = new File( dir + "//crossinfo.txt"); // 此处为交叉路口信息的存放路径 
			FileReader fr = new FileReader(f);
			BufferedReader bf = new BufferedReader(fr);

			for (int i = 0; i < MAP_ROW; i++) {
				String[] s = bf.readLine().split(" ");
				for (int j = 0; j < MAP_COLUMN; j++) {
					MapInfo.cross[i][j] = ( Short.parseShort(s[j]) == 1 )? true: false;
				}
			}
			bf.close();

		} catch (IOException e) {
			System.err.println("交叉路口信息文件错误");
			System.exit(0);
		} catch (IndexOutOfBoundsException e) {
			System.err.println("交叉路口信息文件的点数错误，请检查地图");
			System.exit(0);
		} catch (Exception e) {
			System.err.println("交叉路口信息文件存在未知错误，程序已退出");
			System.exit(0);
		}
		
	}
	
	/*
	 * 可修改
	 * 读入的是不带空格的文件
	 * MODIFIES：MapInfo.cross
	 * EFFECTS：从文件中读入交叉路口的信息，存到Mapinfo.cross数组中，平面交叉：true，立体交叉：false
	 */
	public static void readCross2 () {
		
		try {
			
			String dir = System.getProperty("user.dir");
			File f = new File( dir + "//crossinfo1.txt"); // 此处为交叉路口信息所在的路径
			FileReader fr = new FileReader(f);
			BufferedReader bf = new BufferedReader(fr);

			for (int i = 0; i < MAP_ROW; ++i ) {
				String s = bf.readLine();
				
				for (int j = 0; j < MAP_COLUMN; ++j ) {
					MapInfo.cross[i][j] = ( (short) (s.charAt(j) - '0') == 1 )? true: false;
				}
			
			}
			bf.close();

		} catch (IOException e) {
			System.err.println("交叉路口信息文件错误");
			System.exit(0);
		} catch (IndexOutOfBoundsException e) {
			System.err.println("交叉路口信息的点数错误，请检查地图");
			System.exit(0);
		} catch (Exception e) {
			System.err.println("交叉路口信息文件存在未知错误，程序已退出");
			System.exit(0);
		}
	}
	
	/*
	 * MODIFIES： MapInfo.cost
	 * EFFECTS：初始化6400*4的矩阵用于表示邻接矩阵    下标 ：0上 1下 2左 3右
	 * 值：  true道路存在， false道路不存在
	 */
	public static void initCost() {

		for (short i = 0; i < MAP_ROW; ++i ) {
			for (short j = 0; j < MAP_COLUMN; ++j ) {
				
				short index = calIndex(i, j);
				Point p = new Point(i, j);
				
				MapInfo.cost[index][0] = hasUp(p);
				MapInfo.cost[index][1] = hasDown(p);
				MapInfo.cost[index][2] = hasLeft(p);
				MapInfo.cost[index][3] = hasRight(p);
				
			}
		}
	}
	
	/*
	 * EFFECTS： 用BFS算法判断地图是否为连通图，是则返回true，否则返回false
	 */
	public static boolean testMap() {
		boolean[] visit = new boolean[MAP_ROW*MAP_COLUMN];
		Queue<Short> toVist = new LinkedList<Short>();

		int num = 0;
		short v = 0;
		toVist.add(v);
		visit[v] = true;
		num++;

		while (toVist.size() != 0) {
			
			v = toVist.poll();
			Point nowPoint = calPoint(v);
			
			for (int i = 0; i < 4; i++) {
				
				Point nextPoint = ( i == 0 )? nowPoint.getUpPoint():
					( i == 1 )? nowPoint.getDownPoint():
						( i == 2 )? nowPoint.getLeftPoint(): nowPoint.getRightPoint();
				
				short visitIndex = calIndex(nextPoint);
				
				if (MapInfo.cost[v][i] == true && visit[visitIndex] == false) {
					toVist.add(visitIndex);
					visit[visitIndex] = true;
					num++;
				}
				
			}
			
		}

		return num == MAP_ROW * MAP_COLUMN;
	}
	
	/*
	 * EFFECTS：找到数组dist中最小的且s[i] == false的点，返回其下标
	 */
	public static short minDist(boolean[] s, short[] dist) {
		short min = Short.MAX_VALUE;
		short index = Short.MAX_VALUE;
		for (short i = 0; i < dist.length; i++) {
			if (s[i] == true)
				continue;
			if (min > dist[i]) {
				min = dist[i];
				index = i;
			}

		}
		return index;
	}


	/*
	 * EFFECTS: 通过广搜算法（BFS）算出start到end的最短路径，返回的是最短路径的长度
	 */
	public static int calShortestLen ( Point start, Point end ) {
		LinkedList<Point> sp = new LinkedList<Point>();
		boolean[] visit = new boolean[MAP_ROW*MAP_COLUMN];
		short[] path = new short[MAP_ROW*MAP_COLUMN];
		Queue<Short> toVist = new LinkedList<Short>();
		
		short v = calIndex(start);
		toVist.add(v);
		visit[v] = true;
		
		while ( visit[calIndex(end)] == false ) {
			
			v = toVist.poll();
			Point nowPoint = calPoint(v);
			for (int i = 0; i < 4; ++i ) {
				
				Point nextPoint = ( i == 0 )? nowPoint.getUpPoint():
					( i == 1 )? nowPoint.getDownPoint():
						( i == 2 )? nowPoint.getLeftPoint(): nowPoint.getRightPoint();
				
				short nextIndex = calIndex(nextPoint);
				
				if (MapInfo.cost[v][i] == true && visit[nextIndex] == false) {
					toVist.add(nextIndex);
					path[nextIndex] = v;
					visit[nextIndex] = true;
				}
				
			}
			
		}
		
		short index = calIndex(end);
		sp.add(end);
		while ( index != calIndex(start) ) {
			sp.addFirst(calPoint(path[index]));
			index = path[index];
		}
		
		return sp.size() -1 ;
	}
	
	/*
	 * EFFECTS：计算点start到end应该走的第一步（在同样是最短路径的情况下，车流量小的优先），返回接下来走的点
	 */
 	public static Point getNextStep ( Point start, Point end ) {
		
		LinkedList<Point> chooseList = new LinkedList<Point>();
		LinkedList<Point> minLenPoint = new LinkedList<Point>();
		ArrayList<Short> flow = new ArrayList<Short>();
		ArrayList<Integer> len = new ArrayList<Integer>();
		int minLen = Integer.MAX_VALUE;
		int minFlow = Integer.MAX_VALUE;
		
		// 将可供选择的点加入到队列中
		if ( info[start.x][start.y] == 1 ) {
			chooseList.add(start.getRightPoint());
		}
		else if ( info[start.x][start.y] == 2 ) {
			chooseList.add(start.getDownPoint());
		}
		else if ( info[start.x][start.y] == 3 ) {
			chooseList.add(start.getRightPoint());
			chooseList.add(start.getDownPoint());
		}
		
		if( hasUp(start) && hasLeft(start) ) {
			chooseList.add(start.getUpPoint());
			chooseList.add(start.getLeftPoint());
		}
		else if ( hasUp(start) && !hasLeft(start) ) {
			chooseList.add(start.getUpPoint());
		}
		else if ( !hasUp(start) && hasLeft(start) ) {
			chooseList.add(start.getLeftPoint());
		}
		
		// 找出符合最短路径的点
		for ( int i = 0; i < chooseList.size(); ++i ) {
			int distance = calShortestLen(chooseList.get(i), end);
			if ( distance < minLen ) {
				minLen = distance;
			}
			len.add(distance);
		}
		// 将符合最短路径的点放入到列表里，并且记录他们道路上的车流量
		for ( int i = 0; i < chooseList.size(); ++i ) {
			if ( len.get(i) == minLen ) {
				minLenPoint.add(chooseList.get(i));
				flow.add(Flow.getRoadFlow(start, chooseList.get(i)));
			}
		}
		
		// 找出车流量 最小的 路径对应的点 
		for ( int i = 0; i < minLenPoint.size(); ++i ) {
			if ( flow.get(i) < minFlow ) {
				minFlow  = flow.get(i);
			}
		}
		// 把队列中不符合要求的筛掉  
		for ( int i = 0; i < minLenPoint.size(); ++i ) {
			if ( flow.get(i) > minFlow ) {
				minLenPoint.remove(i);
				flow.remove(i);
				--i;
			}
		}
		
		Random rand = new Random();
		
		return minLenPoint.get(rand.nextInt(minLenPoint.size()));
	}
	
	/*
	 * EFFECTS：得到一个随机邻点（上下左右选一个）
	 */
	public static Point getRandomNbrPoint ( Point p ) {
		
		Point nbr = new Point();
		Random rd = new Random();
		
		if( MapInfo.info[p.x][p.y] == 0 ) { // 左 或 上
			// 在第一行的情况
			if( p.x == 0 ) {// 左
				nbr = p.getLeftPoint();
			}
			// 在第一列的情况
			else if ( p.y == 0 ) { // 上
				nbr = p.getUpPoint();
			}
			// 其他情况
			else {
				if ( hasLeft(p) && !hasUp(p) ) { // 左
					nbr = p.getLeftPoint();
				}
				else if ( !hasLeft(p) && hasUp(p) ) { // 上
					nbr = p.getUpPoint();
				}
				else { // 优先选车流量小的，否则  0 左  ， 1 上
					
					if ( Flow.getRoadFlow(p, p.getUpPoint()) > Flow.getRoadFlow(p, p.getLeftPoint()) ) {
						nbr = p.getLeftPoint();
					}
					else if ( Flow.getRoadFlow(p, p.getUpPoint()) < Flow.getRoadFlow(p, p.getLeftPoint()) ) {
						nbr = p.getUpPoint();
					}
					else {
						nbr = ( rd.nextInt(2) == 0 ) ? p.getLeftPoint() : p.getUpPoint();
					}
					
				}
			}
			
		}
		
		else if ( MapInfo.info[p.x][p.y] == 1 ) { // 右， 左， 上
			// 第一行的情况    右，左
			if ( p.x == 0 ) {
				// 左上角的情况
				if( p.y == 0 ) { // 右
					nbr = p.getRightPoint();
				}
				else {
					if( hasLeft(p) ) { //优先选车流量小的，否则  0 左  ， 1 右
						
						if ( Flow.getRoadFlow(p, p.getRightPoint()) > Flow.getRoadFlow(p, p.getLeftPoint()) ) {
							nbr = p.getLeftPoint();
						}
						else if ( Flow.getRoadFlow(p, p.getRightPoint()) < Flow.getRoadFlow(p, p.getLeftPoint()) ) {
							nbr = p.getRightPoint();
						}
						else {
							nbr = ( rd.nextInt(2) == 0 ) ? p.getLeftPoint() : p.getRightPoint();
						}
						
					}
					else { // 右
						nbr = p.getRightPoint();
					}
				}
			}
			// 第一列的情况   右， 上
			else if ( p.x != 0 && p.y == 0 ) {
				if ( hasUp(p) ) { // 优先选车流量小的, 否则 0 上， 1 右
					
					if ( Flow.getRoadFlow(p, p.getUpPoint()) > Flow.getRoadFlow(p, p.getRightPoint()) ) {
						nbr = p.getRightPoint();
					}
					else if ( Flow.getRoadFlow(p, p.getUpPoint()) < Flow.getRoadFlow(p, p.getRightPoint() ) ) {
						nbr = p.getUpPoint();
					}
					else {
						nbr = ( rd.nextInt(2) == 0 ) ? p.getUpPoint() : p.getRightPoint();
					}
					
				}
				else { // 右
					nbr = p.getRightPoint();
				}
			}
			// 其他情况   右，左，上
			else {
				if ( hasLeft(p) && !hasUp(p) ) { // 优先选车流量小的, 否则 0 左  ， 1 右
					
					if ( Flow.getRoadFlow(p, p.getRightPoint()) > Flow.getRoadFlow(p, p.getLeftPoint()) ) {
						nbr = p.getLeftPoint();
					}
					else if ( Flow.getRoadFlow(p, p.getRightPoint()) < Flow.getRoadFlow(p, p.getLeftPoint()) ) {
						nbr = p.getRightPoint();
					}
					else {
						nbr = ( rd.nextInt(2) == 0 ) ? p.getLeftPoint() : p.getRightPoint();
					}
					
				}
				else if ( !hasLeft(p) && hasUp(p)) { // 优先选车流量小的, 否则 0 上， 1 右
					
					if ( Flow.getRoadFlow(p, p.getUpPoint()) > Flow.getRoadFlow(p, p.getRightPoint()) ) {
						nbr = p.getRightPoint();
					}
					else if ( Flow.getRoadFlow(p, p.getUpPoint()) < Flow.getRoadFlow(p, p.getRightPoint() ) ) {
						nbr = p.getUpPoint();
					}
					else {
						nbr = ( rd.nextInt(2) == 0 ) ? p.getUpPoint() : p.getRightPoint();
					}
					
				}
				else if ( hasLeft(p) && hasUp(p) ) { // 优先选流量最小的， 否则 0 上， 1 右, 2左
					
					ArrayList<Short> list = new ArrayList<Short>();
					ArrayList<Short> temp = new ArrayList<Short>();
					list.add(Flow.getRoadFlow(p, p.getUpPoint()));
					list.add(Flow.getRoadFlow(p, p.getRightPoint()));
					list.add(Flow.getRoadFlow(p, p.getLeftPoint()));
					
					short minFlow = Short.MAX_VALUE;
					
					for ( short i = 0; i < 3; ++i ) {
						if ( list.get(i) < minFlow ) {
							minFlow = list.get(i);
							temp.add(i);
						}
					}
					
					if ( temp.size() == 1 ) {
						nbr = ( temp.get(0) == 0 )?
								p.getUpPoint(): (temp.get(0) == 1 )? p.getRightPoint(): p.getLeftPoint();
					}
					else if ( temp.size() == 2 ) {
						
						int rand = rd.nextInt(2);
						nbr = ( temp.get(rand) == 0 )?
								p.getUpPoint(): (temp.get(rand) == 1 )? p.getRightPoint(): p.getLeftPoint();
					}
					else {
						
						int rand = rd.nextInt(3);
						nbr = ( temp.get(rand) == 0 )?
								p.getUpPoint(): (temp.get(rand) == 1 )? p.getRightPoint(): p.getLeftPoint();
					}
					
				}
				else { // 右
					nbr = p.getRightPoint();
				}
			}
		}
		
		else if ( MapInfo.info[p.x][p.y] == 2 ) { // 下， 上， 左
			// 第一行的情况   下，	左
			if( p.x == 0 ) {
				// 左上角的情况
				if ( p.y == 0 ) { // 下
					nbr = p.getDownPoint();
				}
				else {
					if ( hasLeft(p) ) { // 优先选流量最小的， 否则 0 左 ， 1 下
						
						if ( Flow.getRoadFlow(p, p.getDownPoint()) > Flow.getRoadFlow(p, p.getLeftPoint()) ) {
							nbr = p.getLeftPoint();
						}
						else if ( Flow.getRoadFlow(p, p.getDownPoint()) < Flow.getRoadFlow(p, p.getLeftPoint()) ) {
							nbr = p.getDownPoint();
						}
						else {
							nbr = ( rd.nextInt(2) == 0 ) ? p.getLeftPoint(): p.getDownPoint();
						}
						
					}
					else { // 下
						nbr = p.getDownPoint();
					}
				}
			}
			// 第一列的情况  下， 上
			else if ( p.x != 0 && p.y == 0) {
				if ( hasUp(p) ) { // 优先选流量最小的， 否则 0 上，  1 下
					
					if ( Flow.getRoadFlow(p, p.getUpPoint()) > Flow.getRoadFlow(p, p.getDownPoint()) ) {
						nbr = p.getDownPoint();
					}
					else if ( Flow.getRoadFlow(p, p.getUpPoint()) < Flow.getRoadFlow(p, p.getDownPoint() ) ) {
						nbr = p.getUpPoint();
					}
					else {
						nbr = ( rd.nextInt(2) == 0 ) ? p.getUpPoint(): p.getDownPoint();
					}
					
				}
				else { // 下
					nbr = p.getDownPoint();
				}
			}
			// 其他情况
			else {
				if ( hasLeft(p) && !hasUp(p) ) { // 优先选流量最小的， 否则  0 左  ， 1 下
					

					if ( Flow.getRoadFlow(p, p.getDownPoint()) > Flow.getRoadFlow(p, p.getLeftPoint()) ) {
						nbr = p.getLeftPoint();
					}
					else if ( Flow.getRoadFlow(p, p.getDownPoint()) < Flow.getRoadFlow(p, p.getLeftPoint()) ) {
						nbr = p.getDownPoint();
					}
					else {
						nbr = ( rd.nextInt(2) == 0 ) ? p.getLeftPoint(): p.getDownPoint();
					}
					
				}
				else if ( !hasLeft(p) && hasUp(p)) { // 优先选流量最小的， 否则  0 上， 1 下
					
					if ( Flow.getRoadFlow(p, p.getUpPoint()) > Flow.getRoadFlow(p, p.getDownPoint()) ) {
						nbr = p.getDownPoint();
					}
					else if ( Flow.getRoadFlow(p, p.getUpPoint()) < Flow.getRoadFlow(p, p.getDownPoint() ) ) {
						nbr = p.getUpPoint();
					}
					else {
						nbr = ( rd.nextInt(2) == 0 ) ? p.getUpPoint(): p.getDownPoint();
					}
					
				}
				else if ( hasLeft(p) && hasUp(p) ) { // 优先选流量最小的， 否则  0 上， 1 下, 2左
					
					ArrayList<Short> list = new ArrayList<Short>();
					ArrayList<Short> temp = new ArrayList<Short>();
					list.add(Flow.getRoadFlow(p, p.getUpPoint()));
					list.add(Flow.getRoadFlow(p, p.getDownPoint()));
					list.add(Flow.getRoadFlow(p, p.getLeftPoint()));
					
					short minFlow = Short.MAX_VALUE;
					
					for ( short i = 0; i < 3; ++i ) {
						if ( list.get(i) < minFlow ) {
							minFlow = list.get(i);
							temp.add(i);
						}
					}
					
					if ( temp.size() == 1 ) {
						nbr = ( temp.get(0) == 0 )?
								p.getUpPoint(): (temp.get(0) == 1 )? p.getDownPoint(): p.getLeftPoint();
					}
					else if ( temp.size() == 2 ) {
						
						int rand = rd.nextInt(2);
						nbr = ( temp.get(rand) == 0 )?
								p.getUpPoint(): (temp.get(rand) == 1 )? p.getDownPoint(): p.getLeftPoint();
					}
					else {
						
						int rand = rd.nextInt(3);
						nbr = ( temp.get(rand) == 0 )?
								p.getUpPoint(): (temp.get(rand) == 1 )? p.getDownPoint(): p.getLeftPoint();
					}
					
				}
				else { // 下
					nbr = p.getDownPoint();
				}
			}
		}
		
		else if ( MapInfo.info[p.x][p.y] == 3 ) { // 下， 右，  上， 左
			// 第一行的情况   下， 右，	左
			if( p.x == 0 ) {
				// 左上角的情况
				if ( p.y == 0 ) { // 优先选流量最小的， 否则   0 右 ，1下
					
					if ( Flow.getRoadFlow(p, p.getDownPoint()) > Flow.getRoadFlow(p, p.getRightPoint()) ) {
						nbr = p.getRightPoint();
					}
					else if ( Flow.getRoadFlow(p, p.getDownPoint()) < Flow.getRoadFlow(p, p.getRightPoint()) ) {
						nbr = p.getDownPoint();
					}
					else {
						nbr = ( rd.nextInt(2) == 0 ) ? p.getRightPoint(): p.getDownPoint();
					}
					
				}
				else {
					if ( hasLeft(p) ) { // 优先选流量最小的， 否则  0 左 ， 1 下, 2右
						
						ArrayList<Short> list = new ArrayList<Short>();
						ArrayList<Short> temp = new ArrayList<Short>();
						list.add(Flow.getRoadFlow(p, p.getLeftPoint()));
						list.add(Flow.getRoadFlow(p, p.getDownPoint()));
						list.add(Flow.getRoadFlow(p, p.getRightPoint()));
						
						short minFlow = Short.MAX_VALUE;
						
						for ( short i = 0; i < 3; ++i ) {
							if ( list.get(i) < minFlow ) {
								minFlow = list.get(i);
								temp.add(i);
							}
						}
						
						if ( temp.size() == 1 ) {
							nbr = ( temp.get(0) == 0 )?
									p.getLeftPoint(): (temp.get(0) == 1 )? p.getDownPoint(): p.getRightPoint();
						}
						else if ( temp.size() == 2 ) {
							
							int rand = rd.nextInt(2);
							nbr = ( temp.get(rand) == 0 )?
									p.getLeftPoint(): (temp.get(rand) == 1 )? p.getDownPoint(): p.getRightPoint();
						}
						else {
							
							int rand = rd.nextInt(3);
							nbr = ( temp.get(rand) == 0 )?
									p.getLeftPoint(): (temp.get(rand) == 1 )? p.getDownPoint(): p.getRightPoint();
						}
						
					}
					else { // 优先选流量最小的， 否则   0 右 ，1下
						
						if ( Flow.getRoadFlow(p, p.getDownPoint()) > Flow.getRoadFlow(p, p.getRightPoint()) ) {
							nbr = p.getRightPoint();
						}
						else if ( Flow.getRoadFlow(p, p.getDownPoint()) < Flow.getRoadFlow(p, p.getRightPoint()) ) {
							nbr = p.getDownPoint();
						}
						else {
							nbr = ( rd.nextInt(2) == 0 ) ? p.getRightPoint(): p.getDownPoint();
						}
						
					}
				}
			}
			// 第一列的情况  下， 右，  上
			else if ( p.x != 0 && p.y == 0) {
				if ( hasUp(p) ) { // 优先选流量最小的， 否则  0 上 ， 1 下, 2右
					
					ArrayList<Short> list = new ArrayList<Short>();
					ArrayList<Short> temp = new ArrayList<Short>();
					list.add(Flow.getRoadFlow(p, p.getUpPoint()));
					list.add(Flow.getRoadFlow(p, p.getDownPoint()));
					list.add(Flow.getRoadFlow(p, p.getRightPoint()));
					
					short minFlow = Short.MAX_VALUE;
					
					for ( short i = 0; i < 3; ++i ) {
						if ( list.get(i) < minFlow ) {
							minFlow = list.get(i);
							temp.add(i);
						}
					}
					
					if ( temp.size() == 1 ) {
						nbr = ( temp.get(0) == 0 )?
								p.getUpPoint(): (temp.get(0) == 1 )? p.getDownPoint(): p.getRightPoint();
					}
					else if ( temp.size() == 2 ) {
						
						int rand = rd.nextInt(2);
						nbr = ( temp.get(rand) == 0 )?
								p.getUpPoint(): (temp.get(rand) == 1 )? p.getDownPoint(): p.getRightPoint();
					}
					else {
						
						int rand = rd.nextInt(3);
						nbr = ( temp.get(rand) == 0 )?
								p.getUpPoint(): (temp.get(rand) == 1 )? p.getDownPoint(): p.getRightPoint();
					}
					
				}
				else { // 优先选流量最小的， 否则  0 右 ，1下
					
					if ( Flow.getRoadFlow(p, p.getDownPoint()) > Flow.getRoadFlow(p, p.getRightPoint()) ) {
						nbr = p.getRightPoint();
					}
					else if ( Flow.getRoadFlow(p, p.getDownPoint()) < Flow.getRoadFlow(p, p.getRightPoint()) ) {
						nbr = p.getDownPoint();
					}
					else {
						nbr = ( rd.nextInt(2) == 0 ) ? p.getRightPoint(): p.getDownPoint();
					}
					
				}
			}
			// 其他情况
			else {
				if ( hasLeft(p) && !hasUp(p) ) { // 优先选流量最小的， 否则   0 左  ， 1 下, 2右
					
					ArrayList<Short> list = new ArrayList<Short>();
					ArrayList<Short> temp = new ArrayList<Short>();
					list.add(Flow.getRoadFlow(p, p.getLeftPoint()));
					list.add(Flow.getRoadFlow(p, p.getDownPoint()));
					list.add(Flow.getRoadFlow(p, p.getRightPoint()));
					
					short minFlow = Short.MAX_VALUE;
					
					for ( short i = 0; i < 3; ++i ) {
						if ( list.get(i) < minFlow ) {
							minFlow = list.get(i);
							temp.add(i);
						}
					}
					
					if ( temp.size() == 1 ) {
						nbr = ( temp.get(0) == 0 )?
								p.getLeftPoint(): (temp.get(0) == 1 )? p.getDownPoint(): p.getRightPoint();
					}
					else if ( temp.size() == 2 ) {
						
						int rand = rd.nextInt(2);
						nbr = ( temp.get(rand) == 0 )?
								p.getLeftPoint(): (temp.get(rand) == 1 )? p.getDownPoint(): p.getRightPoint();
					}
					else {
						
						int rand = rd.nextInt(3);
						nbr = ( temp.get(rand) == 0 )?
								p.getLeftPoint(): (temp.get(rand) == 1 )? p.getDownPoint(): p.getRightPoint();
					}
					
				}
				else if ( !hasLeft(p) && hasUp(p)) { // 优先选流量最小的， 否则  0 上， 1 下， 2右
					
					ArrayList<Short> list = new ArrayList<Short>();
					ArrayList<Short> temp = new ArrayList<Short>();
					list.add(Flow.getRoadFlow(p, p.getUpPoint()));
					list.add(Flow.getRoadFlow(p, p.getDownPoint()));
					list.add(Flow.getRoadFlow(p, p.getRightPoint()));
					
					short minFlow = Short.MAX_VALUE;
					
					for ( short i = 0; i < 3; ++i ) {
						if ( list.get(i) < minFlow ) {
							minFlow = list.get(i);
							temp.add(i);
						}
					}
					
					if ( temp.size() == 1 ) {
						nbr = ( temp.get(0) == 0 )?
								p.getUpPoint(): (temp.get(0) == 1 )? p.getDownPoint(): p.getRightPoint();
					}
					else if ( temp.size() == 2 ) {
						
						int rand = rd.nextInt(2);
						nbr = ( temp.get(rand) == 0 )?
								p.getUpPoint(): (temp.get(rand) == 1 )? p.getDownPoint(): p.getRightPoint();
					}
					else {
						
						int rand = rd.nextInt(3);
						nbr = ( temp.get(rand) == 0 )?
								p.getUpPoint(): (temp.get(rand) == 1 )? p.getDownPoint(): p.getRightPoint();
					}
					
				}
				else if ( hasLeft(p) && hasUp(p) ) { // 优先选流量最小的， 否则  0 上， 1 下, 2左，3右
					
					ArrayList<Short> list = new ArrayList<Short>();
					ArrayList<Short> temp = new ArrayList<Short>();
					list.add(Flow.getRoadFlow(p, p.getUpPoint()));
					list.add(Flow.getRoadFlow(p, p.getDownPoint()));
					list.add(Flow.getRoadFlow(p, p.getLeftPoint()));
					list.add(Flow.getRoadFlow(p, p.getRightPoint()));
					
					short minFlow = Short.MAX_VALUE;
					
					for ( short i = 0; i < 3; ++i ) {
						if ( list.get(i) < minFlow ) {
							minFlow = list.get(i);
							temp.add(i);
						}
					}
					
					if ( temp.size() == 1 ) {
						nbr = ( temp.get(0) == 0 )?
								p.getUpPoint(): (temp.get(0) == 1 )? 
										p.getDownPoint(): ( temp.get(0) == 2 )?
												p.getLeftPoint(): p.getRightPoint();
					}
					else if ( temp.size() == 2 ) {
						
						int rand = rd.nextInt(2);
						nbr = ( temp.get(rand) == 0 )?
								p.getUpPoint(): (temp.get(rand) == 1 )? 
										p.getDownPoint(): ( temp.get(rand) == 2 )?
												p.getLeftPoint(): p.getRightPoint();
					}
					else if ( temp.size() == 3 ) {
						
						int rand = rd.nextInt(3);
						nbr = ( temp.get(rand) == 0 )?
								p.getUpPoint(): (temp.get(rand) == 1 )? 
										p.getDownPoint(): ( temp.get(rand) == 2 )?
												p.getLeftPoint(): p.getRightPoint();
					}
					else {
						int rand = rd.nextInt(4);
						nbr = ( temp.get(rand) == 0 )?
								p.getUpPoint(): (temp.get(rand) == 1 )? 
										p.getDownPoint(): ( temp.get(rand) == 2 )?
												p.getLeftPoint(): p.getRightPoint();
					}
					
				}
				else { // 优先选流量最小的， 否则  0 右 ，1下
					
					if ( Flow.getRoadFlow(p, p.getDownPoint()) > Flow.getRoadFlow(p, p.getRightPoint()) ) {
						nbr = p.getRightPoint();
					}
					else if ( Flow.getRoadFlow(p, p.getDownPoint()) < Flow.getRoadFlow(p, p.getRightPoint()) ) {
						nbr = p.getDownPoint();
					}
					else {
						nbr = ( rd.nextInt(2) == 0 ) ? p.getRightPoint(): p.getDownPoint();
					}
					
				}
			}
			
		}
		
		return nbr;
	}
	
	/*
	 * EFFECTS： 得到某个点的附近区域的点集
	 */
	public static LinkedList<Point> NbrPoints ( Point p ) {
		
		LinkedList<Point> nbr = new LinkedList<Point>();
		short leftBorder = (short) (p.y - 2);
		short rightBorder = (short) (p.y + 2);
		short upBorder = (short) (p.x - 2);
		short downBorder = (short) (p.x + 2);
		
		// 判断是否越界
		if( leftBorder < 0 ) {
			leftBorder = 0;
		}
		if ( rightBorder > MAP_COLUMN - 1 ) {
			rightBorder = MAP_COLUMN - 1;
		}
		if ( upBorder < 0 ) {
			upBorder = 0;
		}
		if ( downBorder > MAP_ROW - 1 ) {
			downBorder = MAP_ROW - 1;
		}
		
		for ( short i = leftBorder; i <= rightBorder; i++ ) {
			for ( short j = upBorder; j <= downBorder; j++ ) {
				nbr.add( new Point(j,i) );
			}
		}
		return nbr;
	}
	
	/*
	 * 打开一条道路
	 * REQUIRES:点p1，p2必须是相邻的点
	 * MODIFIES：MapInfo.cost， Mapinfo.info， MapInfo.Flow.roadFlow
	 * EFFECTS: 对应的MapInfo.cost的值置为1,对应的MapInfo.Flow.roadFlow的值置为0,对应的MapInfo.info的值做相应的改变
	 */
	public static void openRoad( Point p1, Point p2 ) {
		int index1 = calIndex(p1);
		int index2 = calIndex(p2);
		
		if ( Point.dirInCross(p1, p2 ) == -1 ) {
			System.err.println("道路" + p1 + "<->" + p2 + "不存在");
			return;
		}
		
		else if ( MapInfo.cost[index1][getCostIndex(p1, p2)] == true ) {
			System.err.println("道路"+ p1 + "<->" + p2 + "已存在，打开道路操作无效");
			return;
		}
		
		else {
			
			MapInfo.cost[index1][getCostIndex(p1, p2)] = true;
			MapInfo.cost[index2][getCostIndex(p2, p1)] = true;
			
			if ( Point.dirInCross(p1, p2) == 1 ) {
				Flow.roadFlow[index1][UP_ROAD] = 0;
				Flow.roadFlow[index2][DOWN_ROAD] = 0;
				info[p2.x][p2.y] = (short) (( info[p2.x][p2.y] == 1 )? 3: 2);
			}
			else if ( Point.dirInCross(p1, p2) == 2 ) {
				Flow.roadFlow[index1][DOWN_ROAD] = 0;
				Flow.roadFlow[index2][UP_ROAD] = 0;
				info[p1.x][p1.y] = (short) (( info[p1.x][p1.y] == 1 )? 3: 2);
			}
			else if ( Point.dirInCross(p1, p2) == 3 ) {
				Flow.roadFlow[index1][LEFT_ROAD] = 0;
				Flow.roadFlow[index2][RIGHT_ROAD] = 0;
				info[p2.x][p2.y] = (short) (( info[p2.x][p2.y] == 2 )? 3: 1);
			}
			else if ( Point.dirInCross(p1, p2) == 4 ) {
				Flow.roadFlow[index1][RIGHT_ROAD] = 0;
				Flow.roadFlow[index2][LEFT_ROAD] = 0;
				info[p1.x][p1.y] = (short) (( info[p1.x][p1.y] == 2 )? 3: 1);
			}
			
			System.out.println("已打开道路：" + p1 + "<->" + p2);
		}
	}
	
	/*
	 * 关闭一条道路
	 * REQUIRES:点p1，p2必须是相邻的点
	 * MODIFIES：MapInfo.cost, MapInfo.Flow.roadFlow
	 * EFFECTS: 对应的MapInfo.cost的值置为 Short.MAX_VALUE,对应的MapInfo.Flow.roadFlow的值置为-1
	 */
	public static void closeRoad( Point p1, Point p2 ) {
		int index1 = calIndex(p1);
		int index2 = calIndex(p2);
		
		if ( Point.dirInCross(p1, p2) == -1  )  {
			System.err.println("道路"+ p1 + "<->" + p2 + "不存在,关闭道路操作无效");
			return;
		}
		else if ( cost[index1][getCostIndex(p1, p2)] == false ) {
			System.err.println("道路"+ p1 + "<->" + p2 + "不存在,关闭道路操作无效");
			return;
		}
		else {
			MapInfo.cost[index1][getCostIndex(p1, p2)] = false;
			MapInfo.cost[index2][getCostIndex(p2, p1)] = false;
			
			if ( Point.dirInCross(p1, p2) == 1 ) {
				Flow.roadFlow[index1][UP_ROAD] = -1;
				Flow.roadFlow[index2][DOWN_ROAD] = -1;
				info[p2.x][p2.y] = (short) (( info[p2.x][p2.y] == 3 )? 1: 0);
			}
			else if ( Point.dirInCross(p1, p2) == 2 ) {
				Flow.roadFlow[index1][DOWN_ROAD] = -1;
				Flow.roadFlow[index2][UP_ROAD] = -1;
				info[p1.x][p1.y] = (short) (( info[p1.x][p1.y] == 3 )? 1: 0);
			}
			else if ( Point.dirInCross(p1, p2) == 3 ) {
				Flow.roadFlow[index1][LEFT_ROAD] = -1;
				Flow.roadFlow[index2][RIGHT_ROAD] = -1;
				info[p2.x][p2.y] = (short) (( info[p2.x][p2.y] == 3 )? 2: 0);
			}
			else if ( Point.dirInCross(p1, p2) == 4 ) {
				Flow.roadFlow[index1][RIGHT_ROAD] = -1;
				Flow.roadFlow[index2][LEFT_ROAD] = -1;
				info[p1.x][p1.y] = (short) (( info[p1.x][p1.y] == 3 )? 2: 0);
			}
			
			if ( !testMap() ) {
				System.err.println("由于道路被关闭，导致地图不连通");
				System.err.println("打车系统已关闭，请重启");
				System.exit(0);
				return;
			}
			
			System.out.println("已关闭道路：" + p1 + "<->" + p2);
		}
	}
	
	/*
	 * EFFECTS:i,j是在地图内的某点的坐标（i,j） 通过（i，j）计算index
	 */
	public static short calIndex(short i, short j) {
		return (short) (i * MAP_COLUMN + j);
	}

	/*
	 * EFFECTS：计算点p的索引值index
	 */
	public static short calIndex(Point p) {
		return (short) (p.x * MAP_COLUMN + p.y);
	}

	/*
	 * EFFECTS：通过 index 计算 Point的坐标
	 */
	public static Point calPoint(short index) {
		Point p = new Point();
		p.x = (short) (index / MAP_COLUMN);
		p.y = (short) (index % MAP_COLUMN);
		return p;
	}

	/*
	 * EFFECTS：给定一个地图中点p1,p2,给出p2在p1的cost数组中对应位置的costIndex
	 */
	public static int getCostIndex ( Point p1, Point p2 ) {
		return ( p2.equals(p1.getUpPoint()) )?  0:
			(  p2.equals(p1.getDownPoint()) )? 1:
				( p2.equals(p1.getLeftPoint()) )? 2:
					(p2.equals(p1.getRightPoint()) )? 3:-1;
	}
	
	/*
	 * EFFECTS：判断一个点的上边是否有路,有返回true，否则返回false
	 */
	public static boolean hasUp ( Point p ) {
		if ( p.x == 0 ) {
			return false;
		}
		return MapInfo.info[p.x-1][p.y] == 3 || MapInfo.info[p.x-1][p.y] == 2;
	}
	
	/*
	 * EFFECTS：判断一个点的下边是否有路,有返回true，否则返回false
	 */
	public static boolean hasDown ( Point p ) {
		if ( p.x == MapInfo.MAP_ROW - 1 ) {
			return false;
		}
		return MapInfo.info[p.x][p.y] == 3 || MapInfo.info[p.x][p.y] == 2;
	}
	
	/*
	 * EFFECTS： 判断一个点的左边是否有路,有返回true，否则返回false
	 */
	public static boolean hasLeft( Point p ) {
		if ( p.y == 0 ) {
			return false;
		}
		return (MapInfo.info[p.x][p.y-1] == 3 || MapInfo.info[p.x][p.y-1] == 1);
	}
	
	/*
	 * EFFECTS： 判断一个点的右边是否有路,有返回true，否则返回false
	 */
	public static boolean hasRight( Point p ) {
		if ( p.y == MapInfo.MAP_COLUMN - 1 ) {
			return false;
		}
		return (MapInfo.info[p.x][p.y] == 3 || MapInfo.info[p.x][p.y] == 1);
	}
	
	/*
	 * EFFECTS：判断该点的道路数量是否为3或4，是则说明这是一个交叉口，返回true，否则返回false
	 */
	public static boolean isCrossRoad ( Point p ) {
		int num = 0;
		
		if( hasUp(p) )
			num++;
		if( hasDown(p) )
			num++;
		if( hasLeft(p) ) 
			num++;
		if( hasDown(p) ) 
			num++;
		
		return ( num ==  3 || num == 4 )? true: false;
	}
	
}


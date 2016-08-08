package TaxiDespatch;

import java.util.LinkedList;

public class Point {
	
	// Overview： 点（x，y）x为行值，y为列值，用于表示地图上一个点的具体位置
	
	// invariant：   0 <= x < MapInfo.MAP_ROW && 0 <= y < MapInfo.MAP_COLUMN
	// (MapInfo.MAP_ROW,MapInfo.MAP_COLUMN分别表示地图的行数和列数)
	
	// 表示对象： short x, short y
	// 抽象函数: AF(c) = (x,y) x = c.x y = c.y 
	
	short x;
	short y;

	// constructor
	/*
	 * MODIFIES x,y
	 * EFFECTS: x = y = 0
	 */
	public Point() {
		x = 0;
		y = 0;
	}

	/*
	 * MODIFIES x,y
	 * EFFECTS: 用两个short型的数字为x和y赋值
	 */
	public Point(short x, short y) {
		this.x = x;
		this.y = y;
	}
	
	//EFFECTS： 判断一个点是否符合不变式，是返回true，否返回false
	public boolean repOK () {
		return x >= 0 && x < MapInfo.MAP_ROW  && y >= 0 && y < MapInfo.MAP_COLUMN;
	}
	
	//EFFECTS： 判断一个点是否在列表里,若在则返回第一个下标值，否则返回-1
	public static int isInLinkedList (Point p, LinkedList<Point> pl ) {
		for( int i = 0; i < pl.size(); ++i ) {
			if( p.equals( pl.get(i) ) ) {
				return i;
			}
		}
		return -1;
	}
	
	/*
	 * EFFECTS：判断点test是否在以点p为中心长度为1的十字架上，上1， 下2， 左3， 右4，若不在十字架上，则返回-1
	 */
	public static int dirInCross ( Point p, Point test ) {
		
		if ( test.x == p.x ) {
			return ( test.y == p.y -1 )? 3: ( test.y == p.y + 1)? 4: -1;
		}
		else if ( test.y == p.y ) {
			return ( test.x == p.x -1 )? 1: ( test.x == p.x + 1)? 2: -1;
		}
		else {
			return -1;
		}
	}
	
	// EFFECTS： 随机产生一个点（x，y）：0 <= x < maxX; 0 <= y < maxY
	public static Point getRandomPoint( short maxX, short maxY ) {
		Point point = new Point( (short)(maxX*Math.random()) , (short)(maxY*Math.random()) );
		return point;
	}
	
	//EFFECTS： 得到左边的点
	public Point getLeftPoint () {
		return new Point(x,(short) (y-1));
	}
	
	// EFFECTS： 得到右边的点
	public Point getRightPoint () {
		return new Point(x,(short) (y+1));
	}
	
	// EFFECTS： 得到上面的点
	public Point getUpPoint () {
		return new Point((short) (x-1),y); 
	}
	
	// EFFECTS：得到下面的点
	public Point getDownPoint () {
		return new Point((short) (x+1),y);
	}

	/*
	 * EFFECTS: 重写的Point 的 toString 方法
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + x + "," + y + ") ";
	}
	
	/*
	 * EFFECTS：重写的equals方法，两个点的x，y值都相等的时候，认为这是同一个点，返回true，否则返回false
	 */
	public boolean equals ( Point p ) {
		return x == p.x && y == p.y;
	}
	
}

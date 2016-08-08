package TaxiDespatch;

public class Taxi extends Thread {
	
	// Overview：出租车用于响应乘客的请求，具有四种运行状态，出租车每次只能响应一个请求
	
	// 表示对象： short id, Point lastPosition, Point position, TaxiSatte state, int credit, 
	// PassengerReq pr, boolean called
	// 抽象函数 ： AF(c) = (id,lastPosition,position,state,credit,pr,called)
	// id = c.id, lastPosition:出租车上次所在点的坐标， position = c.position, credit = c.credit,
	// state: in { WAITTING, READY,RUNNING, REST }
	// pr:乘客的请求（当出租车状态是READY或者RUNNING时），null（其他状态）;
	// called: true（当出租车状态是READY或者RUNNING时），false（其他状态）
	
	// invariant：    if state == WATTING or state == REST: called == false && pr == null
	
	private static final long DRIVE_TIME = 100; // 出租车每跑一格需要的时间
	private static final long REST_TIME = 1000;	// 出租车休息一次需要的时间
	private static final long WATTING_TIMES = 20000; // 出租车处于WATTING状态下运行的时间
	 
	enum TaxiState {
		WAITTING, // 等待服务
		READY, 	// 即将服务
		RUNNING, // 正在服务
		REST	// 停止运行
	}
	
	short id; // 出租车的编号
	Point lastPosition; //出租车的上一个点的位置
	Point position; // 出租车的实时坐标
	TaxiState state;
	int credit;
	PassengerReq pr = null;
	boolean called;
	
	// constructor
	/*
	 * MODIFIES：出租车的所有属性
	 * EFFECTS：创建一辆出租车编号为id的出租车，并且对它的所有属性进行初始化，
	 * 其中position是在地图上随机选择的一个非平面交叉路口的点，初始状态下为WATTING。
	 */
	public Taxi( short id ) {
		this.id = id;
		credit = 0;
		
		position = Point.getRandomPoint(MapInfo.MAP_ROW, MapInfo.MAP_COLUMN); // 出租车的起始位置随机产生
		while ( TrafficLight.tflight[position.x][position.y] != 0 ) {// 确保出租车不会产生在平面交叉路口
			position = Point.getRandomPoint(MapInfo.MAP_ROW, MapInfo.MAP_COLUMN); 
		}
		
		lastPosition = position;
		state = TaxiState.WAITTING;
		called = false;
	}
	
	// EFFECTS： 判断是否符合不变式，是则返回true,否则返回false
	public boolean repOK () {
		if ( state == TaxiState.WAITTING || state == TaxiState.REST ) {
			return pr == null && called == false;
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 * 出租车运行时的方法
	 */
	public void run() {
		
		TaxiState lastState = TaxiState.WAITTING;
		
		while ( true ) {
			
			switch (state) {
			
			case WAITTING:
				
				// 随机跑
				for( long i = WATTING_TIMES/DRIVE_TIME; i > 0; --i ) {
					randomRun();
					if ( called == true ) { // 被叫了
						break;
					}
				}
				
				if ( called == true ) {
					state = TaxiState.READY;
					lastState = TaxiState.WAITTING;
				}
				else { // 跑完了
					state = TaxiState.REST;
					lastState = TaxiState.WAITTING;
				}
				
				break;
			
			case READY:
				
				goDestination(pr.fromPonit);
				state = TaxiState.REST; // 接客
				lastState = TaxiState.READY;
				
				break;
				
			case RUNNING:
				
				goDestination(pr.toPoint);
				addCredit(3);; // 增加信用度
				
				// 相应的输出
				System.out.print("[请求]起：" + pr.fromPonit + "终：" + pr.toPoint );;
				System.out.print("已到达目的地" + pr.toPoint );
				TaxiSystem.printRuntime();
				System.out.print("[请求]起：" + pr.fromPonit + "终：" + pr.toPoint );
				System.out.println("由出租车" + id + "完成,当前信用值为：" + credit );
				
				called = false;
				pr = null;
				state = TaxiState.REST; // 下客
				lastState = TaxiState.RUNNING;
				
				break;
				
			case REST:
				rest();
				if( lastState == TaxiState.READY  ) {
					state = TaxiState.RUNNING;
				}
				else {
					state = TaxiState.WAITTING;
				}
				lastState = TaxiState.REST;
				
				break;
				
			}

		}
	}
	
	/*
	 * MODIFIES： pr, called
	 * EFFECTS: 将乘客请求传给出租车，并将called属性置为true
	 */
	public synchronized void call ( PassengerReq req ) {
		pr = req;
		called = true;
	}
	
	/*
	 * MODIFIES：position
	 * EFFECTS： 通过基于车流量考虑的最短路径到达目的地点end的过程
	 */
	public void goDestination ( Point end ) {
		
		while ( !position.equals(end) ) {
			goPoint(MapInfo.getNextStep(position, end));
		}
		
	}
	
	/*
	 * MODIFIES： Flow.roadFlow， position， lastPosition
	 * EFFECTS：考虑红绿灯，若为需要等待信号灯的情况，则等待，将车开到点p，同时对车流量进行计数
	 */
	public void goPoint( Point p) {
		
		if ( TrafficLight.tflight[position.x][position.y] != 0 ) { // 当有红绿灯的情况
			int i = 0;
			
			while ( TrafficLight.waitLight(lastPosition, position, p) ) {
				                                                               
//				if ( i == 0 && state == TaxiState.RUNNING ) { // 用于测试出租车碰到红绿灯的情况                                             
//					System.out.println("出租车正在"+position+"处等待红绿灯");               
//				}                                    
				
				try {                                                                     
					sleep(100);
				} catch ( InterruptedException e) {
					System.out.println("出租车在等待红绿灯时出错");
				}
				i++;
				
			}
			
		}
		
		// 出租车前往点p
		Flow.addFlow(position, p); // 车流量+1
		try {
			sleep(DRIVE_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Flow.subFlow(position, p); // 车流量-1
		lastPosition = position;
		position = p;
	}

	/*
	 * EFFECTS：休息1s
	 */
	public void rest () {
		try {
			sleep(REST_TIME);
		} catch (InterruptedException e) {
			System.out.println("出租车" + id + "休息时出错");
		}
	}
	
	/*
	 * EFFECTS：在等待服务的状态下随便走一个格子
	 */
	public void randomRun () {
		goPoint(MapInfo.getRandomNbrPoint(position));
	}
	
	/*
	 * EFFECTS： 出租车的信用值增加n
	 */
	public void addCredit ( int n ) {
		credit += n;
	}
	
	/*
	 * EFFECT:返回一个布尔值。若出租车处于WAITTING状态则为true，否则为false
	 */
	public boolean isTaxiWaitting () {
		return state == TaxiState.WAITTING;
	}
	
	/*
	 * 测试者可以使用
	 * 可修改
	 * EFFECTS：返回出租车的信息
	 */
	public String taxiInfo () {
		if ( id < 10 ) {
			return "[Taxi-0" + id + "] 位置: " + position + " 状态： " + state + " 运行时间：" + 
					((System.currentTimeMillis() - TaxiSystem.sysStartTime)/1000.0) + "s";
		}
		else {
			return "[Taxi-" + id + "] 位置: " + position + " 状态： "  + state + " 运行时间：" + 
					((System.currentTimeMillis() - TaxiSystem.sysStartTime)/1000.0) + "s";
		}
	}
	
}

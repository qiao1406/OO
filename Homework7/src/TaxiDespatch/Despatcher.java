package TaxiDespatch;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Despatcher extends Thread  {
	
	// Overview：调度器类，对一个请求进行调度，进程持续windowTime时间，
	// 为请求寻找合适的出租车，若找不到则提示寻车失败
	
	// 表示对象： PassengerReq pr
	// 抽象函数： AF(c) = pr  pr = c,pr
	
	// invariant： pr.repOK()
	
	final static long windowTime = 3000;
	PassengerReq pr = null;
	
	// constructor
	// EFFECTS: 利用乘客请求构造一个调度器线程
	public Despatcher ( PassengerReq pr ) {
		this.pr = pr;
	}
	
	/*  
	 *  EFFECTS:检查否符合不变式,是返回true，否则返回false
	 */
	public boolean repOK () {
		return pr.repOK();
	}
	
	public synchronized void run ( ) {
		
		long reqStartTime = System.currentTimeMillis();
		LinkedList<Point> nbr = MapInfo.NbrPoints(pr.fromPonit);
		ArrayList<Integer> taxiCall = new ArrayList<Integer>();
		
		// 抢单窗口持续3秒
		while ( System.currentTimeMillis() - reqStartTime < windowTime ) {
			
			for ( int i = 0; i < TaxiSystem.TAXI_NUM; ++i ) {
				if(  Point.isInLinkedList(TaxiSystem.taxis[i].position, nbr) != -1
						&& TaxiSystem.taxis[i].isTaxiWaitting()
						&& taxiCall.contains(i) == false ) { // 出租车在附近 且状态是等待
					TaxiSystem.taxis[i].addCredit(1);; // 抢单成功
					taxiCall.add(i);
				}
			}
			
			
			try {
				sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		if( taxiCall.isEmpty() || chooseTaxi(taxiCall) == null) { // 没找到车
			System.out.print("[请求]起：" + pr.fromPonit + "终：" + pr.toPoint );
			System.out.print("没有出租车响应");
			TaxiSystem.printRuntime();
		}
		else {// 选择一辆出租车
			Taxi calledTaxi = chooseTaxi(taxiCall);
			System.out.print("[请求]起：" + pr.fromPonit + "终：" + pr.toPoint );
			System.out.print("被出租车" + calledTaxi.id +"响应");
			TaxiSystem.printRuntime();
			calledTaxi.call(pr);
		}
		
	}
	
	/*
	 * 	REQUIRES：tArray是待选择的出租车队列 
	 * 	MODIFIES：tArray
	 *  EFFECTS：选择最优的出租车
	 */
	public Taxi chooseTaxi ( ArrayList<Integer> tArray ) {
		ArrayList<Integer> maxCreditCar = new ArrayList<Integer>();
		
		// 先把队列里状态为不是WATTING的出租车剔除
		for ( int i = 0; i < tArray.size(); ++i ) {
			if ( !TaxiSystem.taxis[tArray.get(i)].isTaxiWaitting() ) {
				tArray.remove(i);
			}
		}
		
		// 先把credit最高的出租车挑出来
		int maxCredit = getMaxCredit(tArray);
		for( int i = 0; i < tArray.size(); ++i ) {
			Taxi t = TaxiSystem.taxis[tArray.get(i)];
			if( t.credit == maxCredit ){
				maxCreditCar.add(tArray.get(i));
			}
		}
		
		// 挑出距离最近的出租车
		ArrayList<Integer> nearestCar = MinDistList(maxCreditCar, pr);
		
		if ( nearestCar.size() == 1 ) { // 只有一个的情况
			return TaxiSystem.taxis[nearestCar.get(0)];
		}
		else if ( nearestCar.size() == 0 ) { // 没有符合要求的出租车
			return null;
		}
		else{  // 有多个的时候随机返回一个
			Random rd = new Random();
			return TaxiSystem.taxis[nearestCar.get( rd.nextInt( nearestCar.size() ) )];
		}
		
	}
	
	/* 
	 * EFFECTS：返回出租车队列中credit最高值
	 */
	public static int getMaxCredit ( ArrayList<Integer> lk ) {
		int max = 0;
		for( int i = 0; i < lk.size(); ++i ) {
			Taxi t = TaxiSystem.taxis[lk.get(i)];
			if( t.credit > max ) {
				max = t.credit;
			}
		}
		return max;
	}
	
	/*	
	 * EFFECTS： 返回出租车与请求起始点的路程的最近的出租车列表
	 */
	public static ArrayList<Integer> MinDistList( ArrayList<Integer> tArray, PassengerReq req ) {
		
		ArrayList<Integer> nearestCar = new ArrayList<Integer>();
		ArrayList<Integer> dist = new ArrayList<Integer>();
		
		for ( int i = 0; i < tArray.size(); ++i ) {
			Taxi t = TaxiSystem.taxis[tArray.get(i)];
			dist.add(MapInfo.calShortestLen(t.position,req.fromPonit));
		}
		
		int minDist = Integer.MAX_VALUE;
		for ( int i = 0; i < dist.size(); ++i ) {
			minDist = ( minDist > dist.get(i) )? dist.get(i): minDist;
		}
		
		for ( int i = 0; i < tArray.size(); ++i ) {
			if( dist.get(i) == minDist ) {
				nearestCar.add(tArray.get(i));
			}
		}
		
		return nearestCar;
	}
	
}
 
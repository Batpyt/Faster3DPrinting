import java.util.Random;

/**
 * Tian Yuping 
 * this two classed are the implementations of simulated annealing,
 * they uses the coordinates and arrays of each move to change the order of moves,
 * with the target of decrease the distance of empty move.
 * the first class is dividing arrays by layers, and call the second class to do the changes in each layer,
 * the second class is making change on moves in one layer.
 */

public class SimulatedAnnealing {
	public String[][] annealing(double[] xs,double[] ys,double[] xe,double[] ye,String[][] blocks,int count){
		String[][] blocks2=new String[count][]; 
		
		int layer=0;//initiate the number of layers
		int[] nm=new int[999];  //a integer array to store the order number of the start of each layer
		int n=0;
		
		//String[] newblocks=new String[9999999];
		int runnum=0;
		
		
		/*
		 * first part: store the moves into each layer
		 * step 1: find the order number of the start of each layer
		 * step 2: calculate and store the order number of the end
		 */
		
		for(int i=0;i<count;i++){
			if(blocks[i][0].contains("Z")){
				layer++;        //count the number of layers
				nm[n]=i;        //record the start order number
				n++;
				
			}
		}
		
		System.out.println("layer num: "+(layer+1));
		System.out.println("number of moves: "+(count+1));
		
		int[] startnum=new int[layer];  //two arrays to store the boundaries of each layer
		int[] endnum=new int [layer];
		
		for(int i=0;i<startnum.length;i++){
			
			startnum[i]=nm[i];           //boundaries of starting
			
		} 
		
		for(int i=0;i<endnum.length;i++){
			
			//the last layer of the G-code doesn't have next layer
			//so just record the last order number
			if(i==startnum.length-1){
				endnum[i]=count-1;
			}
			
			//for other layers, record their next layers' start order number minus 1
			else{
				endnum[i]=startnum[i+1]-1;
			}
		}
		
		
		/*
		 * second part: call the simulated annealing algorithm for each layer
		 * pseudo code
		 * from the first layer
		 * 		call simulated algorithm
		 * 		store the result
		 * go to next layer
		 * 
		 */
		
		SimulatedAnnealing annl=new SimulatedAnnealing();
		
		int blocklength=0;
		for(int i=0;i<layer;i++){
			
			n=0;
			runnum++;
			int startn=startnum[i];
			
			//there is a condition, only do changes for the layers which have more than 3 moves
			//because if the number of moves is less than 3, the swap won't work, 
			//while we won't change the order of the first and last move of a layer
			if(endnum[i]-startnum[i]>3){
				
				//the boundaries are giving to simulated annealing 
				String[][] blocks3=annl.layer(startnum[i], endnum[i], blocks, count, xs, ys, xe, ye,layer);
				
				for(int k=startnum[i];k<endnum[i]+1;k++){
					
					blocks2[k]=new String[blocks3[n].length];
					
					
					for(int j=0;j<blocks3[n].length;j++){
						
						blocks2[k][j]=blocks3[n][j];  //store the results of simulated annealing
						
					}
					
					n++;
					
				}
				blocklength=blocklength+blocks3.length;
				
			}
			
			//for the layer with less than 3 moves, do nothing with them
			else{
				for(int k=startnum[i];k<endnum[i]+1;k++){
					blocks2[k]=new String[blocks[startn].length];
					for(int j=0;j<blocks[startn].length;j++){
						blocks2[k][j]=blocks[startn][j];
						
					}
					startn++;
					
				}
			}
			
		}
		//System.out.println(layer);
		
		return blocks2;
		
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/*
	 * the second class is the major part of simulated annealing
	 * first, create a new two-dimension array contains the original lines of current layer
	 * then, record each move's start and end coordinates
	 * third, optimize moves
	 * finally, the array with optimized moves will be returned
	 */
	
	
	public String[][] layer(int startnum,int endnum,String[][] blocks,int count,double[] xs,double[] ys,double[] xe,double[] ye,int layer){
		
		long begintime=System.currentTimeMillis();
		
		Random r=new Random();
		ReadFile rf = new ReadFile();
		//exchange d=new exchange();
		Rev rev=new Rev();
		
		String[][] blocks2=new String[endnum-startnum+1][];
		
		int stn=startnum;
		double diszong1=0;
		double diszong2=0;
		int a1=0;
		int a2=0;
		
		//System.out.println("   ");
		//System.out.println("------------------------------------------------------------------------------------------------------");
		//System.out.println("starttttttt: "+startnum+" enddddddd: "+endnum);
		
		for(int i=0;i<endnum-startnum+1;i++){
			
			blocks2[i]=new String[blocks[stn].length];
			for(int j=0;j<blocks[stn].length;j++){
				
				//store the original moves firstly, then make changes on this array
				blocks2[i][j]=blocks[stn][j];
				
			}
			stn++;
		}
		
		
		//then store the start and end coordinates of current layer
		stn=startnum;
		double[] xss=new double[endnum-startnum];
		for(int i=0;i<xss.length;i++){
			xss[i]=xs[stn];
			
			stn++;
		}
		stn=startnum;
		double[] yss=new double[endnum-startnum];
		for(int i=0;i<yss.length;i++){
			yss[i]=ys[stn];
			stn++;
		}
		stn=startnum;
		double[] yee=new double[endnum-startnum];
		for(int i=0;i<yee.length;i++){
			yee[i]=ye[stn];
			stn++;
		}
		stn=startnum;
		double[] xee=new double[endnum-startnum];
		for(int i=0;i<xee.length;i++){
			xee[i]=xe[stn];
			
			stn++;
		}
		
		
		
		/*
		 * then is the step of optimization
		 * pseudo code
		 * set initiate temperature t, cooling parameter k
		 * choose a method to swap with equal probabilities
		 * if is method 1: choose a number r1 randomly, calculate current distance of empty moves: d0
		 * 		calculate the distance of empty moves if shift r1 with r1+1: d1
		 * 		if d1<d0 or exp(-(d1-d0)/t)>random decimal between 0 and 1
		 * 			shift r1 and r1+1
		 * 		end if
		 * 		choose do reverse or not with probability p
		 * 		if doing it
		 * 			calculate the distance of empty move between r1 and r1's neighbor, which is r1-1, r1 and r1+1: d2
		 * 			calculate the distance of it if reverse r1: d3
		 * 			if d3<d2
		 * 				reverse
		 * 			end if
		 * 		end if
		 * end if back to the step of choosing method
		 * 
		 * if is method 2: choose two numbers r1 and r2 randomly, calculate current distance of empty moves: d0
		 * 		calculate the distance of empty moves if swap r1 with r2: d1
		 * 		if d1<d0 or exp(-(d1-d0)/t)>random decimal between 0 and 1
		 * 			shift r1 and r1+1
		 * 		end if
		 * 		choose do reverse or not with probability p
		 * 		if doing it
		 * 			calculate the distance of empty move between r1 and r1's neighbor, which is r1-1, r1 and r1+1: d2
		 * 			calculate the distance of it if reverse r1: d3
		 * 			if d3<d2
		 * 				reverse
		 * 			end if
		 * 		end if
		 * end if back to the step of choosing method
		 */
		
		int revvv=0;
		
		int tstart=10;       //initiate temperature
	    
		double cooling=0.99; //cooling parameter
		
		double t=tstart;
		
		for(int loop=0;loop<10000;loop++){ //loop is the iteration time
		
			double dis2=0;
			double dis1=0;
			
			
			double pifrev=0;   //probability of doing reverse
			double ifrev=r.nextDouble();
			
			double num=r.nextDouble();
			double p=0.5;        //probability of doing method way of swap, in our project, it is set to 0.5 to have equal probabilities
			
			//the first method, shift with neighbor
			if(num<p){
				a1++;
				int r1=r.nextInt(endnum-startnum-2)+1;
				int r2=r1+1;
				dis2=0;
					
				//calculate current distance of empty moves first
				for(int i=0;i<endnum-startnum-1;i++){
					double distance1=Math.sqrt(Math.abs((xee[i]- xss[i+1])* (xee[i] - xss[i+1])+(yee[i] - yss[i+1])* (yee[i] - yss[i+1])));
					dis1=dis1+distance1;
						
				}
				
				//swap the coordinate of the moves which are selected
				double xstemp1=xss[r1];
				double xetemp1=xee[r1]; 
				double ystemp1=yss[r1];
				double yetemp1=yee[r1];
				double xstemp2=xss[r2];
				double xetemp2=xee[r2];
				double ystemp2=yss[r2];
				double yetemp2=yee[r2];
				
					
				xss[r1]=xstemp2;
				yss[r1]=ystemp2;
				xee[r1]=xetemp2;
				yee[r1]=yetemp2;
				xss[r2]=xstemp1;
				yss[r2]=ystemp1;
				xee[r2]=xetemp1;
				yee[r2]=yetemp1;
					
				//calculate "new" distance
				for(int i=0;i<endnum-startnum-1;i++){
					double distance2=Math.sqrt(Math.abs((xee[i]- xss[i+1])* (xee[i] - xss[i+1])+(yee[i] - yss[i+1])* (yee[i] - yss[i+1])));
					dis2=dis2+distance2;
					
				}
				
				//calculate the saving distance
				double disgap=dis2-dis1;
				
				//if it's saving or satisfy the condition, do the shift for two moves
				if(disgap<0||(Math.exp(-disgap/t)>r.nextDouble()&&Math.exp(-disgap/t)<1)){
					String[] tempr1=new String[blocks2[r1].length];
					for(int i=0;i<blocks2[r1].length;i++){
						tempr1[i]=blocks2[r1][i];
					}
					String[] tempr2=new String[blocks2[r2].length];
					for(int i=0;i<blocks2[r2].length;i++){
						tempr2[i]=blocks2[r2][i];
					}
					blocks2[r1]=new String[tempr2.length];
					for(int j=0;j<tempr2.length;j++){
						blocks2[r1][j]=tempr2[j];
						
					}
					blocks2[r2]=new String[tempr1.length];
					for(int j=0;j<tempr1.length;j++){
						blocks2[r2][j]=tempr1[j];
						
					}
					
					//reduce the temperature every time a shift is done
					t=t*cooling;
					
				}
				
				//if it's not saving, put the coordinates back
				else{ 
					xss[r1]=xstemp1;
					yss[r1]=ystemp1;
					xee[r1]=xetemp1;
					yee[r1]=yetemp1;
					xss[r2]=xstemp2;
					yss[r2]=ystemp2;
					xee[r2]=xetemp2;
					yee[r2]=yetemp2;
				}
				
				
				//if doing rev
				if(ifrev<pifrev){
					revvv++;
					
					//calculate the empty moves' distance of before and after reverse
					double origdis=Math.sqrt(Math.abs((xee[r1-1]- xss[r1])* (xee[r1-1] - xss[r1])+(yee[r1-1] - yss[r1])* (yee[r1-1] - yss[r1])))
							+Math.sqrt(Math.abs((xee[r1]- xss[r1+1])* (xee[r1] - xss[r1+1])+(yee[r1] - yss[r1+1])* (yee[r1] - yss[r1+1])));
					double revdis=Math.sqrt(Math.abs((xee[r1-1]- xee[r1])* (xee[r1-1] - xee[r1])+(yee[r1-1] - yee[r1])* (yee[r1-1] - yee[r1])))
							+Math.sqrt(Math.abs((xss[r1]- xss[r1+1])* (xss[r1] - xss[r1+1])+(yss[r1] - yss[r1+1])* (yss[r1] - yss[r1+1])));
					
					//calculte saving distance
					disgap=revdis-origdis;
					
					//if it's saving, do reverse and reverse the coordinates
					//however, just for moves with lines more than 5
					if(disgap<0&&blocks2[r1].length>5){
						
						Node[] nodes=rf.read(blocks2[r1]);
						String[] revblock=rev.reverseBlock(nodes);
						
						if(revblock[0]=="0"){
							//in this situation, the reverse is not available
						}
						else{
							
							for(int s=0;s<revblock.length;s++){
								
								//reverse the block
								blocks2[r1][s]=revblock[s];  
								
							}
							
							//reverse the coordinates
							double xetemp=xee[r1];
							double yetemp=yee[r1];
							xee[r1]=xss[r1];
							yee[r1]=yss[r1];
							xss[r1]=xetemp;
							yss[r1]=yetemp;
							
							
						}
					}	
					else{
						//if it's not saving, do nothing
					}	
					
				}
				
		
			}
					
			
			//the second method, swap two moves far away
			//the only difference is the way of choosing second move, in this method, two moves are both chosen randomly
			if(num>=p){
				a2++	;
					
				int r1=r.nextInt(endnum-startnum-2)+1;
				int r2=r.nextInt(endnum-startnum-2)+1;
				
				while(r1==r2){
					r1=r.nextInt(endnum-startnum-2)+1;
					r2=r.nextInt(endnum-startnum-2)+1;
					
				}
				
				for(int i=0;i<endnum-startnum-1;i++){
					double distance1=Math.sqrt(Math.abs((xee[i]- xss[i+1])* (xee[i] - xss[i+1])+(yee[i] - yss[i+1])* (yee[i] - yss[i+1])));
					dis1=dis1+distance1;
					
				}
				
				double xstemp1=xss[r1];
				double xetemp1=xee[r1]; 
				double ystemp1=yss[r1];
				double yetemp1=yee[r1];
				double xstemp2=xss[r2];
				double xetemp2=xee[r2];
				double ystemp2=yss[r2];
				double yetemp2=yee[r2];
				
				
				xss[r1]=xstemp2;
				yss[r1]=ystemp2;
				xee[r1]=xetemp2;
				yee[r1]=yetemp2;
				xss[r2]=xstemp1;
				yss[r2]=ystemp1;
				xee[r2]=xetemp1;
				yee[r2]=yetemp1;
				
					
				for(int i=0;i<endnum-startnum-1;i++){
					double distancez2=Math.sqrt(Math.abs((xee[i]- xss[i+1])* (xee[i] - xss[i+1])+(yee[i] - yss[i+1])* (yee[i] - yss[i+1])));
					dis2=dis2+distancez2;
					
				}
				
				double disgap=dis2-dis1;
				
				if(disgap<0||(Math.exp(-disgap/t)>r.nextDouble()&&Math.exp(-disgap/t)<1)){
					
					
					String[] temp=new String[blocks2[r1].length];
					for(int i=0;i<blocks2[r1].length;i++){
						temp[i]=blocks2[r1][i];
					}
						
					blocks2[r1]=new String[blocks2[r2].length];
					for(int j=0;j<blocks2[r2].length;j++){
						blocks2[r1][j]=blocks2[r2][j];
						
					}
					
					blocks2[r2]=new String[temp.length];
					for(int j=0;j<temp.length;j++){
						blocks2[r2][j]=temp[j];
						
					}
					
					t=t*cooling;
					
					
				}
				
				else{
					
					xss[r1]=xstemp1;
					yss[r1]=ystemp1;
					xee[r1]=xetemp1;
					yee[r1]=yetemp1;
					xss[r2]=xstemp2;
					yss[r2]=ystemp2;
					xee[r2]=xetemp2;
					yee[r2]=yetemp2;
					
					
				}
				
				
				if(ifrev<pifrev){
					revvv++;
					
					double origdis=Math.sqrt(Math.abs((xee[r1-1]- xss[r1])* (xee[r1-1] - xss[r1])+(yee[r1-1] - yss[r1])* (yee[r1-1] - yss[r1])))
							+Math.sqrt(Math.abs((xee[r1]- xss[r1+1])* (xee[r1] - xss[r1+1])+(yee[r1] - yss[r1+1])* (yee[r1] - yss[r1+1])));
					double revdis=Math.sqrt(Math.abs((xee[r1-1]- xee[r1])* (xee[r1-1] - xee[r1])+(yee[r1-1] - yee[r1])* (yee[r1-1] - yee[r1])))
							+Math.sqrt(Math.abs((xss[r1]- xss[r1+1])* (xss[r1] - xss[r1+1])+(yss[r1] - yss[r1+1])* (yss[r1] - yss[r1+1])));
					disgap=revdis-origdis;
					if(disgap<0&&blocks2[r1].length>5){
						
						Node[] nodes=rf.read(blocks2[r1]);
						String[] revblock=rev.reverseBlock(nodes);
						
						if(revblock[0]=="0"){
							
						}
						else{
							
							for(int s=0;s<revblock.length;s++){
								
								blocks2[r1][s]=revblock[s];
								
							}
							double xetemp=xee[r1];
							double yetemp=yee[r1];
							xee[r1]=xss[r1];
							yee[r1]=yss[r1];
							xss[r1]=xetemp;
							yss[r1]=yetemp;
							
							
						}
					}
					else{
						
					}
					
				}
				
			}
			
			long endtime=System.currentTimeMillis();
			long time=endtime-begintime;
			for(int i=0;i<blocks2.length;i++){
				for(int j=0;j<blocks2[i].length;j++){
					
				}
			}
		
			for(int i=0;i<endnum-startnum-1;i++){
				double distancez2=Math.sqrt(Math.abs((xee[i]- xss[i+1])* (xee[i] - xss[i+1])+(yee[i] - yss[i+1])* (yee[i] - yss[i+1])));
				diszong2=diszong2+distancez2;
			
			}
		
			
		
		}
		
		
		//System.out.println("1 "+a1+" 2 "+a2+" rev "+revvv);
		
		//finally, return the optimized moves
		return blocks2;

	}
}

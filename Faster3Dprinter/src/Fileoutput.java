import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Tian Yuping
 * this class is printing the optimized moves to a new G-code fine
 * 
 *
 */

public class Fileoutput {
	public String output(String[][] blocks,int count,String which) throws IOException{
		String[] newmoves= new String[999999];
		int i=0;
		int b=0;
		int n=0;
		String qqq="0";
		
		//these moves are the first part of a G-code file, most of them are initialize some variables such as fan or temperature
		newmoves[0]="G21";
		newmoves[1]="M107";
		newmoves[2]="M104 S205";
		newmoves[3]="G28";
		newmoves[4]="G1 Z5 F5000";
		newmoves[5]="M109 S205";
		newmoves[6]="G90";
		newmoves[7]="G92 E0";
		newmoves[8]="M82";
		newmoves[9]="G1 F1800.000 E-1.00000";
		newmoves[10]="G92 E0";
		newmoves[11]="  ";
		
		
		//the optimized moves are adding after them
		i=12;
		while(b<count){
			
			n=0;
			while(n<blocks[b].length){
				newmoves[i]=blocks[b][n];
				i++;
				n++;
			}
			b++;
		}
		n=0;
		
		File file = null;
		
		
		
		//output each of simulated annealing and hill climbing
		if(which=="annealing"){
			file=new File("C:/Users/Tian/Desktop/SimulatedAnnealing.gcode"); //this is the path where user would like to find the optimized G-code
			                                                                 //should be changed by hand
			if (!file.exists()) {
			    file.createNewFile();  //if haven't create a new file before, program will create one 
			   }
			
			qqq="the result of simulated annealing is stored in suanfa1.gcode";
		}
		if(which=="hillclimbing"){
			file=new File("C:/Users/Tian/Desktop/HillClimbing.gcode"); //this is the path where user would like to find the optimized G-code
            												           //should be changed by hand
			if (!file.exists()) {
			    file.createNewFile();  //if haven't create a new file before, program will create one 
			   }
			
			qqq="the result of hill climbing is stored in suanfa2.gcode";
		}
		
		
		
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		int xie=0;
		while(xie<i){
			
			bw.write(newmoves[xie]); //write the instructions into the new G-code file
			bw.newLine();
			xie++;
			
		}
		
		
		bw.close();
		
		System.out.println("Finish");
		
		
		return qqq;
		
	}

}

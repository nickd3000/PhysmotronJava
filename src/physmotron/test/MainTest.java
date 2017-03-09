package physmotron.test;

import physmotron.core.CPU;
import physmotron.core.Dissasembler;

public class MainTest {

	public static void main(String[] args) {

		CPU cpu = new CPU();
		loadTestCode(cpu);
		
		System.out.println("" + Dissasembler.decodeLine(cpu, cpu.pc));
		
		for (int i=0;i<5;i++) {
			//System.out.println(cpu.toString());
			cpu.tick();
		}
		
		//System.out.println(cpu.toString());
		
	}

	public static void loadTestCode(CPU cpu) {
		int loc=0x00011000;
		cpu.pc = loc;
		
	    // Scanline flash
	    // s[2]  b[0x00011000] 01  2a  00  00  00  04  01   mov [1025], r1
	    // s[3]  b[0x00011007] 2c  00                       inc r1
	    // s[4]  b[0x00011009] 0b  29  00  01  10  00       jmp start  
	    cpu.storeByte(loc++,0x01);
	    cpu.storeByte(loc++,0x2a);
	    cpu.storeByte(loc++,0x00);
	    cpu.storeByte(loc++,0x00);
	    cpu.storeByte(loc++,0x00);
	    cpu.storeByte(loc++,0x04);
	    cpu.storeByte(loc++,0x01);
	    //
	    cpu.storeByte(loc++,0x2c);
	    cpu.storeByte(loc++,0x00);
	    //
	    cpu.storeByte(loc++,0x0b);
	    cpu.storeByte(loc++,0x29);
	    cpu.storeByte(loc++,0x00);
	    cpu.storeByte(loc++,0x01);
	    cpu.storeByte(loc++,0x10);
	    cpu.storeByte(loc++,0x00);
	}
}

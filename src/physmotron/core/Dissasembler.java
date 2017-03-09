package physmotron.core;

public class Dissasembler {

	static int pc = 0;
	static CPU cpu;

	public static String decodeLine(CPU cpu, int addr) {
		Dissasembler.pc = addr;
		Dissasembler.cpu = cpu;
		String output = "";
		int lineAddress = 0;

		for (int line = 0; line < 5; line++) {
			lineAddress = pc;
			char instr = (char) cpu.getByte(pc++);
			OPCODES opc = OPCODES.getOpcodeForInstructionId(instr);

			int numOps = OPCODES.getNumOperatorsForInstructionId(instr);
			int iOp1 = -1, iOp2 = -1;

			// printf("Instruction: %d num ops:%d \n", curInst, numOps);

			// Fetch operator identifiers from memory if instruction requires
			// them.
			if (numOps > 0)
				iOp1 = cpu.mem[pc++];
			if (numOps > 1)
				iOp2 = cpu.mem[pc++];

			// Decode iOp values
			DecodedOperator decoded1 = null;
			DecodedOperator decoded2 = null;
			int ed1 = -1, ed2 = -1, val1=0, val2=0;

			if (iOp1 != -1)
				decoded1 = cpu.decodeOperator(iOp1);
			if (iOp2 != -1)
				decoded2 = cpu.decodeOperator(iOp2);

			if (iOp1 != -1)
				ed1 = readExtraData(decoded1.opType);
			if (iOp2 != -1)
				ed2 = readExtraData(decoded2.opType);

			if (iOp1 != -1)
				val1 = getSource(decoded1, ed1);
			if (iOp2 != -1)
				val2 = getSource(decoded2, ed2);

			String str_opc = " " + opc.name();
			String str_oper1 = "", str_oper2 = "";
			/*
			if (decoded1 != null && decoded1.opType != null)
				str_oper1 = " " + decoded1.opType.toString() + " " + ed1;
			if (decoded2 != null && decoded2.opType != null)
				str_oper2 = " " + decoded2.opType.toString() + " " + ed2;
			*/
			if (decoded1 != null && decoded1.opType != null) str_oper1 = getOperatorAsString(decoded1, ed1);
			if (decoded2 != null && decoded2.opType != null) str_oper2 = getOperatorAsString(decoded2, ed2);
			
			output += "->" + " " + toHex(lineAddress) + str_opc;
			if (decoded1 != null && decoded1.opType != null) output += " " + str_oper1;
			if (decoded2 != null && decoded2.opType != null) output += ", " + str_oper2;
			output += "\n";
		}

		return output;
	}

	static String toHex(int val) {
		return "0x" + Integer.toHexString(val);
	}
	
	static int readExtraData(OPERATOR_TYPE opType) {
		int read = 0;
		// next byte literal
		if (opType == OPERATOR_TYPE.BLIT) {
			read = getNextByte();
		}
		if (opType == OPERATOR_TYPE.WLIT) {
			read = getNextWord();
		}
		if (opType == OPERATOR_TYPE.BADDR) {
			read = getNextWord();
		}
		if (opType == OPERATOR_TYPE.WADDR) {
			read = getNextWord();
		}
		return read;
	}

	// Returns next byte at PC and increments PC by 1 byte.
	static int getNextByte() {
		int val = getByte(pc);
		pc++;
		return val;
	}

	// Returns next word at PC and increments PC by 4 bytes.
	static int getNextWord() {
		int val = getWord(pc);
		pc += 4;
		return val;
	}

	static int getByte(int addr) {
		return cpu.mem[addr];
	}

	static int getWord(int addr) {
		int combined = 0;
		combined = (cpu.mem[addr] << 24) + (cpu.mem[addr + 1] << 16) + (cpu.mem[addr + 2] << 8) + cpu.mem[addr + 3];
		return combined;
	}

	static int getSource(DecodedOperator decoded, int val) {

		OPERATOR_TYPE opType = decoded.opType;
		int regId = decoded.extra;

		if (opType == null)
			return 0;

		switch (opType) {
		// Register
		case REG:
			return cpu.regs[regId];

		// Byte address of register.
		case BAREG:
			// return getByte(_cpu->mem[_cpu->r[regId]]);
			return getByte(cpu.regs[regId]);

		// Word register pointer.
		case WAREG:
			return getWord(cpu.regs[regId]);

		// Literals
		case BLIT:
		case WLIT:
			return val;

		// Addresses
		case BADDR:
			return getByte(val);

		case WADDR:
			return getWord(val);

		// Special Registers
		case PC:
			return pc;
		case FLAGS:
			return cpu.flags;
		case SP:
			return cpu.sp;
		default:
			break;
		}

		return 0;
	}

	static String getOperatorAsString(DecodedOperator decoded, int val) {

		OPERATOR_TYPE opType = decoded.opType;
		int regId = decoded.extra;

		if (opType == null)
			return "NULL";

		switch (opType) {
		// Register
		case REG:
			return "R"+regId;

		// Byte address of register.
		case BAREG:
			return "b[R"+regId+"]";

		// Word register pointer.
		case WAREG:
			return "w[R"+regId+"]";

		// Literals
		case BLIT:
		case WLIT:
			return ""+toHex(val);

		// Addresses
		case BADDR:
			return "b["+toHex(val)+"]"; //return getByte(val);

		case WADDR:
			return "w["+toHex(val)+"]"; //return getWord(val);

		// Special Registers
		case PC:
			return "PC"; //pc;
		case FLAGS:
			return "FLAGS"; //return cpu.flags;
		case SP:
			return "SP"; // return cpu.sp;
		default:
			break;
		}

		return "Unrecognised";
	}
}

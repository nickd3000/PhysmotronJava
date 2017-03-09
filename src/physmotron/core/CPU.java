package physmotron.core;

public class CPU {

	int MEM_SIZE = 1000000;
	int NUM_REGS = 8;

	// Bit indexes into the flags register.
	int FLAG_SIGN = (1 << 0);
	int FLAG_ZERO = (1 << 1);
	int FLAG_BREAK = (1 << 2);
	int FLAG_CARRY = (1 << 3);

	char[] mem; // [MEM_SIZE];
	int[] regs; // [NUM_REGS];
	public int pc;
	int sp;
	int flags;

	public CPU() {
		mem = new char[MEM_SIZE];
		regs = new int[NUM_REGS];

	}

	public String toString() {

		String retVal = "PC:" + pc + " SP:" + sp + " FLAGS:" + flags;

		String registers = "";
		for (int i=0;i<NUM_REGS;i++) {
			registers+="R"+i+":"+regs[i]+" ";
		}
		
		return retVal+"\n"+registers;
	}

	public void tick() {
		/*
		 * tickCount++; if (tickCount==26234) { int bob = 5; bob++; }
		 */

		// displayCpuInfo();

		char curInst = mem[pc++];

		int numOps = getNumberOfOperatorsForInstruction(curInst);
		int iOp1 = -1, iOp2 = -1;

		// printf("Instruction: %d num ops:%d \n", curInst, numOps);

		// Fetch operator identifiers from memory if instruction requires them.
		if (numOps > 0)
			iOp1 = mem[pc++];
		if (numOps > 1)
			iOp2 = mem[pc++];

		// Decode iOp values
		DecodedOperator decoded1 = null;
		DecodedOperator decoded2 = null;
		int ed1 = -1, ed2 = -1;

		if (iOp1 != -1)
			decoded1 = decodeOperator(iOp1);
		if (iOp2 != -1)
			decoded2 = decodeOperator(iOp2);

		// if (iOp1!=-1) printf("DEC OP 1 A:%d, B:%d\n", decoded1.opType,
		// decoded1.extra);
		// if (iOp2!=-1) printf("DEC OP 2 A:%d, B:%d\n", decoded2.opType,
		// decoded2.extra);

		// Retrieve any extra operator data
		if (iOp1 != -1)
			ed1 = readExtraData(decoded1.opType);
		if (iOp2 != -1)
			ed2 = readExtraData(decoded2.opType);

		int cmpResult;
		int srcVal = 0;
		int val1 = 0;
		int val2 = 0;
		if (iOp1 != -1)
			val1 = getSource(decoded1, ed1);
		if (iOp2 != -1)
			val2 = getSource(decoded2, ed2);

		switch (getOperatorForInstructionId(curInst)) {
		case NOP:
			break;
		case MOV:
			// srcVal = getSource(iOp2, ed2, &decoded2);
			setTarget(decoded1, val2, ed1);
			break;
		case ADD:
			setTarget(decoded1, val1 + val2, ed1);
			break;
		case SUB:
			setTarget(decoded1, val1 - val2, ed1);
			break;
		case MUL:
			setTarget(decoded1, val1 * val2, ed1);
			break;
		case DIV:
			setTarget(decoded1, (val1 / val2) & 0xffffffff, ed1);
			break;

		case TEST: // Assuming TEST and CMP are the same, fallthrough to CMP.
		case CMP:
			cmpResult = val1 - val2;
			setFlag(FLAG_ZERO, 0);
			setFlag(FLAG_SIGN, 0);
			if (cmpResult == 0)
				setFlag(FLAG_ZERO, 1);
			else if (cmpResult < 0)
				setFlag(FLAG_SIGN, 1);
			else
				setFlag(FLAG_SIGN, 0);
			break;

		case INC:
			setTarget(decoded1, val1 + 1, ed1);
			break;
		case DEC:
			setTarget(decoded1, val1 - 1, ed1);
			break;
		case JMP:
			srcVal = getSource(decoded1, ed1);
			pc = srcVal;
			break;
		case JE:
			if (getFlag(FLAG_ZERO) == 1)
				pc = ed1;
			break;
		case JNE:
			if (getFlag(FLAG_ZERO) == 0)
				pc = ed1;
			break;
		case JL:
			if (getFlag(FLAG_SIGN) == 1)
				pc = ed1;
			break;
		case JLE:
			if (getFlag(FLAG_SIGN) == 1 || getFlag(FLAG_ZERO) == 1)
				pc = ed1;
			break;
		case JG:
			if (getFlag(FLAG_SIGN) == 0 && getFlag(FLAG_ZERO) == 0)
				pc = ed1;
			break;
		case JGE:
			if (getFlag(FLAG_SIGN) == 0 || getFlag(FLAG_ZERO) == 1)
				pc = ed1;
			break;

		case PUB:
			pushByte(val1);
			break;
		case PUW:
			pushWord(val1);
			break;
		case POB:
			setTarget(decoded1, popByte(), ed1);
			break;
		case POW:
			setTarget(decoded1, popWord(), ed1);
			break;

		case SYS:
			// TODO: //sysCall(val1);
			break;
		case CAL:
			pushWord(pc);
			pc = ed1;
			break;
		case RET:
			pc = popWord();
			break;
		case BRK:
			setFlag(FLAG_BREAK, 1);
			break;

		case AND:
			setTarget(decoded1, (val1 & val2) & 0xffffffff, ed1);
			break;
		case OR:
			setTarget(decoded1, (val1 | val2) & 0xffffffff, ed1);
			break;
		case XOR:
			setTarget(decoded1, (val1 ^ val2) & 0xffffffff, ed1);
			break;
		case NOT:
			setTarget(decoded1, (~val1) & 0xffffffff, ed1);
			break;

		case PUA:
			pushAll();
			break;
		case POA:
			popAll();
			break;
		case SHL:
			if ((0x80000000 & val1) > 0)
				setFlag(FLAG_CARRY, 1);
			setTarget(decoded1, (val1 << val2) & 0xffffffff, ed1);
			break;
		case SHR:
			if ((0x00000001 & val1) > 0)
				setFlag(FLAG_CARRY, 1);
			setTarget(decoded1, (val1 >> val2) & 0xffffffff, ed1);
			break;
		case CLC:
			setFlag(FLAG_CARRY, 0);
			break;
		case CLZ:
			setFlag(FLAG_ZERO, 0);
			break;
		case CLS:
			setFlag(FLAG_SIGN, 0);
			break;
		case CLB:
			setFlag(FLAG_BREAK, 0);
			break;
		default:
			break;
		}

	}

	 int getNumberOfOperatorsForInstruction(char inst) {
		for (OPCODES opcode : OPCODES.values()) {
			if (opcode.getId() == inst) {
				return opcode.getOpCount();
			}
		}
		return 0;
	}

	OPCODES getOperatorForInstructionId(char inst) {
		for (OPCODES opcode : OPCODES.values()) {
			if (opcode.getId() == inst) {
				return opcode;
			}
		}
		return null;
	}



	 DecodedOperator decodeOperator(int val) {
		val = val & 0xff;

		DecodedOperator result = new DecodedOperator();
		result.opType = null;
		result.extra = -1;

		if (val > -1 && val < 8) {
			result.opType = OPERATOR_TYPE.REG;
			result.extra = val;
			return result;
		}

		if (val > -7 && val < 16) {
			result.opType = OPERATOR_TYPE.BAREG;
			result.extra = val - 8;
			return result;
		}

		if (val > -15 && val < 32) {
			result.opType = OPERATOR_TYPE.WAREG;
			result.extra = val - 16;
			return result;
		}

		if (val == 40) {
			result.opType = OPERATOR_TYPE.BLIT;
			return result;
		}

		if (val == 41) {
			result.opType = OPERATOR_TYPE.WLIT;
			return result;
		}

		if (val == 42) {
			result.opType = OPERATOR_TYPE.BADDR;
			return result;
		}
		if (val == 43) {
			result.opType = OPERATOR_TYPE.WADDR;
			return result;
		}

		// TODO: If we got here, the code was not recognised, should handle it
		// somehow.

		return result;
	}

	// Pass in an operator descriptor, this function reads any other data
	// required
	// by the operator.
	 int readExtraData(OPERATOR_TYPE opType) {
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
	 int getNextByte() {
		int val = getByte(pc);
		pc++;
		return val;
	}

	// Returns next word at PC and increments PC by 4 bytes.
	 int getNextWord() {
		int val = getWord(pc);
		pc += 4;
		return val;
	}

	 int getByte(int addr) {
		return mem[addr];
	}

	 int getWord(int addr) {
		int combined = 0;
		combined = (mem[addr] << 24) + (mem[addr + 1] << 16) + (mem[addr + 2] << 8) + mem[addr + 3];
		return combined;
	}

	public void storeByte(int addr, int val) {
		val = val & 0xff;
		mem[addr] = (char) val;
	}

	 void storeWord(int addr, int val) {
		val = val & 0xffffffff;
		mem[addr] = (char) ((val >> 24) & 0xff);
		mem[addr + 1] = (char) ((val >> 16) & 0xff);
		mem[addr + 2] = (char) ((val >> 8) & 0xff);
		mem[addr + 3] = (char) ((val) & 0xff);
	}

	// Get source
	 int getSource(DecodedOperator decoded, int val) {

		OPERATOR_TYPE opType = decoded.opType;
		int regId = decoded.extra;

		switch (opType) {
		// Register
		case REG:
			return regs[regId];

		// Byte address of register.
		case BAREG:
			// return getByte(_cpu->mem[_cpu->r[regId]]);
			return getByte(regs[regId]);

		// Word register pointer.
		case WAREG:
			return getWord(regs[regId]);

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
			return flags;
		case SP:
			return sp;
		default:
			break;
		}

		return 0;
	}

	 void setTarget(DecodedOperator decoded, int val, int addr) {

		OPERATOR_TYPE opType = decoded.opType;
		int regId = decoded.extra;

		switch (opType) {
		// Register
		case REG:
			regs[regId] = val;
			break;
		// Byte address of register.
		case BAREG:
			storeByte(regs[regId], val);// return
										// getByte(_cpu->mem[_cpu->r[regId]]);
			break;
		// Word register pointer.
		case WAREG:
			storeWord(regs[regId], val);// return
										// getWord(_cpu->mem[_cpu->r[regId]]);
			break;
		case BLIT: // Literals
		case WLIT:
			break;
		case BADDR: // Addresses
			storeByte(addr, val);
			break;
		case WADDR:
			storeWord(addr, val);
			break;
		// Special Registers
		case PC:
			pc = val;
			break;
		case FLAGS:
			flags = val;
			break;
		case SP:
			sp = val;
			break;
		default:
			break;
		}
	}

	// Set or unset a flag, pass in 0 or 1 as the value.
	void setFlag(int flagId, int value) {
		if (value != 0)
			flags |= flagId;
		else
			flags &= ~flagId;
	}

	int getFlag(int flagId) {
		return (flags & flagId) > 0 ? 1 : 0;
	}

	// Stack operations
	// Stack operations.
	 void pushByte(int data) {
		mem[sp--] = (char) (data & 0xff);
	}

	// I'm changing words to be 32 bit...
	void pushWord(int data) {
		pushByte((data >> 24) & 0xff);
		pushByte((data >> 16) & 0xff);
		pushByte((data >> 8) & 0xff);
		pushByte(data & 0xff);
	}

	int popByte() {
		sp++;
		int popValue = mem[sp] & 0xff;
		return popValue;
	}

	int popWord() {
		int byte4 = popByte();
		int byte3 = popByte();
		int byte2 = popByte();
		int byte1 = popByte();

		return (byte1 << 24) + (byte2 << 16) + (byte3 << 8) + byte4;
	}

	 void pushAll() {
		for (int i = 0; i < NUM_REGS; i++) {
			pushWord(regs[i]);
		}
	}

	void popAll() {
		for (int i = 0; i < NUM_REGS; i++) {
			regs[(NUM_REGS - 1) - i] = popWord();
		}

	}

};

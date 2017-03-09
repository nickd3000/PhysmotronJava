package physmotron.core;

// Opcodes, Opcode ID and number of operators.
public enum OPCODES {

	// Instruction Opcodes
	NOP(0,0),
	MOV(1,2),

	// Compare and Jumps
	CMP(10,2),	// Compare
	JMP(11,1),	// Jump
	JE(12,1),		// Jump if equal.
	JNE(13,1),	// Jump if equal.
	JL(14,1),		// Jump if equal.
	JLE(15,1),	// Jump if equal.
	JG(16,1),		// Jump if equal.
	JGE(17,1),	// Jump if equal.
	JC(18,1),		// Jump if carry is set.
	JCC(19,1),	// Jump if carry is clear.
	CAL(20,1),	// Call
	RET(21,0),	// return from call

	// Stack
	PUB(30,1), 	// Push byte
	PUW(31,1), 	// Push word (4 bytes)
	POB(32,1), 	// Pop byte
	POW(33,1), 	// Pop word.
	PUA(34,0), 	// Push all regs
	POA(35,0), 	// Pop all regs

	// Maths
	ADD(40,2), SUB(41,2), MUL(42,2), 
	DIV(43,2), INC(44,1), DEC(45,1), 
	SHL(46,2), SHR(47,2),

	// Bitwise
	AND(60,2), OR(61,2), XOR(62,2), NOT(63,1),
	TEST(64,2),

	// Flags
	CLC(70,0), 	// clear carry flag
	CLZ(71,0), 	// clear zero flag
	CLS(72,0), 	// clear sign flag
	CLB(73,0), 	// clear break flag

	SYS(128,1), BRK(129,0);

	private int id;
	private int opCount;

	OPCODES(int numVal, int opCount) {
		this.id = numVal;
		this.opCount = opCount;
	}

	public int getId() {
		return id;
	}
	public int getOpCount() {
		return opCount;
	}
	
	public static OPCODES getOpcodeForInstructionId(char inst) {
		for (OPCODES opcode : OPCODES.values()) {
			if (opcode.getId() == inst) {
				return opcode;
			}
		}
		return null;
	}
	
	public static int getNumOperatorsForInstructionId(char inst) {
		for (OPCODES opcode : OPCODES.values()) {
			if (opcode.getId() == inst) {
				return opcode.opCount;
			}
		}
		return -1;
	}
}

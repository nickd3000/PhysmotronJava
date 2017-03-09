package physmotron.core;

public enum OPERATOR_TYPE {
	// Classes of operator
	REG(0), // Register
	BAREG(1), // BYTE [register]
	WAREG(2), // WORD [register]
	BAREGO(3), // BYTE [register+offset] (offset is next word)
	WAREGO(4), // WORD [register+offset] (offset is next word)
	BLIT(5), // BYTE LITERAL
	WLIT(6), // WORD LITERAL
	BADDR(7), // BYTE at [ADDRESS]
	WADDR(8), // WORD at [ADDRESS]
	PC(9), // PC
	FLAGS(10), // FLAGS
	SP(11), // SP
	ASP(12), // [SP]
	ASPO(13), // [SP+offset] (offset is next word)
	PLIT(14); // Literal packed into operator byte.

	private int id;

	OPERATOR_TYPE(int numVal) {
		this.id = numVal;
	}

	public int getId() {
		return id;
	}
}

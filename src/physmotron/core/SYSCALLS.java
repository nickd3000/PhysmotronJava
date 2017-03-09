package physmotron.core;

public enum SYSCALLS {
	SYS_LOGBYTE(1), SYS_LOGWORD(2), SYS_LOGCHAR(3), SYS_TEXT(5), // bx,by,wptr

	SYS_SETPIXEL(20), SYS_RGBTOPIXEL(21), // (bytes)r,g,b ->
	SYS_PIXELTORGB(22), // byte -> (bytes)r,g,b

	SYS_MUL(30), SYS_DIV(31), SYS_SQRT(32), SYS_MIN(33), SYS_MAX(34),

	SYS_MEMSET(50), // byte value, word start, word length
	SYS_MEMCPY(51), // word src, word dest, word length
	SYS_MEMCPYI(52); // word src, word dest, word length, byte ignore

	private int id;

	SYSCALLS(int numVal) {
		this.id = numVal;
	}

	public int getId() {
		return id;
	}
}

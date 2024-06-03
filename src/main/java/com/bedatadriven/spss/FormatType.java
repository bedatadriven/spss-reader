package com.bedatadriven.spss;

public enum FormatType {

    A(1),
    AHEX(2),
    COMMA(3),
    DOLLAR(4),
    F(5),
    IB(6),
    PIBHEX(7),
    P(8),
    PIB(9),
    PK(10),
    RB(11),
    RBHEX(12),
    Z(15),
    N(16),
    E(17),

    DATE(20),
    TIME(21),
    DATETIME(22),
    ADATE(23),
    JDATE(24),
    DTIME(25),
    WKDAY(26),
    MONTH(27),
    MOYR(28),
    QYR(29),
    WKYR(30),
    PCT(31),
    DOT(32),
    CCA(33),
    CCB(34),
    CCC(35),
    CCD(36),
    CCE(37),
    EDATE(38),
    SDATE(39),
    MTIME(40),
    YMDHMS(41);

    private final int code;

    FormatType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

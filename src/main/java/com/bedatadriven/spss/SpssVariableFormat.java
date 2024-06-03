package com.bedatadriven.spss;

/**
 * Specifies the format to be used for an {@link SpssVariableReader}.
 *
 * <p>
 *  <a href="https://www.ibm.com/support/knowledgecenter/en/SSLVMB_24.0.0/spss/base/display_formats.html">More info</a>
 * </p>
 *
 * <p>
 *  <a href="https://www.gnu.org/software/pspp/pspp-dev/html_node/Variable-Record.html#Variable-Record">Spec</a>
 * </p>
 */
public class SpssVariableFormat {
	private final int decimalPlaces;
	private final int columnWidth;
	private final int type;

	/**
	 * Construct a format instance based on the encoded format information as obtained from the SPSS file.
	 * @param encodedFormat the encoded format information from which to create the format instance.
	 */
	SpssVariableFormat(int encodedFormat) {
		this.decimalPlaces = encodedFormat & 255;
		this.columnWidth = encodedFormat >> 8 & 255;
		this.type = encodedFormat >> 16 & 255;
	}

	public SpssVariableFormat(int decimalPlaces, int columnWidth, FormatType type) {
		this.decimalPlaces = decimalPlaces;
		this.columnWidth = columnWidth;
		this.type = type.getCode();
	}

	public int encode() {
		return (decimalPlaces) | (columnWidth << 8) | (type << 16);
	}

	/**
	 * @return the number of decimal places to use for this variable.
	 * For example, 0 means that the integer value is to be displayed.
	 */
	public int getDecimalPlaces() {
		return decimalPlaces;
	}

	/**
	 * @return the number of characters to use when displaying this variable.
	 * Padding is used to fill the remaining characters.
	 */
	public int getColumnWidth() {
		return columnWidth;
	}

	/**
	 * @return the type code for this variable.
	 * See the <a href=https://www.gnu.org/software/pspp/pspp-dev/html_node/Variable-Record.html#Variable-Record>format
	 * types table</a> for a description of each type.
	 */
	public int getTypeCode() {
		return type;
	}

	public FormatType getType() {
		for (FormatType value : FormatType.values()) {
			if(value.getCode() == type) {
				return value;
			}
		}
		throw new IllegalStateException("Unknown format type: " + type);
	}
}

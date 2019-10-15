package air.kanna.mystorage.util;

public class NumberUtil {

    /**
     * 字节数字到hex字符串的转换表
     */
    private static final String[] BYTE_2_HEX = new String[] {
            "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B", "0C", "0D", "0E", "0F", 
            "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1A", "1B", "1C", "1D", "1E", "1F", 
            "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2A", "2B", "2C", "2D", "2E", "2F", 
            "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3A", "3B", "3C", "3D", "3E", "3F", 
            "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "4A", "4B", "4C", "4D", "4E", "4F", 
            "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "5A", "5B", "5C", "5D", "5E", "5F", 
            "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "6A", "6B", "6C", "6D", "6E", "6F", 
            "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "7A", "7B", "7C", "7D", "7E", "7F", 
            "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "8A", "8B", "8C", "8D", "8E", "8F", 
            "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "9A", "9B", "9C", "9D", "9E", "9F", 
            "A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "AA", "AB", "AC", "AD", "AE", "AF", 
            "B0", "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "BA", "BB", "BC", "BD", "BE", "BF", 
            "C0", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "CA", "CB", "CC", "CD", "CE", "CF", 
            "D0", "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "DA", "DB", "DC", "DD", "DE", "DF", 
            "E0", "E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9", "EA", "EB", "EC", "ED", "EE", "EF", 
            "F0", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "FA", "FB", "FC", "FD", "FE", "FF"
    };
    
    /**
     * 把字节数组转为hex字符串
     * @param bytes 字节数组
     * @return 字节数组的hex字符串
     */
    public static String toHexString(byte[] bytes) {
        if(bytes == null || bytes.length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for(int idx=0; idx<bytes.length; idx++) {
            sb.append(BYTE_2_HEX[(0xff & bytes[idx])]);
        }
        return sb.toString();
    }
    
    /**
     * 从hex字符串转为字节数组
     * @param hexString hex字符串
     * @return 字节数组
     */
    public static byte[] fromHexString(String hexString) {
        if(StringUtil.isNull(hexString)) {
            return new byte[0];
        }
        
        int length = hexString.length() >> 1, idxa = 0, idxb = 0;
        byte[] result = new byte[length];
        length <<= 1;
        
        for(int i=0,j=1,k=0; i<length; i+=2,j+=2,k++) {
            idxa = getIndexFromChar(hexString.charAt(i));
            idxb = getIndexFromChar(hexString.charAt(j));
            result[k] = (byte)((idxa << 4) | idxb);
        }
        return result;
    }
    
    /**
     * 在不足fixedLength长度的数字前，加0补足长度
     * 如果长度够，或者超过的情况，不处理，直接返回number的字符串版本
     * @param number
     * @param fixedLength
     * @return
     */
    public static String toFixedLength(long number, int fixedLength) {
        StringBuilder sb = new StringBuilder();
        
        sb.append(number);
        int numLength = sb.length(), insertIdx = 0, diffLength;
        if(number < 0) {
            numLength--;
            insertIdx = 1;
        }
        
        diffLength = fixedLength - numLength;
        if(diffLength < 0) {
            return sb.toString();
        }
        
        for(int i=0; i<diffLength; i++) {
            sb.insert(insertIdx, '0');
        }
        
        return sb.toString();
    }
    
    /**
     * hex字符转数字
     * @param ch 字符
     * @return 数字
     */
    private static int getIndexFromChar(char ch) {
        if(ch >= '0' && ch <= '9') {
            return ch - '0';
        }
        if(ch >= 'A' && ch <= 'F') {
            return 10 + (ch - 'A');
        }
        if(ch >= 'a' && ch <= 'f') {
            return 10 + (ch - 'A');
        }
        throw new java.lang.IllegalArgumentException("Cannot parse hex char: " + ch);
    }
}

package lt.vinted.enumerated;

public enum ShipmentSize {
    SMALL('S'), MEDIUM('M'), LARGE('L');
    public final char sizeChar;
    private ShipmentSize(char sizeChar) {
        this.sizeChar = sizeChar;
    }
    public static ShipmentSize fromSizeChar(String character) {
        char sizeChar = character.charAt(0);
        ShipmentSize size = null;
        switch (sizeChar){
            case 'S' -> size = ShipmentSize.SMALL;
            case 'M' -> size = ShipmentSize.MEDIUM;
            case 'L' -> size = ShipmentSize.LARGE;
        }
        return size;
    }
}

package com.ids.fixot.enums;


public class enums {


    public enum MarketStatuss {
        StartOfDay("StartOfDay", -2), //Added to be used in accu trade, in OT it will be similar to Closed
        Closed("Closed", -1), // No new order, No Update, No Cancell ****END-AUCT****
        PreOpen("PreOpen", 0), // No new order, No Update, No Cancell ****PRE-AUCT****
        Open("Open", 1), //  New order, Update, cancell *******TRADING*********
        Enquiry("Enquiry", 2), // No new order, No Update, cancell
        Acceptance("Acceptance", 3), //New Order,Price update, No Cancell
        PreClose("PreClose", 4), //Good untill Orders with Limit, No Update, No Cancell ********CLOSE_AUCT*****

        FWDAcceptance("FWDAcceptance", 5),
        FWDPreopen("FWDPreopen", 6),
        FWDTrading("FWDTrading", 7),
        FWDClose("FWDClose", 8),

        TradeAtLast("TradeAtLast", 10), // LTAL  late trading at last

        AUCTMKT_ENQUIRY("AUCTMKT_ENQUIRY", 11),
        AUCTMKT_AUCT("AUCTMKT_AUCT", 12),
        AUCTMKT_END_AUCT("AUCTMKT_END_AUCT", 13),
        AUCTMKT_CLOSE_OF_DAY("AUCTMKT_CLOSE_OF_DAY", 14),
        AUCTMKT_ENDOFDAY("AUCTMKT_ENDOFDAY", 15),
        CLOSE_ACCPT2("CLOSE_ACCPT2", 16);


        private String stringValue;
        private int intValue;

        MarketStatuss(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }

        public int getValue() {
            return intValue;
        }
    }


    public enum TradeTypes {
        All("All", 0),
        Buy("Buy", 1),
        Sell("Sell", 2),
        ShortSell("ShortSell", 5);

        private String stringValue;
        private int intValue;

        TradeTypes(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }


    public enum ShortSellStatus {
        Active("Active", 0),
        Suspended("Suspended", 1),
        None("None", 2);

        private int intValue;
        private String stringValue;

        ShortSellStatus(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }

        public int getValue() {
            return intValue;
        }
    }


    public enum MarketType {
        All("All", 0),
        DSMD("DSMD", 1),
        XKUW("XKUW", 2),
        KWOTC("KWOTC", 3);

        private int intValue;
        private String stringValue;

        MarketType(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }

        public int getValue() {
            return intValue;
        }

    }


    public enum TradingSession {
        /*<string name="MarketSegment_ALL">الكل</string>
        <string name="MarketSegment_REG">الرئيسي</string>
        <string name="MarketSegment_FUNDS">الصناديق</string>*/
        All("All", 0),
        REG("REG", 1),
        FWD("FWD", 2),
        INDX("INDX", 3),
        BUY_IN("BUY_IN", 4),
        OTC_EQTY_KWD("OTC_EQTY_KWD", 5),
        FUNDS("FUNDS", 6),


        All_ar("الكل", 0),
        REG_ar("السوق العادي", 1),
        FUNDS_ar("الصناديق", 6);

        private int intValue;
        private String stringValue;

        TradingSession(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }

        public int getValue() {
            return intValue;
        }

    }


}
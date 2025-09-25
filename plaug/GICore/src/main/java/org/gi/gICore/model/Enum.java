package org.gi.gICore.model;

public class Enum {
    public enum TransactionType {
        DEPOSIT("입금"),
        WITHDRAW("출금"),
        SET("설정"),
        TRANSFER("송금");

        private final String displayName;

        TransactionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}

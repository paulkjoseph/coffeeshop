package com.coffeeshop.datasource;

public enum ReceiptTemplate {
    HEADER("""
            Charlene's Coffee Corner
            Soodring 6 and 33
            8134 Adliswil - Switzerland
            Phone: +41 43 285 2121
            ID: 258.365.965-25
            """),
    BODY("""
            -----------------------------------------------
            DATE: %s
            -----------------------------------------------
            %s
            %s
            -----------------------------------------------
            Subtotal: %s
            Discount: %s
            Total: %s
            -----------------------------------------------
            """),
    FOOTER("""
            Customer: %s
            THANKS FOR SHOPPING WITH US
            """);

    private String content;

    ReceiptTemplate(final String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}

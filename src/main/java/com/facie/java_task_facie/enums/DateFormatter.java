package com.facie.java_task_facie.enums;

import java.time.format.DateTimeFormatter;

public enum DateFormatter {
    DEFAULT(DateTimeFormatter.ofPattern("yyyyMMdd"));

    public final DateTimeFormatter dateTimeFormatter;

    DateFormatter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }
}

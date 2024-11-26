package net.vojko.paurus.models;

import java.sql.Timestamp;

public record TimingInfo(Timestamp startTime, Timestamp endTime, long count, long elapsedTime) {

}

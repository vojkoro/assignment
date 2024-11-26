package net.vojko.paurus.models;

import java.util.Objects;

public record IncomingEvent(String matchId, int marketId, String outcomeId, String specifiers,
                            long internalSequence, long processingTime) {


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IncomingEvent that = (IncomingEvent) o;
        return internalSequence == that.internalSequence && matchId.equalsIgnoreCase(that.matchId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId, internalSequence);
    }
}
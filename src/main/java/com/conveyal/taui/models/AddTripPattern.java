package com.conveyal.taui.models;

import com.mongodb.client.model.geojson.LineString;

import java.util.List;
import com.conveyal.r5.analyst.scenario.AddTripPattern.PatternTimetable;

/**
 * Add a trip pattern.
 */
public class AddTripPattern extends Modification {
    public List<Segment> segments;
    public String name;

    /** for now just using R5 timetables directly */
    public List<PatternTimetable> timetables;

    public String getType() {
        return "add-trip-pattern";
    }

    /** Represents a segment, i.e. the path between control points */
    private static class Segment {
        public LineString geometry;
        public List<Stop> stops;
    }

    private static class Stop {
        public double lat;
        public double lon;

        /** if ID is specified, use existing stop, otherwise, create one */
        public String id;

        public boolean autoGenerated;
    }
}

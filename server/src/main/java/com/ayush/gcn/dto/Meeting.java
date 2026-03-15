package com.ayush.gcn.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Maps the Google Calendar API v3 Event resource.
 * https://developers.google.com/workspace/calendar/api/v3/reference/events#resource
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Meeting {

    // ── Top-level scalar fields ───────────────────────────────────────────────
    private String kind;
    private String etag;
    private String id;
    private String status;
    private String htmlLink;
    private String created;
    private String updated;
    private String summary;
    private String description;
    private String location;
    private String colorId;
    private String iCalUID;
    private Integer sequence;
    private String transparency;
    private String visibility;
    private Boolean anyoneCanAddSelf;
    private Boolean attendeesOmitted;
    private Boolean endTimeUnspecified;
    private Boolean privateCopy;
    private Boolean locked;
    private Boolean guestsCanInviteOthers;
    private Boolean guestsCanModify;
    private Boolean guestsCanSeeOtherGuests;
    private String hangoutLink;
    private String eventType;
    private String recurringEventId;
    private List<String> recurrence;

    // ── Nested objects ────────────────────────────────────────────────────────
    private Person creator;
    private Person organizer;
    private EventDateTime start;
    private EventDateTime end;
    private EventDateTime originalStartTime;
    private List<Attendee> attendees;
    private Reminders reminders;
    private ConferenceData conferenceData;
    private ExtendedProperties extendedProperties;
    private Gadget gadget;
    private Source source;
    private List<Attachment> attachments;
    private WorkingLocationProperties workingLocationProperties;
    private OutOfOfficeProperties outOfOfficeProperties;
    private FocusTimeProperties focusTimeProperties;
    private BirthdayProperties birthdayProperties;

    // ── Convenience accessors used by AlertStorage ────────────────────────────

    /** Returns the event title (v3 field is "summary"). */
    public String getTitle() {
        return summary;
    }

    /**
     * Returns the start time as an ISO-8601 string.
     * Uses dateTime for timed events, falls back to date for all-day events.
     */
    public String getStartTime() {
        if (start == null) return null;
        return start.getDateTime() != null ? start.getDateTime() : start.getDate();
    }

    // ── Inner types ───────────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Person {
        private String id;
        private String email;
        private String displayName;
        private Boolean self;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class EventDateTime {
        private String date;
        private String dateTime;
        private String timeZone;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attendee {
        private String id;
        private String email;
        private String displayName;
        private Boolean organizer;
        private Boolean self;
        private Boolean resource;
        private Boolean optional;
        private String responseStatus;
        private String comment;
        private Integer additionalGuests;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Reminders {
        private Boolean useDefault;
        private List<ReminderOverride> overrides;

        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ReminderOverride {
            private String method;
            private Integer minutes;
        }
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConferenceData {
        private String conferenceId;
        private String signature;
        private String notes;
        private ConferenceSolution conferenceSolution;
        private CreateRequest createRequest;
        private List<EntryPoint> entryPoints;

        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ConferenceSolution {
            private String name;
            private String iconUri;
            private Key key;

            @Data
            @NoArgsConstructor
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Key {
                private String type;
            }
        }

        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class CreateRequest {
            private String requestId;
            private ConferenceSolutionKey conferenceSolutionKey;
            private Status status;

            @Data
            @NoArgsConstructor
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class ConferenceSolutionKey {
                private String type;
            }

            @Data
            @NoArgsConstructor
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class Status {
                private String statusCode;
            }
        }

        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class EntryPoint {
            private String entryPointType;
            private String uri;
            private String label;
            private String pin;
            private String accessCode;
            private String meetingCode;
            private String passcode;
            private String password;
        }
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExtendedProperties {
        @JsonProperty("private")
        private Map<String, String> privateProperties;
        private Map<String, String> shared;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Gadget {
        private String type;
        private String title;
        private String link;
        private String iconLink;
        private Integer width;
        private Integer height;
        private String display;
        private Map<String, String> preferences;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Source {
        private String url;
        private String title;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Attachment {
        private String fileUrl;
        private String title;
        private String mimeType;
        private String iconLink;
        private String fileId;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WorkingLocationProperties {
        private String type;
        private Object homeOffice;
        private CustomLocation customLocation;
        private OfficeLocation officeLocation;

        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class CustomLocation {
            private String label;
        }

        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class OfficeLocation {
            private String buildingId;
            private String floorId;
            private String floorSectionId;
            private String deskId;
            private String label;
        }
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OutOfOfficeProperties {
        private String autoDeclineMode;
        private String declineMessage;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FocusTimeProperties {
        private String autoDeclineMode;
        private String declineMessage;
        private String chatStatus;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BirthdayProperties {
        private String contact;
        private String type;
        private String customTypeName;
    }
}

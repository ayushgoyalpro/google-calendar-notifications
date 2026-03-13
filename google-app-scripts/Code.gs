const USER_KEY = "REPLACE_ME"; // Paste your key from /key here

function syncDailyMeetings() {
  const scriptCache = CacheService.getScriptCache();

  const startOfDay = new Date();
  startOfDay.setHours(0, 0, 0, 0);
  const endOfDay = new Date();
  endOfDay.setHours(23, 59, 59, 999);

  // 1. Get all events from today
  const events = CalendarApp.getDefaultCalendar().getEvents(startOfDay, endOfDay);

  // 2. Map to Meeting DTO shape
  const meetings = events.map(event => ({
    id: event.getId(),
    title: event.getTitle(),
    startTime: event.getStartTime().toISOString(),
    endTime: event.getEndTime().toISOString(),
    location: event.getLocation(),
    description: event.getDescription(),
    status: event.getMyStatus().toString(),
    lastUpdated: event.getLastUpdated().getTime()
  }));

  // 3. Change detection: hash only the meetings so key changes don't cause false diffs
  const meetingsString = JSON.stringify(meetings);
  const currentHash = Utilities.base64Encode(
    Utilities.computeDigest(Utilities.DigestAlgorithm.MD5, meetingsString)
  );
  const cachedHash = scriptCache.get("last_calendar_hash");

  if (currentHash === cachedHash) {
    console.log("No changes detected. Skipping API call.");
    return;
  }

  // 4. Wrap into SyncRequest and POST
  const payload = JSON.stringify({ key: USER_KEY, meetings: meetings });

  const options = {
    method: 'post',
    contentType: 'application/json',
    payload: payload,
    muteHttpExceptions: true,
    headers: {
      "ngrok-skip-browser-warning": "true"
    }
  };

  try {
    const response = UrlFetchApp.fetch("https://google-calendar-notifications.onrender.com/api/sync", options);
    console.log("Status Code: " + response.getResponseCode());
    console.log("Server Response: " + response.getContentText());
    scriptCache.put("last_calendar_hash", currentHash, 21600); // Cache for 6 hours
  } catch (e) {
    console.error("Sync failed: " + e.toString());
  }
}

const USER_KEY = "REPLACE_ME"; // Paste your key from /key here
const CALENDAR_ID = "primary";

function syncDailyMeetings() {
  const scriptCache = CacheService.getScriptCache();

  const startOfDay = new Date();
  startOfDay.setHours(0, 0, 0, 0);
  const endOfDay = new Date();
  endOfDay.setHours(23, 59, 59, 999);

  // 1. Fetch all events for today using Advanced Calendar API v3
  const events = [];
  let pageToken = undefined;
  do {
    const params = {
      timeMin: startOfDay.toISOString(),
      timeMax: endOfDay.toISOString(),
      singleEvents: true,
      orderBy: "startTime",
      maxResults: 2500,
    };
    if (pageToken) params.pageToken = pageToken;

    const response = Calendar.Events.list(CALENDAR_ID, params);
    if (response.items) {
      events.push(...response.items);
    }
    pageToken = response.nextPageToken;
  } while (pageToken);

  // 2. Change detection: hash the raw event list
  const eventsString = JSON.stringify(events);
  const currentHash = Utilities.base64Encode(
    Utilities.computeDigest(Utilities.DigestAlgorithm.MD5, eventsString)
  );
  const cachedHash = scriptCache.get("last_calendar_hash");

  if (currentHash === cachedHash) {
    console.log("No changes detected. Skipping API call.");
    return;
  }

  // 3. POST the raw event objects as-is
  const payload = JSON.stringify({ key: USER_KEY, meetings: events });

  const options = {
    method: "post",
    contentType: "application/json",
    payload: payload,
    muteHttpExceptions: true,
    headers: {
      "ngrok-skip-browser-warning": "true",
    },
  };

  try {
    const response = UrlFetchApp.fetch(
      "https://google-calendar-notifications.onrender.com/api/sync",
      options
    );
    console.log("Status Code: " + response.getResponseCode());
    console.log("Server Response: " + response.getContentText());
    scriptCache.put("last_calendar_hash", currentHash, 21600); // Cache for 6 hours
  } catch (e) {
    console.error("Sync failed: " + e.toString());
  }
}

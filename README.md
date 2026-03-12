# Google Calendar Notifications for Mac

> Because your Google Calendar knows about your meetings. Your Mac doesn't. This fixes that.

Ever been deep in the zone, headphones on, only to surface 20 minutes late to a meeting you definitely had on your calendar? Yeah. This is for you.

**GCN** watches your Google Calendar and fires native macOS notifications before your meetings — no browser tab required, no app to keep open, no excuses.

---

## How it works

```
Google Calendar
      ↓  (Google Apps Script, runs every minute)
   Server
      ↓  (WebSocket)
  Your Mac  →  🔔 Notification
```

That's genuinely it.

---

## Installation (Mac)

You'll need to do three things: install the Mac client, set up the Google Apps Script, and connect them with a key. Should take about 10 minutes.

### Step 1 — Install the Mac client

You'll need [Homebrew](https://brew.sh) installed. If you don't have it, open Terminal and paste the one-liner from the Homebrew website. Once you have it:

```sh
brew tap ayushgoyalpro/brew
brew install gcn
```

Now start it:

```sh
gcn start
```

On first launch it'll ask for your **User Key** — don't have one yet? Read on.

---

### Step 2 — Get your User Key

Your User Key is what tells the server which notifications belong to you. Visit:

**[https://google-calendar-notifications.onrender.com/key](https://google-calendar-notifications.onrender.com/key)**

You'll get a unique key. Copy it and keep it somewhere safe — you'll need it in Step 1 (the `gcn start` prompt) and Step 3.

> **Note:** The server runs on Render's free tier, so the first visit might take ~30 seconds to wake up. Completely normal. Make yourself a coffee.

---

### Step 3 — Set up Google Apps Script

This is the part that watches your calendar and pings the server.

1. Go to **[script.google.com](https://script.google.com)** and click **New project**
2. Delete whatever's in there and paste the contents of [`server/google-apps-scripts/Code.gs`](server/google-apps-scripts/Code.gs)
3. On line 1, replace `REPLACE_ME` with your User Key from Step 2:
   ```js
   const USER_KEY = "your-key-here";
   ```
4. Click **Save** (the floppy disk icon, yes they still use that)
5. Set up the trigger:
   - Click the **clock icon** in the left sidebar (Triggers)
   - Click **Add Trigger** (bottom right)
   - Set it to run `syncDailyMeetings` → **Time-driven** → **Minute timer** → **Every minute**
   - Click **Save** and approve the permissions Google asks for

That's it. Your calendar is now connected.

---

### Managing the client

```sh
gcn start    # start the client
gcn stop     # stop it
gcn status   # check if it's running
gcn setup    # re-enter your User Key if needed
```

Logs live at `/tmp/gcn-client.log` if something seems off.

---

## Troubleshooting

**No notifications showing up**
- Check `gcn status` — is it running?
- Check that your Google Apps Script trigger is active (script.google.com → your project → Triggers)
- Check `/tmp/gcn-client.log` for errors
- Make sure your User Key is the same in both the script and the client

**`gcn` command not found after install**
- Run `brew link gcn` or restart your terminal

**Notifications show up but I'm still late to meetings**
- That's on you. The software is working fine.

---

## Contributing

Found a bug? Have an idea? PRs are very welcome.

The project is a monorepo with three parts:

| Directory | What it is |
|---|---|
| `client/` | Java/Spring Boot WebSocket client for Mac |
| `server/` | Java/Spring Boot server, deployed on Render |
| `server/google-apps-scripts/` | Google Apps Script that polls your calendar |

### Running locally

You'll need Java 21 and Maven.

```sh
# Run the server
./mvnw spring-boot:run -f server/pom.xml

# Run the client (in another terminal)
./mvnw spring-boot:run -f client/pom.xml
```

Set `USER_KEY` in `client/.env` (see `client/.env.example`).

### Releasing (maintainers)

Tag a commit and push — GitHub Actions handles the rest:

```sh
git tag -a v1.2.3 -m "what changed"
git push origin v1.2.3
```

This builds the JAR, creates a GitHub Release, and updates the Homebrew formula automatically.

---

## Why does this exist

macOS notifications from browser-based Google Calendar are unreliable, require keeping a browser tab open, and frankly just don't feel right. Native is better.

Also, it was a fun thing to build.

---

## License

MIT — do whatever you want with it.
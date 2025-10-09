# 🌸 Clematis Discord Bot

A lightweight Discord bot built with Kotlin.
It provides simple utility and fun commands like greetings and voice chat time tracking.

---

## 📦 Features

### `/hello`
> Replies with simple greeting.

### `/day-hello {set | unset}`
> Sends a cheerful “Good Morning!” message every day.

*⚠️ This command has not been fully tested yet and may not work as expected.*

### `/voicetime [@user]`
> Shows how long you or a mentioned user has been in voice chat.

### `/voicerank`
> Displays the top 10 users by total VC time.

---

## 🛠️ Setup

1. **Download the latest JAR file** from the [Releases](../../releases) page.
2. Run the bot once:
   
   ```bash
   java -jar Clematis-x.y.z.jar
   ```

3. Open the generated `bot.properties` file  and fill in your **bot token** and **guild ID**.
4. Run the bot again.

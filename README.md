# The Screening Room

A Spring Boot movie ticket booking application with a responsive cinema storefront. Browse films and future showtimes, pick seats from a live availability map, book and look up reservations, or cancel a booking. Sample movies and a rolling seven-day schedule are added automatically on first startup, with ticket prices displayed in Indian rupees (INR). The local H2 database keeps bookings on disk; no MySQL or Docker setup is required.

The sample checkout confirms seat reservations only. It does not process payment or send email. This project is a development/demo application, not a production-ready cinema operation system.

## Requirements

- Java 21 or later

## Run the application

In PowerShell, open this project directory and run:

```powershell
.\gradlew.bat bootRun
```

Open [http://localhost:8080](http://localhost:8080). The H2 database is stored under `data/`. Stop the application with `Ctrl+C`.

If Gradle reports that it is using Java 11, set `JAVA_HOME` to your installed JDK before starting Gradle:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21.0.2'
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
.\gradlew.bat bootRun
```

## API

The API is public in this demo; there is no customer account system.

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/movies` | List films; optional `q` and `genre` filters |
| `GET` | `/api/showings?date=YYYY-MM-DD` | List the day's showtimes; optional `movieId` filter |
| `GET` | `/api/showings/{id}/seats` | Show seat availability for a screening |
| `POST` | `/api/bookings` | Reserve 1–8 available seats |
| `GET` | `/api/bookings/{reference}?email=...` | Find a booking using its reference and booking email |
| `DELETE` | `/api/bookings/{reference}?email=...` | Cancel a future booking and release its seats |

Example booking request:

```json
{
  "customerName": "Taylor Example",
  "email": "taylor@example.com",
  "showingId": 1,
  "seatLabels": ["C5", "C6"]
}
```

The successful `201 Created` response includes a booking reference, reserved seats, screening details, and total (INR). The same seat cannot be reserved twice for a screening. Use the returned reference and email to retrieve or cancel the reservation.

## Tests

```powershell
.\gradlew.bat test
```

Tests use an isolated in-memory H2 database and do not require the local persistent database or Docker.

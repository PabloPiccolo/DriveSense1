1.REJESTRACJA:
curl -i -X POST "BASE_URL/users/register" \
-H "Content-Type: application/json" \
-d '{
  "login":"endpointtest",
  "email":"endpointtest@hivesense.pl",
  "password":"NoweHaslo123"
}'
otrzymujemy:
{
  "id": 25,
  "login": "endpointtest",
  "email": "endpointtest@hivesense.pl",
  "apiKey": "1955ea45acff"
}



2.Logowanie
curl -i -X POST "BASE_URL/users/login" \
-H "Content-Type: application/json" \
-d '{
  "login":"endpointtest",
  "password":"NoweHaslo123"
}'

Backend zwraca JWT
{
  "token": "eyJhbGciOi..."
}

3. Pobranie urządzeń użytkownika
curl -i "BASE_URL/app/devices?apiKey=1955ea45acff" \
-H "Authorization: Bearer TOKEN"

Pobiera urządzenia należące do użytkownika, przykładowa odpowiedź
[
  {
    "id":12,
    "deviceName":"TestDevice",
    "userId":25
  }
]

4. ESP WYSYŁA TEMPERATURY
curl -i -X POST "BASE_URL/temperatures" \
-H "Content-Type: application/json" \
-d '{
  "apiKey":"1955ea45acff",
  "deviceName":"TestDevice",
  "temperature":35
}'



5. Pobranie wszystkich temperatur konkretnego urządzenia GET /temperatures/device/{deviceId}
curl -i "BASE_URL/temperatures/device/12" \
-H "Authorization: Bearer TOKEN"

Przykładowa odpowiedź:
[
  {
    "deviceId":12,
    "id":99,
    "measuredAt":"2026-09-29T11:37:19",
    "temperature":35.0
  },
  {
    "deviceId":12,
    "id":100,
    "measuredAt":"2026-09-29T11:47:22",
    "temperature":35.0
  }
]

6. Pobranie temperatur urządzenia z konkretnego dnia
curl -i "BASE_URL/app/devices/12/temperatures?date=2026-09-29" \
-H "Authorization: Bearer TOKEN"

Odpowiedź, React rysuje z tego wykres:
[
  {
    "deviceId":12,
    "id":99,
    "measuredAt":"2026-09-29T11:37:19",
    "temperature":35.0
  },
  {
    "deviceId":12,
    "id":100,
    "measuredAt":"2026-09-29T11:47:22",
    "temperature":35.0
  },
  {
    "deviceId":12,
    "id":101,
    "measuredAt":"2026-09-29T12:00:20",
    "temperature":35.0
  }
]


7. Usunięcie urządzenia DELETE /app/devices/{deviceName}
curl -i -X DELETE "BASE_URL/app/devices/TestDevice?apiKey=1955ea45acff" \
-H "Authorization: Bearer TOKEN"

8. Usuwanie konta
curl -i -X DELETE "BASE_URL/users/me" \
-H "Authorization: Bearer TOKEN"
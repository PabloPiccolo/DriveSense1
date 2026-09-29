# HiveSense — notatki do nauki Spring Boot

## 1. Co zbudowaliśmy?

HiveSense to backend napisany w:

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL

Projekt działa podobnie do mojego wcześniejszego backendu Express.js.

Główne zadania:

- rejestracja użytkownika,
- logowanie użytkownika,
- JWT,
- API Key użytkownika,
- obsługa urządzeń,
- odbieranie temperatur z ESP32,
- zapisywanie temperatur w MySQL,
- pobieranie urządzeń,
- pobieranie temperatur,
- usuwanie urządzenia,
- usuwanie konta.

Najważniejszy podział:

Aplikacja React Native:
    ↓
JWT

ESP32:
    ↓
apiKey

---

# 2. Jak działa cały program?

Najważniejszy schemat:

HTTP request
    ↓
Controller
    ↓
Service
    ↓
Repository
    ↓
MySQL

Czyli:

Controller = odbiera żądanie HTTP

Service = wykonuje logikę programu

Repository = rozmawia z bazą danych

Entity = opisuje dane przechowywane w bazie

DTO = opisuje dane przychodzące z HTTP

Security = pilnuje dostępu do endpointów

---

# 3. Struktura projektu

Najważniejsze foldery:

src/main/java/com/hivesense/hivesense/

├── config/
│   └── SecurityConfig.java
│
├── controller/
│   ├── UserController.java
│   ├── DeviceController.java
│   └── TemperatureController.java
│
├── dto/
│   └── TemperatureRequest.java
│
├── entity/
│   ├── User.java
│   ├── Device.java
│   └── Temperature.java
│
├── repository/
│   ├── UserRepository.java
│   ├── DeviceRepository.java
│   └── TemperatureRepository.java
│
├── security/
│   └── JwtAuthenticationFilter.java
│
├── service/
│   ├── UserService.java
│   ├── DeviceService.java
│   ├── TemperatureService.java
│   └── JwtService.java
│
└── HiveSenseApplication.java

---

# 4. Entity

Entity opisuje dane znajdujące się w bazie danych.

Mamy trzy główne encje:

User
Device
Temperature

Czyli:

User
 ↓
Device
 ↓
Temperature

---

# 5. User

User odpowiada tabeli:

users

Najważniejsze pola:

id
login
email
password
apiKey

Przykład:

User:

id = 25
login = endpointtest
email = endpointtest@hivesense.pl
apiKey = 1955ea45acff

---

# 6. Device

Device odpowiada tabeli:

devices

Pola:

id
userId
deviceName

Przykład:

id = 12
userId = 25
deviceName = TestDevice

Czyli:

użytkownik 25
    ↓
urządzenie 12
    ↓
TestDevice

---

# 7. Temperature

Temperature odpowiada tabeli:

temperatures

Pola:

id
deviceId
temperature
measuredAt

Przykład:

id = 101
deviceId = 12
temperature = 35.0
measuredAt = 2026-09-29T12:00:20

Czyli:

User #25
    ↓
Device #12
    ↓
Temperature #101

---

# 8. Co robi @Entity?

Jeżeli mamy:

@Entity

Spring/JPA wie, że dana klasa jest encją i ma być powiązana z tabelą w bazie.

Przykład:

@Entity
@Table(name = "devices")
public class Device {
}

Oznacza:

Klasa Device odpowiada tabeli devices.

---

# 9. Co robi @Id?

@Id

oznacza klucz główny tabeli.

Przykład:

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

Czyli:

id jest kluczem głównym i jego wartość generuje baza danych.

---

# 10. Repository

Repository służy do komunikacji z bazą danych.

Przykład:

public interface UserRepository extends JpaRepository<User, Long> {
}

Dzięki JpaRepository dostajemy gotowe metody, np.:

findAll()
findById()
save()
delete()
deleteById()

Nie musimy pisać ich ręcznie.

---

# 11. Spring Data JPA

Jedna z ważnych rzeczy w Springu:

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByApiKey(String apiKey);

}

Spring rozumie nazwę:

findByApiKey

jako:

znajdź użytkownika po apiKey.

Czyli:

find
By
ApiKey

Podobnie:

findByUserIdAndDeviceName

oznacza:

znajdź urządzenie po userId ORAZ deviceName.

To jest jedna z rzeczy, która na początku wygląda jak magia Springa.

---

# 12. Service

Service zawiera logikę programu.

Controller nie powinien robić całej logiki.

Przykładowo:

Controller
    ↓
TemperatureService
    ↓
UserRepository
DeviceRepository
TemperatureRepository

Controller mówi:

"otrzymałem temperaturę"

Service wykonuje:

1. znajdź użytkownika po apiKey
2. znajdź urządzenie
3. jeżeli nie istnieje — utwórz
4. zapisz temperaturę

---

# 13. @Service

Jeżeli mamy:

@Service
public class TemperatureService {
}

Spring wie:

To jest klasa będąca serwisem.

Spring tworzy jej obiekt i może go przekazywać innym klasom.

---

# 14. Dependency Injection

W projekcie korzystamy z constructor injection.

Przykład:

private final TemperatureService temperatureService;

public TemperatureController(
        TemperatureService temperatureService
) {
    this.temperatureService = temperatureService;
}

Spring sam przekazuje TemperatureService do TemperatureController.

Nie musimy pisać:

new TemperatureService()

Spring robi to za nas.

Jeżeli klasa ma jeden konstruktor, nie potrzebujemy @Autowired.

---

# 15. Controller

Controller obsługuje HTTP.

Przykład:

@RestController
@RequestMapping("/temperatures")
public class TemperatureController {
}

@RestController mówi Springowi:

Ta klasa obsługuje endpointy HTTP.

@RequestMapping("/temperatures") mówi:

Wszystkie endpointy tej klasy zaczynają się od:

/temperatures

---

# 16. TemperatureController

Mamy:

@GetMapping

czyli:

GET /temperatures

Pobiera wszystkie temperatury.

Mamy również:

@GetMapping("/device/{deviceId}")

czyli:

GET /temperatures/device/12

Pobiera temperatury urządzenia o ID 12.

---

# 17. POST temperatury

Mamy:

@PostMapping

czyli:

POST /temperatures

Przyjmuje JSON:

{
    "apiKey": "1955ea45acff",
    "deviceName": "TestDevice",
    "temperature": 35
}

---

# 18. TemperatureRequest

TemperatureRequest jest DTO.

DTO = Data Transfer Object.

Czyli obiekt służący do przenoszenia danych pomiędzy HTTP a aplikacją.

Mamy:

public class TemperatureRequest {

    @NotBlank
    private String apiKey;

    @NotBlank
    private String deviceName;

    @NotNull
    @Min(-50)
    @Max(100)
    private Double temperature;

}

---

# 19. Walidacja

@NotBlank

oznacza:

wartość tekstowa nie może być pusta.

@NotNull

oznacza:

wartość nie może być null.

@Min(-50)

oznacza:

temperatura musi być >= -50.

@Max(100)

oznacza:

temperatura musi być <= 100.

Dlatego test:

"temperature": 1000

został odrzucony.

To było prawidłowe działanie programu.

---

# 20. @Valid

W Controllerze mamy:

@Valid @RequestBody TemperatureRequest request

@RequestBody:

JSON z HTTP zostaje zamieniony na obiekt Java.

@Valid:

Spring sprawdza reguły walidacji z TemperatureRequest.

---

# 21. Najważniejszy przepływ temperatury

ESP32 wysyła:

{
    "apiKey": "1955ea45acff",
    "deviceName": "TestDevice",
    "temperature": 35
}

do:

POST /temperatures

Następnie:

ESP32
 ↓
POST /temperatures
 ↓
TemperatureController
 ↓
TemperatureRequest
 ↓
walidacja
 ↓
TemperatureService
 ↓
UserRepository
 ↓
znajdź użytkownika po apiKey
 ↓
DeviceRepository
 ↓
znajdź urządzenie
 ↓
jeżeli nie istnieje → utwórz urządzenie
 ↓
TemperatureRepository
 ↓
zapis temperatury
 ↓
MySQL

---

# 22. Dlaczego ESP32 nie używa JWT?

Zdecydowaliśmy, że ESP32 nie będzie dostawało JWT.

ESP32 używa apiKey.

Czyli:

ESP32
 ↓
apiKey
 ↓
POST /temperatures

Przykładowo w ESP32 możemy mieć:

const char* apiKey = "1955ea45acff";

I przy każdym pomiarze ESP32 wysyła:

{
    "apiKey": "1955ea45acff",
    "deviceName": "TestDevice",
    "temperature": 35
}

JWT jest potrzebny głównie do komunikacji aplikacji użytkownika z backendem.

---

# 23. SecurityConfig

SecurityConfig mówi Spring Security:

które endpointy są publiczne,
a które wymagają uwierzytelnienia.

Mamy:

.requestMatchers(
        "/users/register",
        "/users/login",
        "/temperatures"
).permitAll()

Czyli:

/users/register
    ↓
bez JWT

/users/login
    ↓
bez JWT

/temperatures
    ↓
bez JWT

Pozostałe endpointy:

.anyRequest().authenticated()

czyli wymagają uwierzytelnienia.

---

# 24. Dlaczego /temperatures jest permitAll?

Ponieważ ESP32 nie wysyła JWT.

ESP32 wysyła:

apiKey

Dlatego Spring Security nie może wymagać JWT dla tego endpointu.

Dalsza logika sprawdza apiKey w TemperatureService.

Czyli:

Spring Security
    ↓
przepuszcza /temperatures
    ↓
TemperatureController
    ↓
TemperatureService
    ↓
sprawdzenie apiKey

---

# 25. JwtAuthenticationFilter

JwtAuthenticationFilter jest filtrem bezpieczeństwa.

Sprawdza nagłówek:

Authorization

Przykład:

Authorization: Bearer eyJ...

Jeżeli request zawiera JWT:

JWT Filter
    ↓
odczytuje token
    ↓
sprawdza token
    ↓
odczytuje login
    ↓
ustawia użytkownika w SecurityContext
    ↓
request idzie dalej

---

# 26. Wyjątek dla ESP32

W filtrze mamy:

if (request.getRequestURI().equals("/temperatures")) {
    filterChain.doFilter(request, response);
    return;
}

Czyli:

Jeżeli request idzie na:

/temperatures

to nie wymagamy JWT.

Przepuszczamy request dalej.

Dzięki temu ESP32 może wysłać pomiar tylko z apiKey.

---

# 27. JWT

Po zalogowaniu użytkownik otrzymuje token:

{
    "token": "eyJ..."
}

Potem aplikacja wysyła:

Authorization: Bearer eyJ...

JWT pozwala backendowi rozpoznać zalogowanego użytkownika.

---

# 28. DeviceController

Endpoint:

GET /app/devices?apiKey=1955ea45acff

służy do pobrania urządzeń użytkownika.

Najpierw:

apiKey
 ↓
UserRepository
 ↓
User

Potem:

User.id
 ↓
DeviceService
 ↓
devices

Przykładowa odpowiedź:

[
    {
        "deviceName": "TestDevice",
        "id": 12,
        "userId": 25
    }
]

---

# 29. Skąd wzięło się deviceId = 12?

Z odpowiedzi backendu.

Backend zwrócił:

[
    {
        "deviceName": "TestDevice",
        "id": 12,
        "userId": 25
    }
]

Dlatego później użyliśmy:

/app/devices/12/temperatures

12 to ID urządzenia zapisane w bazie.

---

# 30. Pobieranie temperatur urządzenia

Endpoint:

GET /app/devices/{deviceId}/temperatures?date=YYYY-MM-DD

Przykład:

GET /app/devices/12/temperatures?date=2026-09-29

Znaczenie:

12
    ↓
deviceId

2026-09-29
    ↓
data pomiarów

Controller:

@PathVariable Long deviceId

pobiera:

12

A:

@RequestParam String date

pobiera:

2026-09-29

---

# 31. Odpowiedź z temperaturami

Otrzymaliśmy:

[
    {
        "deviceId": 12,
        "id": 99,
        "measuredAt": "2026-09-29T11:37:19",
        "temperature": 35.0
    },
    {
        "deviceId": 12,
        "id": 100,
        "measuredAt": "2026-09-29T11:47:22",
        "temperature": 35.0
    },
    {
        "deviceId": 12,
        "id": 101,
        "measuredAt": "2026-09-29T12:00:20",
        "temperature": 35.0
    }
]

Czyli urządzenie #12 miało trzy pomiary.

---

# 32. Usuwanie urządzenia

Endpoint:

DELETE /app/devices/{deviceName}?apiKey=...

Przykład:

DELETE /app/devices/TestDevice?apiKey=1955ea45acff

Najpierw backend sprawdza:

apiKey
 ↓
User
 ↓
userId

Potem:

userId + deviceName
 ↓
DeviceService
 ↓
usunięcie urządzenia

---

# 33. Usuwanie konta

Usunięcie konta jest zabezpieczone JWT.

Schemat:

React Native
 ↓
DELETE /users/me
 ↓
Authorization: Bearer JWT
 ↓
Spring Security
 ↓
UserController
 ↓
UserService
 ↓
MySQL

---

# 34. Najważniejsze endpointy

## Rejestracja

POST /users/register

Nie wymaga JWT.

---

## Logowanie

POST /users/login

Nie wymaga JWT.

Zwraca:

{
    "token": "..."
}

---

## Wysyłanie temperatury

POST /temperatures

Nie wymaga JWT.

Wymaga apiKey w JSON.

Przykład:

{
    "apiKey": "1955ea45acff",
    "deviceName": "TestDevice",
    "temperature": 35
}

To jest endpoint dla ESP32.

---

## Pobieranie urządzeń

GET /app/devices?apiKey=1955ea45acff

Wymaga JWT.

---

## Pobieranie temperatur urządzenia

GET /app/devices/12/temperatures?date=2026-09-29

Wymaga JWT.

---

## Pobieranie wszystkich temperatur

GET /temperatures

---

## Pobieranie temperatur po deviceId

GET /temperatures/device/12

---

## Usuwanie urządzenia

DELETE /app/devices/TestDevice?apiKey=1955ea45acff

---

## Usuwanie konta

DELETE /users/me

Wymaga JWT.

---

# 35. Co oznaczają GET, POST i DELETE?

GET

Pobierz dane.

Przykład:

GET /app/devices

---

POST

Wyślij / utwórz dane.

Przykład:

POST /temperatures

---

DELETE

Usuń dane.

Przykład:

DELETE /app/devices/TestDevice

---

# 36. Co oznacza @GetMapping?

@GetMapping

oznacza:

ten endpoint reaguje na HTTP GET.

Przykład:

@GetMapping("/device/{deviceId}")

---

# 37. Co oznacza @PostMapping?

@PostMapping

oznacza:

ten endpoint reaguje na HTTP POST.

Przykład:

@PostMapping

---

# 38. Co oznacza @DeleteMapping?

@DeleteMapping

oznacza:

ten endpoint reaguje na HTTP DELETE.

---

# 39. @PathVariable

Przykład:

@GetMapping("/device/{deviceId}")

Jeżeli wywołamy:

/device/12

to:

@PathVariable Long deviceId

otrzyma:

12

---

# 40. @RequestParam

Przykład:

/devices?apiKey=1955ea45acff

Kod:

@RequestParam String apiKey

otrzyma:

1955ea45acff

---

# 41. @RequestBody

Przykład JSON:

{
    "temperature": 35
}

Kod:

@RequestBody TemperatureRequest request

mówi Springowi:

zamień JSON na obiekt TemperatureRequest.

---

# 42. Hibernate

Spring używa Hibernate do komunikacji z bazą.

Schemat:

Java
 ↓
JPA
 ↓
Hibernate
 ↓
JDBC
 ↓
MySQL

Dlatego możemy napisać:

temperatureRepository.save(temperature);

zamiast ręcznie pisać:

INSERT INTO temperatures ...

---

# 43. Baza danych

Mamy:

users
devices
temperatures

Relacja:

users
  |
  | userId
  ↓
devices
  |
  | deviceId
  ↓
temperatures

Przykład:

users:

id = 25

devices:

id = 12
userId = 25

temperatures:

id = 101
deviceId = 12
temperature = 35

Czyli:

User #25
    ↓
Device #12
    ↓
Temperature #101

---

# 44. Co oznacza 403?

403 = Forbidden

Spring Security nie pozwolił wykonać operacji.

Podczas tworzenia endpointu temperatur mieliśmy problem:

ESP32 wysyłało:

POST /temperatures

bez JWT.

Ale Spring Security wymagał uwierzytelnienia.

Dlatego dodaliśmy:

"/temperatures"

do:

permitAll()

oraz wyjątek w JwtAuthenticationFilter.

Po tej zmianie ESP32 może wysyłać temperaturę z apiKey.

---

# 45. Co oznacza 401?

401 = Unauthorized

Najczęściej oznacza problem z uwierzytelnieniem.

Np.:

- brak JWT,
- nieprawidłowy JWT,
- wygasły JWT.

---

# 46. Co oznacza 400?

400 = Bad Request

Request zawiera nieprawidłowe dane.

Przykład:

temperature = 1000

gdy mamy:

@Max(100)

---

# 47. Najważniejszy schemat całego HiveSense

ESP32
    |
    | Wi-Fi
    |
    | POST /temperatures
    | apiKey + deviceName + temperature
    ↓
HiveSense Spring Boot
    |
    ↓
TemperatureController
    |
    ↓
TemperatureService
    |
    ├── UserRepository
    |
    ├── DeviceRepository
    |
    └── TemperatureRepository
    |
    ↓
MySQL
    |
    ↓
temperatures


Aplikacja React Native:

React Native
    |
    | login
    ↓
/users/login
    |
    ↓
JWT
    |
    ↓
Authorization: Bearer JWT
    |
    ↓
Spring Security
    |
    ↓
Controller
    |
    ↓
Service
    |
    ↓
Repository
    |
    ↓
MySQL

---

# 48. Express vs Spring

W Express miałem:

route
 ↓
controller
 ↓
service
 ↓
database

W Spring mam:

Controller
 ↓
Service
 ↓
Repository
 ↓
database

Spring dodatkowo automatycznie zarządza wieloma rzeczami.

Np.:

@RestController

Spring wie:

to jest kontroler HTTP.

@Service

Spring wie:

to jest serwis.

@Repository

Spring wie:

to jest warstwa dostępu do danych.

@Entity

Hibernate wie:

to jest encja powiązana z bazą.

JpaRepository

Spring daje gotowe operacje na bazie.

---

# 49. Dlaczego Spring wydaje się trudniejszy?

W Express większość rzeczy pisałem ręcznie.

W Springu dużo rzeczy jest robionych przez framework.

Np.:

findByApiKey()

Spring sam interpretuje nazwę metody.

Dlatego na początku może się wydawać, że Spring robi jakieś rzeczy "magicznie".

Ale w rzeczywistości trzeba poznać reguły Springa.

---

# 50. Najważniejsze rzeczy do zapamiętania

Nie muszę na początku pamiętać całego kodu.

Mam przede wszystkim rozumieć:

Controller
    =
odbiera HTTP

Service
    =
logika programu

Repository
    =
baza danych

Entity
    =
model danych z bazy

DTO
    =
dane przychodzące z HTTP

SecurityConfig
    =
zasady dostępu

JwtAuthenticationFilter
    =
sprawdza JWT

---

# 51. Najważniejszy przykład do zapamiętania

ESP32 wysyła:

{
    "apiKey": "1955ea45acff",
    "deviceName": "TestDevice",
    "temperature": 35
}

↓

POST /temperatures

↓

TemperatureController

↓

TemperatureService

↓

znajdź User po apiKey

↓

znajdź lub utwórz Device

↓

zapisz Temperature

↓

MySQL

---

# 52. Co będziemy robić dalej?

Backend HiveSense działa.

Następny etap:

React Native

Czyli:

Spring Boot
    ↓
API
    ↓
React Native
    ↓
logowanie
    ↓
lista urządzeń
    ↓
wybór urządzenia
    ↓
pobieranie temperatur
    ↓
wykres temperatur

Później:

ESP32
    ↓ Wi-Fi
HiveSense
    ↓
MySQL
    ↓
React Native
    ↓
wykres

Wtedy będziemy mieli kompletny system IoT.

---

# 53. Jedno zdanie do zapamiętania

Controller przyjmuje żądanie.

Service wykonuje logikę.

Repository rozmawia z bazą.

Entity opisuje dane z bazy.

DTO opisuje dane przychodzące.

Security pilnuje dostępu.

JWT identyfikuje zalogowanego użytkownika.

apiKey pozwala urządzeniu, np. ESP32, wysyłać pomiary.




--------------------------------------------------------------------------------------------
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
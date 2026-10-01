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
- usuwanie konta,
- zmiana hasła.

Najważniejszy podział:

React Native:

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

│   ├── TemperatureRequest.java

│   ├── LoginRequest.java

│   ├── RegisterRequest.java

│   ├── LoginResponse.java

│   └── ChangePasswordRequest.java

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

Po zalogowaniu użytkownik otrzymuje token.

Przykładowa odpowiedź:

{
    "message": "Zalogowano pomyślnie",
    "user": {
        "id": 26,
        "login": "testspring",
        "email": "testspring@test.pl",
        "apiKey": "74f175f2cf49",
        "token": "eyJ..."
    }
}

Potem aplikacja wysyła:

Authorization: Bearer eyJ...

JWT pozwala backendowi rozpoznać zalogowanego użytkownika.


---

# 28. Kompatybilność z wcześniejszym Express.js

Podczas migracji z Express.js bardzo ważne jest, aby Spring zwracał JSON w takiej samej strukturze, jakiej oczekuje aplikacja React Native.

Stary Express przy rejestracji zwracał:

{
    "message": "Konto zostało utworzone",
    "user": {
        "id": 25,
        "login": "endpointtest",
        "email": "endpointtest@hivesense.pl",
        "apiKey": "1955ea45acff"
    }
}

Dlatego Spring został dostosowany do tej struktury.

Rejestracja:

POST /users/register

zwraca:

{
    "user": {
        "id": 27,
        "login": "compatibilitytest",
        "email": "compatibility@test.pl",
        "apiKey": "1c90d05f5a5c"
    },
    "message": "Konto zostało utworzone"
}

Kolejność pól JSON nie ma znaczenia.

Najważniejsze jest to, że istnieją odpowiednie pola:

message

user

oraz wewnątrz user:

id

login

email

apiKey


---

# 29. UserController — /users/me

Endpoint:

GET /users/me

w Springu zwraca:

{
    "user": {
        "id": 13,
        "login": "kinga",
        "email": "kinga@kinga.pl",
        "apiKey": "e6a1b6517e4c"
    }
}

Jest to zgodne ze starym Expressowym kontrolerem, który zwracał:

{
    "user": user
}

Endpoint wymaga JWT.


---

# 30. UserController — logowanie

Endpoint:

POST /users/login

Spring zwraca:

{
    "message": "Zalogowano pomyślnie",
    "user": {
        "id": 26,
        "login": "testspring",
        "email": "testspring@test.pl",
        "apiKey": "74f175f2cf49",
        "token": "eyJ..."
    }
}

Jest to zgodne ze starym Expressowym:

res.status(200).json({
    message: "Zalogowano pomyślnie",
    user,
});

Token znajduje się wewnątrz:

user.token


---

# 31. Zmiana hasła

Endpoint:

PUT /users/change-password

Wymaga JWT.

Przykładowy request:

{
    "oldPassword": "test123",
    "newPassword": "nowe123"
}

Nagłówek:

Authorization: Bearer TOKEN

Po poprawnej zmianie:

Hasło zostało zmienione

Jeżeli stare hasło jest nieprawidłowe:

Nieprawidłowy login lub hasło


---

# 32. Usuwanie konta

Endpoint:

DELETE /users/me

Wymaga JWT.

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

Przed usunięciem użytkownika usuwane są:

1. temperatury jego urządzeń

2. jego urządzenia

3. użytkownik


---

# 33. DeviceController

Endpoint:

GET /app/devices?apiKey=1955ea45acff

służy do pobrania urządzeń użytkownika.

Schemat:

apiKey

    ↓

UserRepository

    ↓

User

    ↓

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

# 34. Skąd wzięło się deviceId = 12?

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

# 35. Pobieranie temperatur urządzenia

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

# 36. Odpowiedź z temperaturami

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

# 37. Usuwanie urządzenia

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

# 38. Pobieranie wszystkich temperatur

Endpoint:

GET /temperatures

Pobiera wszystkie temperatury.


---

# 39. Pobieranie temperatur po deviceId

Endpoint:

GET /temperatures/device/12

Pobiera temperatury urządzenia o ID 12.


---

# 40. Co oznaczają GET, POST, PUT i DELETE?

GET

Pobierz dane.

Przykład:

GET /app/devices


POST

Wyślij / utwórz dane.

Przykład:

POST /temperatures


PUT

Zmień istniejące dane.

Przykład:

PUT /users/change-password


DELETE

Usuń dane.

Przykład:

DELETE /app/devices/TestDevice


---

# 41. Co oznacza @GetMapping?

@GetMapping

oznacza:

ten endpoint reaguje na HTTP GET.

Przykład:

@GetMapping("/device/{deviceId}")


---

# 42. Co oznacza @PostMapping?

@PostMapping

oznacza:

ten endpoint reaguje na HTTP POST.

Przykład:

@PostMapping


---

# 43. Co oznacza @PutMapping?

@PutMapping

oznacza:

ten endpoint reaguje na HTTP PUT.

Przykład:

@PutMapping("/change-password")


---

# 44. Co oznacza @DeleteMapping?

@DeleteMapping

oznacza:

ten endpoint reaguje na HTTP DELETE.


---

# 45. @PathVariable

Przykład:

@GetMapping("/device/{deviceId}")

Jeżeli wywołamy:

/device/12

to:

@PathVariable Long deviceId

otrzyma:

12


---

# 46. @RequestParam

Przykład:

/devices?apiKey=1955ea45acff

Kod:

@RequestParam String apiKey

otrzyma:

1955ea45acff


---

# 47. @RequestBody

Przykład JSON:

{
    "temperature": 35
}

Kod:

@RequestBody TemperatureRequest request

mówi Springowi:

zamień JSON na obiekt TemperatureRequest.


---

# 48. Hibernate

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

# 49. Baza danych

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

# 50. Co oznacza 403?

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

# 51. Co oznacza 401?

401 = Unauthorized

Najczęściej oznacza problem z uwierzytelnieniem.

Np.:

- brak JWT,
- nieprawidłowy JWT,
- wygasły JWT.


---

# 52. Co oznacza 400?

400 = Bad Request

Request zawiera nieprawidłowe dane.

Przykład:

temperature = 1000

gdy mamy:

@Max(100)


---

# 53. Co oznacza 500?

500 = Internal Server Error

Oznacza, że podczas obsługi requestu wystąpił błąd po stronie backendu.

Przykładowo:

- błąd w Service,
- błąd zapytania do bazy,
- błąd konfiguracji,
- nieobsłużony wyjątek.

Przy 500 trzeba sprawdzić logi Spring Boot.


---

# 54. Najważniejszy schemat całego HiveSense

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

# 55. Express vs Spring

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

# 56. Dlaczego Spring wydaje się trudniejszy?

W Express większość rzeczy pisałem ręcznie.

W Springu dużo rzeczy jest robionych przez framework.

Np.:

findByApiKey()

Spring sam interpretuje nazwę metody.

Dlatego na początku może się wydawać, że Spring robi jakieś rzeczy "magicznie".

Ale w rzeczywistości trzeba poznać reguły Springa.


---

# 57. Najważniejsze rzeczy do zapamiętania

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


JWT

    =

identyfikuje zalogowanego użytkownika


apiKey

    =

pozwala urządzeniu, np. ESP32, wysyłać pomiary


---

# 58. Najważniejszy przykład do zapamiętania

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

# 59. Endpointy HiveSense

## 1. Rejestracja

POST /users/register

Nie wymaga JWT.

Request:

{
    "login": "endpointtest",
    "email": "endpointtest@hivesense.pl",
    "password": "NoweHaslo123"
}

Odpowiedź:

{
    "user": {
        "id": 25,
        "login": "endpointtest",
        "email": "endpointtest@hivesense.pl",
        "apiKey": "1955ea45acff"
    },
    "message": "Konto zostało utworzone"
}


---

## 2. Logowanie

POST /users/login

Nie wymaga JWT.

Request:

{
    "login": "endpointtest",
    "password": "NoweHaslo123"
}

Odpowiedź:

{
    "message": "Zalogowano pomyślnie",
    "user": {
        "id": 25,
        "login": "endpointtest",
        "email": "endpointtest@hivesense.pl",
        "apiKey": "1955ea45acff",
        "token": "eyJ..."
    }
}

Token znajduje się w:

user.token


---

## 3. Pobranie danych użytkownika

GET /users/me

Wymaga JWT.

curl:

curl -i "BASE_URL/users/me" \
-H "Authorization: Bearer TOKEN"

Odpowiedź:

{
    "user": {
        "id": 13,
        "login": "kinga",
        "email": "kinga@kinga.pl",
        "apiKey": "e6a1b6517e4c"
    }
}


---

## 4. Zmiana hasła

PUT /users/change-password

Wymaga JWT.

curl:

curl -i -X PUT "BASE_URL/users/change-password" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer TOKEN" \
-d '{
    "oldPassword": "test123",
    "newPassword": "nowe123"
}'

Odpowiedź:

Hasło zostało zmienione


---

## 5. Pobranie urządzeń użytkownika

GET /app/devices?apiKey=API_KEY

Wymaga JWT.

curl:

curl -i "BASE_URL/app/devices?apiKey=1955ea45acff" \
-H "Authorization: Bearer TOKEN"

Przykładowa odpowiedź:

[
    {
        "id": 12,
        "deviceName": "TestDevice",
        "userId": 25
    }
]


---

## 6. ESP32 wysyła temperaturę

POST /temperatures

Nie wymaga JWT.

Wymaga apiKey w JSON.

curl:

curl -i -X POST "BASE_URL/temperatures" \
-H "Content-Type: application/json" \
-d '{
    "apiKey": "1955ea45acff",
    "deviceName": "TestDevice",
    "temperature": 35
}'


---

## 7. Pobranie wszystkich temperatur

GET /temperatures

W zależności od konfiguracji endpoint może wymagać JWT.

curl:

curl -i "BASE_URL/temperatures" \
-H "Authorization: Bearer TOKEN"


---

## 8. Pobranie temperatur po deviceId

GET /temperatures/device/{deviceId}

Przykład:

GET /temperatures/device/12

curl:

curl -i "BASE_URL/temperatures/device/12" \
-H "Authorization: Bearer TOKEN"

Przykładowa odpowiedź:

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
    }
]


---

## 9. Pobranie temperatur urządzenia z konkretnego dnia

GET /app/devices/{deviceId}/temperatures?date=YYYY-MM-DD

Przykład:

GET /app/devices/12/temperatures?date=2026-09-29

curl:

curl -i "BASE_URL/app/devices/12/temperatures?date=2026-09-29" \
-H "Authorization: Bearer TOKEN"

Odpowiedź:

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

React Native może wykorzystać te dane do narysowania wykresu.


---

## 10. Usunięcie urządzenia

DELETE /app/devices/{deviceName}?apiKey=API_KEY

Przykład:

DELETE /app/devices/TestDevice?apiKey=1955ea45acff

curl:

curl -i -X DELETE "BASE_URL/app/devices/TestDevice?apiKey=1955ea45acff" \
-H "Authorization: Bearer TOKEN"


---

## 11. Usunięcie konta

DELETE /users/me

Wymaga JWT.

curl:

curl -i -X DELETE "BASE_URL/users/me" \
-H "Authorization: Bearer TOKEN"

Odpowiedź:

Konto zostało usunięte


---

# 60. Testowanie endpointów curl

Przy testowaniu najlepiej używać:

curl -i

Dzięki temu widzimy również:

HTTP status

nagłówki

body odpowiedzi.

Przykład:

curl -i -X POST "BASE_URL/users/register" \
-H "Content-Type: application/json" \
-d '{
    "login": "test",
    "email": "test@test.pl",
    "password": "test123"
}'


---

# 61. Ważne — kolejność testowania całego systemu

Najlepiej testować backend w tej kolejności:

1. uruchomić Spring Boot

2. sprawdzić rejestrację

POST /users/register

3. sprawdzić logowanie

POST /users/login

4. skopiować JWT

5. sprawdzić:

GET /users/me

6. sprawdzić pobieranie urządzeń:

GET /app/devices?apiKey=...

7. wysłać temperaturę:

POST /temperatures

8. ponownie pobrać urządzenia

9. sprawdzić, czy nowe urządzenie zostało utworzone

10. pobrać temperatury urządzenia

11. sprawdzić temperatury z konkretnego dnia

12. sprawdzić zmianę hasła

13. sprawdzić usuwanie urządzenia

14. sprawdzić usuwanie konta


---

# 62. Ważne — urządzenie może powstać automatycznie

ESP32 nie musi najpierw tworzyć urządzenia osobnym endpointem.

Jeżeli wysyła:

{
    "apiKey": "1955ea45acff",
    "deviceName": "NoweESP32",
    "temperature": 35
}

TemperatureService:

1. znajduje użytkownika po apiKey,

2. szuka urządzenia "NoweESP32",

3. jeżeli urządzenia nie ma — tworzy je,

4. zapisuje temperaturę.

Czyli nowe urządzenie może zostać utworzone podczas pierwszego pomiaru.


---

# 63. Ważne — jeden użytkownik może mieć wiele urządzeń

Relacja:

User #25

    ↓

Device #12

Device #13

Device #14

Device #15

Każde urządzenie może mieć własne temperatury:

Device #12

    ↓

Temperature #101

Temperature #102

Temperature #103


Device #13

    ↓

Temperature #104

Temperature #105


Dzięki temu jeden użytkownik może obsługiwać wiele urządzeń.


---

# 64. React Native a Spring Boot

React Native nie musi wiedzieć, że backend został przepisany z Express na Spring.

Dla aplikacji najważniejsze są:

- adres endpointu,
- metoda HTTP,
- JSON requestu,
- JSON odpowiedzi,
- status HTTP,
- sposób uwierzytelnienia.

Jeżeli te elementy pozostają kompatybilne, React Native może korzystać z nowego backendu Spring Boot.


---

# 65. Najważniejsza zasada migracji Express → Spring

Nie wystarczy przepisać kodu 1:1.

Trzeba zachować kontrakt API.

Czyli:

Express

↓

JSON response

↓

React Native


Po migracji:

Spring Boot

↓

JSON response

↓

React Native


React Native powinien otrzymać dane w takiej strukturze, jakiej oczekuje.

Przykład:

Stary Express:

{
    "message": "Zalogowano pomyślnie",
    "user": {
        "id": 26,
        "login": "testspring",
        "email": "testspring@test.pl",
        "apiKey": "74f175f2cf49",
        "token": "eyJ..."
    }
}

Spring:

{
    "message": "Zalogowano pomyślnie",
    "user": {
        "id": 26,
        "login": "testspring",
        "email": "testspring@test.pl",
        "apiKey": "74f175f2cf49",
        "token": "eyJ..."
    }
}

Dla React Native jest to ten sam kontrakt API.


---

# 66. Jedno ważne rozróżnienie — JWT vs apiKey

JWT:

- używa React Native,
- identyfikuje zalogowanego użytkownika,
- jest wysyłany w:

Authorization: Bearer TOKEN

apiKey:

- należy do użytkownika,
- jest używany przez ESP32,
- pozwala backendowi znaleźć użytkownika,
- jest wysyłany przy pomiarze temperatury.

Czyli:

React Native

    ↓

JWT

    ↓

Spring Security


ESP32

    ↓

apiKey

    ↓

TemperatureService


---

# 67. Cały system w jednym schemacie

                    ┌──────────────────┐
                    │   React Native   │
                    └────────┬─────────┘
                             │
                           JWT
                             │
                             ↓
                    ┌──────────────────┐
                    │    Spring Boot   │
                    │     HiveSense    │
                    └────────┬─────────┘
                             │
                     Controller
                             │
                           Service
                             │
                         Repository
                             │
                             ↓
                         MySQL


                    ┌──────────────────┐
                    │      ESP32       │
                    └────────┬─────────┘
                             │
                           Wi-Fi
                             │
                    apiKey + pomiar
                             │
                             ↓
                    POST /temperatures
                             │
                             ↓
                    ┌──────────────────┐
                    │    Spring Boot   │
                    └────────┬─────────┘
                             │
                    TemperatureService
                             │
                    ┌────────┼────────┐
                    ↓        ↓        ↓
                  User    Device   Temperature
                Repository Repository Repository
                             │
                             ↓
                           MySQL


---

# 68. Co będziemy robić dalej?

Backend HiveSense został zbudowany.

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

# 69. Jedno zdanie do zapamiętania

Controller przyjmuje żądanie.

Service wykonuje logikę.

Repository rozmawia z bazą.

Entity opisuje dane z bazy.

DTO opisuje dane przychodzące.

Security pilnuje dostępu.

JWT identyfikuje zalogowanego użytkownika.

apiKey pozwala urządzeniu, np. ESP32, wysyłać pomiary.


---

# 70. Najważniejszy obraz całego projektu

                            INTERNET
                               │
                ┌──────────────┴──────────────┐
                │                             │
                ↓                             ↓
         React Native                       ESP32
                │                             │
               JWT                          apiKey
                │                             │
                ↓                             ↓
        ┌──────────────────────────────────────────┐
        │              Spring Boot                │
        │                                          │
        │  Controller                              │
        │      ↓                                   │
        │  Service                                 │
        │      ↓                                   │
        │  Repository                              │
        │      ↓                                   │
        │  Hibernate / JPA                        │
        │      ↓                                   │
        │  MySQL                                  │
        └──────────────────────────────────────────┘
                           │
                           ↓
                    users / devices /
                    temperatures


Najważniejsza idea:

React Native służy użytkownikowi.

ESP32 wysyła pomiary.

Spring Boot jest backendem.

MySQL przechowuje dane.

JWT zabezpiecza dostęp użytkownika.

apiKey identyfikuje urządzenie/użytkownika wysyłającego pomiar.
# User platform (steps 1-5)

Landing page, register/login, Free/Premium plans, Razorpay payment, dashboard.
Backend: Spring Boot, port 8081. Frontend: React, port 5171. Database: MongoDB.

## Chalane se pehle
Java 17, Maven, Node 18+, MongoDB chalu.

## Razorpay (TEST mode)
1. razorpay.com par free account banao, dashboard me **Test Mode** on rakho.
2. Settings, API Keys, Generate Test Key.
3. Backend wale terminal me (PowerShell), backend chalane se pehle:
```
$env:RAZORPAY_KEY_ID="rzp_test_xxxxxxxx"
$env:RAZORPAY_KEY_SECRET="xxxxxxxxxxxxxxxx"
```
Ye keys sirf us terminal ke liye rehti h. GitHub par kabhi commit mat karna.

## Backend
```
cd backend
mvn spring-boot:run
```

## Frontend
```
cd frontend
npm install
npm run dev
```
Browser: http://localhost:5171

## APIs
| Method | URL | Auth |
|---|---|---|
| POST | /api/auth/register | nahi |
| POST | /api/auth/login | nahi |
| GET | /api/auth/me | JWT |
| POST | /api/payment/create-order | JWT |
| POST | /api/payment/verify | JWT |
| GET | /api/dashboard | JWT |

## Dashboard ka data
Dashboard `videos` collection se padhta h. Transcoding feature jab videos save karega
(userId, originalName, resolution, status, originalSize, outputSize), tab yaha dikhenge.

## Demo payment (Razorpay ke bina)
Presentation ke liye, backend chalane se pehle:
```
$env:PAYMENT_DEMO_MODE="true"
```
Phir "Pay" dabane par demo payment panel aata h aur Premium activate ho jata h.
Ye sirf local demo ke liye h. Real use ya deploy me ise `false` (default) rakho.

# Website fullstack bán sách

## Frontend (React + TypeScript + Vite)

### B1: npm instal

### B2: npm run dev

## Backend Spring boot - MySQL database tên là bookstore

### B1: Tạo env (nhớ đổi mật khẩu và username database)

DATASOURCE_URL=jdbc:mysql://localhost:3306/bookstore?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true

DATASOURCE_USERNAME=root

DATASOURCE_PASSWORD=quanglam456

FRONTEND_URL=http://localhost:5173

BACKEND_URL=http://localhost:8080

JWT_SECRET=a3f4c8e9d0b1f2a3c4e5f67890abcd1234567890abcdef1234567890abcdef12

MOMO_PARTNERCODE=MOMO

MOMO_ACCESSKEY=F8BBA842ECF85

MOMO_SECRETKEY=K951B6PE1waDMi640xX08PD3vg6EkVlz

MOMO_URL=https://test-payment.momo.vn/v2/gateway/api/create

MOMO_REFUND_URL=https://test-payment.momo.vn/v2/gateway/api/refund

### B2: chạy backend

## Hướng dẫn mvn clean up

### B1: Cài đặt env

$env:DATASOURCE_URL="jdbc:mysql://localhost:3306/bookstore?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"

$env:DATASOURCE_USERNAME="root"

$env:DATASOURCE_PASSWORD="quanglam456"

$env:FRONTEND_URL="http://localhost:5173"

$env:BACKEND_URL="http://localhost:8080"

$env:JWT_SECRET="a3f4c8e9d0b1f2a3c4e5f67890abcd1234567890abcdef1234567890abcdef12"

$env:MOMO_PARTNERCODE="MOMO"

$env:MOMO_ACCESSKEY="F8BBA842ECF85"

$env:MOMO_SECRETKEY="K951B6PE1waDMi640xX08PD3vg6EkVlz"

$env:MOMO_URL="https://test-payment.momo.vn/v2/gateway/api/create"

$env:MOMO_REFUND_URL="https://test-payment.momo.vn/v2/gateway/api/refund"

### B2: `./mvnw clean package`
